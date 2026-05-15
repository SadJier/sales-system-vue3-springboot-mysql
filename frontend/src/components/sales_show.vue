<template>
    <div class="product-show-container" v-loading="loading">
        <!-- 用户信息展示区 -->
        <div class="user-info-section" v-if="currentUser">
            <div class="user-card">
                <div class="user-header">
                    <div class="user-avatar">
                        <el-icon :size="36"><User /></el-icon>
                    </div>
                    <div class="user-details">
                        <h3>{{ currentUser.username }}</h3>
                        <p class="user-role">{{ currentUser.role === 'ADMIN' ? '管理员' : '商家' }}</p>
                    </div>
                </div>

                <div class="user-stats">
                    <div class="stat-item">
                        <div class="stat-icon">📦</div>
                        <div class="stat-content">
                            <div class="stat-value">{{ productList.length }}</div>
                            <div class="stat-label">商品总数</div>
                        </div>
                    </div>

                    <div class="stat-item">
                        <div class="stat-icon">🏷️</div>
                        <div class="stat-content">
                            <div class="stat-value">{{ categories.length }}</div>
                            <div class="stat-label">商品分类</div>
                        </div>
                    </div>

                    <div class="stat-item">
                        <div class="stat-icon">✅</div>
                        <div class="stat-content">
                            <div class="stat-value">{{ inStockCount }}</div>
                            <div class="stat-label">有货商品</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 搜索和筛选区 -->
        <div class="search-section">
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
                    <label>分类：</label>
                    <el-select v-model="selectedCategory" placeholder="全部" clearable size="default" @change="handleSearch">
                        <el-option
                            v-for="category in categories"
                            :key="category"
                            :label="category"
                            :value="category"
                        />
                    </el-select>
                </div>

                <div class="filter-group">
                    <label>排序：</label>
                    <el-select v-model="sortBy" size="default" @change="handleSearch">
                        <el-option label="名称A-Z" value="name" />
                        <el-option label="名称Z-A" value="name_desc" />
                        <el-option label="价格从低到高" value="salePrice" />
                        <el-option label="价格从高到低" value="salePrice_desc" />
                    </el-select>
                </div>
            </div>
        </div>

        <!-- 商品列表 -->
        <div class="product-list-section">
            <div class="section-header">
                <h3>商品列表</h3>
                <div class="list-stats">
                    共 {{ filteredProducts.length }} 件商品
                    <el-tag v-if="searchQuery" type="info" size="small" style="margin-left: 8px;">
                        搜索到 {{ filteredProducts.length }} 条结果
                    </el-tag>
                </div>
            </div>

            <el-empty
                v-if="!loading && filteredProducts.length === 0"
                :description="searchQuery ? '未找到相关商品' : '暂无商品'"
            >
                <el-button v-if="searchQuery" @click="clearSearch">清空搜索</el-button>
            </el-empty>

            <el-table
                v-else
                :data="paginatedProducts"
                stripe
                border
                style="width: 100%"
            >
                <el-table-column prop="productId" label="商品ID" width="100" />
                <el-table-column prop="name" label="商品名称" width="200">
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
            </el-table>

            <!-- 分页 -->
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

    </div>
</template>

<script>
import { useUserStore } from '@/pinia/userStores.js';
import { ElMessage } from 'element-plus';
import { User, Search } from '@element-plus/icons-vue';
import { getProductList } from '@/api/index.js';

export default {
    name: 'ProductShow',
    components: { User, Search },
    data() {
        return {
            // 商品数据
            productList: [],
            searchQuery: '',
            selectedCategory: '',
            sortBy: 'name',
            loading: false,

            // 分页
            currentPage: 1,
            pageSize: 10,

            // 当前用户
            currentUser: null,
        }
    },
    computed: {
        categories() {
            const categories = new Set(this.productList.map(product => product.category).filter(Boolean))
            return Array.from(categories).sort()
        },

        // 有库存商品数量
        inStockCount() {
            return this.productList.filter(product => product.stock > 0).length
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

            products.sort((a, b) => {
                if (this.sortBy === 'name') {
                    return a.name.localeCompare(b.name)
                } else if (this.sortBy === 'name_desc') {
                    return b.name.localeCompare(a.name)
                } else if (this.sortBy === 'salePrice') {
                    return (a.salePrice || 0) - (b.salePrice || 0)
                } else if (this.sortBy === 'salePrice_desc') {
                    return (b.salePrice || 0) - (a.salePrice || 0)
                }
                return 0
            })

            return products
        },

        // 分页后的商品
        paginatedProducts() {
            const start = (this.currentPage - 1) * this.pageSize
            return this.filteredProducts.slice(start, start + this.pageSize)
        },

        totalPages() {
            return Math.ceil(this.filteredProducts.length / this.pageSize)
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        async loadData() {
            this.loading = true;

            try {
                const userStore = useUserStore();
                userStore.loadUserFromStorage();

                if (!userStore.isLoggedIn) {
                    ElMessage.error('未获取到用户信息，请重新登录');
                    this.loading = false;
                    return;
                }

                this.currentUser = {
                    userId: userStore.userId,
                    username: userStore.username,
                    role: userStore.role
                };

                // 获取商品列表
                const response = await getProductList({
                    productName: '',
                    category: '',
                    pageIndex: 1,
                    pageSize: 1000
                });

                if (response.data.code === 1) {
                    const res_data = response.data.data;
                    if (res_data && Array.isArray(res_data.items)) {
                        this.productList = res_data.items;
                    } else {
                        this.productList = [];
                    }
                } else {
                    ElMessage.error(response.data.msg || '获取商品列表失败');
                }

            } catch (error) {
                console.error('加载数据失败:', error);
                ElMessage.error('加载数据失败，请稍后重试');
            } finally {
                this.loading = false;
            }
        },

        handleSearch() {
            this.currentPage = 1
        },

        clearSearch() {
            this.searchQuery = ''
            this.selectedCategory = ''
            this.sortBy = 'name'
            this.currentPage = 1
        },

        changePage(page) {
            this.currentPage = page
        }
    }
}
</script>
<style scoped>
.product-show-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

/* 用户信息区域 */
.user-info-section {
    margin-bottom: 24px;
}

.user-card {
    background: linear-gradient(220deg, #409eff 0%, #66b1ff 100%);
    border-radius: 12px;
    padding: 24px;
    color: white;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.user-header {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 24px;
}

.user-avatar {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.user-details h3 {
    margin: 0 0 8px 0;
    font-size: 24px;
    font-weight: 600;
}

.user-role {
    margin: 4px 0;
    opacity: 0.9;
    font-size: 14px;
}

.user-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
}

.stat-item {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
    padding: 16px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: transform 0.3s ease;
}

.stat-item:hover {
    transform: translateY(-2px);
    background: rgba(255, 255, 255, 0.15);
}

.stat-icon {
    font-size: 24px;
}

.stat-content {
    flex: 1;
}

.stat-value {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 4px;
}

.stat-label {
    font-size: 14px;
    opacity: 0.9;
}

/* 搜索区域 */
.search-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.filter-options {
    display: flex;
    gap: 24px;
    flex-wrap: wrap;
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
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
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

/* 分页 */
.pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #e2e8f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .product-show-container {
        padding: 12px;
    }

    .user-stats {
        grid-template-columns: 1fr;
        gap: 12px;
    }

    .filter-options {
        flex-direction: column;
        gap: 12px;
    }

    .filter-group {
        flex-direction: column;
        align-items: flex-start;
    }

    .section-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }
}
</style>
