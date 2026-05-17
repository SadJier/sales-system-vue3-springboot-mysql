import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';

let config = {
    baseURL:"http://localhost:8080",//要连接的后端url
    timeout:60000,
    withCredentials:true
}

const axiosInstance = axios.create(config)

// 操作成功
const CODE_SUCCESS = 200;
// 操作出错
const CODE_ERROR = 400;
// 数据不存在
const CODE_NO_DATA = 401;
// 数据重复
const CODE_DATA_DUPLICATE = 402;
// 无数据操作权限
const CODE_DATA_NO_PERMISSION = 403;
// 数据缺失或无效
const CODE_DATA_MISSING = 404;
// Token过期或无效
const CODE_TOKEN_INVALID = 1001;
// Token缺失
const CODE_TOKEN_MISSING = 1002;
// 无操作权限
const CODE_NO_PERMISSION = 1101;
// 防止多次触发登出逻辑的标记
let is_logging_out = false;

// 强制登出处理（token过期/缺失时调用，弹出确认框后清空数据并跳转登录页）
function handleForceLogout(msg) {
    if (is_logging_out) return;
    is_logging_out = true;
    ElMessageBox.alert(
        msg || '登录已过期，请重新登录',
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

// 检查是否为权限相关状态码
function isPermissionCode(code) {
    return code === CODE_NO_PERMISSION || code === CODE_DATA_NO_PERMISSION;
}

// 处理Result响应，按业务状态码分类处理，返回结构化结果
function processResult(res_data, fallback_msg) {
    if (!res_data) {
        if (!is_logging_out) ElMessage.error(fallback_msg || '操作失败');
        return { success: false, data: null, msg: fallback_msg || '操作失败' };
    }

    const code = res_data.code;

    if (code === CODE_SUCCESS) {
        return { success: true, data: res_data.data, msg: res_data.msg };
    }

    // Token相关：已由拦截器处理登出，此处仅返回失败
    if (isTokenCode(code)) {
        return { success: false, data: null, msg: res_data.msg };
    }

    // 权限相关
    if (isPermissionCode(code)) {
        if (!is_logging_out) ElMessage.error(res_data.msg || '无操作权限');
        return { success: false, data: null, msg: res_data.msg };
    }

    // 其他业务错误（400/401/402/404等）
    if (!is_logging_out) ElMessage.error(res_data.msg || fallback_msg || '操作失败');
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

// 响应拦截器：仅处理Token相关状态码的强制登出
axiosInstance.interceptors.response.use(
    (response) => {
        const res_data = response.data;
        if (res_data && isTokenCode(res_data.code)) {
            handleForceLogout(res_data.msg);
        }
        return response;
    },
    (error) => {
        if (is_logging_out) {
            return new Promise(() => {});
        }
        if (error.response && error.response.data) {
            const res_data = error.response.data;
            if (isTokenCode(res_data.code)) {
                handleForceLogout(res_data.msg);
                return new Promise(() => {});
            }
        }
        return Promise.reject(error);
    }
)

export { processResult };
export default axiosInstance
