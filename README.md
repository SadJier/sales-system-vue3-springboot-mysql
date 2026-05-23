Copyright (C) 2026 sadjier
This project is licensed under the GNU General Public License v3.0

# 商品销售管理系统

基于前后端分离架构的商品销售管理系统，支持商家和管理员两种角色，涵盖商品管理、订单管理、店铺统计、分类管理等核心业务。

## 功能概览

- **用户管理**：注册、登录、退出、修改密码、头像上传、用户查询与删除（管理员）
- **商品管理**：商品增删改查、分页查询、图片上传、商品详情统计
- **商品分类管理**：分类增删改查（管理员）
- **订单管理**：订单增删改查、分页查询、状态更新
- **店铺统计**：订单统计、收入统计、商品销售占比
- **权限控制**：商家/管理员角色区分，双Token认证（AccessToken + RefreshToken），@RequireRole注解权限校验，路由守卫保护

## 技术栈

### 后端

| 技术                   | 说明                              |
| ---------------------- | --------------------------------- |
| Java 17                | 开发语言                          |
| Spring Boot 3.2.5      | 后端框架                          |
| Spring Data JPA        | 数据库 ORM                        |
| MySQL                  | 数据库                            |
| Spring Security Crypto | 密码加密（BCrypt）                |
| JWT (jjwt 0.11.5)      | 用户认证令牌                      |
| Spring Data Redis      | Token白名单与刷新令牌缓存         |
| Spring AMQP (RabbitMQ) | 消息队列（订单事件异步处理）      |
| Redis分布式锁          | 防止库存超卖与令牌并发冲突        |
| Knife4j (OpenAPI 3)    | API 接口文档                      |
| Lombok                 | 简化实体类代码                    |
| Maven                  | 项目构建工具                      |
| Nginx                  | 反向代理、HTTPS终止、静态资源加速 |

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
│       │   ├── SpringDocConfig       #   API文档配置
│       │   ├── RedisConfig           #   Redis序列化配置
│       │   └── RabbitMQConfig        #   RabbitMQ交换机/队列/绑定配置
│       ├── controller/               # 控制器
│       │   ├── UserController        #   用户管理
│       │   ├── ProductController     #   商品管理
│       │   ├── OrderController       #   订单管理
│       │   ├── CategoryController    #   分类管理
│       │   ├── StoreController       #   店铺统计
│       │   └── BusinessStatusController # 业务完成状态查询
│       ├── dao/                      # 数据访问层
│       ├── model/                    # 数据模型
│       │   ├── dto/                  #   请求DTO（按模块分子包）
│       │   ├── entity/               #   数据库实体
│       │   └── vo/                   #   响应VO（按模块分子包）
│       ├── service/                  # 业务逻辑层
│       │   └── Impl/                 #   实现类
│       ├── interceptor/              # 拦截器（Token白名单校验、权限校验）
│       ├── annotations/              # 自定义注解（@RequireRole、@DistributedLock）
│       ├── state/                    # 订单状态机（抽象类+各状态实现+工厂）
│       ├── mq/                       # 消息队列
│       │   ├── model/                #   消息DTO（ImageUploadMessage、StoreStatsUpdateMessage）
│       │   ├── producer/             #   生产者（BusinessMessageProducer）
│       │   └── consumer/             #   消费者（ImageUploadConsumer、StoreStatsUpdateConsumer、OrderDlxConsumer）
│       ├── enums/                    # 枚举（订单状态、用户角色、结果状态）
│       ├── constant/                 # 常量（返回消息）
│       ├── util/                     # 工具类（JWT、通用工具、JSON、RedisLockUtil）
│       ├── common/                   # 公共类（统一响应结果）
│       └── exception/                # 全局异常处理
│       └── aop/                      # AOP 配置（日志记录、分布式锁切面）
├── frontend/                         # 前端项目
│   └── src/
│       ├── api/                      # API 接口定义
│       ├── axios/                    # Axios 实例配置
│       ├── components/               # 页面组件
│       ├── pinia/                    # 状态管理
│       └── router/                   # 路由配置
├── docker/                           # Docker部署
│   ├── backend/                      #   后端镜像构建
│   │   └── Dockerfile                #     后端Dockerfile
│   └── frontend/                     #   前端镜像构建
│       ├── Dockerfile                #     前端Dockerfile
│       ├── nginx.conf                #     Nginx配置文件
│       ├── cert/                     #     SSL证书目录
│       └── dist/                     #     前端构建产物
└── README.md
```

## 数据库表说明

| 表名        | 说明                                            |
| ----------- | ----------------------------------------------- |
| sys_user    | 系统用户表（商家、管理员）                      |
| product     | 商品表                                          |
| orders      | 订单表                                          |
| category    | 商品分类表                                      |
| store_stats | 店铺统计缓存表（MQ消费者全量更新，API直接查表） |

## API 接口总览

| 模块 | 方法   | 路径                            | 说明               |
| ---- | ------ | ------------------------------- | ------------------ |
| 用户 | POST   | `/api/users/login`              | 登录               |
| 用户 | POST   | `/api/users/register`           | 注册               |
| 用户 | POST   | `/api/users/refresh`            | 刷新访问令牌       |
| 用户 | POST   | `/api/users/logout`             | 退出登录           |
| 用户 | PUT    | `/api/users/update/password`    | 修改密码           |
| 用户 | POST   | `/api/users/upload/avatar`      | 上传头像           |
| 用户 | GET    | `/api/users/avatars/{id}`       | 获取用户头像       |
| 用户 | GET    | `/api/users`                    | 用户分页查询       |
| 用户 | DELETE | `/api/users/delete/{id}`        | 删除用户           |
| 商品 | GET    | `/api/products`                 | 商品分页查询       |
| 商品 | POST   | `/api/products`                 | 新增商品           |
| 商品 | GET    | `/api/products/get/{id}`        | 商品详情           |
| 商品 | PUT    | `/api/products/update`          | 更新商品           |
| 商品 | DELETE | `/api/products/delete/{id}`     | 删除商品           |
| 商品 | POST   | `/api/products/upload/products` | 上传商品图片       |
| 商品 | GET    | `/api/products/image/{id}`      | 获取商品图片       |
| 商品 | GET    | `/api/products/stats/{id}`      | 商品详情统计       |
| 订单 | GET    | `/api/orders`                   | 订单分页查询       |
| 订单 | POST   | `/api/orders`                   | 新增订单           |
| 订单 | PUT    | `/api/orders/update`            | 更新订单           |
| 订单 | DELETE | `/api/orders/delete/{id}`       | 删除订单           |
| 订单 | GET    | `/api/orders/transitions/{id}`  | 获取订单可转换状态 |
| 分类 | GET    | `/api/categories/list`          | 分类列表           |
| 分类 | POST   | `/api/categories/add`           | 新增分类           |
| 分类 | PUT    | `/api/categories/update`        | 更新分类           |
| 分类 | DELETE | `/api/categories/delete/{id}`   | 删除分类           |
| 店铺 | GET    | `/api/stores/stats`             | 店铺统计           |
| 业务 | GET    | `/api/business/completed/{id}`  | 查询业务是否完成   |

## 部署方法

### 环境准备

| 软件     | 版本要求         | 用途                                    |
| -------- | ---------------- | --------------------------------------- |
| JDK      | 17 或以上        | 运行后端                                |
| Node.js  | 20.19+ 或 22.12+ | 运行前端                                |
| MySQL    | 5.7+ 或 8.0+     | 数据库                                  |
| Redis    | 任意稳定版       | 缓存（Token白名单、刷新令牌、分布式锁） |
| RabbitMQ | 3.x+             | 消息队列（订单事件异步处理）            |
| Maven    | 3.6+             | 后端构建（项目自带 wrapper，可不装）    |
| Docker   | 20.x+（可选）    | 容器化部署后端                          |

### 第一步：配置后端

1. 将 `backend/src/main/resources/application.properties.example` 复制为 `application.properties`（移除 `.example` 后缀）。

2. 创建 MySQL 数据库：

```sql
CREATE DATABASE sales_db DEFAULT CHARACTER SET utf8mb4;
```

3. 修改 `application.properties` 中的配置项：

**数据库配置**

```properties
spring.datasource.url=jdbc:mysql://数据库地址:端口/sales_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
```

**JWT 配置**

```properties
jwt.secret=你的JWT密钥
```

**Redis 配置**

```properties
spring.data.redis.host=Redis地址
spring.data.redis.port=Redis端口
spring.data.redis.password=Redis密码（无密码留空即可）
```

**RabbitMQ 配置**

```properties
spring.rabbitmq.host=RabbitMQ地址
spring.rabbitmq.port=RabbitMQ端口
spring.rabbitmq.username=用户名
spring.rabbitmq.password=密码
```

**文件上传路径配置**

```properties
file.avatar.path=uploads/avatars/
file.product.path=uploads/products/
```

4. 根据运行环境替换地址：

| 配置项       | 本地运行    | Docker 部署（后端容器连接中间件容器） |
| ------------ | ----------- | -------------------------------------- |
| 数据库地址   | `localhost` | `mysql_sales_manager`                  |
| Redis地址    | `localhost` | `redis_sales_manager`                  |
| RabbitMQ地址 | `localhost` | `rabbitmq_sales_manager`               |

> JPA 会根据实体类自动创建数据表，无需手动建表。

### 第二步：启动 Redis

找到 Redis 安装目录，运行 `redis-server.exe`（Windows）或 `redis-server`（Linux/Mac）。

### 第三步：启动 RabbitMQ

1. 安装 RabbitMQ（需先安装 Erlang）：参考 [RabbitMQ 官方安装指南](https://www.rabbitmq.com/download.html)
2. 启动 RabbitMQ 服务：
   - Windows：`rabbitmq-server`
   - Linux：`sudo systemctl start rabbitmq-server`
3. 默认账户：`guest` / `guest`，管理界面：`http://localhost:15672`
4. 如需修改连接信息，编辑 `application.properties` 中的 `spring.rabbitmq.*` 配置

### 第四步：启动后端

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

### 第五步：开始使用（开发模式）

打开一个新的终端，进入 `frontend` 目录：

```bash
cd frontend

# 安装依赖（只需执行一次）
npm install

# 启动开发服务器
npm run dev
```

前端启动成功后，在浏览器中打开终端显示的地址（通常为 `http://localhost:5173`）即可使用系统。

### 第六步：开始使用

1. 在登录页面注册一个新账号（系统初始化时已创建管理员：账号 `admin`，密码 `admin123`）
2. 使用注册的账号登录
3. 登录后即可使用各种功能

### 生产环境构建

前端打包：

```bash
cd frontend
npm run build
```

打包后的文件在 `frontend/dist` 目录中，将 `dist` 目录内容复制到 `docker/frontend/dist/` 即可用于 Docker 镜像构建。

后端打包：

```bash
cd backend
mvnw.cmd package -DskipTests
```

打包后的 JAR 文件在 `backend/target/` 目录中。将其复制到 `docker/backend/` 并保持其名称与Dockerfile中的jar包名称相同，即可用于 Docker 镜像构建。

### Docker 部署

项目提供完整的 Docker 部署方案，后端和前端分别构建为独立镜像，中间件（MySQL、Redis、RabbitMQ）使用 Docker 容器运行，所有容器通过自定义网络互联。

#### 1. 准备工作

确保已安装 Docker，并完成以下准备：

- 后端 JAR 包已复制到 `docker/backend/`下并于Dockerfile中的JAR包名称一致
- 前端构建产物已复制到 `docker/frontend/dist/`
- SSL 证书已放置到 `docker/frontend/cert/`（需要 `localhost.crt` 和 `localhost.key`，PEM 格式）
- `application.properties` 中各中间件地址已改为容器名（见第一步配置表格）

#### 2. 创建 Docker 网络

```bash
docker network create sales_manager_network
```

#### 3. 构建镜像

```bash
# 构建后端镜像
cd docker/backend
docker build -t sales_manager:windows .

# 构建前端镜像
cd ../frontend
docker build -t sales_manager_frontend:windows .
```

#### 4. 启动中间件容器

**MySQL**

```bash
docker run -d `
  --name mysql_sales_manager `
  --network sales_manager_network `
  -p 3306:3306 `
  -v mysql-data:/var/lib/mysql `
  -e MYSQL_ROOT_PASSWORD=你的数据库密码 `
  -e MYSQL_DATABASE=sales_db `
  mysql:8
```

**Redis**

```bash
docker run -d `
  --name redis_sales_manager `
  --network sales_manager_network `
  -p 6379:6379 `
  -v redis-data:/data `
  redis:alpine `
  redis-server --appendonly yes
```

**RabbitMQ**

```bash
docker run -d `
  --name rabbitmq_sales_manager `
  --network sales_manager_network `
  -p 5672:5672 `
  -p 15672:15672 `
  -v rabbitmq-data:/var/lib/rabbitmq `
  -e RABBITMQ_DEFAULT_USER=你的rabbitmq用户名 `
  -e RABBITMQ_DEFAULT_PASS=你的rabbitmq密码 `
  rabbitmq:3-management
```

> **注意**：RabbitMQ 容器不能使用默认的 `guest` / `guest` 账户，必须通过环境变量指定自定义用户名和密码，且需与 `application.properties` 中的 `spring.rabbitmq.username` 和 `spring.rabbitmq.password` 一致。

#### 5. 启动应用容器

**后端**

```bash
docker run -d `
  --name sales_manager `
  --network sales_manager_network `
  -p 8080:8080 `
  sales_manager:windows
```

**前端**

```bash
docker run -d `
  --name sales_manager_frontend `
  --network sales_manager_network `
  -p 8443:8443 `
  sales_manager_frontend:windows
```

#### 6. 访问系统

访问 `https://localhost:8443` 即可使用系统。
