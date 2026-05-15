<template>
    <div class="login-container">
        <!-- 登录卡片 -->
        <div class="login-card">
            <h2 class="login-title">商品销售管理系统</h2>

            <!-- 登录/注册切换 -->
            <div class="tab-switch">
                <div
                    class="tab-item"
                    :class="{ active: !isRegister }"
                    @click="switchTab(false)"
                >
                    登录
                </div>
                <div
                    class="tab-item"
                    :class="{ active: isRegister }"
                    @click="switchTab(true)"
                >
                    注册
                </div>
            </div>

            <!-- 角色选择（Element Plus 单选框） -->
            <el-radio-group v-model="form.role" class="role-select" @change="handleRoleChange">
                <el-radio
                    v-for="role in roleList"
                    :key="role.value"
                    :label="role.value"
                    class="role-item"
                >
                    {{ role.label }}
                </el-radio>
            </el-radio-group>

            <!-- 登录/注册表单（Element Plus 表单） -->
            <el-form
                ref="loginFormRef"
                :model="form"
                :rules="formRules"
                label-width="auto"
                class="login-form"
                @submit.prevent="isRegister ? handleRegister() : handleLogin()"
            >
                <!-- 账号输入 -->
                <el-form-item
                    :label="form.role === 'ADMIN' ? '管理员账号' : '商家账号'"
                    prop="username"
                >
                    <el-input
                        v-model="form.username"
                        :placeholder="`请输入${form.role === 'ADMIN' ? '管理员账号' : '商家账号'}`"
                        clearable
                        size="default"
                    />
                </el-form-item>

                <!-- 密码输入 -->
                <el-form-item label="密码" prop="password">
                    <el-input
                        v-model="form.password"
                        type="password"
                        placeholder="请输入密码"
                        show-password
                        clearable
                        size="default"
                    />
                </el-form-item>

                <!-- 确认密码（仅注册显示） -->
                <el-form-item label="确认密码" prop="confirmPassword" v-if="isRegister">
                    <el-input
                        v-model="form.confirmPassword"
                        type="password"
                        placeholder="请再次输入密码"
                        show-password
                        clearable
                        size="default"
                    />
                </el-form-item>

                <!-- 提交按钮（Element Plus 按钮） -->
                <el-form-item label-width="0">
                    <el-button
                        type="primary"
                        class="login-btn"
                        :loading="loading"
                        native-type="submit"
                    >
                        {{ isRegister ? (loading ? '注册中...' : '注册') : (loading ? '登录中...' : '登录') }}
                    </el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
import router from '@/router';
import { useUserStore } from '@/pinia/userStores.js';
import { userLogin, userRegister } from '@/api/index.js';
import { ElMessage } from 'element-plus';

export default {
    name: 'LoginPage',
    data() {
        // 账号自定义验证规则
        const validateName = (rule, value, callback) => {
            if (!value) {
                callback(new Error(`请输入${this.form.role === 'ADMIN' ? '管理员账号' : '商家账号'}`));
            } else {
                callback();
            }
        };
        // 确认密码自定义验证规则
        const validateConfirmPassword = (rule, value, callback) => {
            if (!value) {
                callback(new Error('请输入确认密码'));
            } else if (value !== this.form.password) {
                callback(new Error('两次输入的密码不一致'));
            } else {
                callback();
            }
        };

        return {
            // 是否为注册状态
            isRegister: false,
            // 表单数据
            form: {
                role: 'MERCHANT', // 默认角色：商家(MERCHANT)/管理员(ADMIN)
                username: '', // 账号
                password: '', // 密码
                confirmPassword: '' // 确认密码
            },
            // 角色列表
            roleList: [
                { label: '商家', value: 'MERCHANT' },
                { label: '管理员', value: 'ADMIN' }
            ],
            // 加载状态
            loading: false,
            // 表单验证规则（单一静态对象，避免切换时触发重新校验）
            formRules: {
                username: [
                    { required: true, validator: validateName, trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
                ],
                confirmPassword: [
                    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        /**
         * 切换登录/注册标签
         * @param {Boolean} isRegister 是否为注册
         */
        switchTab(isRegister) {
            this.isRegister = isRegister;
            this.form.username = '';
            this.form.password = '';
            this.form.confirmPassword = '';
            this.$nextTick(() => {
                this.$refs.loginFormRef?.clearValidate();
            });
        },

        /**
         * 切换角色时清除错误提示和输入内容
         */
        handleRoleChange() {
            this.form.username = '';
            this.form.password = '';
            this.form.confirmPassword = '';
            this.$nextTick(() => {
                this.$refs.loginFormRef?.clearValidate();
            });
        },

        /**
         * 处理登录逻辑
         */
        async handleLogin() {
            try {
                await this.$refs.loginFormRef.validate();
            } catch {
                return;
            }
            this.loading = true;
            try {
                const res = await userLogin({
                    role: this.form.role,
                    username: this.form.username,
                    password: this.form.password
                });

                if (res.data.code === 1) {
                    const { userId, role, username, token } = res.data.data;
                    if (token) {
                        localStorage.setItem('token', token);
                        const userStore = useUserStore();
                        userStore.login({ userId, role, username, token });
                    }

                    ElMessage.success('登录成功！');
                    router.push('/goods/manage');
                } else {
                    ElMessage.error(res.data.msg || '账号或密码错误');
                }
            } catch (err) {
                console.error('登录请求失败:', err);
                ElMessage.error('网络连接异常，请稍后重试');
            } finally {
                this.loading = false;
            }
        },

        /**
         * 处理注册逻辑
         */
        async handleRegister() {
            try {
                await this.$refs.loginFormRef.validate();
            } catch {
                return;
            }
            this.loading = true;
            try {
                const res = await userRegister({
                    role: this.form.role,
                    username: this.form.username,
                    password: this.form.password
                });

                if (res.data.code === 1) {
                    ElMessage.success(res.data.data || '注册成功，请登录');
                    this.form.password = '';
                    this.form.confirmPassword = '';
                    setTimeout(() => this.switchTab(false), 3000);
                } else {
                    ElMessage.error(res.data.msg || '注册失败，该账号已存在');
                }
            } catch (err) {
                console.error('注册请求失败:', err);
                ElMessage.error('网络连接异常，请稍后重试');
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>

<style scoped>
* {
    margin: 0;
    padding: 0;
}

/* 登录页面容器 */
.login-container {
    width: 100vw;
    height: 100vh;
    background: linear-gradient(135deg, #409eff, #66b1ff);
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: "Microsoft Yahei", sans-serif;
}

/* 登录卡片 */
.login-card {
    width: 400px;
    padding: 40px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

/* 系统标题 */
.login-title {
    text-align: center;
    color: #333;
    margin: 0 0 20px 0;
    font-size: 24px;
    font-weight: 600;
}

/* 标签切换 */
.tab-switch {
    display: flex;
    margin-bottom: 20px;
    border-radius: 8px;
    background: #f5f7fa;
}

.tab-item {
    flex: 1;
    text-align: center;
    padding: 10px 0;
    cursor: pointer;
    font-size: 16px;
    border-radius: 8px;
    transition: all 0.3s;
}

.tab-item.active {
    background: #409eff;
    color: #fff;
}

/* 角色选择 */
.role-select {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
}

.role-item {
    margin: 0 15px;
    cursor: pointer;
    color: #666;
    font-size: 16px;
}

/* 表单容器 */
.login-form {
    width: 100%;
}

/* 防止表单标签换行 */
.login-form :deep(.el-form-item__label) {
    white-space: nowrap;
}

/* 登录按钮 */
.login-btn {
    width: 100%;
}

/* 响应式适配 */
@media (max-width: 450px) {
    .login-card {
        width: 90%;
        padding: 30px 20px;
    }
}
</style>
