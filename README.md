# SleekFlow TODO List API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-9.5-blue.svg)](https://www.mysql.com)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](LICENSE)

> An enterprise-grade real-time collaborative TODO list REST API built with Java 21, Spring Boot 3.3.5, and MySQL
>
> 企业级实时协作待办事项 REST API，基于 Java 21、Spring Boot 3.3.5 和 MySQL 构建

---

## 🌟 Features

### Core Features

- **Todo CRUD**: Create, read, update, delete todos with priority, due date, and tags
- **Advanced Filtering**: Filter by status, priority, date range, tags, and ownership
- **Soft Delete**: Todos are marked as deleted instead of being permanently removed
- **Priority Management**: Three priority levels (HIGH, MEDIUM, LOW)

### Advanced Features

- **User Tags**: User-scoped tags for organizing todos (e.g., work, personal, urgent)
- **Collaboration & Sharing**: Share todos with other users and grant granular permissions
- **Role-Based Access Control (RBAC)**: Three permission levels - OWNER (full control), EDITOR (update), VIEWER (read-only)
- **Activity Feed**: Immutable audit log tracking all todo mutations
- **Team Management**: Create teams and manage members with ADMIN/MEMBER roles
- **JWT Authentication**: Secure token-based authentication with 24-hour expiration

### 功能特性

#### 核心功能

- **待办事项增删改查**：创建、查询、更新、删除待办事项，支持优先级、截止日期和标签
- **高级过滤**：按状态、优先级、日期范围、标签、所有权等多维度过滤
- **软删除**：待办事项采用软删除机制，数据可恢复
- **优先级管理**：三个优先级：高、中、低

#### 高级功能

- **用户标签**：用户私有标签，支持分类管理（如：工作、个人、紧急）
- **协作与分享**：与其他用户分享待办事项并授予细粒度权限
- **基于角色的访问控制 (RBAC)**：三级权限——所有者（完全控制）、编辑者（可更新）、查看者（只读）
- **活动记录**：不可变审计日志，记录所有待办事项变更
- **团队管理**：创建团队并管理成员，支持管理员/成员角色
- **JWT 认证**：基于 Token 的安全认证，24小时过期

---

## 🛠 Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Language** | Java | 21 (LTS) |
| **Framework** | Spring Boot | 3.3.5 |
| **Database** | MySQL | 9.5 |
| **ORM** | Spring Data JPA (Hibernate) | 3.3.5 |
| **Migration** | Flyway | 10.10.0 |
| **Security** | Spring Security + jjwt | 6.2.1 + 0.12.6 |
| **API Documentation** | SpringDoc OpenAPI (Swagger UI) | 2.6.0 |
| **Build Tool** | Maven | 3.9.x |
| **Testing** | JUnit 5 + Mockito + Testcontainers | 5.11.4 |

### 技术栈

| 层级 | 技术栈 | 版本 |
|------|--------|------|
| **编程语言** | Java | 21 (LTS) |
| **框架** | Spring Boot | 3.3.5 |
| **数据库** | MySQL | 9.5 |
| **ORM** | Spring Data JPA (Hibernate) | 3.3.5 |
| **数据库迁移** | Flyway | 10.10.0 |
| **安全** | Spring Security + jjwt | 6.2.1 + 0.12.6 |
| **API 文档** | SpringDoc OpenAPI (Swagger UI) | 2.6.0 |
| **构建工具** | Maven | 3.9.x |
| **测试框架** | JUnit 5 + Mockito + Testcontainers | 5.11.4 |

---

## 🏗 Architecture

### DDD 4-Layer Architecture

```
com.sleekflow/
├── interfaces/          # REST Controllers + DTOs + Assemblers
│   ├── rest/           # Controllers: Handle HTTP requests
│   ├── dto/            # Data Transfer Objects: Request/Response DTOs
│   └── assembler/      # Assemblers: DTO ↔ Entity conversion
├── application/         # Use Cases: Business logic orchestration
│   ├── todo/           # Todo use cases
│   ├── auth/           # Authentication use cases
│   └── team/           # Team use cases
├── domain/             # Domain Layer: Core business logic
│   ├── todo/           # Todo aggregate root
│   ├── user/           # User entity
│   └── team/           # Team aggregate root
└── infrastructure/     # Infrastructure Layer: Persistence, security, config
    ├── persistence/    # Data Access Layer (Repositories)
    ├── security/       # Security module (JWT, Filters)
    └── config/         # Configuration classes
```

### Design Patterns

- **Domain-Driven Design (DDD)**
- **Repository Pattern**
- **Assembler Pattern**
- **Dependency Injection**
- **Builder Pattern**

### 架构设计

#### DDD 四层架构

```
com.sleekflow/
├── interfaces/          # REST 控制器 + DTO + 组装器
│   ├── rest/           # 控制器层：处理 HTTP 请求
│   ├── dto/            # 数据传输对象：请求/响应 DTO
│   └── assembler/      # 对象组装器：DTO ↔ Entity 转换
├── application/         # 用例层：业务逻辑编排
│   ├── todo/           # 待办事项用例
│   ├── auth/           # 认证用例
│   └── team/           # 团队用例
├── domain/             # 领域层：核心业务逻辑
│   ├── todo/           # 待办事项聚合根
│   ├── user/           # 用户实体
│   └── team/           # 团队聚合根
└── infrastructure/     # 基础设施层：持久化、安全、配置
    ├── persistence/    # 数据访问层（Repository）
    ├── security/       # 安全模块（JWT、Filter）
    └── config/         # 配置类
```

#### 设计模式

- **领域驱动设计 (DDD)**
- **仓储模式**
- **组装器模式**
- **依赖注入**
- **构建器模式**

---

## 🚀 Quick Start

### Prerequisites

- **Java 21+** ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **MySQL 9.5+** or Docker ([Download](https://www.mysql.com/downloads/))

---

### Option 1: Maven + Local MySQL

**For:** Using local MySQL installation

```bash
# 1. Clone the repository
git clone <repository-url>
cd todoLists

# 2. Create database
mysql -u root -p -e "CREATE DATABASE todolist_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. Configure MySQL credentials
# Edit src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/todolist_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# 4. Build and run
mvn clean spring-boot:run

# 5. Access the application
open http://localhost:8080/swagger-ui.html
```

---

### Option 2: IDEA + Docker MySQL (Recommended for Development)

**For:** Development in IDEA with Docker MySQL

```bash
# 1. Start MySQL container only
docker compose up -d mysql

# 2. Wait for MySQL to be ready
docker compose logs -f mysql  # Press Ctrl+C when you see "ready for connections"

# 3. Run application in IDEA
#    - Open TodoListApplication.java
#    - Click Run (green triangle)
#    Or use Maven: mvn spring-boot:run -Dspring-boot.run.profiles=local

# 4. Verify application is running
curl http://localhost:8080/actuator/health
```

**Port Configuration:**
- Docker MySQL: `localhost:3307` (Avoids conflict with local MySQL)
- Spring Boot: `localhost:8080`

**Stop Services:**
```bash
# Stop MySQL container (data persists in volume)
docker compose down
```

---

### Option 3: Docker Compose (Full Containerization)

**For:** Fully containerized deployment, isolated environment

```bash
# 1. Build and start all services
docker compose up --build

# 2. Wait for services to be healthy
docker compose ps  # All services should show "Up"

# 3. Access the application
curl http://localhost:8080/actuator/health

# 4. Stop all services
docker compose down
```

### 快速开始

#### 前置条件

- **Java 21+** ([下载](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.9+** ([下载](https://maven.apache.org/download.cgi))
- **MySQL 9.5+** 或 Docker ([下载](https://www.mysql.com/downloads/))

---

#### 方式一：Maven + 本地 MySQL

**适用场景：** 使用本地已安装的 MySQL

```bash
# 1. 克隆仓库
git clone <repository-url>
cd todoLists

# 2. 创建数据库
mysql -u root -p -e "CREATE DATABASE todolist_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. 配置数据库连接
# 编辑 src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/todolist_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# 4. 构建并运行
mvn clean spring-boot:run

# 5. 访问应用
open http://localhost:8080/swagger-ui.html
```

---

#### 方式二：IDEA + Docker MySQL（开发推荐）

**适用场景：** 在 IDEA 中开发，使用 Docker 运行 MySQL

```bash
# 1. 仅启动 MySQL 容器
docker compose up -d mysql

# 2. 等待 MySQL 就绪
docker compose logs -f mysql  # 看到 "ready for connections" 时按 Ctrl+C

# 3. 在 IDEA 中运行应用
#    - 打开 TodoListApplication.java
#    - 点击运行（绿色三角形）
#    或使用 Maven: mvn spring-boot:run -Dspring-boot.run.profiles=local

# 4. 验证应用运行
curl http://localhost:8080/actuator/health
```

**端口配置：**
- Docker MySQL: `localhost:3307`（避免与本地 MySQL 冲突）
- Spring Boot: `localhost:8080`

**停止服务：**
```bash
# 停止 MySQL 容器（数据持久化到卷）
docker compose down
```

---

#### 方式三：Docker Compose（完全容器化）

**适用场景：** 完全容器化部署，隔离环境

```bash
# 1. 构建并启动所有服务
docker compose up --build

# 2. 等待服务健康
docker compose ps  # 所有服务应显示 "Up"

# 3. 访问应用
curl http://localhost:8080/actuator/health

# 4. 停止所有服务
docker compose down
```

---

## 📚 API Documentation

### Swagger UI (Interactive Documentation)

**Access URL:** `http://localhost:8080/swagger-ui.html`

**Features:**
- Browse all API endpoints
- Try out APIs directly from browser
- View request/response schemas
- Authentication with JWT Bearer Token

**How to Use:**
1. Open Swagger UI
2. Click "Authorize" button
3. Enter JWT token (format: `Bearer your-token`)
4. Try any endpoint

---

### API Endpoints Summary

#### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login and get JWT token |

#### Todo Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/todos` | List todos with filtering |
| POST | `/api/v1/todos` | Create new todo |
| GET | `/api/v1/todos/{id}` | Get todo details |
| PUT | `/api/v1/todos/{id}` | Update todo |
| DELETE | `/api/v1/todos/{id}` | Delete todo (soft delete) |

#### Collaboration

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/todos/{id}/share` | Share todo with user |
| GET | `/api/v1/todos/{id}/permissions` | List permissions |
| DELETE | `/api/v1/todos/{id}/share/{userId}` | Revoke access |
| GET | `/api/v1/todos/{id}/activities` | Get activity feed |

#### Tags

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/tags` | List user's tags |
| POST | `/api/v1/tags` | Create new tag |
| DELETE | `/api/v1/tags/{id}` | Delete tag |

#### Teams

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/teams` | List user's teams |
| POST | `/api/v1/teams` | Create team |
| GET | `/api/v1/teams/{id}` | Get team details |
| PUT | `/api/v1/teams/{id}` | Update team |
| DELETE | `/api/v1/teams/{id}` | Delete team |
| GET | `/api/v1/teams/{id}/members` | List team members |
| POST | `/api/v1/teams/{id}/members` | Add team member |
| DELETE | `/api/v1/teams/{id}/members/{userId}` | Remove team member |

---

### 📮 Postman Collection

**Location:**
```
postman/
├── SleekFlow-TodoAPI.postman_collection.json   # API requests collection
└── SleekFlow-Local.postman_environment.json     # Local environment variables
```

#### Features

✅ **Pre-configured Requests**
- All API endpoints with example payloads
- Organized by feature folders

✅ **Auto-Save JWT Token**
- Login request automatically saves token to environment
- All subsequent requests use the saved token

✅ **Complete User Story**
- Register → Login → Create → Share → Update → Delete
- Demonstrates RBAC with multiple users

#### How to Use

##### Step 1: Import Collection

1. Open Postman
2. Click **Import**
3. Select files:
   - `postman/SleekFlow-TodoAPI.postman_collection.json`
   - `postman/SleekFlow-Local.postman_environment.json`

##### Step 2: Run Auth Folder

1. Open **Auth** folder
2. Run requests in order:
   - `Register Alice` → Register Alice user
   - `Register Bob` → Register Bob user
   - `Login Alice` → Login and get JWT

✅ **Important:** The `Login Alice` request automatically saves the JWT token to the environment

##### Step 3: Test Todo CRUD

1. Open **Todos** folder
2. Run requests:
   - `Create Todo` → Creates a todo for Alice
   - `List Todos` → List all Alice's todos
   - `Get Todo` → Get todo details
   - `Update Todo` → Update todo

##### Step 4: Test RBAC

1. Open **RBAC** folder
2. Run requests:
   - `Share Todo (Owner → Viewer)` → Share with Bob as Viewer
   - `Bob Tries to Update (FORBIDDEN)` → Bob tries to update (403)
   - `Share Todo (Owner → Editor)` → Re-share with Bob as Editor
   - `Bob Updates Todo (SUCCESS)` → Bob updates todo (200)

##### Step 5: Test Activity Feed

1. Open **Activity Feed** folder
2. Run `Get Activities` request
3. Verify all operations are logged

##### Step 6: Test Teams

1. Open **Teams** folder
2. Run requests:
   - `Create Team` → Create a team
   - `Add Member` → Add Bob as member
   - `List Members` → View team members

### API 文档

#### Swagger UI（交互式文档）

**访问地址：** `http://localhost:8080/swagger-ui.html`

**功能：**
- 浏览所有 API 端点
- 直接在浏览器中测试 API
- 查看请求/响应模式
- JWT Bearer Token 认证

**使用方法：**
1. 打开 Swagger UI
2. 点击 "Authorize" 按钮
3. 输入 JWT Token（格式：`Bearer your-token`）
4. 测试任何端点

---

#### API 端点概览

##### 认证

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/v1/auth/register` | 注册新用户 |
| POST | `/api/v1/auth/login` | 登录并获取 JWT |

##### 待办事项管理

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/v1/todos` | 查询待办事项（支持过滤） |
| POST | `/api/v1/todos` | 创建待办事项 |
| GET | `/api/v1/todos/{id}` | 查询待办事项详情 |
| PUT | `/api/v1/todos/{id}` | 更新待办事项 |
| DELETE | `/api/v1/todos/{id}` | 删除待办事项（软删除） |

##### 协作功能

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/v1/todos/{id}/share` | 分享待办事项给用户 |
| GET | `/api/v1/todos/{id}/permissions` | 查询权限列表 |
| DELETE | `/api/v1/todos/{id}/share/{userId}` | 撤销访问权限 |
| GET | `/api/v1/todos/{id}/activities` | 查询活动记录 |

##### 标签

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/v1/tags` | 查询用户的所有标签 |
| POST | `/api/v1/tags` | 创建新标签 |
| DELETE | `/api/v1/tags/{id}` | 删除标签 |

##### 团队

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/v1/teams` | 查询用户的团队列表 |
| POST | `/api/v1/teams` | 创建团队 |
| GET | `/api/v1/teams/{id}` | 查询团队详情 |
| PUT | `/api/v1/teams/{id}` | 更新团队 |
| DELETE | `/api/v1/teams/{id}` | 删除团队 |
| GET | `/api/v1/teams/{id}/members` | 查询团队成员 |
| POST | `/api/v1/teams/{id}/members` | 添加团队成员 |
| DELETE | `/api/v1/teams/{id}/members/{userId}` | 移除团队成员 |

---

### 📮 Postman 集合

**文件位置：**
```
postman/
├── SleekFlow-TodoAPI.postman_collection.json   # API 请求集合
└── SleekFlow-Local.postman_environment.json     # 本地环境变量
```

#### 功能特性

✅ **预配置的请求**
- 所有 API 端点附带示例请求体
- 按功能模块组织

✅ **自动保存 JWT Token**
- 登录请求自动保存 Token 到环境
- 后续请求自动使用保存的 Token

✅ **完整的用户故事**
- 注册 → 登录 → 创建 → 分享 → 更新 → 删除
- 通过多用户演示 RBAC

#### 使用方法

##### 步骤 1：导入集合

1. 打开 Postman
2. 点击 **Import**（导入）
3. 选择文件：
   - `postman/SleekFlow-TodoAPI.postman_collection.json`
   - `postman/SleekFlow-Local.postman_environment.json`

##### 步骤 2：运行认证模块

1. 打开 **Auth** 文件夹
2. 按顺序运行请求：
   - `Register Alice` → 注册 Alice 用户
   - `Register Bob` → 注册 Bob 用户
   - `Login Alice` → 登录并获取 JWT

✅ **重要：** `Login Alice` 请求会自动保存 JWT Token 到环境变量

##### 步骤 3：测试待办事项 CRUD

1. 打开 **Todos** 文件夹
2. 运行请求：
   - `Create Todo` → 为 Alice 创建待办事项
   - `List Todos` → 查询 Alice 的所有待办事项
   - `Get Todo` → 查询待办事项详情
   - `Update Todo` → 更新待办事项

##### 步骤 4：测试 RBAC

1. 打开 **RBAC** 文件夹
2. 运行请求：
   - `Share Todo (Owner → Viewer)` → 以 Viewer 角色分享给 Bob
   - `Bob Tries to Update (FORBIDDEN)` → Bob 尝试更新（403 禁止）
   - `Share Todo (Owner → Editor)` → 以 Editor 角色重新分享
   - `Bob Updates Todo (SUCCESS)` → Bob 更新待办事项（成功）

##### 步骤 5：测试活动记录

1. 打开 **Activity Feed** 文件夹
2. 运行 `Get Activities` 请求
3. 验证所有操作都被记录

##### 步骤 6：测试团队功能

1. 打开 **Teams** 文件夹
2. 运行请求：
   - `Create Team` → 创建团队
   - `Add Member` → 添加 Bob 为成员
   - `List Members` → 查看团队成员

---

## 📊 Data Model

### Core Entities

#### Todo

```
┌─────────────────────────────────────────────┐
│ Todo                                        │
├─────────────────────────────────────────────┤
│ id: UUID (PK)                               │
│ name: String (required)                      │
│ description: Text (optional)                 │
│ status: NOT_STARTED | IN_PROGRESS | COMPLETED│
│ priority: LOW | MEDIUM | HIGH                │
│ dueDate: DateTime (optional)                 │
│ owner: User (FK) → users.id                 │
│ tags: Set<Tag> (Many-to-Many)               │
│ permissions: Set<TodoPermission> (One-to-Many)│
│ activities: Set<ActivityFeed> (One-to-Many)  │
│ createdAt: LocalDateTime                   │
│ updatedAt: LocalDateTime                   │
│ deletedAt: LocalDateTime (Soft Delete)      │
└─────────────────────────────────────────────┘
```

#### Permission Levels

| Role | Create | Read | Update | Delete | Share |
|-------|:------:|:----:|:------:|:------:|:-----:|
| **OWNER** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **EDITOR** | ❌ | ✅ | ✅ | ❌ | ❌ |
| **VIEWER** | ❌ | ✅ | ❌ | ❌ | ❌ |

### 数据模型

#### 核心实体

##### 待办事项

```
┌─────────────────────────────────────────────┐
│ Todo                                        │
├─────────────────────────────────────────────┤
│ id: UUID (主键)                             │
│ name: String (必填)                          │
│ description: Text (可选)                      │
│ status: NOT_STARTED | IN_PROGRESS | COMPLETED│
│ priority: LOW | MEDIUM | HIGH                │
│ dueDate: DateTime (可选)                     │
│ owner: User (外键) → users.id                 │
│ tags: Set<Tag> (多对多)                       │
│ permissions: Set<TodoPermission> (一对多)     │
│ activities: Set<ActivityFeed> (一对多)         │
│ createdAt: LocalDateTime                   │
│ updatedAt: LocalDateTime                   │
│ deletedAt: LocalDateTime (软删除)            │
└─────────────────────────────────────────────┘
```

#### 权限级别

| 角色 | 创建 | 读取 | 更新 | 删除 | 分享 |
|------|:----:|:----:|:----:|:----:|:----:|
| **所有者** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **编辑者** | ❌ | ✅ | ✅ | ❌ | ❌ |
| **查看者** | ❌ | ✅ | ❌ | ❌ | ❌ |

---

## 🔐 Authentication & Authorization

### JWT Authentication Flow

```
┌─────────┐                ┌─────────────┐                ┌─────────┐
│ Client  │──Register/Login──▶│  Auth API   │──Return JWT───▶│ Client  │
│(Browser) │                  │(Spring Security)              │         │
└─────────┘                  └─────────────┘                └─────────┘
                                                          │
                                                          ▼
                                                 ┌─────────────────────┐
                                                 │ Store JWT Token     │
                                                 │ (LocalStorage/Cookie)│
                                                 └─────────────────────┘
                                                          │
                     ┌─────────────────────────────────────────────┼────────────────┐
                     │                                             │                │
                     ▼                                             ▼                ▼
              ┌─────────────┐                           ┌─────────────┐  ┌──────────┐
              │ API Request  │                           │API Request  │  │API Request│
              │+ JWT Token   │                           │+ JWT Token   │  │No Token   │
              └──────┬───────┘                           └──────┬───────┘  └─────┬────┘
                     │                                         │               │
                     ▼                                         ▼               ▼
              ┌─────────────┐                           ┌─────────────┐  ┌──────────┐
              │ JwtAuthFilter│                           │JwtAuthFilter │  │401       │
              │Validates JWT │                           │Validates JWT │  │Unauthorized│
              └──────┬───────┘                           └──────┬───────┘  └──────────┘
                     │                                         │
                     ▼                                         ▼
              ┌─────────────┐                           ┌─────────────┐
              │ Controller   │                           │Controller   │
              └─────────────┘                           └─────────────┘
```

### Permission Check Flow

```
User requests GET /api/v1/todos/{id}
           │
           ▼
    ┌──────────────────┐
    │ Security Context  │
    │ (UserContext)     │
    └────────┬─────────┘
             │
             ▼
    ┌─────────────────────┐
    │ TodoService.getTodo() │
    │                       │
    │ 1. Is owner? ✅       │──▶ Full Access
    │ 2. Has permission? ✅ │──▶ Full Access
    │ 3. Neither ❌          │──▶ 403 Forbidden
    └─────────────────────┘
```

### 认证与授权

#### JWT 认证流程

```
┌─────────┐                ┌─────────────┐                ┌─────────┐
│ 客户端  │──注册/登录──────▶│  认证 API    │──返回 JWT─────▶│ 客户端  │
│(浏览器) │                  │(Spring Security)              │         │
└─────────┘                  └─────────────┘                └─────────┘
                                                          │
                                                          ▼
                                                 ┌─────────────────────┐
                                                 │ 存储 JWT Token      │
                                                 │ (LocalStorage/Cookie)│
                                                 └─────────────────────┘
                                                          │
                     ┌─────────────────────────────────────────────┼────────────────┐
                     │                                             │                │
                     ▼                                             ▼                ▼
              ┌─────────────┐                           ┌─────────────┐  ┌──────────┐
              │ API 请求     │                           │ API 请求     │  │API 请求  │
              │+ JWT Token   │                           │+ JWT Token   │  │无 Token  │
              └──────┬───────┘                           └──────┬───────┘  └─────┬────┘
                     │                                         │               │
                     ▼                                         ▼               ▼
              ┌─────────────┐                           ┌─────────────┐  ┌──────────┐
              │Jwt认证过滤器 │                           │Jwt认证过滤器 │  │401       │
              │验证 JWT      │                           │验证 JWT      │  │未授权    │
              └──────┬───────┘                           └──────┬───────┘  └──────────┘
                     │                                         │
                     ▼                                         ▼
              ┌─────────────┐                           ┌─────────────┐
              │ 控制器       │                           │ 控制器       │
              └─────────────┘                           └─────────────┘
```

#### 权限检查流程

```
用户请求 GET /api/v1/todos/{id}
           │
           ▼
    ┌──────────────────┐
    │ 安全上下文       │
    │ (UserContext)     │
    └────────┬─────────┘
             │
             ▼
    ┌─────────────────────┐
    │TodoService.getTodo() │
    │                       │
    │ 1. 是所有者? ✅       │──▶ 完全访问
    │ 2. 有权限? ✅        │──▶ 完全访问
    │ 3. 都不是 ❌         │──▶ 403 禁止访问
    └─────────────────────┘
```

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

**Expected Output:**
```
Tests run: 89, Failures: 0, Errors: 0, Skipped: 9
BUILD SUCCESS
```

### Test Coverage

| Layer | Test Type | Count | Coverage |
|-------|-----------|-------|----------|
| **Service** | Unit Tests (Mockito) | 43 | ~90% |
| **Controller** | Integration Tests (MockMvc) | 37 | ~85% |
| **Repository** | Integration Tests (Testcontainers) | 8 | ~80% |
| **Total** | | **89** | **~80%+** |

### Run Specific Test

```bash
# Run all tests in a class
mvn test -Dtest=TodoServiceTest

# Run a specific test method
mvn test -Dtest=TodoServiceTest#createTodo

# Run tests matching a pattern
mvn test -Dtest=*ServiceTest
```

### 测试

#### 运行所有测试

```bash
mvn test
```

**预期输出：**
```
Tests run: 89, Failures: 0, Errors: 0, Skipped: 9
BUILD SUCCESS
```

#### 测试覆盖率

| 层级 | 测试类型 | 数量 | 覆盖率 |
|------|----------|------|--------|
| **Service** | 单元测试 | 43 | ~90% |
| **Controller** | 集成测试 | 37 | ~85% |
| **Repository** | 集成测试 | 8 | ~80% |
| **总计** | | **89** | **~80%+** |

#### 运行特定测试

```bash
# 运行某个类的所有测试
mvn test -Dtest=TodoServiceTest

# 运行某个测试方法
mvn test -Dtest=TodoServiceTest#createTodo

# 运行匹配模式的测试
mvn test -Dtest=*ServiceTest
```

---

## 🐳 Docker Deployment

### Docker Compose Services

| Service | Port | Description |
|---------|------|-------------|
| **App** | 8080 | Spring Boot application |
| **MySQL** | 3307 (host) / 3306 (container) | MySQL 9.5 database |

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `MYSQL_HOST` | `mysql` | MySQL host (service name) |
| `MYSQL_PORT` | `3306` | MySQL port |
| `MYSQL_DATABASE` | `todolist_db` | Database name |
| `MYSQL_USER` | `sleekflow` | Database user |
| `MYSQL_PASSWORD` | `sleekflow` | Database password |
| `JWT_SECRET` | `sleekflow-super-secret-...` | JWT signing key |

### Production Deployment Tips

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  app:
    image: sleekflow/todo-api:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - MYSQL_HOST=mysql-prod.internal
      - JWT_SECRET=${JWT_SECRET}  # Use strong random key
    restart: unless-stopped

  mysql:
    image: mysql:9.5
    volumes:
      - mysql_data:/var/lib/mysql
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
    restart: unless-stopped

volumes:
  mysql_data:
```

### Docker 部署

#### Docker Compose 服务

| 服务 | 端口 | 描述 |
|------|------|------|
| **应用** | 8080 | Spring Boot 应用 |
| **MySQL** | 3307 (宿主机) / 3306 (容器) | MySQL 9.5 数据库 |

#### 环境变量

| 变量 | 默认值 | 描述 |
|------|--------|------|
| `MYSQL_HOST` | `mysql` | MySQL 主机名（服务名） |
| `MYSQL_PORT` | `3306` | MySQL 端口 |
| `MYSQL_DATABASE` | `todolist_db` | 数据库名称 |
| `MYSQL_USER` | `sleekflow` | 数据库用户 |
| `MYSQL_PASSWORD` | `sleekflow` | 数据库密码 |
| `JWT_SECRET` | `sleekflow-super-secret-...` | JWT 签名密钥 |

#### 生产部署建议

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  app:
    image: sleekflow/todo-api:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - MYSQL_HOST=mysql-prod.internal
      - JWT_SECRET=${JWT_SECRET}  # 使用强随机密钥
    restart: unless-stopped

  mysql:
    image: mysql:9.5
    volumes:
      - mysql_data:/var/lib/mysql
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
    restart: unless-stopped

volumes:
  mysql_data:
```

---

## ⚙️ Configuration

### Application Properties

#### Database Configuration

```properties
# MySQL Connection
spring.datasource.url=jdbc:mysql://localhost:3307/todolist_db?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=sleekflow
spring.datasource.password=sleekflow
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.open-in-view=false
```

#### JWT Configuration

```properties
# JWT Settings
app.jwt.secret=sleekflow-super-secret-key-change-in-production-at-least-256-bits!!
app.jwt.expiration-ms=86400000  # 24 hours
```

#### Flyway Configuration

```properties
# Database Migration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=false
```

### 配置参考

#### 应用配置

##### 数据库配置

```properties
# MySQL 连接
spring.datasource.url=jdbc:mysql://localhost:3307/todolist_db?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=sleekflow
spring.datasource.password=sleekflow
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.open-in-view=false
```

##### JWT 配置

```properties
# JWT 设置
app.jwt.secret=sleekflow-super-secret-key-change-in-production-at-least-256-bits!!
app.jwt.expiration-ms=86400000  # 24 小时
```

##### Flyway 配置

```properties
# 数据库迁移
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=false
```

---

## 📖 Demo Story

### Complete User Journey

#### Step 1: User Registration

```bash
# Register Alice
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "alice@example.com",
  "username": "alice",
  "password": "Pass123!"
}

# Response
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "username": "alice",
      "email": "alice@example.com"
    }
  }
}
```

#### Step 2: Create Todo

```bash
# Create todo
POST /api/v1/todos
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "name": "Review PR #42",
  "description": "Review and merge PR #42 for feature X",
  "priority": "HIGH",
  "dueDate": "2026-06-15T10:00:00",
  "tags": ["work", "urgent"]
}
```

#### Step 3: Share with Bob

```bash
# Share todo with Bob
POST /api/v1/todos/550e8400-e29b-41d4-a716-446655440000/share
Authorization: Bearer <alice-token>

{
  "userId": "<bob-id>",
  "role": "EDITOR"
}
```

#### Step 4: Bob Updates Todo

```bash
# Bob updates todo (allowed as EDITOR)
PUT /api/v1/todos/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <bob-token>

{
  "name": "Review PR #42 - In Progress",
  "status": "IN_PROGRESS"
}
```

#### Step 5: View Activity Feed

```bash
# View all activities
GET /api/v1/todos/550e8400-e29b-41d4-a716-446655440000/activities
Authorization: Bearer <alice-token>

# Response
{
  "success": true,
  "data": {
    "content": [
      {
        "actor": "alice",
        "action": "TODO_CREATED",
        "payload": "{\"name\":\"Review PR #42\"}",
        "createdAt": "2026-03-02T10:00:00"
      },
      {
        "actor": "alice",
        "action": "TODO_SHARED",
        "payload": "{\"targetUser\":\"bob\",\"role\":\"EDITOR\"}",
        "createdAt": "2026-03-02T10:05:00"
      },
      {
        "actor": "bob",
        "action": "TODO_UPDATED",
        "payload": "{\"status\":\"IN_PROGRESS\"}",
        "createdAt": "2026-03-02T10:10:00"
      }
    ]
  }
}
```

### 演示场景

#### 完整用户流程

##### 步骤 1：用户注册

```bash
# 注册 Alice
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "alice@example.com",
  "username": "alice",
  "password": "Pass123!"
}

# 响应
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "username": "alice",
      "email": "alice@example.com"
    }
  }
}
```

##### 步骤 2：创建待办事项

```bash
# 创建待办事项
POST /api/v1/todos
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "name": "Review PR #42",
  "description": "Review and merge PR #42 for feature X",
  "priority": "HIGH",
  "dueDate": "2026-06-15T10:00:00",
  "tags": ["work", "urgent"]
}
```

##### 步骤 3：分享给 Bob

```bash
# 分享待办事项给 Bob
POST /api/v1/todos/550e8400-e29b-41d4-a716-446655440000/share
Authorization: Bearer <alice-token>

{
  "userId": "<bob-id>",
  "role": "EDITOR"
}
```

##### 步骤 4：Bob 更新待办事项

```bash
# Bob 更新待办事项（EDITOR 权限允许）
PUT /api/v1/todos/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <bob-token>

{
  "name": "Review PR #42 - In Progress",
  "status": "IN_PROGRESS"
}
```

##### 步骤 5：查看活动记录

```bash
# 查看所有活动
GET /api/v1/todos/550e8400-e29b-41d4-a716-446655440000/activities
Authorization: Bearer <alice-token>

# 响应
{
  "success": true,
  "data": {
    "content": [
      {
        "actor": "alice",
        "action": "TODO_CREATED",
        "payload": "{\"name\":\"Review PR #42\"}",
        "createdAt": "2026-03-02T10:00:00"
      },
      {
        "actor": "alice",
        "action": "TODO_SHARED",
        "payload": "{\"targetUser\":\"bob\",\"role\":\"EDITOR\"}",
        "createdAt": "2026-03-02T10:05:00"
      },
      {
        "actor": "bob",
        "action": "TODO_UPDATED",
        "payload": "{\"status\":\"IN_PROGRESS\"}",
        "createdAt": "2026-03-02T10:10:00"
      }
    ]
  }
}
```

---

## 📈 Project Status

### Completed Phases

✅ **Phase 0-8: Core Features**
- Domain-driven design architecture
- Full CRUD operations for todos, tags, teams
- JWT authentication and authorization
- Activity feed for audit trail
- Role-based access control (RBAC)
- API documentation with Swagger UI
- Docker deployment ready
- 80+ tests with 80%+ coverage

### Code Quality

- ✅ **Clean Code**: Follows SOLID principles
- ✅ **Well-Documented**: Chinese-English Javadoc
- ✅ **Database Comments**: All tables and columns have COMMENT
- ✅ **Comprehensive Tests**: Unit + Integration tests

---

## 📄 License

© 2025 SleekFlow. All rights reserved.

---

## 📞 Support

For questions or issues:
- **GitHub Issues**: [Project Issues](https://github.com/your-repo/issues)
- **Email**: support@sleekflow.com

---

**Built with ❤️ by SleekFlow Team**
