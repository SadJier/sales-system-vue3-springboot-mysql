import axios from 'axios';

let config = {
    baseURL:"http://localhost:8080",//要连接的后端url
    timeout:60000,
    withCredentials:true
}

const axiosInstance = axios.create(config)

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

export default axiosInstance
