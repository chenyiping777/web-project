# tlias-ui 前端项目

tlias 教学管理系统前端，基于接口文档 `../API接口文档.md` 开发，与后端 `tlias-web-management`（端口 8080）联调连通。

## 技术栈

- Vue 3（Composition API，`<script setup>` 语法）
- Vite 5 构建
- Vue Router 4（hash 模式 + 登录守卫）
- Axios（统一封装：token 注入、Result 拆包、401 处理）
- ECharts 5（数据统计图表）

## 快速开始

前置条件：Node.js >= 18，后端服务已在 `http://localhost:8080` 启动。

```bash
# 1. 安装依赖
npm install

# 2. 启动开发服务器（http://localhost:5173）
npm run dev

# 3. 生产构建（产物在 dist/）
npm run build
```

登录账号：`zhangsan / 123456`（Token 有效期 2 小时）。

## 目录结构

```
src/
├── main.js              # 应用入口
├── App.vue              # 根组件（路由出口 + 全局 Toast）
├── style.css            # 全局样式（简洁风格）
├── router/index.js      # 路由 + 登录守卫
├── api/                 # 接口层
│   ├── request.js       # axios 封装（baseURL=/api，token 请求头，Result 拆包）
│   ├── auth.js          # token 存取
│   ├── user.js          # 登录
│   ├── dept.js          # 部门管理
│   ├── emp.js           # 员工管理 + 文件上传
│   ├── clazz.js         # 班级管理
│   ├── student.js       # 学员管理
│   └── report.js        # 数据统计 + 操作日志
├── utils/
│   ├── enums.js         # 枚举字典（性别/职位/学历/学科/班级状态）
│   ├── format.js        # 日期格式化
│   └── message.js       # 轻提示
├── components/
│   ├── Layout.vue       # 侧边栏布局
│   ├── ModalDialog.vue  # 通用弹窗
│   ├── Pagination.vue   # 通用分页
│   └── Toast.vue        # 消息提示
└── views/
    ├── Login.vue        # 登录页
    ├── DeptView.vue     # 部门管理
    ├── EmpView.vue      # 员工管理（含头像上传、工作经历）
    ├── ClazzView.vue    # 班级管理
    ├── StudentView.vue  # 学员管理（含违纪处理字段）
    └── ReportView.vue   # 数据统计（ECharts）+ 操作日志
```

## 跨域说明

开发环境下前端运行在 5173 端口，`vite.config.js` 将 `/api/*` 代理到 `http://localhost:8080` 并去掉 `/api` 前缀，因此无需后端额外配置 CORS。

## 与后端约定

- 统一响应 `Result{code,msg,data}`：`code=1` 成功，`code=0` 失败（拦截器自动弹提示）。
- 登录成功后 Token 放入请求头 `token`；401 时自动清除 Token 并跳转登录页。
- 请求中枚举传数字编码（如 gender: 1 男 / 2 女），响应中返回中文描述。
- 分页结构 `PageVo{total,pages,list}`，分页参数 `pageNo / pageSize`。
