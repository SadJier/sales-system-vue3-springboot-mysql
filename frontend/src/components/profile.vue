<template>
    <div class="profile-container">
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

        <!-- 个人信息卡片 -->
        <div class="profile-card">
            <div class="profile-header">
                <div class="avatar-wrapper">
                    <el-avatar :size="80" class="profile-avatar" :src="avatarUrl">
                        <el-icon :size="40"><User /></el-icon>
                    </el-avatar>
                    <el-upload
                        class="avatar-upload"
                        action=""
                        :http-request="handleAvatarUpload"
                        :show-file-list="false"
                        :before-upload="beforeAvatarUpload"
                        accept="image/*"
                    >
                        <div class="avatar-overlay">
                            <el-icon :size="20"><Camera /></el-icon>
                            <span>更换头像</span>
                        </div>
                    </el-upload>
                </div>
                <div class="profile-basic">
                    <h2>{{ currentUser?.username }}</h2>
                    <el-tag :type="currentUser?.role === 'ADMIN' ? 'danger' : ''" size="default">
                        {{ currentUser?.role === 'ADMIN' ? '管理员' : '商家' }}
                    </el-tag>
                </div>
            </div>

            <el-divider />

            <!-- 信息编辑表单 -->
            <el-form
                ref="profileFormRef"
                :model="form"
                :rules="formRules"
                label-width="100px"
                class="profile-form"
            >
                <el-form-item label="用户ID">
                    <el-input :model-value="currentUser?.userId" disabled />
                </el-form-item>

                <el-form-item label="账号">
                    <el-input :model-value="currentUser?.username" disabled />
                </el-form-item>

                <el-form-item label="身份">
                    <el-input :model-value="currentUser?.role === 'ADMIN' ? '管理员' : '商家'" disabled />
                </el-form-item>

                <el-form-item label="原密码" prop="oldPassword">
                    <el-input
                        v-model="form.oldPassword"
                        type="password"
                        placeholder="如需修改密码请输入原密码"
                        show-password
                        clearable
                    />
                </el-form-item>

                <el-form-item label="新密码" prop="newPassword">
                    <el-input
                        v-model="form.newPassword"
                        type="password"
                        placeholder="请输入新密码（至少6位）"
                        show-password
                        clearable
                    />
                </el-form-item>

                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input
                        v-model="form.confirmPassword"
                        type="password"
                        placeholder="请再次输入新密码"
                        show-password
                        clearable
                    />
                </el-form-item>

                <el-form-item label-width="0">
                    <el-button type="primary" @click="handleSaveProfile" :loading="submitting">
                        保存修改
                    </el-button>
                    <el-button @click="resetForm">重置</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
import { useUserStore } from '@/pinia/userStores.js';
import { userLogout, updateUserPassword, uploadUserAvatar, getAvatarUrl } from '@/api/index.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, ArrowDown, SwitchButton, Camera } from '@element-plus/icons-vue';
import router from '@/router';

export default {
    name: 'UserProfile',
    components: { User, ArrowDown, SwitchButton, Camera },
    data() {
        // 新密码自定义验证规则
        const validateNewPassword = (rule, value, callback) => {
            if (this.form.oldPassword && !value) {
                callback(new Error('请输入新密码'));
            } else if (value && value.length < 6) {
                callback(new Error('密码长度不能少于6位'));
            } else {
                if (this.form.confirmPassword) {
                    this.$refs.profileFormRef?.validateField('confirmPassword');
                }
                callback();
            }
        };
        // 确认密码自定义验证规则
        const validateConfirmPassword = (rule, value, callback) => {
            if (this.form.newPassword && !value) {
                callback(new Error('请输入确认密码'));
            } else if (value && value !== this.form.newPassword) {
                callback(new Error('两次输入的密码不一致'));
            } else {
                callback();
            }
        };

        return {
            // 当前用户信息
            currentUser: null,
            // 头像时间戳（用于破缓存刷新头像）
            avatar_timestamp: Date.now(),
            // 头像上传加载状态
            avatarUploading: false,
            // 表单数据
            form: {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
            },
            // 加载状态
            submitting: false,
            // 表单验证规则
            formRules: {
                oldPassword: [
                    { required: false }
                ],
                newPassword: [
                    { validator: validateNewPassword, trigger: 'blur' }
                ],
                confirmPassword: [
                    { validator: validateConfirmPassword, trigger: 'blur' }
                ]
            }
        };
    },
    computed: {
        // 当前激活的标签页
        activeTab: {
            get() {
                return '/profile';
            },
            set() {}
        },

        // 头像URL，通过API获取用户头像
        avatarUrl() {
            if (this.currentUser?.userId) {
                return getAvatarUrl(this.currentUser.userId) + `?t=${this.avatar_timestamp}`;
            }
            return '';
        }
    },
    mounted() {
        this.loadUserInfo();
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
            } else {
                ElMessage.warning('请先登录');
                router.push('/login');
            }
        },

        /**
         * 标签页切换导航
         * @param {String} tab 目标路由路径
         */
        handleTabChange(tab) {
            if (tab !== '/profile') {
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
         * 保存个人信息修改
         */
        async handleSaveProfile() {
            if (!this.form.oldPassword && !this.form.newPassword && !this.form.confirmPassword) {
                ElMessage.info('未做任何修改');
                return;
            }

            if (this.form.newPassword || this.form.confirmPassword) {
                if (!this.form.oldPassword) {
                    ElMessage.warning('修改密码需要输入原密码');
                    return;
                }
            }

            try {
                await this.$refs.profileFormRef.validate();
            } catch {
                return;
            }

            this.submitting = true;
            try {
                const response = await updateUserPassword({
                    username: this.currentUser.username,
                    oldPassword: this.form.oldPassword,
                    newPassword: this.form.newPassword
                });

                if (response.data.code === 1) {
                    ElMessage.success('密码修改成功，请重新登录');
                    const userStore = useUserStore();
                    userStore.logout();
                    router.push('/login');
                } else {
                    ElMessage.error(response.data.msg || '修改失败');
                }
            } catch (error) {
                console.error('修改失败:', error);
                ElMessage.error('修改失败，请稍后重试');
            } finally {
                this.submitting = false;
            }
        },

        // 重置表单
        resetForm() {
            this.form = {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
            };
            this.$refs.profileFormRef?.clearValidate();
        },

        /**
         * 头像上传前校验
         * @param {File} file 上传的文件
         */
        beforeAvatarUpload(file) {
            const is_image = file.type.startsWith('image/');
            const is_lt2m = file.size / 1024 / 1024 < 2;

            if (!is_image) {
                ElMessage.error('只能上传图片文件');
                return false;
            }
            if (!is_lt2m) {
                ElMessage.error('图片大小不能超过2MB');
                return false;
            }
            return true;
        },

        /**
         * 自定义头像上传
         * @param {Object} params 上传参数
         */
        async handleAvatarUpload(params) {
            this.avatarUploading = true;

            try {
                const response = await uploadUserAvatar({
                    user_id: this.currentUser.userId,
                    file: params.file
                });

                if (response.data.code === 1) {
                    // 更新时间戳以破缓存刷新头像
                    this.avatar_timestamp = Date.now();
                    ElMessage.success('头像更新成功');
                } else {
                    ElMessage.error(response.data.msg || '头像上传失败');
                }
            } catch (error) {
                console.error('头像上传失败:', error);
                ElMessage.error('头像上传失败，请稍后重试');
            } finally {
                this.avatarUploading = false;
            }
        }
    }
};
</script>

<style scoped>
.profile-container {
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

/* 标签栏导航 */
.nav-tabs {
    background: white;
    border-radius: 0 0 12px 12px;
    padding: 0 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
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

/* 个人信息卡片 */
.profile-card {
    background: white;
    border-radius: 12px;
    padding: 32px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    max-width: 600px;
    margin: 0 auto;
}

.profile-header {
    display: flex;
    align-items: center;
    gap: 24px;
}

.avatar-wrapper {
    position: relative;
    display: inline-block;
}

.avatar-wrapper:hover .avatar-overlay {
    opacity: 1;
}

.avatar-upload {
    position: absolute;
    top: 0;
    left: 0;
    width: 80px;
    height: 80px;
}

.avatar-upload :deep(.el-upload) {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    position: absolute;
    top: 0;
    left: 0;
}

.avatar-overlay {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.5);
    color: white;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
    cursor: pointer;
    font-size: 12px;
    gap: 4px;
}

.profile-avatar {
    background: linear-gradient(135deg, #409eff, #66b1ff);
    color: white;
}

.profile-basic h2 {
    margin: 0 0 8px 0;
    color: #303133;
    font-size: 24px;
}

.profile-form {
    margin-top: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .profile-container {
        padding: 12px;
    }

    .profile-card {
        padding: 20px;
    }

    .profile-header {
        flex-direction: column;
        text-align: center;
    }
}
</style>
