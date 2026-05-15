import http from '@/axios/index.js';

// API路径常量
const API = {
    // 用户相关
    USER_LOGIN: '/api/users/login',
    USER_REGISTER: '/api/users/register',
    USER_LOGOUT: '/api/users/logout',
    USER_UPDATE_PASSWORD: '/api/users/update/password',
    USER_UPLOAD_AVATAR: '/api/users/upload/avatar',
    // 用户名模糊查询
    USER_LIST: '/api/users',
    // 删除用户
    USER_DELETE: (user_id) => `/api/users/${user_id}`,
    // 商品相关
    PRODUCT_LIST: '/api/products',
    PRODUCT_DETAIL: (product_id) => `/api/products/${product_id}`,
    PRODUCT_CREATE: '/api/products',
    PRODUCT_UPDATE: (product_id) => `/api/products/${product_id}`,
    PRODUCT_DELETE: (product_id) => `/api/products/${product_id}`,
    // 商品图片上传
    PRODUCT_UPLOAD_IMAGE: '/api/products/upload/products',
    // 商品图片获取（按商品ID）
    PRODUCT_IMAGE_URL: (product_id) => `http://localhost:8080/api/products/image/${product_id}`,
    // 客户相关
    CUSTOMER_LIST: '/api/customers',
    CUSTOMER_CREATE: '/api/customers',
    CUSTOMER_UPDATE: (customer_id) => `/api/customers/${customer_id}`,
    CUSTOMER_DELETE: (customer_id) => `/api/customers/${customer_id}`,
    // 头像获取
    AVATAR_URL: (user_id) => `http://localhost:8080/api/users/avatars/${user_id}`,
};

/**
 * 用户登录
 * @param {String} role 身份(admin/user)
 * @param {String} username 账号
 * @param {String} password 密码
 */
export function userLogin({ role, username, password }) {
    return http.post(API.USER_LOGIN, { role, username, password });
}

/**
 * 用户注册
 * @param {String} role 身份(admin/user)
 * @param {String} username 账号
 * @param {String} password 密码
 */
export function userRegister({ role, username, password }) {
    return http.post(API.USER_REGISTER, { role, username, password });
}

// 用户退出登录
export function userLogout() {
    return http.post(API.USER_LOGOUT);
}

/**
 * 修改用户密码
 * @param {String} username 账号
 * @param {String} oldPassword 旧密码
 * @param {String} newPassword 新密码
 */
export function updateUserPassword({ username, oldPassword, newPassword }) {
    return http.put(API.USER_UPDATE_PASSWORD, { username, oldPassword, newPassword });
}

/**
 * 上传用户头像
 * @param {Number} user_id 用户ID
 * @param {File} file 图片文件
 */
export function uploadUserAvatar({ user_id, file }) {
    const form_data = new FormData();
    form_data.append('userId', String(user_id));
    form_data.append('file', file);
    return http.post(API.USER_UPLOAD_AVATAR, form_data, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
}

// 获取头像URL
export function getAvatarUrl(user_id) {
    return API.AVATAR_URL(user_id);
}

/**
 * 用户名模糊查询
 * @param {String} keyword 搜索关键词
 */
export function getUserList(keyword) {
    return http.get(API.USER_LIST, { params: { keyword } });
}

/**
 * 删除用户
 * @param {Number} user_id 用户ID
 */
export function deleteUser(user_id) {
    return http.delete(API.USER_DELETE(user_id));
}

/**
 * 上传商品图片
 * @param {Number} product_id 商品ID
 * @param {Number} merchant_id 商家ID
 * @param {File} file 图片文件
 */
export function uploadProductImage({ product_id, merchant_id, file }) {
    const form_data = new FormData();
    form_data.append('productId', String(product_id));
    form_data.append('merchantId', String(merchant_id));
    form_data.append('file', file);
    return http.post(API.PRODUCT_UPLOAD_IMAGE, form_data, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
}

// 获取商品图片URL（按商品ID）
export function getProductImageUrl(product_id) {
    if (!product_id) return '';
    return API.PRODUCT_IMAGE_URL(product_id);
}

/**
 * 分页查询商品
 * @param {String} product_name 商品名称
 * @param {String} category 商品分类
 * @param {Number} page_index 页码
 * @param {Number} page_size 页大小
 */
export function getProductList({ productName, category, pageIndex, pageSize }) {
    return http.get(API.PRODUCT_LIST, {
        params: { productName, category, pageIndex, pageSize }
    });
}

/**
 * 根据ID获取商品
 * @param {Number} product_id 商品ID
 */
export function getProductDetail(product_id) {
    return http.get(API.PRODUCT_DETAIL(product_id));
}

/**
 * 新增商品
 * @param {String} name 商品名称
 * @param {String} category 分类
 * @param {Number} purchasePrice 进价
 * @param {Number} salePrice 售价
 * @param {Number} stock 库存
 * @param {String} imagePath 图片路径(可选)
 */
export function createProduct({ name, category, purchasePrice, salePrice, stock, imagePath }) {
    return http.post(API.PRODUCT_CREATE, { name, category, purchasePrice, salePrice, stock, imagePath });
}

/**
 * 更新商品
 * @param {Number} product_id 商品ID
 * @param {String} name 商品名称
 * @param {String} category 分类
 * @param {Number} purchasePrice 进价
 * @param {Number} salePrice 售价
 * @param {Number} stock 库存
 * @param {String} imagePath 图片路径(可选)
 */
export function updateProduct(product_id, { name, category, purchasePrice, salePrice, stock, imagePath }) {
    return http.put(API.PRODUCT_UPDATE(product_id), { name, category, purchasePrice, salePrice, stock, imagePath });
}

/**
 * 删除商品
 * @param {Number} product_id 商品ID
 */
export function deleteProduct(product_id) {
    return http.delete(API.PRODUCT_DELETE(product_id));
}

/**
 * 客户模糊查询
 * @param {String} keyword 搜索关键词
 */
export function getCustomerList(keyword) {
    return http.get(API.CUSTOMER_LIST, { params: { keyword } });
}

/**
 * 新增客户
 * @param {String} name 姓名
 * @param {String} phone 电话
 * @param {String} address 地址
 */
export function createCustomer({ name, phone, address }) {
    return http.post(API.CUSTOMER_CREATE, { name, phone, address });
}

/**
 * 更新客户
 * @param {Number} customer_id 客户ID
 * @param {Number} customerId 客户ID(body)
 * @param {String} name 姓名
 * @param {String} phone 电话
 * @param {String} address 地址
 */
export function updateCustomer(customer_id, { customerId, name, phone, address }) {
    return http.put(API.CUSTOMER_UPDATE(customer_id), { customerId, name, phone, address });
}

/**
 * 删除客户
 * @param {Number} customer_id 客户ID
 */
export function deleteCustomer(customer_id) {
    return http.delete(API.CUSTOMER_DELETE(customer_id));
}

export default API;
