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
    USER_DELETE: (user_id) => `/api/users/delete/${user_id}`,
    // 商品相关
    PRODUCT_LIST: '/api/products',
    // 商品详情
    PRODUCT_DETAIL: (product_id) => `/api/products/get/${product_id}`,
    PRODUCT_CREATE: '/api/products',
    // 商品更新
    PRODUCT_UPDATE: '/api/products/update',
    // 商品删除
    PRODUCT_DELETE: (product_id) => `/api/products/delete/${product_id}`,
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
    // 订单相关
    ORDER_LIST: '/api/orders',
    ORDER_CREATE: '/api/orders',
    ORDER_UPDATE: '/api/orders/update',
    // 订单删除
    ORDER_DELETE: (order_id) => `/api/orders/delete/${order_id}`,
    // 店铺统计
    STORE_STATS: '/api/stores/stats',
    // 商品详情统计
    PRODUCT_STATS: (product_id) => `/api/products/stats/${product_id}`,
    // 分类相关
    // 获取所有商品分类
    CATEGORY_LIST: '/api/categories/list',
    CATEGORY_CREATE: '/api/categories/add',
    CATEGORY_UPDATE: '/api/categories/update',
    // 分类删除
    CATEGORY_DELETE: (category_id) => `/api/categories/delete/${category_id}`,
};

/**
 * 用户登录
 * @param {String} role 身份(MERCHANT/ADMIN)
 * @param {String} username 账号
 * @param {String} password 密码
 */
export function userLogin({ role, username, password }) {
    return http.post(API.USER_LOGIN, { role, username, password });
}

/**
 * 用户注册
 * @param {String} role 身份(MERCHANT/ADMIN)
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
 * 修改用户密码（身份由token判断）
 * @param {String} oldPassword 旧密码
 * @param {String} newPassword 新密码
 */
export function updateUserPassword({ oldPassword, newPassword }) {
    return http.put(API.USER_UPDATE_PASSWORD, { oldPassword, newPassword });
}

/**
 * 上传用户头像（身份由token判断）
 * @param {File} file 图片文件
 */
export function uploadUserAvatar({ file }) {
    const form_data = new FormData();
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
 * 用户名模糊查询（分页）
 * @param {String} username 搜索用户名
 * @param {Number} pageIndex 页码
 * @param {Number} pageSize 页大小
 */
export function getUserList({ username, pageIndex, pageSize }) {
    return http.get(API.USER_LIST, { params: { username, pageIndex, pageSize } });
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
 * @param {File} file 图片文件
 */
export function uploadProductImage({ product_id, file }) {
    const form_data = new FormData();
    form_data.append('productId', String(product_id));
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
 * @param {String} productName 商品名称
 * @param {Number} categoryId 商品分类ID
 * @param {Number} pageIndex 页码
 * @param {Number} pageSize 页大小
 */
export function getProductList({ productName, categoryId, pageIndex, pageSize }) {
    return http.get(API.PRODUCT_LIST, {
        params: { productName, categoryId, pageIndex, pageSize }
    });
}

/**
 * 根据ID获取商品详细信息
 * @param {Number} product_id 商品ID
 */
export function getProductDetail(product_id) {
    return http.get(API.PRODUCT_DETAIL(product_id));
}

/**
 * 新增商品
 * @param {String} name 商品名称
 * @param {Number} categoryId 分类ID
 * @param {Number} purchasePrice 进价
 * @param {Number} salePrice 售价
 * @param {Number} stock 库存
 */
export function createProduct({ name, categoryId, purchasePrice, salePrice, stock }) {
    return http.post(API.PRODUCT_CREATE, { name, categoryId, purchasePrice, salePrice, stock });
}

/**
 * 更新商品（productId在body中传递）
 * @param {Number} productId 商品ID
 * @param {String} name 商品名称
 * @param {Number} categoryId 分类ID
 * @param {Number} purchasePrice 进价
 * @param {Number} salePrice 售价
 * @param {Number} stock 库存
 */
export function updateProduct({ productId, name, categoryId, purchasePrice, salePrice, stock }) {
    return http.put(API.PRODUCT_UPDATE, { productId, name, categoryId, purchasePrice, salePrice, stock });
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

/**
 * 分页查询订单（商家查自己，管理员查全部）
 * @param {Number} pageIndex 页码
 * @param {Number} pageSize 页大小
 */
export function getOrderList({ pageIndex, pageSize }) {
    return http.get(API.ORDER_LIST, { params: { pageIndex, pageSize } });
}

/**
 * 新增订单
 * @param {String} buyerName 买家姓名
 * @param {String} buyerPhone 买家电话
 * @param {Number} productId 商品ID
 * @param {Number} quantity 购买数量
 * @param {String} remark 备注
 */
export function createOrder({ buyerName, buyerPhone, productId, quantity, remark }) {
    return http.post(API.ORDER_CREATE, { buyerName, buyerPhone, productId, quantity, remark });
}

/**
 * 更新订单（修改备注和状态）
 * @param {Number} orderId 订单ID
 * @param {String} status 订单状态
 * @param {String} remark 备注
 */
export function updateOrder({ orderId, status, remark }) {
    return http.put(API.ORDER_UPDATE, { orderId, status, remark });
}

/**
 * 删除订单
 * @param {Number} order_id 订单ID
 */
export function deleteOrder(order_id) {
    return http.delete(API.ORDER_DELETE(order_id));
}

/**
 * 获取店铺统计
 * @param {Number} merchantId 商家ID（管理员查看指定商家时传入）
 */
export function getStoreStats(merchantId) {
    const params = {};
    if (merchantId) params.merchantId = merchantId;
    return http.get(API.STORE_STATS, { params });
}

/**
 * 获取商品详情统计
 * @param {Number} product_id 商品ID
 */
export function getProductStats(product_id) {
    return http.get(API.PRODUCT_STATS(product_id));
}

/**
 * 查询所有分类
 */
export function getCategoryList() {
    return http.get(API.CATEGORY_LIST);
}

/**
 * 新增分类
 * @param {String} name 分类名称
 */
export function createCategory(name) {
    return http.post(API.CATEGORY_CREATE, name, {
        headers: { 'Content-Type': 'application/json' }
    });
}

/**
 * 更新分类
 * @param {Number} categoryId 分类ID
 * @param {String} name 分类名称
 */
export function updateCategory({ categoryId, name }) {
    return http.put(API.CATEGORY_UPDATE, { categoryId, name });
}

/**
 * 删除分类
 * @param {Number} category_id 分类ID
 */
export function deleteCategory(category_id) {
    return http.delete(API.CATEGORY_DELETE(category_id));
}

export default API;
