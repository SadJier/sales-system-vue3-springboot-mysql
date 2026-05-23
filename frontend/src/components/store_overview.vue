<template>
    <div class="store-overview-container">
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
            <el-tab-pane v-if="currentUser?.role === 'MERCHANT'" label="店铺信息" name="/store/overview" />
            <el-tab-pane v-if="currentUser?.role === 'ADMIN'" label="分类管理" name="/categories/manage" />
            <el-tab-pane v-if="currentUser?.role === 'ADMIN'" label="用户管理" name="/merchants/manage" />
            <el-tab-pane label="个人信息" name="/profile" />
        </el-tabs>

        <!-- 页面标题 -->
        <div class="page-header">
            <div class="header-content">
                <h1 class="page-title">🏪 店铺信息</h1>
                <p class="page-subtitle">{{ storeTitle }}</p>
            </div>
            <!-- 管理员查看商家店铺时显示返回按钮 -->
            <div class="header-actions">
                <el-button v-if="view_merchant_id" @click="goBack">返回用户管理</el-button>
                <el-button @click="refreshAll" :loading="loading">刷新</el-button>
            </div>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-cards" v-loading="loading">
            <div class="stat-card">
                <div class="stat-icon total">📋</div>
                <div class="stat-content">
                    <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
                    <div class="stat-label">总订单数</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon unpaid">💳</div>
                <div class="stat-content">
                    <div class="stat-value">{{ stats.unpaidOrders || 0 }}</div>
                    <div class="stat-label">未支付</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon paid">🧾</div>
                <div class="stat-content">
                    <div class="stat-value">{{ stats.paidOrders || 0 }}</div>
                    <div class="stat-label">已支付</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon shipped">🚚</div>
                <div class="stat-content">
                    <div class="stat-value">{{ stats.shippedOrders || 0 }}</div>
                    <div class="stat-label">已发货</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon completed">✅</div>
                <div class="stat-content">
                    <div class="stat-value">{{ stats.completedOrders || 0 }}</div>
                    <div class="stat-label">已完成</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon cancelled">❌</div>
                <div class="stat-content">
                    <div class="stat-value">{{ stats.cancelledOrders || 0 }}</div>
                    <div class="stat-label">已取消</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon revenue">💰</div>
                <div class="stat-content">
                    <div class="stat-value">¥{{ (stats.totalRevenue || 0).toFixed(2) }}</div>
                    <div class="stat-label">总收入</div>
                </div>
            </div>
        </div>

        <!-- 商品销售额占比 -->
        <div class="sales-section" v-loading="loading">
            <h3>商品销售额占比（仅已完成订单）</h3>
            <div class="chart-container" v-if="productRevenueList.length > 0">
                <div class="pie-chart">
                    <div class="pie-visual">
                        <div class="pie" :style="pieStyle"></div>
                    </div>
                    <div class="pie-legend">
                        <div class="legend-item" v-for="(item, index) in productRevenueList" :key="item.productName">
                            <span class="legend-color" :style="{ background: pieColors[index % pieColors.length] }"></span>
                            <span class="legend-name">{{ item.productName }}</span>
                            <span class="legend-value">¥{{ item.revenue.toFixed(2) }}</span>
                            <span class="legend-percent">{{ calcPercentage(item.revenue) }}%</span>
                        </div>
                    </div>
                </div>
            </div>
            <el-table :data="productRevenueList" stripe border style="width: 100%; margin-top: 16px;">
                <el-table-column prop="productName" label="商品名称" />
                <el-table-column prop="quantity" label="销售数量" width="120" align="center" />
                <el-table-column label="销售额" width="140" align="right">
                    <template #default="{ row }">
                        ¥{{ row.revenue.toFixed(2) }}
                    </template>
                </el-table-column>
                <el-table-column label="占比" width="200">
                    <template #default="{ row }">
                        <el-progress
                            :percentage="calcPercentage(row.revenue)"
                            :stroke-width="14"
                            :format="() => calcPercentage(row.revenue) + '%'"
                        />
                    </template>
                </el-table-column>
            </el-table>
            <el-empty v-if="!loading && productRevenueList.length === 0" description="暂无销售数据" />
        </div>
    </div>
</template>

<script>
import { useUserStore } from '@/pinia/userStores.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, ArrowDown, SwitchButton } from '@element-plus/icons-vue';
import router from '@/router';
import { processResult } from '@/axios/index.js';
import { userLogout, getStoreStats, getAvatarUrl } from '@/api/index.js';

export default {
    name: 'StoreOverview',
    components: { User, ArrowDown, SwitchButton },
    // 饼状图配色
    PIE_COLORS: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#9b59b6', '#1abc9c', '#e74c3c'],
    data() {
        return {
            // 当前用户信息
            currentUser: null,
            // 查看的商家ID（管理员查看指定商家时使用）
            view_merchant_id: null,
            // 店铺统计数据
            stats: {},
            // 商品销售占比列表（来自后端，按商家过滤）
            productRevenueList: [],
            loading: false,
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
        },
        // 店铺标题
        storeTitle() {
            if (this.view_merchant_id) return '查看商家店铺数据';
            return '查看我的店铺数据';
        },
        // 有效销售额总计（仅已支付+已完成）
        validTotalRevenue() {
            return this.productRevenueList.reduce((sum, item) => sum + (item.revenue || 0), 0);
        },
        // 饼状图配色列表（供模板使用）
        pieColors() {
            return this.$options.PIE_COLORS;
        },
        // 饼状图CSS conic-gradient样式
        pieStyle() {
            if (!this.productRevenueList.length || !this.validTotalRevenue) {
                return { background: '#f5f7fa' };
            }
            let current_deg = 0;
            const stops = [];
            for (let i = 0; i < this.productRevenueList.length; i++) {
                const item = this.productRevenueList[i];
                const percent = item.revenue / this.validTotalRevenue;
                const start_deg = current_deg;
                const end_deg = current_deg + percent * 360;
                const color = this.$options.PIE_COLORS[i % this.$options.PIE_COLORS.length];
                stops.push(`${color} ${start_deg}deg ${end_deg}deg`);
                current_deg = end_deg;
            }
            return { background: `conic-gradient(${stops.join(', ')})` };
        }
    },
    mounted() {
        this.loadUserInfo();
        this.loadStats();
    },
    methods: {
        // 加载店铺统计
        async loadStats() {
            this.loading = true;
            try {
                // 商家查询也需上传商家ID
                const merchant_id = this.view_merchant_id || this.currentUser?.userId;
                const response = await getStoreStats(merchant_id);
                const result = processResult(response.data, '获取店铺统计失败');
                if (result.success) {
                    this.stats = result.data || {};
                    //从后端统计中提取商品销售占比（已按商家过滤）
                    this.productRevenueList = (result.data && Array.isArray(result.data.productSales))
                        ? result.data.productSales.sort((a, b) => (b.revenue || 0) - (a.revenue || 0))
                        : [];
                } else {
                    ElMessage.error(result.msg || '获取店铺统计失败');
                }
            } catch (error) {
                console.error('获取店铺统计失败:', error);
                ElMessage.error('获取店铺统计失败，请检查网络连接');
            } finally {
                this.loading = false;
            }
        },

        // 计算销售额占比百分比
        calcPercentage(revenue) {
            if (!this.validTotalRevenue) return 0;
            return Math.round((revenue / this.validTotalRevenue) * 100);
        },

        // 刷新全部数据
        refreshAll() {
            this.loadStats();
        },

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
            // 从路由参数获取商家ID（管理员查看指定商家）
            this.view_merchant_id = this.$route.query.merchantId ? Number(this.$route.query.merchantId) : null;
        },

        handleTabChange(tab) {
            if (tab !== '/store/overview') {
                router.push(tab);
            }
        },

        // 返回上一页（管理员从用户管理进入时返回，保留搜索条件）
        goBack() {
            router.back();
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

    }
};
</script>

<style scoped>
.store-overview-container {
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
.page-header { margin-bottom: 24px; display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px; }
.header-actions { display: flex; gap: 12px; flex-wrap: wrap; }
.page-title { margin: 0 0 8px 0; color: #1a202c; font-size: 28px; font-weight: 700; }
.page-subtitle { margin: 0; color: #718096; font-size: 16px; }
.stats-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 24px; }
.stat-card { background: white; border-radius: 12px; padding: 24px; display: flex; align-items: center; gap: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); transition: transform 0.3s ease; }
.stat-card:hover { transform: translateY(-4px); box-shadow: 0 4px 20px rgba(0,0,0,0.12); }
.stat-icon { width: 60px; height: 60px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 28px; }
.stat-icon.total { background: linear-gradient(135deg, #667eea, #764ba2); color: white; }
.stat-icon.unpaid { background: linear-gradient(135deg, #f59e0b, #fbbf24); color: white; }
.stat-icon.paid { background: linear-gradient(135deg, #3b82f6, #60a5fa); color: white; }
.stat-icon.shipped { background: linear-gradient(135deg, #8b5cf6, #a78bfa); color: white; }
.stat-icon.completed { background: linear-gradient(135deg, #10b981, #34d399); color: white; }
.stat-icon.cancelled { background: linear-gradient(135deg, #6b7280, #9ca3af); color: white; }
.stat-icon.revenue { background: linear-gradient(135deg, #ef4444, #f87171); color: white; }
.stat-content { flex: 1; }
.stat-value { font-size: 28px; font-weight: 700; color: #1a202c; margin-bottom: 4px; }
.stat-label { font-size: 14px; color: #718096; }
.sales-section { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
.sales-section h3 { margin: 0 0 20px 0; color: #2d3748; font-size: 20px; font-weight: 600; }
.chart-container { margin-bottom: 16px; }
.pie-chart { display: flex; gap: 32px; align-items: center; flex-wrap: wrap; }
.pie-visual { flex-shrink: 0; }
.pie { width: 200px; height: 200px; border-radius: 50%; }
.pie-legend { flex: 1; min-width: 200px; }
.legend-item { display: flex; align-items: center; gap: 8px; padding: 6px 0; }
.legend-color { width: 14px; height: 14px; border-radius: 3px; flex-shrink: 0; }
.legend-name { flex: 1; color: #303133; font-size: 14px; }
.legend-value { color: #606266; font-size: 13px; font-weight: 500; }
.legend-percent { color: #909399; font-size: 13px; min-width: 40px; text-align: right; }
</style>
