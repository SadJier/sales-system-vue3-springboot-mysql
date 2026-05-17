# 商品销售管理系统

基于前后端分离架构的商品销售管理系统，支持商家和管理员两种角色，涵盖商品管理、订单管理、店铺统计、分类管理等核心业务。

## 功能概览

- **用户管理**：注册、登录、退出、修改密码、头像上传、用户查询与删除（管理员）
- **商品管理**：商品增删改查、分页查询、图片上传、商品详情统计
- **商品分类管理**：分类增删改查（管理员）
- **订单管理**：订单增删改查、分页查询、状态更新
- **店铺统计**：订单统计、收入统计、商品销售占比
- **权限控制**：商家/管理员角色区分，JWT 认证，路由守卫保护

## 技术栈

### 后端

| 技术                   | 说明               |
| ---------------------- | ------------------ |
| Java 17                | 开发语言           |
| Spring Boot 3.2.5      | 后端框架           |
| Spring Data JPA        | 数据库 ORM         |
| MySQL                  | 数据库             |
| Spring Security Crypto | 密码加密（BCrypt） |
| JWT (jjwt 0.11.5)      | 用户认证令牌       |
| Spring Data Redis      | Token 黑名单缓存   |
| Knife4j (OpenAPI 3)    | API 接口文档       |
| Lombok                 | 简化实体类代码     |
| Maven                  | 项目构建工具       |

### 前端

| 技术         | 说明        |
| ------------ | ----------- |
| Vue 3        | 前端框架    |
| Vite 7       | 构建工具    |
| Vue Router 4 | 路由管理    |
| Pinia        | 状态管理    |
| Element Plus | UI 组件库   |
| Axios        | HTTP 请求库 |

## 项目结构

```
sales_system/
├── backend/                          # 后端项目
│   └── src/main/java/com/sadjier/
│       ├── config/                   # 配置类
│       │   ├── CorsConfig            #   跨域配置
│       │   ├── SecurityConfig        #   安全配置（BCrypt）
│       │   ├── WebConfig             #   拦截器注册
│       │   ├── DataInitializer       #   数据初始化
│       │   └── SpringDocConfig       #   API文档配置
│       ├── controller/               # 控制器
│       │   ├── UserController        #   用户管理
│       │   ├── ProductController     #   商品管理
│       │   ├── OrderController       #   订单管理
│       │   ├── CategoryController    #   分类管理
│       │   └── StoreController       #   店铺统计
│       ├── dao/                      # 数据访问层
│       ├── model/                    # 数据模型
│       │   ├── dto/                  #   请求DTO（按模块分子包）
│       │   ├── entity/               #   数据库实体
│       │   └── vo/                   #   响应VO（按模块分子包）
│       ├── service/                  # 业务逻辑层
│       │   └── Impl/                 #   实现类
│       ├── interceptor/              # 登录拦截器
│       ├── enums/                    # 枚举（订单状态、用户角色、结果状态）
│       ├── constant/                 # 常量（返回消息）
│       ├── util/                     # 工具类（JWT、通用工具、JSON）
│       ├── common/                   # 公共类（统一响应结果）
│       └── exception/                # 全局异常处理
├── frontend/                         # 前端项目
│   └── src/
│       ├── api/                      # API 接口定义
│       ├── axios/                    # Axios 实例配置
│       ├── components/               # 页面组件
│       ├── pinia/                    # 状态管理
│       └── router/                   # 路由配置
└── README.md
```

## API 接口总览

| 模块 | 方法   | 路径                            | 说明         |
| ---- | ------ | ------------------------------- | ------------ |
| 用户 | POST   | `/api/users/login`              | 登录         |
| 用户 | POST   | `/api/users/register`           | 注册         |
| 用户 | POST   | `/api/users/logout`             | 退出登录     |
| 用户 | PUT    | `/api/users/update/password`    | 修改密码     |
| 用户 | POST   | `/api/users/upload/avatar`      | 上传头像     |
| 用户 | GET    | `/api/users`                    | 用户分页查询 |
| 用户 | DELETE | `/api/users/delete/{id}`        | 删除用户     |
| 商品 | GET    | `/api/products`                 | 商品分页查询 |
| 商品 | POST   | `/api/products`                 | 新增商品     |
| 商品 | GET    | `/api/products/get/{id}`        | 商品详情     |
| 商品 | PUT    | `/api/products/update`          | 更新商品     |
| 商品 | DELETE | `/api/products/delete/{id}`     | 删除商品     |
| 商品 | POST   | `/api/products/upload/products` | 上传商品图片 |
| 商品 | GET    | `/api/products/stats/{id}`      | 商品详情统计 |
| 订单 | GET    | `/api/orders`                   | 订单分页查询 |
| 订单 | POST   | `/api/orders`                   | 新增订单     |
| 订单 | PUT    | `/api/orders/update`            | 更新订单     |
| 订单 | DELETE | `/api/orders/delete/{id}`       | 删除订单     |
| 分类 | GET    | `/api/categories/list`          | 分类列表     |
| 分类 | POST   | `/api/categories/add`           | 新增分类     |
| 分类 | PUT    | `/api/categories/update`        | 更新分类     |
| 分类 | DELETE | `/api/categories/delete/{id}`   | 删除分类     |
| 店铺 | GET    | `/api/stores/stats`             | 店铺统计     |

## 部署方法

### 环境准备

| 软件    | 版本要求         | 用途                                 |
| ------- | ---------------- | ------------------------------------ |
| JDK     | 17 或以上        | 运行后端                             |
| Node.js | 20.19+ 或 22.12+ | 运行前端                             |
| MySQL   | 5.7+ 或 8.0+     | 数据库                               |
| Redis   | 任意稳定版       | 缓存（Token黑名单）                  |
| Maven   | 3.6+             | 后端构建（项目自带 wrapper，可不装） |

### 第一步：配置数据库

1. 启动 MySQL，创建数据库：

```sql
CREATE DATABASE sales_db DEFAULT CHARACTER SET utf8mb4;
```

2. 修改后端配置文件 `backend/src/main/resources/application.properties`，将数据库用户名和密码改为你自己的：

```properties
spring.datasource.username=你的用户名
spring.datasource.password=你的密码
```

> JPA 会根据实体类自动创建数据表，无需手动建表。

### 第二步：启动 Redis

找到 Redis 安装目录，运行 `redis-server.exe`（Windows）或 `redis-server`（Linux/Mac）。

### 第三步：启动后端

打开终端，进入 `backend` 目录：

```bash
cd backend

# Windows 使用 Maven Wrapper 启动
mvnw.cmd spring-boot:run

# 如果已安装 Maven，也可以用
mvn spring-boot:run
```

后端启动成功后，默认运行在 `http://localhost:8080`。

API 文档访问地址：`http://localhost:8080/doc.html`

### 第四步：启动前端

打开一个新的终端，进入 `frontend` 目录：

```bash
cd frontend

# 安装依赖（只需执行一次）
npm install

# 启动开发服务器
npm run dev
```

前端启动成功后，在浏览器中打开终端显示的地址（通常为 `http://localhost:5173`）即可使用系统。

### 第五步：开始使用

1. 在登录页面注册一个新账号（系统初始化时已创建管理员：账号 `admin`，密码 `admin123`）
2. 使用注册的账号登录
3. 登录后即可使用各种功能

### 生产环境构建（可选）

前端打包：

```bash
cd frontend
npm run build
```

打包后的文件在 `frontend/dist` 目录中，可部署到 Nginx 等静态服务器。

后端打包：

```bash
cd backend
mvnw.cmd package -DskipTests
java -jar target/sale_system-0.0.1-SNAPSHOT.jar
```
