<template>
    <div class="product-detail-container" v-loading="loading">
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

        <!-- 返回按钮 -->
        <div class="back-bar">
            <el-button @click="goBack" text>
                <el-icon><ArrowLeft /></el-icon>
                返回商品列表
            </el-button>
        </div>

        <!-- 商品基本信息 -->
        <div class="detail-card" v-if="product">
            <div class="detail-header">
                <div class="detail-image">
                    <el-image
                        v-if="product.productId"
                        :src="getProductImageUrl(product.productId) + '?t=' + product_image_ts"
                        fit="cover"
                        style="width: 200px; height: 200px; border-radius: 12px;"
                    >
                        <template #error>
                            <div class="image-placeholder">暂无图片</div>
                        </template>
                    </el-image>
                </div>
                <div class="detail-info">
                    <h1 class="product-name">{{ product.name }}</h1>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="info-label">商品ID</span>
                            <span class="info-value">{{ product.productId }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">分类</span>
                            <span class="info-value">
                                <el-tag v-if="product.categoryId && getCategoryName(product.categoryId)" size="small">{{ getCategoryName(product.categoryId) }}</el-tag>
                                <el-tag v-else size="small" type="info">无分类</el-tag>
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">进价</span>
                            <span class="info-value">¥{{ (product.purchasePrice || 0).toFixed(2) }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">售价</span>
                            <span class="info-value price">¥{{ (product.salePrice || 0).toFixed(2) }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">库存</span>
                            <span class="info-value">
                                <el-tag :type="(product.stock || 0) > 0 ? '' : 'danger'" size="small">
                                    {{ product.stock || 0 }}
                                </el-tag>
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">创建时间</span>
                            <span class="info-value">{{ formatTime(product.createTime) }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 销售统计 -->
        <div class="stats-card" v-if="productStats">
            <h3>📊 销售统计</h3>
            <div class="stats-row">
                <div class="stats-item">
                    <div class="stats-number">{{ productStats.totalSales || 0 }}</div>
                    <div class="stats-desc">总销售量</div>
                </div>
                <div class="stats-item">
                    <div class="stats-number">¥{{ (productStats.totalRevenue || 0).toFixed(2) }}</div>
                    <div class="stats-desc">总销售额</div>
                </div>
            </div>
        </div>

        <!-- 近期销售记录 -->
        <div class="sales-card" v-if="productStats">
            <h3>🕐 近期销售记录</h3>
            <el-table :data="productStats.recentSales || []" stripe border style="width: 100%">
                <el-table-column prop="orderId" label="订单ID" width="100" />
                <el-table-column prop="quantity" label="数量" width="100" align="center" />
                <el-table-column label="时间">
                    <template #default="{ row }">
                        {{ formatTime(row.createTime) }}
                    </template>
                </el-table-column>
            </el-table>
            <el-empty v-if="!productStats.recentSales || productStats.recentSales.length === 0" description="暂无销售记录" />
        </div>
    </div>
</template>

<script>
import { useUserStore } from '@/pinia/userStores.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, ArrowDown, SwitchButton, ArrowLeft } from '@element-plus/icons-vue';
import router from '@/router';
import { processResult } from '@/axios/index.js';
import { userLogout, getProductDetail, getProductStats, getProductImageUrl, getAvatarUrl, getCategoryList } from '@/api/index.js';

export default {
    name: 'ProductDetail',
    components: { User, ArrowDown, SwitchButton, ArrowLeft },
    data() {
        return {
            // 当前用户信息
            currentUser: null,
            // 商品详情数据
            product: null,
            // 商品销售统计数据
            productStats: null,
            loading: false,
            // 头像时间戳
            avatar_timestamp: Date.now(),
            // 商品图片时间戳（用于破缓存刷新商品图片）
            product_image_ts: Date.now(),
            // 分类id到名称的映射
            categoryMap: {}
        };
    },
    computed: {
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
        this.loadProductDetail();
        this.loadProductStats();
    },
    methods: {
        getProductImageUrl,
        // 格式化时间
        formatTime(time) {
            if (!time) return '';
            return new Date(time).toLocaleString('zh-CN');
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
        },

        // 返回上一页
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

        // 获取分类名称
        getCategoryName(categoryId) {
            return this.categoryMap[categoryId] || '';
        },

        // 加载分类列表
        async loadCategories() {
            try {
                const response = await getCategoryList();
                const result = processResult(response.data, '获取分类列表失败');
                if (result.success) {
                    const res_data = result.data;
                    if (res_data && Array.isArray(res_data.categories)) {
                        this.categoryMap = res_data.categories.reduce((map, cat) => {
                            map[cat.categoryId] = cat.name;
                            return map;
                        }, {});
                    }
                } else {
                    ElMessage.error(result.msg || '获取分类列表失败');
                }
            } catch (error) {
                console.error('获取分类列表失败:', error);
            }
        },

        // 加载商品详情
        async loadProductDetail() {
            const product_id = this.$route.params.productId;
            if (!product_id) {
                ElMessage.error('商品ID缺失');
                router.back();
                return;
            }
            this.loading = true;
            try {
                const response = await getProductDetail(product_id);
                const result = processResult(response.data, '获取商品详情失败');
                if (result.success) {
                    this.product = result.data;
                } else {
                    ElMessage.error(result.msg || '获取商品详情失败');
                }
            } catch (error) {
                console.error('获取商品详情失败:', error);
                ElMessage.error('获取商品详情失败，请检查网络连接');
            } finally {
                this.loading = false;
            }
        },

        // 加载商品销售统计
        async loadProductStats() {
            const product_id = this.$route.params.productId;
            if (!product_id) return;
            try {
                const response = await getProductStats(product_id);
                const result = processResult(response.data, '获取商品统计失败');
                if (result.success) {
                    this.productStats = result.data;
                } else {
                    ElMessage.error(result.msg || '获取商品统计失败');
                }
            } catch (error) {
                console.error('获取商品统计失败:', error);
            }
        }
    }
};
</script>

<style scoped>
.product-detail-container {
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
.back-bar { margin: 16px 0; }
.detail-card { background: white; border-radius: 12px; padding: 32px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); margin-bottom: 24px; }
.detail-header { display: flex; gap: 32px; flex-wrap: wrap; }
.image-placeholder { width: 200px; height: 200px; display: flex; align-items: center; justify-content: center; background: #f5f7fa; color: #909399; border-radius: 12px; font-size: 14px; }
.detail-info { flex: 1; min-width: 300px; }
.product-name { margin: 0 0 20px 0; color: #1a202c; font-size: 28px; font-weight: 700; }
.info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; }
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-label { font-size: 13px; color: #909399; }
.info-value { font-size: 16px; color: #303133; font-weight: 500; }
.info-value.price { color: #f56c6c; }
.stats-card { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); margin-bottom: 24px; }
.stats-card h3 { margin: 0 0 20px 0; color: #2d3748; font-size: 20px; font-weight: 600; }
.stats-row { display: flex; gap: 40px; flex-wrap: wrap; }
.stats-item { text-align: center; min-width: 150px; }
.stats-number { font-size: 32px; font-weight: 700; color: #1a202c; }
.stats-desc { font-size: 14px; color: #718096; margin-top: 4px; }
.sales-card { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.sales-card h3 { margin: 0 0 20px 0; color: #2d3748; font-size: 20px; font-weight: 600; }
</style>
