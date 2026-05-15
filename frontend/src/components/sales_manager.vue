<template>
    <div class="product-management-container">
        <!-- 顶部导航栏 -->
        <div class="top-nav">
            <div class="nav-left">
                <h2 class="nav-title">商品销售管理系统</h2>
            </div>
            <div class="nav-right">
                <el-dropdown trigger="click" @command="handleUserCommand">
                    <div class="user-info-trigger">
                        <el-avatar :size="36" class="user-avatar" :src="avatarUrl">
                            <el-icon><User /></el-icon>
                        </el-avatar>
                        <span class="user-name">{{ currentUser?.username || '未登录' }}</span>
                        <el-icon class="arrow-icon"><ArrowDown /></el-icon>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="logout">
                                <el-icon><SwitchButton /></el-icon>
                                退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>

        <!-- 标签栏导航 -->
        <el-tabs v-model="activeTab" class="nav-tabs" @tab-change="handleTabChange">
            <el-tab-pane label="商品管理" name="/goods/manage" />
            <el-tab-pane v-if="currentUser?.role === 'ADMIN'" label="商家管理" name="/merchants/manage" />
            <el-tab-pane label="个人信息" name="/profile" />
        </el-tabs>

        <!-- 页面标题和操作按钮 -->
        <div class="page-header">
            <div class="header-content">
                <h1 class="page-title">📦 商品管理</h1>
                <p class="page-subtitle">管理商品信息，支持添加、编辑和删除操作</p>
            </div>
            <div class="header-actions">
                <el-button v-if="currentUser?.role === 'MERCHANT'" type="primary" @click="handleAddProduct">
                    添加商品
                </el-button>
                <el-button @click="loadProducts">
                    刷新
                </el-button>
            </div>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-cards">
            <div class="stat-card">
                <div class="stat-icon total">📦</div>
                <div class="stat-content">
                    <div class="stat-value">{{ totalProducts }}</div>
                    <div class="stat-label">商品总数</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon in-stock">✅</div>
                <div class="stat-content">
                    <div class="stat-value">{{ inStockProducts }}</div>
                    <div class="stat-label">有货商品</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon out-stock">⚠️</div>
                <div class="stat-content">
                    <div class="stat-value">{{ outOfStockProducts }}</div>
                    <div class="stat-label">缺货商品</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon category-icon">🏷️</div>
                <div class="stat-content">
                    <div class="stat-value">{{ categories.length }}</div>
                    <div class="stat-label">商品分类</div>
                </div>
            </div>
        </div>

        <!-- 搜索和筛选区域 -->
        <div class="search-filter-section">
            <el-input
                v-model="searchQuery"
                placeholder="搜索商品（名称、分类）"
                clearable
                size="large"
                @input="handleSearch"
            >
                <template #prefix>
                    <el-icon><Search /></el-icon>
                </template>
            </el-input>

            <div class="filter-options">
                <div class="filter-group">
                    <label>分类筛选：</label>
                    <el-select v-model="selectedCategory" placeholder="全部分类" clearable size="default" @change="handleSearch">
                        <el-option
                            v-for="category in categories"
                            :key="category"
                            :label="category"
                            :value="category"
                        />
                    </el-select>
                </div>

                <div class="filter-group">
                    <el-button @click="clearFilters">清空筛选</el-button>
                </div>
            </div>
        </div>

        <!-- 商品列表 -->
        <div class="product-list-section">
            <div class="section-header">
                <h3>商品列表</h3>
                <div class="list-stats">
                    共 {{ filteredProducts.length }} 件商品
                    <el-tag v-if="searchQuery || selectedCategory" type="info" size="small" style="margin-left: 8px;">
                        已筛选
                    </el-tag>
                </div>
            </div>

            <el-empty
                v-if="!loading && filteredProducts.length === 0"
                :description="(searchQuery || selectedCategory) ? '未找到符合条件的商品' : '暂无商品数据'"
            >
                <el-button v-if="searchQuery || selectedCategory" @click="clearFilters">
                    清空筛选条件
                </el-button>
            </el-empty>

            <el-table
                v-else
                :data="filteredProducts"
                stripe
                border
                v-loading="loading"
                style="width: 100%"
            >
                <el-table-column prop="productId" label="商品ID" width="100" />
                <el-table-column label="图片" width="80" align="center">
                    <template #default="{ row }">
                        <el-image
                            v-if="row.productId"
                            :src="getProductImageUrl(row.productId)"
                            style="width: 50px; height: 50px;"
                            fit="cover"
                        >
                            <template #error>
                                <div class="image-placeholder">无图</div>
                            </template>
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="商品名称">
                    <template #default="{ row }">
                        <strong>{{ row.name }}</strong>
                    </template>
                </el-table-column>
                <el-table-column prop="category" label="分类" width="100">
                    <template #default="{ row }">
                        <el-tag size="small">{{ row.category }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="进价" width="100" align="right">
                    <template #default="{ row }">
                        ¥{{ row.purchasePrice?.toFixed(2) }}
                    </template>
                </el-table-column>
                <el-table-column label="售价" width="100" align="right">
                    <template #default="{ row }">
                        <span class="price-text">¥{{ row.salePrice?.toFixed(2) }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="库存" width="80" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.stock > 0 ? '' : 'danger'" size="small">
                            {{ row.stock }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150" align="center">
                    <template #default="{ row }">
                        <el-button v-if="currentUser?.role === 'MERCHANT'" type="primary" size="small" link @click="handleEditProduct(row)">
                            编辑
                        </el-button>
                        <el-button v-if="currentUser?.role === 'MERCHANT'" type="danger" size="small" link @click="handleDeleteProduct(row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页组件 -->
            <div class="pagination-wrapper" v-if="totalPages > 1">
                <el-pagination
                    v-model:current-page="currentPage"
                    :page-size="pageSize"
                    :total="filteredProducts.length"
                    layout="prev, pager, next"
                    @current-change="changePage"
                />
            </div>
        </div>

        <!-- 添加/编辑商品对话框 -->
        <el-dialog
            v-model="showProductDialog"
            :title="isEditing ? '编辑商品' : '添加商品'"
            width="700px"
            :close-on-click-modal="false"
        >
            <el-form :model="form" label-width="100px" ref="productFormRef">
                <div class="form-grid">
                    <el-form-item label="商品名称" required>
                        <el-input v-model="form.name" placeholder="请输入商品名称" />
                    </el-form-item>

                    <el-form-item label="分类" required>
                        <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;" @change="onCategoryChange">
                            <el-option value="" label="请选择分类" disabled />
                            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
                            <el-option value="__new__" label="新建分类" />
                        </el-select>
                        <el-input
                            v-if="form.category === '__new__'"
                            v-model="newCategory"
                            placeholder="请输入新分类名称"
                            style="margin-top: 8px;"
                        />
                    </el-form-item>

                    <el-form-item label="进价" required>
                        <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" style="width: 100%;" />
                    </el-form-item>

                    <el-form-item label="售价" required>
                        <el-input-number v-model="form.salePrice" :min="0" :precision="2" style="width: 100%;" />
                    </el-form-item>

                    <el-form-item label="库存" required>
                        <el-input-number v-model="form.stock" :min="0" style="width: 100%;" />
                    </el-form-item>

                    <!-- 编辑时才可上传图片 -->
                    <el-form-item v-if="isEditing" label="商品图片">
                        <el-upload
                            class="product-image-upload"
                            action=""
                            :http-request="handleProductImageUpload"
                            :show-file-list="false"
                            :before-upload="beforeProductImageUpload"
                            accept="image/*"
                        >
                            <img v-if="editingProductId" :src="productImagePreviewUrl" class="product-image-preview" />
                            <el-icon v-else class="product-image-upload-icon"><Plus /></el-icon>
                        </el-upload>
                    </el-form-item>
                </div>
            </el-form>

            <template #footer>
                <el-button @click="closeProductDialog">取消</el-button>
                <el-button type="primary" @click="submitProductForm" :loading="submitting">
                    {{ isEditing ? '更新' : '添加' }}
                </el-button>
            </template>
        </el-dialog>

        <!-- 删除确认对话框 -->
        <el-dialog
            v-model="showDeleteDialog"
            title="确认删除"
            width="500px"
            :close-on-click-modal="false"
        >
            <el-alert
                title="确定要删除以下商品吗？"
                type="warning"
                :closable="false"
                show-icon
                style="margin-bottom: 16px;"
            />

            <div class="product-to-delete" v-if="productToDelete">
                <div class="product-info">
                    <div class="product-title-delete">{{ productToDelete.name }}</div>
                    <div class="product-details">
                        <p><strong>商品ID：</strong>{{ productToDelete.productId }}</p>
                        <p><strong>分类：</strong>{{ productToDelete.category }}</p>
                        <p><strong>售价：</strong>¥{{ productToDelete.salePrice?.toFixed(2) }}</p>
                        <p><strong>库存：</strong>{{ productToDelete.stock }} 件</p>
                    </div>
                </div>
            </div>

            <el-alert
                type="error"
                :closable="false"
                style="margin-top: 16px;"
            >
                <template #title>
                    <div><strong>注意：</strong>此操作不可撤销，删除后商品信息将无法恢复。</div>
                </template>
            </el-alert>

            <template #footer>
                <el-button @click="closeDeleteDialog">取消</el-button>
                <el-button type="danger" @click="confirmDelete" :loading="submitting">
                    确认删除
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { useUserStore } from '@/pinia/userStores.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, User, ArrowDown, SwitchButton, Plus } from '@element-plus/icons-vue';
import router from '@/router';
import { userLogout, getProductList, createProduct, updateProduct, deleteProduct, getAvatarUrl, uploadProductImage, getProductImageUrl } from '@/api/index.js';

export default {
    name: 'ProductManagement',
    components: { Search, User, ArrowDown, SwitchButton, Plus },
    data() {
        return {
            // 当前用户信息
            currentUser: null,
            // 商品数据
            productList: [],
            searchQuery: '',
            selectedCategory: '',

            // 分页
            currentPage: 1,
            pageSize: 10,

            // 加载状态
            loading: false,
            submitting: false,

            // 表单数据
            form: {
                name: '',
                category: '',
                purchasePrice: 0,
                salePrice: 0,
                stock: 0
            },
            // 新建分类名称
            newCategory: '',
            // 编辑时的商品ID
            editingProductId: null,

            // 对话框状态
            showProductDialog: false,
            showDeleteDialog: false,
            isEditing: false,

            // 待删除商品
            productToDelete: null,
            // 头像时间戳（用于破缓存刷新头像）
            avatar_timestamp: Date.now(),
            // 商品图片时间戳（用于破缓存刷新商品图片）
            product_image_timestamp: Date.now()
        }
    },
    computed: {
        // 当前激活的标签页
        activeTab: {
            get() {
                return '/goods/manage';
            },
            set() {}
        },

        // 头像URL，通过API获取用户头像
        avatarUrl() {
            if (this.currentUser?.userId) {
                return getAvatarUrl(this.currentUser.userId) + `?t=${this.avatar_timestamp}`;
            }
            return '';
        },

        // 编辑时商品图片预览URL
        productImagePreviewUrl() {
            if (this.editingProductId) {
                return getProductImageUrl(this.editingProductId) + `?t=${this.product_image_timestamp}`;
            }
            return '';
        },

        categories() {
            const categories = new Set(this.productList.map(product => product.category).filter(Boolean))
            return Array.from(categories).sort()
        },

        filteredProducts() {
            let products = [...this.productList]

            if (this.searchQuery) {
                const query = this.searchQuery.toLowerCase()
                products = products.filter(product =>
                    product.name.toLowerCase().includes(query) ||
                    (product.category && product.category.toLowerCase().includes(query))
                )
            }

            if (this.selectedCategory) {
                products = products.filter(product => product.category === this.selectedCategory)
            }

            return products
        },

        totalProducts() {
            return this.productList.length
        },

        inStockProducts() {
            return this.productList.filter(product => product.stock > 0).length
        },

        outOfStockProducts() {
            return this.productList.filter(product => product.stock <= 0).length
        },

        totalPages() {
            return Math.ceil(this.filteredProducts.length / this.pageSize)
        }
    },
    mounted() {
        this.loadUserInfo();
        this.loadProducts()
    },
    methods: {
        // 获取商品图片URL
        getProductImageUrl,

        // 加载当前用户信息
        loadUserInfo() {
            const userStore = useUserStore();
            userStore.loadUserFromStorage();
            if (userStore.isLoggedIn) {
                this.currentUser = {
                    userId: userStore.userId,
                    username: userStore.username,
                    role: userStore.role
                };
            }
        },

        /**
         * 标签页切换导航
         * @param {String} tab 目标路由路径
         */
        handleTabChange(tab) {
            if (tab !== '/goods/manage') {
                router.push(tab);
            }
        },

        /**
         * 处理用户下拉菜单命令
         * @param {String} command 命令类型
         */
        handleUserCommand(command) {
            if (command === 'logout') {
                ElMessageBox.confirm('确定要退出登录吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(async () => {
                    try {
                        await userLogout();
                    } catch (error) {
                        console.error('退出登录请求失败:', error);
                    }
                    const userStore = useUserStore();
                    userStore.logout();
                    this.currentUser = null;
                    ElMessage.success('已退出登录');
                    router.push('/login');
                }).catch(() => {});
            }
        },
        async loadProducts() {
            this.loading = true
            try {
                const response = await getProductList({
                    productName: '',
                    category: '',
                    pageIndex: 1,
                    pageSize: 20
                })
                if (response.data.code === 1) {
                    const res_data = response.data.data;
                    if (res_data && Array.isArray(res_data.items)) {
                        this.productList = res_data.items;
                    } else {
                        this.productList = [];
                    }
                } else {
                    ElMessage.error(response.data.msg || '加载商品数据失败')
                }
            } catch (error) {
                console.error('加载商品数据失败:', error)
                ElMessage.error('加载商品数据失败，请检查网络连接')
            } finally {
                this.loading = false
            }
        },

        handleSearch() {
            this.currentPage = 1
        },

        clearFilters() {
            this.searchQuery = ''
            this.selectedCategory = ''
            this.currentPage = 1
        },

        // 分类选择变化
        onCategoryChange(val) {
            if (val !== '__new__') {
                this.newCategory = ''
            }
        },

        handleAddProduct() {
            this.isEditing = false
            this.editingProductId = null
            this.resetForm()
            this.showProductDialog = true
        },

        handleEditProduct(product) {
            this.isEditing = true
            this.editingProductId = product.productId
            this.form = {
                name: product.name,
                category: product.category,
                purchasePrice: product.purchasePrice,
                salePrice: product.salePrice,
                stock: product.stock
            }
            this.product_image_timestamp = Date.now()
            this.showProductDialog = true
        },

        handleDeleteProduct(product) {
            this.productToDelete = product
            this.showDeleteDialog = true
        },

        async submitProductForm() {
            if (!this.validateForm()) {
                return
            }

            this.submitting = true

            try {
                const formData = { ...this.form }
                if (formData.category === '__new__' && this.newCategory) {
                    formData.category = this.newCategory
                }

                let response
                if (this.isEditing) {
                    response = await updateProduct(this.editingProductId, formData)
                } else {
                    response = await createProduct(formData)
                }

                if (response.data.code === 1) {
                    ElMessage.success(this.isEditing ? '商品更新成功' : '商品添加成功')
                    this.closeProductDialog()
                    this.loadProducts()
                } else {
                    ElMessage.error(response.data.msg || '操作失败')
                }
            } catch (error) {
                console.error('操作失败:', error)
                ElMessage.error('操作失败，请稍后重试')
            } finally {
                this.submitting = false
            }
        },

        async confirmDelete() {
            if (!this.productToDelete) return

            this.submitting = true

            try {
                const response = await deleteProduct(this.productToDelete.productId)

                if (response.data.code === 1) {
                    ElMessage.success('商品删除成功')
                    this.closeDeleteDialog()
                    this.loadProducts()
                } else {
                    ElMessage.error(response.data.msg || '删除失败')
                }
            } catch (error) {
                console.error('删除失败:', error)
                ElMessage.error('删除失败，请稍后重试')
            } finally {
                this.submitting = false
            }
        },

        validateForm() {
            if (!this.form.name.trim()) {
                ElMessage.warning('请输入商品名称')
                return false
            }

            if (!this.form.category.trim() || (this.form.category === '__new__' && !this.newCategory.trim())) {
                ElMessage.warning('请选择或输入分类')
                return false
            }

            if (this.form.purchasePrice < 0) {
                ElMessage.warning('进价不能为负数')
                return false
            }

            if (this.form.salePrice < 0) {
                ElMessage.warning('售价不能为负数')
                return false
            }

            if (this.form.stock < 0) {
                ElMessage.warning('库存不能为负数')
                return false
            }

            return true
        },

        resetForm() {
            this.form = {
                name: '',
                category: '',
                purchasePrice: 0,
                salePrice: 0,
                stock: 0
            }
            this.newCategory = ''
        },

        closeProductDialog() {
            this.showProductDialog = false
            this.resetForm()
        },

        closeDeleteDialog() {
            this.showDeleteDialog = false
            this.productToDelete = null
        },

        changePage(page) {
            this.currentPage = page
        },

        /**
         * 商品图片上传前校验
         * @param {File} file 上传的文件
         */
        beforeProductImageUpload(file) {
            const is_image = file.type.startsWith('image/');
            const is_lt5m = file.size / 1024 / 1024 < 5;

            if (!is_image) {
                ElMessage.error('只能上传图片文件');
                return false;
            }
            if (!is_lt5m) {
                ElMessage.error('图片大小不能超过5MB');
                return false;
            }
            return true;
        },

        /**
         * 自定义商品图片上传（编辑时，需productId和merchantId）
         * @param {Object} params 上传参数
         */
        async handleProductImageUpload(params) {
            try {
                const response = await uploadProductImage({
                    product_id: this.editingProductId,
                    merchant_id: this.currentUser.userId,
                    file: params.file
                });

                if (response.data.code === 1) {
                    // 更新时间戳以破缓存刷新商品图片
                    this.product_image_timestamp = Date.now();
                    ElMessage.success('图片上传成功');
                } else {
                    ElMessage.error(response.data.msg || '图片上传失败');
                }
            } catch (error) {
                console.error('图片上传失败:', error);
                ElMessage.error('图片上传失败，请稍后重试');
            }
        }
    }
}
</script>

<style scoped>
.product-management-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Microsoft YaHei', sans-serif;
    background-color: #f8fafc;
    min-height: 100vh;
}

/* 顶部导航栏 */
.top-nav {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    border-radius: 12px;
    padding: 12px 24px;
    margin-bottom: 0;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.nav-left {
    display: flex;
    align-items: center;
}

.nav-title {
    margin: 0;
    color: #409eff;
    font-size: 20px;
    font-weight: 600;
}

.nav-right {
    display: flex;
    align-items: center;
}

.user-info-trigger {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    padding: 4px 12px;
    border-radius: 8px;
    transition: background 0.3s;
}

.user-info-trigger:hover {
    background: #f5f7fa;
}

.user-avatar {
    background: linear-gradient(135deg, #409eff, #66b1ff);
    color: white;
}

.user-name {
    font-size: 14px;
    color: #303133;
    font-weight: 500;
}

.arrow-icon {
    font-size: 12px;
    color: #909399;
}

/* 标签栏导航 */
.nav-tabs {
    background: white;
    border-radius: 0 0 12px 12px;
    padding: 0 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

/* 页面标题区域 */
.page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;
    flex-wrap: wrap;
    gap: 16px;
}

.header-content {
    flex: 1;
    min-width: 300px;
}

.page-title {
    margin: 0 0 8px 0;
    color: #1a202c;
    font-size: 28px;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 12px;
}

.page-subtitle {
    margin: 0;
    color: #718096;
    font-size: 16px;
}

.header-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}

/* 统计卡片 */
.stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 24px;
}

.stat-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 20px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
    transition: transform 0.3s ease;
}

.stat-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
}

.stat-icon.total {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

.stat-icon.in-stock {
    background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
    color: white;
}

.stat-icon.out-stock {
    background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%);
    color: white;
}

.stat-icon.category-icon {
    background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);
    color: white;
}

.stat-content {
    flex: 1;
}

.stat-value {
    font-size: 32px;
    font-weight: 700;
    color: #1a202c;
    margin-bottom: 4px;
}

.stat-label {
    font-size: 14px;
    color: #718096;
}

/* 搜索和筛选区域 */
.search-filter-section {
    background: white;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.filter-options {
    display: flex;
    gap: 24px;
    flex-wrap: wrap;
    align-items: center;
    margin-top: 16px;
}

.filter-group {
    display: flex;
    align-items: center;
    gap: 8px;
}

.filter-group label {
    font-weight: 500;
    color: #4a5568;
    white-space: nowrap;
}

/* 商品列表区域 */
.product-list-section {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    flex-wrap: wrap;
    gap: 16px;
}

.section-header h3 {
    margin: 0;
    color: #2d3748;
    font-size: 20px;
    font-weight: 600;
}

.list-stats {
    color: #718096;
    font-size: 14px;
}

.price-text {
    color: #f56c6c;
    font-weight: 600;
}

/* 图片占位 */
.image-placeholder {
    width: 50px;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    color: #909399;
    font-size: 12px;
    border-radius: 4px;
}

/* 分页 */
.pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #e2e8f0;
}

/* 表单网格 */
.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0 20px;
}

/* 商品图片上传 */
.product-image-upload :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 120px;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: border-color 0.3s;
}

.product-image-upload :deep(.el-upload:hover) {
    border-color: #409eff;
}

.product-image-preview {
    width: 120px;
    height: 120px;
    object-fit: cover;
    display: block;
}

.product-image-upload-icon {
    font-size: 28px;
    color: #8c939d;
}

/* 删除对话框 */
.product-to-delete {
    background: #f7fafc;
    border-radius: 8px;
    padding: 20px;
}

.product-title-delete {
    font-size: 18px;
    font-weight: 600;
    color: #1a202c;
    margin-bottom: 12px;
}

.product-details p {
    margin: 8px 0;
    color: #4a5568;
    font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .product-management-container {
        padding: 12px;
    }

    .page-header {
        flex-direction: column;
        align-items: stretch;
    }

    .header-actions {
        justify-content: flex-start;
    }

    .stats-cards {
        grid-template-columns: 1fr;
    }

    .filter-options {
        flex-direction: column;
        align-items: stretch;
        gap: 16px;
    }

    .filter-group {
        flex-direction: column;
        align-items: flex-start;
    }

    .form-grid {
        grid-template-columns: 1fr;
        gap: 0;
    }
}
</style>
