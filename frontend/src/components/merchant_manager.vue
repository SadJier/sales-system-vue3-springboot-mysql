<template>
    <div class="merchant-management-container">
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
                <h1 class="page-title">👥 用户管理</h1>
                <p class="page-subtitle">搜索、查看和管理用户账户</p>
            </div>
            <div class="header-actions">
                <el-button @click="handleSearch" :loading="loading">刷新</el-button>
            </div>
        </div>

        <!-- 搜索区域 -->
        <div class="search-section">
            <el-input
                v-model="searchKeyword"
                placeholder="搜索用户（用户名）"
                clearable
                size="large"
                @keyup.enter="handleSearch"
                @clear="handleSearch"
            >
                <template #prefix>
                    <el-icon><Search /></el-icon>
                </template>
                <template #append>
                    <el-button @click="handleSearch" :loading="loading">搜索</el-button>
                </template>
            </el-input>
            <div class="filter-options">
                <div class="filter-group">
                    <label>身份筛选：</label>
                    <el-select v-model="roleFilter" placeholder="全部身份" clearable size="default" style="min-width: 140px;" @change="handleSearch">
                        <el-option label="商家" value="MERCHANT" />
                        <el-option label="管理员" value="ADMIN" />
                    </el-select>
                </div>
            </div>
        </div>

        <!-- 用户列表 -->
        <div class="merchant-list-section">
            <div class="section-header">
                <h3>用户列表</h3>
                <div class="list-stats">
                    共 {{ filteredUserList.length }} 个用户
                </div>
            </div>

            <el-table
                :data="filteredUserList"
                stripe
                border
                v-loading="loading"
                style="width: 100%"
            >
                <el-table-column prop="userId" label="用户ID" width="100" />
                <el-table-column prop="userName" label="用户名">
                    <template #default="{ row }">
                        <el-link v-if="row.role === 'MERCHANT'" type="primary" @click="goToStoreOverview(row.userId)">{{ row.userName }}</el-link>
                        <strong v-else>{{ row.userName }}</strong>
                    </template>
                </el-table-column>
                <el-table-column prop="role" label="身份" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.role === 'ADMIN' ? 'danger' : ''" size="small">
                            {{ row.role === 'ADMIN' ? '管理员' : '商家' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="160" align="center">
                    <template #default="{ row }">
                        <el-button
                            v-if="row.role === 'MERCHANT'"
                            type="primary"
                            size="small"
                            link
                            @click="goToStoreOverview(row.userId)"
                        >
                            店铺
                        </el-button>
                        <el-button
                            v-if="row.role === 'MERCHANT'"
                            type="danger"
                            size="small"
                            link
                            @click="handleDeleteUser(row)"
                        >
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-empty
                v-if="!loading && userList.length === 0"
                :description="searchKeyword ? '未找到相关用户' : '暂无用户数据'"
            >
                <el-button v-if="searchKeyword" @click="clearSearch">清空搜索</el-button>
            </el-empty>
        </div>

        <!-- 删除确认对话框 -->
        <el-dialog
            v-model="showDeleteDialog"
            title="确认删除"
            width="500px"
            :close-on-click-modal="false"
        >
            <el-alert
                title="确定要删除以下用户吗？"
                type="warning"
                :closable="false"
                show-icon
                style="margin-bottom: 16px;"
            />

            <div class="merchant-to-delete" v-if="userToDelete">
                <div class="merchant-info">
                    <div class="merchant-title-delete">{{ userToDelete.userName }}</div>
                    <div class="merchant-details">
                        <p><strong>用户ID：</strong>{{ userToDelete.userId }}</p>
                        <p><strong>身份：</strong>{{ userToDelete.role === 'ADMIN' ? '管理员' : '商家' }}</p>
                    </div>
                </div>
            </div>

            <el-alert
                type="error"
                :closable="false"
                style="margin-top: 16px;"
            >
                <template #title>
                    <div><strong>注意：</strong>此操作不可撤销，删除后用户信息将无法恢复。</div>
                </template>
            </el-alert>

            <template #footer>
                <el-button @click="closeDeleteDialog">取消</el-button>
                <el-button type="danger" @click="confirmDeleteUser" :loading="submitting">
                    确认删除
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { useUserStore } from '@/pinia/userStores.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, User, ArrowDown, SwitchButton } from '@element-plus/icons-vue';
import router from '@/router';
import { processResult } from '@/axios/index.js';
import { userLogout, getUserList, deleteUser, getAvatarUrl } from '@/api/index.js';

export default {
    name: 'MerchantManagement',
    components: { Search, User, ArrowDown, SwitchButton },
    data() {
        return {
            // 当前用户信息
            currentUser: null,
            // 用户数据列表
            userList: [],
            // 搜索关键词
            searchKeyword: '',
            // 角色筛选
            roleFilter: '',
            // 加载状态
            loading: false,
            submitting: false,
            // 待删除用户
            userToDelete: null,
            // 删除对话框状态
            showDeleteDialog: false,
            // 头像时间戳（用于破缓存刷新头像）
            avatar_timestamp: Date.now()
        }
    },
    computed: {
        // 当前激活的标签页（跟随当前路由路径）
        activeTab: {
            get() {
                return this.$route.path;
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

        // 按角色筛选后的用户列表
        filteredUserList() {
            if (!this.roleFilter) return this.userList;
            return this.userList.filter(user => user.role === this.roleFilter);
        }
    },
    mounted() {
        this.loadUserInfo();
        this.handleSearch();
    },
    methods: {
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
                if (userStore.role !== 'ADMIN') {
                    ElMessage.warning('仅管理员可访问此页面');
                    router.push('/goods/manage');
                }
            } else {
                ElMessage.warning('请先登录');
                router.push('/login');
            }
        },

        // 跳转到商家店铺概览页
        goToStoreOverview(merchant_id) {
            router.push({ path: '/store/overview', query: { merchantId: merchant_id } });
        },

        /**
         * 标签页切换导航
         * @param {String} tab 目标路由路径
         */
        handleTabChange(tab) {
            if (tab !== '/merchants/manage') {
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

        /**
         * 搜索用户
         */
        async handleSearch() {
            this.loading = true;
            try {
                const response = await getUserList({ username: this.searchKeyword || '', pageIndex: 1, pageSize: 100 });
                const result = processResult(response.data, '获取用户列表失败');
                if (result.success) {
                    const res_data = result.data;
                    if (res_data && Array.isArray(res_data.items)) {
                        this.userList = res_data.items;
                    } else {
                        this.userList = [];
                    }
                }
            } catch (error) {
                console.error('获取用户列表失败:', error);
                ElMessage.error('获取用户列表失败，请检查网络连接');
            } finally {
                this.loading = false;
            }
        },

        // 清空搜索
        clearSearch() {
            this.searchKeyword = '';
            this.handleSearch();
        },

        /**
         * 处理删除用户
         * @param {Object} user 待删除的用户
         */
        handleDeleteUser(user) {
            this.userToDelete = user;
            this.showDeleteDialog = true;
        },

        /**
         * 确认删除用户
         */
        async confirmDeleteUser() {
            if (!this.userToDelete) return;

            this.submitting = true;
            try {
                const response = await deleteUser(this.userToDelete.userId);

                const result = processResult(response.data, '删除失败');
                if (result.success) {
                    ElMessage.success(result.msg || '用户删除成功');
                    this.closeDeleteDialog();
                    this.handleSearch();
                }
            } catch (error) {
                console.error('删除失败:', error);
                ElMessage.error('删除失败，请稍后重试');
            } finally {
                this.submitting = false;
            }
        },

        // 关闭删除对话框
        closeDeleteDialog() {
            this.showDeleteDialog = false;
            this.userToDelete = null;
        }
    }
}
</script>

<style scoped>
.merchant-management-container {
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
/* 禁用标签页蓝条滑动动画（页面切换时组件重建会导致动画从错误位置开始） */
.nav-tabs :deep(.el-tabs__active-bar) {
    transition: none;
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

.header-actions { display: flex; gap: 12px; flex-wrap: wrap; }

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

/* 搜索区域 */
.search-section {
    background: white;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.filter-options {
    display: flex;
    gap: 16px;
    margin-top: 16px;
    flex-wrap: wrap;
}

.filter-group {
    display: flex;
    align-items: center;
    gap: 8px;
}

.filter-group label {
    font-size: 14px;
    color: #606266;
    white-space: nowrap;
}

/* 用户列表区域 */
.merchant-list-section {
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

/* 删除对话框 */
.merchant-to-delete {
    background: #f7fafc;
    border-radius: 8px;
    padding: 20px;
}

.merchant-title-delete {
    font-size: 18px;
    font-weight: 600;
    color: #1a202c;
    margin-bottom: 12px;
}

.merchant-details p {
    margin: 8px 0;
    color: #4a5568;
    font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .merchant-management-container {
        padding: 12px;
    }

    .page-header {
        flex-direction: column;
        align-items: stretch;
    }
}
</style>
