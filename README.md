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
├── nginx/                            # Nginx配置
│   └── conf/
│        └── nginx.conf               #   Nginx配置文件
├── docker/                           # Docker部署
│   └── Dockerfile                    #   后端Docker镜像构建文件
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

| 配置项       | 本地运行    | Docker 部署            |
| ------------ | ----------- | ---------------------- |
| 数据库地址   | `localhost` | `host.docker.internal` |
| Redis地址    | `localhost` | `host.docker.internal` |
| RabbitMQ地址 | `localhost` | `host.docker.internal` |

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

### 第五步：配置并启动 Nginx

1. 将 SSL 证书文件放置到 `nginx/conf/cert/` 目录（需要 `localhost.crt` 和 `localhost.key`，PEM 格式）。

2. 构建前端并部署到 Nginx：

```bash
cd frontend
npm run build
# 将 dist 目录内容复制到 nginx/html/dist/
```

3. 启动 Nginx：

```bash
cd nginx
nginx.exe          # Windows
# 或 nginx         # Linux/Mac
```

4. 访问 `https://localhost:8443` 即可使用系统。

> Nginx 配置文件位于 `nginx/conf/nginx.conf`，监听 8443 端口（SSL），负责 API 反向代理（`/api/` → 后端8080）、前端静态资源服务与 Gzip 压缩加速。不提供 HTTP 自动跳转，需直接通过 HTTPS 访问。

### 第六步：开始使用（开发模式）

打开一个新的终端，进入 `frontend` 目录：

```bash
cd frontend

# 安装依赖（只需执行一次）
npm install

# 启动开发服务器
npm run dev
```

前端启动成功后，在浏览器中打开终端显示的地址（通常为 `http://localhost:5173`）即可使用系统。

### 第七步：开始使用

1. 在登录页面注册一个新账号（系统初始化时已创建管理员：账号 `admin`，密码 `admin123`）
2. 使用注册的账号登录
3. 登录后即可使用各种功能

### 生产环境构建

前端打包：

```bash
cd frontend
npm run build
```

打包后的文件在 `frontend/dist` 目录中，部署到 `nginx/html/dist/` 即可。

后端打包：

```bash
cd backend
mvnw.cmd package -DskipTests
java -jar target/sale_system-0.0.1-SNAPSHOT.jar
```

### Docker 部署

项目提供了 `docker/Dockerfile`，可将后端打包为 Docker 镜像运行。

#### 1. 构建后端 JAR 包

```bash
cd backend
mvnw.cmd package -DskipTests
```

构建完成后 JAR 包位于 `backend/target/你的jar包名.jar`。

#### 2. 构建 Docker 镜像

将 JAR 包复制到 `docker/` 目录并构建镜像：

```bash
# 复制 JAR 包到 docker 目录（重命名为 Dockerfile 中指定的名称）
copy backend\target\你的jar包名.jar docker\你的jar包名.jar
```

如果你的JAR包名经过修改，则需要修改Dockerfile中的包名

```
# 构建镜像
cd docker
docker build -t sales-system:latest .
```

#### 3. 运行 Docker 容器

```bash
docker run -d -p 8080:8080 --name sales-system sales-system:latest
```

#### 4. 关于 host.docker.internal

Docker 容器内部无法直接访问宿主机的 `localhost`，需要使用 `host.docker.internal` 来替代。当前 `application.properties` 中的以下配置已使用 `host.docker.internal`：

```properties
# MySQL - 使用 host.docker.internal 替代 localhost
spring.datasource.url=jdbc:mysql://host.docker.internal:3306/sales_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true

# Redis - 使用 host.docker.internal 替代 localhost
spring.data.redis.host=host.docker.internal

# RabbitMQ - 使用 host.docker.internal 替代 localhost
spring.rabbitmq.host=host.docker.internal
```

> **注意**：`host.docker.internal` 在 Docker Desktop（Windows/Mac）上默认可用。在 Linux 上需要添加 `--add-host=host.docker.internal:host-gateway` 参数：
>
> ```bash
> docker run -d -p 8080:8080 --add-host=host.docker.internal:host-gateway --name sales-system sales-system:latest
> ```

#### 5. 非 Docker 环境（本地开发）

如果不在 Docker 中运行后端，需要将 `application.properties` 中的 `host.docker.internal` 改回 `localhost`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sales_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.data.redis.host=localhost
spring.rabbitmq.host=localhost
```

#### 6. Docker 部署完整流程

确保宿主机已启动 MySQL、Redis、RabbitMQ，然后：

```bash
# 1. 构建后端
cd backend
mvnw.cmd package -DskipTests

# 2. 构建 Docker 镜像
copy target\sale_system-1.0.0.jar ..\docker\sale_system-1.0.0.jar
cd ..\docker
docker build -t sales-system:latest .

# 3. 运行容器
docker run -d -p 8080:8080 --add-host=host.docker.internal:host-gateway --name sales-system sales-system:latest

# 4. 构建前端并部署到 Nginx
cd ..\frontend
npm run build
# 将 dist 目录内容复制到 nginx/html/dist/

# 5. 启动 Nginx
cd ..\nginx
nginx.exe
```

访问 `https://localhost:8443` 即可使用系统。
