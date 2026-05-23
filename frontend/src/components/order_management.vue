<template>
    <div class="order-management-container">
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
                <h1 class="page-title">📋 订单管理</h1>
                <p class="page-subtitle">管理订单信息，支持新增、修改状态和备注、删除操作</p>
            </div>
            <div class="header-actions">
                <el-button v-if="currentUser?.role === 'MERCHANT'" type="primary" @click="handleAddOrder">新增订单</el-button>
                <el-button @click="loadOrders">刷新</el-button>
            </div>
        </div>

        <!-- 订单列表 -->
        <div class="order-list-section">
            <el-table :data="orderList" stripe border v-loading="loading" style="width: 100%">
                <el-table-column prop="orderId" label="订单ID" width="80" />
                <el-table-column v-if="currentUser?.role === 'ADMIN'" prop="merchantName" label="商家" width="120" />
                <el-table-column prop="buyerName" label="买家姓名" width="120" />
                <el-table-column prop="buyerPhone" label="买家电话" width="130" />
                <el-table-column prop="productName" label="商品名称" width="150" />
                <el-table-column prop="quantity" label="数量" width="70" align="center" />
                <el-table-column label="总价" width="100" align="right">
                    <template #default="{ row }">
                        ¥{{ row.totalPrice?.toFixed(2) }}
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="statusTagType(row.status)" size="small">
                            {{ statusLabel(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
                <el-table-column label="创建时间" width="170">
                    <template #default="{ row }">
                        {{ formatTime(row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="160" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" link @click="handleEditOrder(row)">编辑</el-button>
                        <el-button type="danger" size="small" link @click="handleDeleteOrder(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-wrapper" v-if="totalOrders > pageSize">
                <el-pagination
                    v-model:current-page="currentPage"
                    :page-size="pageSize"
                    :total="totalOrders"
                    layout="prev, pager, next"
                    @current-change="loadOrders"
                />
            </div>
        </div>

        <!-- 新增订单对话框 -->
        <el-dialog v-model="showAddDialog" title="新增订单" width="500px" :close-on-click-modal="false">
            <el-form :model="addForm" label-width="100px" ref="addFormRef" @submit.prevent="submitAddOrder">
                <el-form-item label="买家姓名" required>
                    <el-input v-model="addForm.buyerName" placeholder="请输入买家姓名" @keyup.enter="submitAddOrder" />
                </el-form-item>
                <el-form-item label="买家电话" required>
                    <el-input v-model="addForm.buyerPhone" placeholder="请输入买家电话" @keyup.enter="submitAddOrder" />
                </el-form-item>
                <el-form-item label="商品" required>
                    <el-select v-model="addForm.productId" placeholder="请选择商品" style="width: 100%;" filterable>
                        <el-option
                            v-for="product in productList"
                            :key="product.productId"
                            :label="product.name"
                            :value="product.productId"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="数量" required>
                    <el-input-number v-model="addForm.quantity" :min="1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="addForm.remark" type="textarea" placeholder="请输入备注（可选）" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showAddDialog = false">取消</el-button>
                <el-button type="primary" @click="submitAddOrder" :loading="submitting">确认</el-button>
            </template>
        </el-dialog>

        <!-- 编辑订单对话框 -->
        <el-dialog v-model="showEditDialog" title="编辑订单" width="500px" :close-on-click-modal="false">
            <el-form :model="editForm" label-width="100px" @submit.prevent="submitEditOrder">
                <el-form-item label="订单状态">
                    <el-select v-model="editForm.status" style="width: 100%;">
                        <el-option
                            v-for="status in statusOptions"
                            :key="status"
                            :label="statusLabel(status)"
                            :value="status"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="editForm.remark" type="textarea" placeholder="请输入备注" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showEditDialog = false">取消</el-button>
                <el-button type="primary" @click="submitEditOrder" :loading="submitting">确认</el-button>
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
import { userLogout, getOrderList, createOrder, updateOrder, deleteOrder, getProductList, getAvatarUrl, getOrderTransitions } from '@/api/index.js';

// 订单状态标签映射
const STATUS_MAP = {
    UNPAID: { label: '未支付', type: 'warning' },
    PAID: { label: '已支付', type: '' },
    SHIPPED: { label: '已发货', type: '' },
    COMPLETED: { label: '已完成', type: 'success' },
    CANCELLED: { label: '已取消', type: 'info' }
};

export default {
    name: 'OrderManagement',
    components: { User, ArrowDown, SwitchButton },
    data() {
        return {
            // 当前用户信息
            currentUser: null,
            // 订单列表
            orderList: [],
            // 商品列表（新增订单时选择）
            productList: [],
            // 分页
            currentPage: 1,
            // 订单总数
            totalOrders: 0,
            pageSize: 10,
            // 加载状态
            loading: false,
            submitting: false,
            // 新增对话框
            showAddDialog: false,
            addForm: {
                buyerName: '',
                buyerPhone: '',
                productId: null,
                quantity: 1,
                remark: ''
            },
            // 编辑对话框
            showEditDialog: false,
            editForm: {
                orderId: null,
                status: '',
                remark: ''
            },
            // 订单可转换的状态列表
            allowedTransitions: [],
            // 包含当前状态在内的完整选项列表
            statusOptions: [],
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
        // 头像URL
        avatarUrl() {
            if (this.currentUser?.userId) {
                return getAvatarUrl(this.currentUser.userId) + `?t=${this.avatar_timestamp}`;
            }
            return '';
        }
    },
    mounted() {
        this.loadUserInfo();
        this.loadOrders();
        this.loadProductOptions();
    },
    methods: {
        // 状态标签文字
        statusLabel(status) {
            return STATUS_MAP[status]?.label || status;
        },
        // 状态标签类型
        statusTagType(status) {
            return STATUS_MAP[status]?.type || '';
        },
        // 格式化时间
        formatTime(time) {
            if (!time) return '';
            return new Date(time).toLocaleString('zh-CN');
        },

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

        // 标签页切换导航
        handleTabChange(tab) {
            if (tab !== '/orders/manage') {
                router.push(tab);
            }
        },

        // 处理用户下拉菜单命令
        handleUserCommand(command) {
            if (command === 'logout') {
                ElMessageBox.confirm('确定要退出登录吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
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

        // 加载订单列表
        async loadOrders() {
            this.loading = true;
            try {
                const response = await getOrderList({ pageIndex: this.currentPage, pageSize: this.pageSize });
                const result = processResult(response.data, '获取订单列表失败');
                if (result.success) {
                    const res_data = result.data;
                    this.orderList = res_data && Array.isArray(res_data.items) ? res_data.items : [];
                    this.totalOrders = res_data?.total || 0;
                } else {
                    ElMessage.error(result.msg || '获取订单列表失败');
                }
            } catch (error) {
                console.error('获取订单列表失败:', error);
                ElMessage.error('获取订单列表失败，请检查网络连接');
            } finally {
                this.loading = false;
            }
        },

        // 加载商品选项（新增订单时使用）
        async loadProductOptions() {
            try {
                const response = await getProductList({ productName: '', categoryId: '', pageIndex: 1, pageSize: 1000 });
                const result = processResult(response.data, '获取商品列表失败');
                if (result.success) {
                    const res_data = result.data;
                    this.productList = res_data && Array.isArray(res_data.items) ? res_data.items : [];
                } else {
                    ElMessage.error(result.msg || '获取商品列表失败');
                }
            } catch (error) {
                console.error('获取商品列表失败:', error);
            }
        },

        // 新增订单
        handleAddOrder() {
            this.addForm = { buyerName: '', buyerPhone: '', productId: null, quantity: 1, remark: '' };
            this.showAddDialog = true;
        },

        // 提交新增订单
        async submitAddOrder() {
            if (!this.addForm.buyerName || !this.addForm.buyerPhone || !this.addForm.productId) {
                ElMessage.warning('请填写必要信息');
                return;
            }
            // 电话号码格式验证
            const phone_reg = /^1[3-9]\d{9}$/;
            if (!phone_reg.test(this.addForm.buyerPhone)) {
                ElMessage.warning('请输入正确的手机号码');
                return;
            }
            this.submitting = true;
            try {
                const response = await createOrder(this.addForm);
                const result = processResult(response.data, '新增订单失败');
                if (result.success) {
                    ElMessage.success(result.msg || '新增订单成功');
                    this.showAddDialog = false;
                    this.loadOrders();
                } else {
                    ElMessage.error(result.msg || '新增订单失败');
                }
            } catch (error) {
                console.error('新增订单失败:', error);
                ElMessage.error('新增订单失败，请稍后重试');
            } finally {
                this.submitting = false;
            }
        },

        // 编辑订单
        async handleEditOrder(order) {
            this.editForm = { orderId: order.orderId, status: order.status, remark: order.remark || '' };
            this.allowedTransitions = [];
            this.statusOptions = [];
            this.showEditDialog = true;
            try {
                const response = await getOrderTransitions(order.orderId);
                const result = processResult(response.data, '获取可转换状态失败');
                if (result.success) {
                    this.allowedTransitions = result.data || [];
                    //构建选项列表：当前状态 + 可转换状态（去重）
                    this.statusOptions = [order.status, ...this.allowedTransitions.filter(s => s !== order.status)];
                } else {
                    ElMessage.error(result.msg || '获取可转换状态失败');
                }
            } catch (error) {
                console.error('获取可转换状态失败:', error);
            }
        },

        // 提交编辑订单
        async submitEditOrder() {
            this.submitting = true;
            try {
                const response = await updateOrder(this.editForm);
                const result = processResult(response.data, '修改订单失败');
                if (result.success) {
                    ElMessage.success(result.msg || '修改订单成功');
                    this.showEditDialog = false;
                    this.loadOrders();
                } else {
                    ElMessage.error(result.msg || '修改订单失败');
                }
            } catch (error) {
                console.error('修改订单失败:', error);
                ElMessage.error('修改订单失败，请稍后重试');
            } finally {
                this.submitting = false;
            }
        },

        // 删除订单
        handleDeleteOrder(order) {
            ElMessageBox.confirm(`确定要删除订单 #${order.orderId} 吗？`, '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(async () => {
                try {
                    const response = await deleteOrder(order.orderId);
                    const result = processResult(response.data, '删除订单失败');
                    if (result.success) {
                        ElMessage.success(result.msg || '删除订单成功');
                        this.loadOrders();
                    } else {
                        ElMessage.error(result.msg || '删除订单失败');
                    }
                } catch (error) {
                    console.error('删除订单失败:', error);
                    ElMessage.error('删除订单失败，请稍后重试');
                }
            }).catch(() => {});
        }
    }
};
</script>

<style scoped>
.order-management-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Microsoft YaHei', sans-serif;
    background-color: #f8fafc;
    min-height: 100vh;
}
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
.nav-left { display: flex; align-items: center; }
.nav-title { margin: 0; color: #409eff; font-size: 20px; font-weight: 600; }
.nav-right { display: flex; align-items: center; }
.user-info-trigger { display: flex; align-items: center; gap: 10px; cursor: pointer; padding: 4px 12px; border-radius: 8px; transition: background 0.3s; }
.user-info-trigger:hover { background: #f5f7fa; }
.user-avatar { background: linear-gradient(135deg, #409eff, #66b1ff); color: white; }
.user-name { font-size: 14px; color: #303133; font-weight: 500; }
.arrow-icon { font-size: 12px; color: #909399; }
.nav-tabs { background: white; border-radius: 0 0 12px 12px; padding: 0 24px; margin-bottom: 24px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08); }
.nav-tabs :deep(.el-tabs__active-bar) { transition: none; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; flex-wrap: wrap; gap: 16px; }
.header-content { flex: 1; min-width: 300px; }
.page-title { margin: 0 0 8px 0; color: #1a202c; font-size: 28px; font-weight: 700; }
.page-subtitle { margin: 0; color: #718096; font-size: 16px; }
.header-actions { display: flex; gap: 12px; flex-wrap: wrap; }
.order-list-section { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08); }
.pagination-wrapper { display: flex; justify-content: center; margin-top: 24px; padding-top: 24px; border-top: 1px solid #e2e8f0; }
</style>
