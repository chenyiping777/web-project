# tlias 教学管理系统

基于 **Spring Boot 4 + Vue 3** 前后端分离的教学管理系统，支持部门、员工、班级、学员的增删改查操作，提供数据统计可视化（ECharts）和操作日志审计功能。

---

## 技术栈

**后端**

| 技术 | 版本 | 说明 |
| --- | --- | --- |
| Spring Boot | 4.1.0 | 基础框架 |
| MyBatis-Plus | 3.5.17 | ORM 框架，简化 CRUD |
| MySQL | 8.x | 关系型数据库 |
| JWT | - | 用户认证（Token 存于请求头） |
| 阿里云 OSS | v2 SDK | 文件/图片上传存储 |
| Lombok | - | 简化实体类代码 |

**前端**

| 技术 | 版本 | 说明 |
| --- | --- | --- |
| Vue | 3.4 | 渐进式 JavaScript 框架（Composition API + `<script setup>`） |
| Vite | 5.4 | 前端构建工具 |
| vue-router | 4.2 | 路由管理（Hash 模式 + 登录守卫） |
| axios | 1.7 | HTTP 请求库（统一封装 Token 头 / 响应拆包 / 401 跳转） |
| ECharts | 5.5 | 数据可视化图表 |

---

## 项目结构

```
web-project/
├── tlias-entity/          # 实体层：Entity、VO、DTO、Query、枚举
├── tlias-mapper/          # 数据访问层：Mapper 接口 + XML
├── tlias-service/         # 业务逻辑层：Service 接口 + 实现
├── tlias-web-management/  # Web 层：Controller、配置、拦截器、AOP、启动类
│   └── src/main/resources/
│       └── application.yml  # 数据库连接、JWT 密钥等配置（不提交）
├── tlias-ui/              # 前端工程（Vue 3 + Vite）
│   ├── src/
│   │   ├── api/           # 接口调用封装
│   │   ├── components/    # 公共组件
│   │   ├── router/        # 路由配置
│   │   ├── utils/         # 工具函数（axios 封装、日期格式化）
│   │   └── views/         # 页面组件
│   └── package.json
├── API接口文档.md          # 接口文档（28 个接口详细说明）
└── pom.xml                # Maven 父 POM
```

---

## 功能模块

| 模块 | 功能 |
| --- | --- |
| 登录认证 | JWT Token 登录，拦截器校验，前端路由守卫 |
| 部门管理 | 部门列表查询、新增、修改、删除 |
| 员工管理 | 分页查询（支持性别/部门筛选）、新增（含工作经历）、修改、批量删除、详情查询 |
| 班级管理 | 分页查询（支持学科/时间筛选）、查询所有、新增、修改、删除；自动计算开班状态 |
| 学员管理 | 分页查询（支持姓名/班级/学历筛选）、新增、修改、批量删除、详情查询 |
| 数据统计 | 员工性别分布、员工职位分布、学员学历分布（饼图）；班级人数统计（柱状图） |
| 操作日志 | AOP 切面自动记录接口调用，支持分页查询 |
| 文件上传 | 图片/文件上传至阿里云 OSS，返回访问 URL |

---

## 快速启动

### 环境要求

| 环境 | 版本要求 |
| --- | --- |
| JDK | 21+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.x |

### 1. 准备数据库

启动 MySQL，创建数据库 `work-manage-db`，导入项目提供的 SQL 脚本（含建表和初始数据）。

```sql
CREATE DATABASE `work-manage-db` DEFAULT CHARACTER SET utf8mb4;
```

### 2. 配置后端

在 `tlias-web-management/src/main/resources/` 下创建 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/work-manage-db
    username: root
    password: 你的密码
  jackson:
    time-zone: GMT+8

jwt:
  secret: 你的JWT密钥（至少32个字符）
```

> 注意：`application.yml` 包含数据库密码，已加入 `.gitignore`，不会提交到仓库。

### 3. 启动后端

```bash
# 在项目根目录编译安装各模块
mvn clean install -DskipTests

# 进入 Web 模块启动服务
cd tlias-web-management
mvn spring-boot:run
```

后端服务启动在 `http://localhost:8080`。

### 4. 启动前端

```bash
cd tlias-ui
npm install
npm run dev
```

前端开发服务器启动在 `http://localhost:5173`，已配置代理将 `/api` 请求转发到后端 `8080` 端口。

### 5. 访问系统

浏览器打开 `http://localhost:5173`，使用默认账号登录：

- 用户名：`zhangsan`
- 密码：`123456`

---

## 接口文档

详见 [API接口文档.md](./API接口文档.md)，共 28 个接口，覆盖部门、员工、班级、学员、统计、日志、登录、上传 8 个模块。

---

## 核心设计说明

**认证机制**：JWT Token 存于请求头 `token` 字段，拦截器 `TokenInterceptor` 统一校验，仅放行 `/user/login` 和 `/user/register`；前端 axios 拦截器自动附加 Token，401 响应自动跳转登录页。

**枚举处理**：所有枚举（性别、职位、学历、学科）实现 MyBatis-Plus 的 `IEnum<Integer>` 接口，数据库存数字编码；通过 `@JsonCreator` 接收前端数字码反序列化，通过 `@JsonValue` 序列化返回中文描述；查询参数中的枚举由自定义 `IEnumConverterFactory` 完成转换。

**操作日志**：基于 AOP 切面实现，Controller 方法标注 `@Log` 注解即自动记录操作人、类名、方法名、参数、返回值和执行耗时，日志持久化到 `operate_log` 表。

**文件上传**：前端 `multipart/form-data` 上传文件，后端接收后调用阿里云 OSS SDK 上传，返回文件访问 URL 供前端展示。

**异常处理**：`GlobalExceptionHandler` 统一捕获参数校验失败（`@Valid`）、JSON 解析错误、唯一键冲突等异常，返回结构化 `Result` 响应，避免直接暴露堆栈信息。
