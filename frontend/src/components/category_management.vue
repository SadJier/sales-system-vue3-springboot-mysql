<template>
    <div class="category-management-container">
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
            <el-tab-pane label="订单管理" name="/orders/manage" />
            <el-tab-pane label="分类管理" name="/categories/manage" />
            <el-tab-pane label="用户管理" name="/merchants/manage" />
            <el-tab-pane label="个人信息" name="/profile" />
        </el-tabs>

        <!-- 页面标题 -->
        <div class="page-header">
            <div class="header-content">
                <h1 class="page-title">🏷️ 分类管理</h1>
                <p class="page-subtitle">管理商品分类，支持新增、编辑和删除</p>
            </div>
            <div class="header-actions">
                <el-button type="primary" @click="handleAddCategory">新增分类</el-button>
                <el-button @click="loadCategories">刷新</el-button>
            </div>
        </div>

        <!-- 分类列表 -->
        <div class="category-list-section">
            <el-table :data="categoryList" stripe border v-loading="loading" style="width: 100%">
                <el-table-column prop="categoryId" label="分类ID" width="100" />
                <el-table-column prop="name" label="分类名称">
                    <template #default="{ row }">
                        <strong>{{ row.name }}</strong>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="160" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" link @click="handleEditCategory(row)">编辑</el-button>
                        <el-button type="danger" size="small" link @click="handleDeleteCategory(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-empty v-if="!loading && categoryList.length === 0" description="暂无分类数据" />
        </div>

        <!-- 新增/编辑分类对话框 -->
        <el-dialog v-model="showDialog" :title="isEditing ? '编辑分类' : '新增分类'" width="400px" :close-on-click-modal="false">
            <el-form label-width="80px" @submit.prevent="submitForm">
                <el-form-item label="分类名称">
                    <el-input v-model="form.name" placeholder="请输入分类名称" @keyup.enter="submitForm" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showDialog = false">取消</el-button>
                <el-button type="primary" @click="submitForm" :loading="submitting">确认</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { useUserStore } from '@/pinia/userStores.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, ArrowDown, SwitchButton } from '@element-plus/icons-vue';
import router from '@/router';
import { processResult } from '@/axios/index.js';
import { userLogout, getCategoryList, createCategory, updateCategory, deleteCategory, getAvatarUrl } from '@/api/index.js';

export default {
    name: 'CategoryManagement',
    components: { User, ArrowDown, SwitchButton },
    data() {
        return {
            // 当前用户信息
            currentUser: null,
            // 分类列表
            categoryList: [],
            loading: false,
            submitting: false,
            // 对话框状态
            showDialog: false,
            isEditing: false,
            // 表单数据
            form: {
                categoryId: null,
                name: ''
            },
            // 头像时间戳
            avatar_timestamp: Date.now()
        };
    },
    computed: {
        // 当前激活的标签页（跟随当前路由路径）
        activeTab: {
            get() { return this.$route.path; },
            set() {}
        },
        avatarUrl() {
            if (this.currentUser?.userId) {
                return getAvatarUrl(this.currentUser.userId) + `?t=${this.avatar_timestamp}`;
            }
            return '';
        }
    },
    mounted() {
        this.loadUserInfo();
        this.loadCategories();
    },
    methods: {
        loadUserInfo() {
            const userStore = useUserStore();
            userStore.loadUserFromStorage();
            if (userStore.isLoggedIn) {
                this.currentUser = {
                    userId: userStore.userId,
                    username: userStore.username,
                    role: userStore.role
                };
                if (userStore.role !== 'ADMIN') {
                    ElMessage.warning('仅管理员可访问此页面');
                    router.push('/goods/manage');
                }
            } else {
                ElMessage.warning('请先登录');
                router.push('/login');
            }
        },

        handleTabChange(tab) {
            if (tab !== '/categories/manage') {
                router.push(tab);
            }
        },

        handleUserCommand(command) {
            if (command === 'logout') {
                ElMessageBox.confirm('确定要退出登录吗？', '提示', {
                    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
                }).then(async () => {
                    try { await userLogout(); } catch (error) { console.error('退出登录请求失败:', error); }
                    const userStore = useUserStore();
                    userStore.logout();
                    this.currentUser = null;
                    ElMessage.success('已退出登录');
                    router.push('/login');
                }).catch(() => {});
            }
        },

        // 加载分类列表
        async loadCategories() {
            this.loading = true;
            try {
                const response = await getCategoryList();
                const result = processResult(response.data, '获取分类列表失败');
                if (result.success) {
                    const res_data = result.data;
                    if (res_data && Array.isArray(res_data.categories)) {
                        this.categoryList = res_data.categories;
                    } else {
                        this.categoryList = [];
                    }
                } else {
                    ElMessage.error(result.msg || '获取分类列表失败');
                }
            } catch (error) {
                console.error('获取分类列表失败:', error);
                ElMessage.error('获取分类列表失败，请检查网络连接');
            } finally {
                this.loading = false;
            }
        },

        // 新增分类
        handleAddCategory() {
            this.isEditing = false;
            this.form = { categoryId: null, name: '' };
            this.showDialog = true;
        },

        // 编辑分类
        handleEditCategory(category) {
            this.isEditing = true;
            this.form = { categoryId: category.categoryId, name: category.name };
            this.showDialog = true;
        },

        // 提交表单
        async submitForm() {
            if (!this.form.name.trim()) {
                ElMessage.warning('请输入分类名称');
                return;
            }
            this.submitting = true;
            try {
                let response;
                if (this.isEditing) {
                    response = await updateCategory(this.form);
                } else {
                    response = await createCategory(this.form.name);
                }
                const result = processResult(response.data, '操作失败');
                if (result.success) {
                    ElMessage.success(result.msg || (this.isEditing ? '分类更新成功' : '分类新增成功'));
                    this.showDialog = false;
                    this.loadCategories();
                } else {
                    ElMessage.error(result.msg || '操作失败');
                }
            } catch (error) {
                console.error('操作失败:', error);
                ElMessage.error('操作失败，请稍后重试');
            } finally {
                this.submitting = false;
            }
        },

        // 删除分类
        handleDeleteCategory(category) {
            ElMessageBox.confirm(`确定要删除分类"${category.name}"吗？`, '确认删除', {
                confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
            }).then(async () => {
                try {
                    const response = await deleteCategory(category.categoryId);
                    const result = processResult(response.data, '删除分类失败');
                    if (result.success) {
                        ElMessage.success(result.msg || '删除分类成功');
                        this.loadCategories();
                    } else {
                        ElMessage.error(result.msg || '删除分类失败');
                    }
                } catch (error) {
                    console.error('删除分类失败:', error);
                    ElMessage.error('删除分类失败，请稍后重试');
                }
            }).catch(() => {});
        }
    }
};
</script>

<style scoped>
.category-management-container {
    max-width: 1400px; margin: 0 auto; padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Microsoft YaHei', sans-serif;
    background-color: #f8fafc; min-height: 100vh;
}
.top-nav { display: flex; justify-content: space-between; align-items: center; background: white; border-radius: 12px; padding: 12px 24px; margin-bottom: 0; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
.nav-left { display: flex; align-items: center; }
.nav-title { margin: 0; color: #409eff; font-size: 20px; font-weight: 600; }
.nav-right { display: flex; align-items: center; }
.user-info-trigger { display: flex; align-items: center; gap: 10px; cursor: pointer; padding: 4px 12px; border-radius: 8px; transition: background 0.3s; }
.user-info-trigger:hover { background: #f5f7fa; }
.user-avatar { background: linear-gradient(135deg, #409eff, #66b1ff); color: white; }
.user-name { font-size: 14px; color: #303133; font-weight: 500; }
.arrow-icon { font-size: 12px; color: #909399; }
.nav-tabs { background: white; border-radius: 0 0 12px 12px; padding: 0 24px; margin-bottom: 24px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
.nav-tabs :deep(.el-tabs__active-bar) { transition: none; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; flex-wrap: wrap; gap: 16px; }
.header-content { flex: 1; min-width: 300px; }
.page-title { margin: 0 0 8px 0; color: #1a202c; font-size: 28px; font-weight: 700; }
.page-subtitle { margin: 0; color: #718096; font-size: 16px; }
.header-actions { display: flex; gap: 12px; flex-wrap: wrap; }
.category-list-section { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
</style>
