# SleekFlow TODO List API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-9.5-blue.svg)](https://www.mysql.com)

A real-time collaborative TODO list REST API built with Java 21, Spring Boot 3.3.5, and MySQL

---

## Features

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
- **Input & Data Integrity Hardening**: Strict pagination/sorting validation, tag ownership checks, and 409 mapping for DB constraint conflicts

---

## Tech Stack

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

---

## Architecture

### DDD 4-Layer Architecture

```
com.sleekflow/
├── interfaces/          # REST Controllers + DTOs + Assemblers
│   ├── rest/           # Controllers: Handle HTTP requests
│   ├── dto/            # Data Transfer Objects: Request/Response DTOs
│   └── assembler/      # Assemblers: DTO <-> Entity conversion
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

---

## Quick Start

### Prerequisites

- **Java 21+** ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **MySQL 9.5+** or Docker ([Download](https://www.mysql.com/downloads/))

---

### Option 1: Maven + Local MySQL

**For:** Using local MySQL installation

```bash
# 1. Clone the repository
git clone https://github.com/1ifebytes/todoLists.git
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

---

## API Documentation

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

### Postman Collection

**Location:**
```
postman/
├── SleekFlow-TodoAPI.postman_collection.json   # API requests collection
└── SleekFlow-Local.postman_environment.json     # Local environment variables
```

#### Features

- **Pre-configured Requests**: All API endpoints with example payloads
- **Auto-Save JWT Token**: Login request automatically saves token to environment
- **Complete User Story**: Register -> Login -> Create -> Share -> Update -> Delete

#### How to Use

1. Open Postman and import the collection files
2. Run the Auth folder requests first (Register, Login)
3. Test Todo CRUD operations
4. Test RBAC with multiple users
5. View activity feed for audit trail

---

## Testing

### Run All Tests

```bash
mvn test
```

**Expected Output:**
```
Tests run: 96, Failures: 0, Errors: 0, Skipped: 9
BUILD SUCCESS
```

### Test Coverage

| Layer | Test Type | Count | Coverage |
|-------|-----------|-------|----------|
| **Service** | Unit Tests (Mockito) | 46 | ~90% |
| **Controller** | Integration Tests (MockMvc) | 41 | ~85% |
| **Repository** | Integration Tests (Testcontainers) | 9 | ~80% |
| **Total** | | **96** | **~80%+** |

### Run Specific Test

```bash
# Run all tests in a class
mvn test -Dtest=TodoServiceTest

# Run a specific test method
mvn test -Dtest=TodoServiceTest#createTodo

# Run tests matching a pattern
mvn test -Dtest=*ServiceTest
```

---

## Docker Deployment

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

---

## Data Model

### Core Entities

#### Todo

| Field | Type | Description |
|-------|------|-------------|
| id | UUID (PK) | Unique identifier |
| name | String (required) | Todo name |
| description | Text (optional) | Todo description |
| status | Enum | NOT_STARTED, IN_PROGRESS, COMPLETED |
| priority | Enum | LOW, MEDIUM, HIGH |
| dueDate | DateTime (optional) | Due date |
| owner | User (FK) | Todo owner |
| tags | Set<Tag> (Many-to-Many) | Associated tags |
| permissions | Set<TodoPermission> (One-to-Many) | Share permissions |
| activities | Set<ActivityFeed> (One-to-Many) | Activity log |
| createdAt | LocalDateTime | Creation timestamp |
| updatedAt | LocalDateTime | Last update timestamp |
| deletedAt | LocalDateTime | Soft delete timestamp |

#### Permission Levels

| Role | Create | Read | Update | Delete | Share |
|-------|:------:|:----:|:------:|:------:|:-----:|
| **OWNER** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **EDITOR** | ❌ | ✅ | ✅ | ❌ | ❌ |
| **VIEWER** | ❌ | ✅ | ❌ | ❌ | ❌ |

---

## Authentication & Authorization

### JWT Authentication Flow

1. User registers or logs in via `/api/v1/auth/register` or `/api/v1/auth/login`
2. Server returns a JWT token (24-hour expiration)
3. Client stores the token (localStorage/cookie)
4. Client includes the token in the `Authorization` header for all subsequent requests
5. `JwtAuthenticationFilter` validates the token and sets up security context
6. Service layer checks permissions before executing operations

### Permission Check Flow

When a user requests access to a todo:
1. System checks if the user is the owner (full access)
2. If not owner, checks if user has shared permissions
3. If neither, returns 403 Forbidden

---

## Configuration

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

---

## Demo Story

### Complete User Journey

#### Step 1: User Registration

```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "alice@example.com",
  "username": "alice",
  "password": "Pass123!"
}
```

#### Step 2: Create Todo

```bash
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
POST /api/v1/todos/{todo-id}/share
Authorization: Bearer <alice-token>

{
  "userId": "<bob-id>",
  "role": "EDITOR"
}
```

#### Step 4: Bob Updates Todo (EDITOR permission allows)

```bash
PUT /api/v1/todos/{todo-id}
Authorization: Bearer <bob-token>

{
  "name": "Review PR #42 - In Progress",
  "status": "IN_PROGRESS"
}
```

#### Step 5: View Activity Feed

```bash
GET /api/v1/todos/{todo-id}/activities
Authorization: Bearer <alice-token>
```

---

## Project Status

### Completed Features

- ✅ Domain-driven design architecture
- ✅ Full CRUD operations for todos, tags, teams
- ✅ JWT authentication and authorization
- ✅ Activity feed for audit trail
- ✅ Role-based access control (RBAC)
- ✅ API documentation with Swagger UI
- ✅ Docker deployment ready
- ✅ 80+ tests with 80%+ coverage

### Code Quality

- ✅ **Clean Code**: Follows SOLID principles
- ✅ **Well-Documented**: Chinese-English Javadoc
- ✅ **Database Comments**: All tables and columns have COMMENT
- ✅ **Comprehensive Tests**: Unit + Integration tests

---

---

# SleekFlow 待办事项 API (中文版)

实时协作待办事项 REST API，基于 Java 21、Spring Boot 3.3.5 和 MySQL 构建

---

## 功能特性

### 核心功能

- **待办事项增删改查**：创建、查询、更新、删除待办事项，支持优先级、截止日期和标签
- **高级过滤**：按状态、优先级、日期范围、标签、所有权等多维度过滤
- **软删除**：待办事项采用软删除机制，数据可恢复
- **优先级管理**：三个优先级：高、中、低

### 高级功能

- **用户标签**：用户私有标签，支持分类管理（如：工作、个人、紧急）
- **协作与分享**：与其他用户分享待办事项并授予细粒度权限
- **基于角色的访问控制 (RBAC)**：三级权限——所有者（完全控制）、编辑者（可更新）、查看者（只读）
- **活动记录**：不可变审计日志，记录所有待办事项变更
- **团队管理**：创建团队并管理成员，支持管理员/成员角色
- **JWT 认证**：基于 Token 的安全认证，24小时过期

---

## 技术栈

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

## 架构设计

### DDD 四层架构

```
com.sleekflow/
├── interfaces/          # REST 控制器 + DTO + 组装器
│   ├── rest/           # 控制器层：处理 HTTP 请求
│   ├── dto/            # 数据传输对象：请求/响应 DTO
│   └── assembler/      # 对象组装器：DTO <-> Entity 转换
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

---

## 快速开始

### 前置条件

- **Java 21+** ([下载](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.9+** ([下载](https://maven.apache.org/download.cgi))
- **MySQL 9.5+** 或 Docker ([下载](https://www.mysql.com/downloads/))

---

### 方式一：Maven + 本地 MySQL

**适用场景：** 使用本地已安装的 MySQL

```bash
# 1. 克隆仓库
git clone https://github.com/1ifebytes/todoLists.git
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

### 方式二：IDEA + Docker MySQL（开发推荐）

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
docker compose down
```

---

### 方式三：Docker Compose（完全容器化）

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

## API 文档

### Swagger UI（交互式文档）

**访问地址：** `http://localhost:8080/swagger-ui.html`

**使用方法：**
1. 打开 Swagger UI
2. 点击 "Authorize" 按钮
3. 输入 JWT Token（格式：`Bearer your-token`）
4. 测试任何端点

---

### API 端点概览

#### 认证

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/v1/auth/register` | 注册新用户 |
| POST | `/api/v1/auth/login` | 登录并获取 JWT |

#### 待办事项管理

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/v1/todos` | 查询待办事项（支持过滤） |
| POST | `/api/v1/todos` | 创建待办事项 |
| GET | `/api/v1/todos/{id}` | 查询待办事项详情 |
| PUT | `/api/v1/todos/{id}` | 更新待办事项 |
| DELETE | `/api/v1/todos/{id}` | 删除待办事项（软删除） |

#### 协作功能

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/v1/todos/{id}/share` | 分享待办事项给用户 |
| GET | `/api/v1/todos/{id}/permissions` | 查询权限列表 |
| DELETE | `/api/v1/todos/{id}/share/{userId}` | 撤销访问权限 |
| GET | `/api/v1/todos/{id}/activities` | 查询活动记录 |

#### 标签

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/v1/tags` | 查询用户的所有标签 |
| POST | `/api/v1/tags` | 创建新标签 |
| DELETE | `/api/v1/tags/{id}` | 删除标签 |

#### 团队

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

### Postman 集合

**文件位置：**
```
postman/
├── SleekFlow-TodoAPI.postman_collection.json   # API 请求集合
└── SleekFlow-Local.postman_environment.json     # 本地环境变量
```

#### 功能特性

- **预配置的请求**：所有 API 端点附带示例请求体
- **自动保存 JWT Token**：登录请求自动保存 Token 到环境
- **完整的用户故事**：注册 → 登录 → 创建 → 分享 → 更新 → 删除

#### 使用方法

1. 打开 Postman 并导入集合文件
2. 先运行认证模块的请求（注册、登录）
3. 测试待办事项 CRUD 操作
4. 用多用户测试 RBAC 功能
5. 查看活动记录作为审计日志

---

## 测试

### 运行所有测试

```bash
mvn test
```

**预期输出：**
```
Tests run: 89, Failures: 0, Errors: 0, Skipped: 9
BUILD SUCCESS
```

### 测试覆盖率

| 层级 | 测试类型 | 数量 | 覆盖率 |
|------|----------|------|--------|
| **Service** | 单元测试 | 43 | ~90% |
| **Controller** | 集成测试 | 37 | ~85% |
| **Repository** | 集成测试 | 8 | ~80% |
| **总计** | | **89** | **~80%+** |

---

## Docker 部署

### Docker Compose 服务

| 服务 | 端口 | 描述 |
|------|------|------|
| **应用** | 8080 | Spring Boot 应用 |
| **MySQL** | 3307 (宿主机) / 3306 (容器) | MySQL 9.5 数据库 |

---

## 数据模型

### 核心实体

#### 待办事项

| 字段 | 类型 | 描述 |
|------|------|------|
| id | UUID (主键) | 唯一标识符 |
| name | String (必填) | 待办事项名称 |
| description | Text (可选) | 描述 |
| status | Enum | NOT_STARTED, IN_PROGRESS, COMPLETED |
| priority | Enum | LOW, MEDIUM, HIGH |
| dueDate | DateTime (可选) | 截止日期 |
| owner | User (外键) | 所有者 |
| tags | Set<Tag> (多对多) | 关联标签 |
| permissions | Set<TodoPermission> (一对多) | 分享权限 |
| activities | Set<ActivityFeed> (一对多) | 活动日志 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |
| deletedAt | LocalDateTime | 软删除时间 |

#### 权限级别

| 角色 | 创建 | 读取 | 更新 | 删除 | 分享 |
|------|:----:|:----:|:----:|:----:|:----:|
| **所有者** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **编辑者** | ❌ | ✅ | ✅ | ❌ | ❌ |
| **查看者** | ❌ | ✅ | ❌ | ❌ | ❌ |

---

## 认证与授权

### JWT 认证流程

1. 用户通过 `/api/v1/auth/register` 或 `/api/v1/auth/login` 注册或登录
2. 服务器返回 JWT Token（24小时过期）
3. 客户端存储 Token（localStorage/cookie）
4. 客户端在后续请求的 `Authorization` 头中携带 Token
5. `JwtAuthenticationFilter` 验证 Token 并设置安全上下文
6. Service 层在执行操作前检查权限

### 权限检查流程

当用户请求访问待办事项时：
1. 系统检查用户是否为所有者（完全访问）
2. 如果不是所有者，检查用户是否有分享权限
3. 如果都没有，返回 403 禁止访问

---

## 配置参考

### 应用配置

#### 数据库配置

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

#### JWT 配置

```properties
# JWT 设置
app.jwt.secret=sleekflow-super-secret-key-change-in-production-at-least-256-bits!!
app.jwt.expiration-ms=86400000  # 24 小时
```

#### Flyway 配置

```properties
# 数据库迁移
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=false
```

---

## 演示场景

### 完整用户流程

#### 步骤 1：用户注册

```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "alice@example.com",
  "username": "alice",
  "password": "Pass123!"
}
```

#### 步骤 2：创建待办事项

```bash
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

#### 步骤 3：分享给 Bob

```bash
POST /api/v1/todos/{todo-id}/share
Authorization: Bearer <alice-token>

{
  "userId": "<bob-id>",
  "role": "EDITOR"
}
```

#### 步骤 4：Bob 更新待办事项（EDITOR 权限允许）

```bash
PUT /api/v1/todos/{todo-id}
Authorization: Bearer <bob-token>

{
  "name": "Review PR #42 - In Progress",
  "status": "IN_PROGRESS"
}
```

#### 步骤 5：查看活动记录

```bash
GET /api/v1/todos/{todo-id}/activities
Authorization: Bearer <alice-token>
```

---

## 项目状态

### 已完成功能

- ✅ 领域驱动设计架构
- ✅ 待办事项、标签、团队的完整 CRUD 操作
- ✅ JWT 认证和授权
- ✅ 活动记录审计日志
- ✅ 基于角色的访问控制（RBAC）
- ✅ Swagger UI API 文档
- ✅ Docker 部署支持
- ✅ 80+ 测试，80%+ 覆盖率

### 代码质量

- ✅ **清洁代码**：遵循 SOLID 原则
- ✅ **完善的文档**：中英文 Javadoc
- ✅ **数据库注释**：所有表和字段都有 COMMENT
- ✅ **全面测试**：单元测试 + 集成测试

---

**© 2025 SleekFlow. All rights reserved.**
