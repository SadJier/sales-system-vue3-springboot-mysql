import axios from 'axios';
import { ElMessageBox } from 'element-plus';
import { refreshToken as refreshTokenApi } from '@/api/index.js';

let config = {
    baseURL:"",//Nginx反向代理后使用相对路径
    timeout:60000,
    withCredentials:true
}

const axiosInstance = axios.create(config)

// 操作成功
const CODE_SUCCESS = 200;
// Token过期或无效
const CODE_TOKEN_INVALID = 1001;
// Token缺失
const CODE_TOKEN_MISSING = 1002;
// 防止多次触发登出逻辑的标记
let is_logging_out = false;
// 是否正在刷新令牌
let is_refreshing = false;
// 等待令牌刷新的请求队列
let refresh_subscribers = [];

// 将等待中的请求加入队列
function subscribeTokenRefresh(callback) {
    refresh_subscribers.push(callback);
}

// 令牌刷新成功后，执行队列中的请求
function onTokenRefreshed(new_token) {
    refresh_subscribers.forEach(callback => callback(new_token));
    refresh_subscribers = [];
}

// 强制登出处理（refreshToken也过期时调用）
function handleForceLogout() {
    if (is_logging_out) return;
    is_logging_out = true;
    ElMessageBox.alert(
        '登录已过期，请重新登录',
        '登录过期',
        {
            confirmButtonText: '确定',
            type: 'warning',
            showClose: false,
            closeOnClickModal: false,
            closeOnPressEscape: false
        }
    ).then(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        is_logging_out = false;
        window.location.href = '/login';
    });
}

// 检查是否为Token相关状态码
function isTokenCode(code) {
    return code === CODE_TOKEN_INVALID || code === CODE_TOKEN_MISSING;
}

// 处理Result响应，按业务状态码分类处理，返回结构化结果
function processResult(res_data, fallback_msg) {
    if (!res_data) {
        return { success: false, data: null, msg: fallback_msg || '操作失败' };
    }

    const code = res_data.code;

    if (code === CODE_SUCCESS) {
        return { success: true, data: res_data.data, msg: res_data.msg };
    }

    // Token相关：由拦截器处理，此处仅返回失败
    if (isTokenCode(code)) {
        return { success: false, data: null, msg: res_data.msg };
    }

    // 其他业务错误
    return { success: false, data: null, msg: res_data.msg };
}

// 请求拦截器：自动添加Authorization头
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = token;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
)

// 响应拦截器：处理Token过期自动刷新
axiosInstance.interceptors.response.use(
    (response) => {
        const res_data = response.data;
        //刷新令牌接口的响应直接放行，避免刷新失败时递归触发刷新导致死锁
        if (response.config.url && response.config.url.includes('/api/users/refresh')) {
            return response;
        }
        if (res_data && isTokenCode(res_data.code)) {
            //访问令牌过期，尝试用cookie中的refreshToken刷新
            if (is_refreshing) {
                //正在刷新中，将请求加入队列等待
                return new Promise((resolve) => {
                    subscribeTokenRefresh((new_token) => {
                        response.config.headers.Authorization = new_token;
                        resolve(axiosInstance(response.config));
                    });
                });
            }
            is_refreshing = true;
            return refreshTokenApi()
                .then((refresh_res) => {
                    const refresh_result = refresh_res.data;
                    if (refresh_result && refresh_result.code === CODE_SUCCESS && refresh_result.data) {
                        const new_access_token = refresh_result.data.accessToken;
                        localStorage.setItem('token', new_access_token);
                        onTokenRefreshed(new_access_token);
                        //重试原请求
                        response.config.headers.Authorization = new_access_token;
                        return axiosInstance(response.config);
                    } else {
                        //刷新令牌也失效，强制登出
                        handleForceLogout();
                        return response;
                    }
                })
                .catch(() => {
                    handleForceLogout();
                    return response;
                })
                .finally(() => {
                    is_refreshing = false;
                });
        }
        return response;
    },
    (error) => {
        if (is_logging_out) {
            return new Promise(() => {});
        }
        //刷新令牌接口自身的错误直接放行，避免无限循环刷新
        if (error.config && error.config.url && error.config.url.includes('/api/users/refresh')) {
            return Promise.reject(error);
        }
        if (error.response && error.response.data) {
            const res_data = error.response.data;
            if (isTokenCode(res_data.code)) {
                //访问令牌过期（后端返回HTTP 401），尝试用cookie中的refreshToken刷新
                if (is_refreshing) {
                    return new Promise((resolve) => {
                        subscribeTokenRefresh((new_token) => {
                            error.config.headers.Authorization = new_token;
                            resolve(axiosInstance(error.config));
                        });
                    });
                }
                is_refreshing = true;
                return refreshTokenApi()
                    .then((refresh_res) => {
                        const refresh_result = refresh_res.data;
                        if (refresh_result && refresh_result.code === CODE_SUCCESS && refresh_result.data) {
                            const new_access_token = refresh_result.data.accessToken;
                            localStorage.setItem('token', new_access_token);
                            onTokenRefreshed(new_access_token);
                            error.config.headers.Authorization = new_access_token;
                            return axiosInstance(error.config);
                        } else {
                            handleForceLogout();
                            return new Promise(() => {});
                        }
                    })
                    .catch(() => {
                        handleForceLogout();
                        return new Promise(() => {});
                    })
                    .finally(() => {
                        is_refreshing = false;
                    });
            }
        }
        return Promise.reject(error);
    }
)

export { processResult };
export default axiosInstance
