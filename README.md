# 商品销售管理系统

一个基于前后端分离架构的商品销售管理系统，支持用户管理、商品管理、客户管理等功能，区分管理员和普通用户角色。

## 功能概览

- **用户管理**：注册、登录、退出、修改密码、头像上传
- **商品管理**：商品增删改查、分页查询、图片上传
- **商品分类管理**：商品分类增删改查
- **客户管理**：客户增删改查、模糊搜索
- **订单管理**：订单增删改查、分页查询、状态更新

## 技术栈

### 后端

| 技术                   | 说明               |
| ---------------------- | ------------------ |
| Java 17                | 开发语言           |
| Spring Boot 3.2.5      | 后端框架           |
| Spring Data JPA        | 数据库ORM          |
| MySQL                  | 数据库             |
| Spring Security Crypto | 密码加密（BCrypt） |
| JWT (jjwt)             | 用户认证令牌       |
| Spring Data Redis      | 缓存支持           |
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
├── backend/                  # 后端项目
│   └── src/main/
│       ├── java/com/sadjier/
│       │   ├── config/       # 配置类（跨域、安全、Web）
│       │   ├── controller/   # 控制器（用户、商品、客户）
│       │   ├── dao/          # 数据访问层
│       │   ├── model/        # 数据模型
│       │   │   ├── dto/      # 请求数据传输对象
│       │   │   ├── entity/   # 数据库实体
│       │   │   └── vo/       # 响应视图对象
│       │   ├── service/      # 业务逻辑层
│       │   ├── interceptor/  # 登录拦截器
│       │   ├── util/         # 工具类（JWT、通用工具）
│       │   └── common/       # 公共类（统一响应结果）
│       └── resources/
│           └── application.properties  # 后端配置文件
├── frontend/                 # 前端项目
│   └── src/
│       ├── api/              # API 接口定义
│       ├── axios/            # Axios 实例配置
│       ├── components/       # 页面组件
│       ├── pinia/            # 状态管理
│       └── router/           # 路由配置
└── README.md
```

## 部署方法

### 环境准备

在部署之前，请确保你的电脑已安装以下软件：

| 软件    | 版本要求         | 用途                                 |
| ------- | ---------------- | ------------------------------------ |
| JDK     | 17 或以上        | 运行后端                             |
| Node.js | 20.19+ 或 22.12+ | 运行前端                             |
| MySQL   | 5.7+ 或 8.0+     | 数据库                               |
| Redis   | 任意稳定版       | 缓存                                 |
| Maven   | 3.6+             | 后端构建（项目自带 wrapper，可不装） |

### 第一步：配置数据库

1. 启动 MySQL，创建数据库：

```sql
CREATE DATABASE sales_db DEFAULT CHARACTER SET utf8mb4;
```

2. 修改后端配置文件 `backend/src/main/resources/application.properties`，将数据库用户名和密码改为你自己的：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sales_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=你的用户名
spring.datasource.password=你的密码
```

> JPA 会根据实体类自动创建数据表，无需手动建表。

### 第二步：启动后端

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

### 第三步：启动前端

打开一个新的终端，进入 `frontend` 目录：

```bash
cd frontend

# 安装依赖（只需执行一次）
npm install

# 启动开发服务器
npm run dev
```

前端启动成功后，在浏览器中打开终端显示的地址（通常为 `http://localhost:5173`）即可使用系统。

### 第四步：启动Redis

找到下载并安装的redis目录，进入并运行redis_server.exe

### 第五步：开始使用

1. 在登录页面注册一个新账号（默认初始化创建管理员：账号(admin),密码(admin123)）
2. 使用注册的账号登录
3. 登录后即可使用各种功能

### 生产环境构建（可选）

如果需要部署到服务器，可以将前端打包为静态文件：

```bash
cd frontend
npm run build
```

打包后的文件在 `frontend/dist` 目录中，可部署到 Nginx 等静态服务器。

后端可打包为 JAR 文件运行：

```bash
cd backend
mvnw.cmd package -DskipTests
java -jar target/sale_system-0.0.1-SNAPSHOT.jar
```
