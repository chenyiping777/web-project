# tlias 教学管理系统 API 接口文档

> 项目：`web-project`（tlias-web-management 多模块工程）
> 技术栈：Spring Boot 4.1.0 + MyBatis-Plus 3.5.17 + MySQL
> 服务端口：`8080`，基础地址：`http://localhost:8080`
> 文档版本：v2.0  更新日期：2026-08-13  依据：源码逐接口核对，示例均为实测格式

---

## 一、接口总览

系统共实现 **28 个接口**，按业务划分为 8 个模块。

| 模块 | 前缀 | 数量 | 说明 |
| --- | --- | --- | --- |
| 1. 部门管理 | `/depts` | 5 | 增删改查 + 列表 |
| 2. 员工管理 | `/emp` | 5 | 分页查询、增删改查详情 |
| 3. 班级管理 | `/clazz` | 6 | 分页查询、查询所有、增删改查 |
| 4. 学员管理 | `/students` | 5 | 分页查询、增删改查 |
| 5. 数据统计 | `/report` | 4 | 性别/职位/学历/班级人数统计 |
| 6. 操作日志 | `/log` | 1 | 日志分页查询 |
| 7. 用户登录 | `/user` | 1 | JWT 登录 |
| 8. 文件上传 | `/upload` | 1 | 阿里云 OSS 上传 |

### 全量接口清单

| 序号 | 模块 | 接口名称 | 方式 | 路径 | 需 Token |
| --- | --- | --- | --- | --- | --- |
| 1.1 | 部门 | 查询部门列表 | GET | `/depts` | 是 |
| 1.2 | 部门 | 根据 ID 查询部门 | GET | `/depts/{id}` | 是 |
| 1.3 | 部门 | 新增部门 | POST | `/depts` | 是 |
| 1.4 | 部门 | 修改部门 | PUT | `/depts` | 是 |
| 1.5 | 部门 | 删除部门 | DELETE | `/depts/{id}` | 是 |
| 2.1 | 员工 | 分页查询员工列表 | POST | `/emp/getAllEmp` | 是 |
| 2.2 | 员工 | 新增员工 | POST | `/emp` | 是 |
| 2.3 | 员工 | 修改员工 | PUT | `/emp` | 是 |
| 2.4 | 员工 | 根据 ID 查询员工详情 | GET | `/emp/{id}` | 是 |
| 2.5 | 员工 | 删除员工（批量） | DELETE | `/emp/{ids}` | 是 |
| 3.1 | 班级 | 分页查询班级列表 | GET | `/clazz` | 是 |
| 3.2 | 班级 | 查询所有班级 | GET | `/clazz/all` | 是 |
| 3.3 | 班级 | 根据 ID 查询班级 | GET | `/clazz/{id}` | 是 |
| 3.4 | 班级 | 新增班级 | POST | `/clazz` | 是 |
| 3.5 | 班级 | 修改班级 | PUT | `/clazz` | 是 |
| 3.6 | 班级 | 删除班级 | DELETE | `/clazz/{id}` | 是 |
| 4.1 | 学员 | 分页查询学员列表 | GET | `/students/page` | 是 |
| 4.2 | 学员 | 新增学员 | POST | `/students` | 是 |
| 4.3 | 学员 | 根据 ID 查询学员 | GET | `/students/{id}` | 是 |
| 4.4 | 学员 | 修改学员 | PUT | `/students` | 是 |
| 4.5 | 学员 | 删除学员（批量） | DELETE | `/students/{ids}` | 是 |
| 5.1 | 统计 | 员工性别统计 | GET | `/report/empGenderData` | 是 |
| 5.2 | 统计 | 员工职位统计 | GET | `/report/empJobData` | 是 |
| 5.3 | 统计 | 学员学历统计 | GET | `/report/studentDegreeData` | 是 |
| 5.4 | 统计 | 班级人数统计 | GET | `/report/studentCountData` | 是 |
| 6.1 | 日志 | 操作日志分页查询 | GET | `/log/page` | 是 |
| 7.1 | 登录 | 用户登录 | POST | `/user/login` | **否** |
| 8.1 | 上传 | 文件上传 | POST | `/upload` | 是 |

---

## 二、通用说明

### 2.1 统一响应格式 `Result`

所有接口统一返回 `Result` 包装对象，HTTP 状态码除登录拦截外恒为 `200`，业务成败看 `code`。

```json
{
  "code": 1,
  "msg": "success",
  "data": {}
}
```

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：`1` 成功，`0` 失败 |
| msg | String | 提示信息，成功为 `success` |
| data | Object | 业务数据，无数据时为 `null` |

> 说明：服务端发生未捕获异常时，由全局异常处理器（`GlobalExceptionHandler`）统一返回 `{"code":0,"msg":"程序出错，赶快找后端","data":null}`（HTTP 仍为 200）。仅登录拦截器在 Token 缺失/非法时直接返回 HTTP `401`（纯文本）。

### 2.2 统一分页结构 `PageVo`

所有分页接口的 `data` 均为该结构：

```json
{
  "total": 17,
  "pages": 4,
  "list": []
}
```

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| total | Integer | 总记录数 |
| pages | Integer | 总页数 |
| list | Array | 当前页数据列表 |

### 2.3 公共分页/排序参数 `PageQuery`

以下分页接口的查询条件对象都继承 `PageQuery`，可附带这 4 个分页参数：

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| pageNo | Integer | 否 | 1 | 页码，从 1 开始 |
| pageSize | Integer | 否 | 10 | 每页条数 |
| sortBy | String | 否 | - | 排序字段名（数据库列名） |
| asc | Boolean | 否 | false | 是否升序，`true` 升序 / `false` 降序 |

### 2.4 认证说明（Token）

- 登录成功后获得 JWT Token，后续请求放在请求头 `token` 中。
- **免 Token**（拦截器放行）：仅 `/user/login` 和 `/user/register`。
- **其他所有接口**均需携带有效 Token，包括 `/emp/getAllEmp`、`/students/**` 等。
- Token 缺失（请求头无 `token` 或为空）时，拦截器返回 HTTP `401` + 纯文本 `未携带令牌，请先登录`。
- Token 非法或已失效时，返回 HTTP `401` + 纯文本 `令牌失效或非法，请重新登录`。

### 2.5 枚举编码表

请求体 JSON 中枚举字段传**数字编码**（由 `@JsonCreator` 反序列化）；查询参数中枚举同样传数字编码（由自定义 `IEnumConverterFactory` 转换）；响应中枚举统一返回**中文描述**（由 `@JsonValue` 序列化）。

性别 `GenderEnum`：

| code | 描述 |
| --- | --- |
| 1 | 男 |
| 2 | 女 |

职位 `JobEnum`：

| code | 描述 |
| --- | --- |
| 1 | 班主任 |
| 2 | 讲师 |
| 3 | 学工主管 |
| 4 | 教学主管 |
| 5 | 校长 |

学历 `DegreeEnum`：

| code | 描述 |
| --- | --- |
| 1 | 初中 |
| 2 | 高中 |
| 3 | 大专 |
| 4 | 本科 |
| 5 | 硕士 |
| 6 | 博士 |

学科 `SubjectEnum`：

| code | 描述 |
| --- | --- |
| 1 | 语文 |
| 2 | 数学 |
| 3 | 英语 |
| 4 | 物理 |
| 5 | 化学 |
| 6 | 生物 |

班级状态 `ClazzVo.status`（由 SQL CASE 根据当前时间计算）：`0` 未开班、`1` 已开班、`2` 结课。

### 2.6 日期格式

请求体 / 查询参数中的日期统一使用 `yyyy-MM-dd`（如 `2025-01-01`）。响应中的时间字段为 ISO-8601 字符串，时区为东八区（`GMT+8`）。

### 2.7 全局异常处理

`GlobalExceptionHandler` 统一处理以下异常类型，均返回 `Result`（HTTP 200）：

| 异常类型 | 触发场景 | 返回 msg |
| --- | --- | --- |
| `MethodArgumentNotValidException` | `@Valid` + `@RequestBody` 校验失败 | 第一个校验失败字段的 message |
| `ConstraintViolationException` | 单个参数校验失败 | 第一条校验消息 |
| `BindException` | 表单/查询参数对象绑定校验失败 | 第一个校验失败字段的 message |
| `HttpMessageNotReadableException` | JSON 格式非法 / 枚举编码越界 | `请求参数格式不正确` |
| `MissingServletRequestParameterException` | 缺少必传查询参数 | `缺少请求参数：xxx` |
| `DuplicateKeyException` | 数据库唯一键冲突 | `Duplicate entry 'xxx' for key 'yyy'` |
| `Exception`（兜底） | 其他所有未处理异常 | `程序出错，赶快找后端` |

---

## 三、部门管理（/depts）

### 3.1 查询部门列表

> **请求路径**：`/depts`
> **请求方式**：GET
> **接口描述**：查询所有部门（数据量少不分页），按最后修改时间倒序排序。

**请求参数**

无。

**响应数据**

格式：`Result<List<Dept>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| id | Integer | 部门 ID |
| name | String | 部门名称 |
| createTime | String | 创建时间（`LocalDateTime`，格式 `yyyy-MM-ddTHH:mm:ss`） |
| updateTime | String | 修改时间（格式同上） |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 3,
      "name": "财务部",
      "createTime": "2024-09-25T09:47:40",
      "updateTime": "2026-08-06T14:01:11"
    }
  ]
}
```

> 说明：`Dept` 实体的时间字段类型为 `LocalDateTime`（无 `@JsonFormat`），序列化格式为 `yyyy-MM-ddTHH:mm:ss`，与其他使用 `Date` + `@JsonFormat` 的实体格式略有不同。

---

### 3.2 根据 ID 查询部门

> **请求路径**：`/depts/{id}`
> **请求方式**：GET
> **接口描述**：根据部门 ID 查询单个部门详情，常用于修改前回显。

**请求参数**

路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 部门 ID |

请求示例：`GET /depts/1`

**响应数据**

格式：`Result<Dept>`

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "学工部",
    "createTime": "2024-09-25T09:47:40",
    "updateTime": "2024-09-25T09:47:40"
  }
}
```

---

### 3.3 新增部门

> **请求路径**：`/depts`
> **请求方式**：POST
> **接口描述**：新增一个部门，部门名称必填、唯一，长度 2~10 位。

**请求参数**

请求体（JSON）。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| name | String | 是 | 部门名称，2~10 个字符，不可重复 |

请求示例：

```json
{
  "name": "教研部"
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**备注**：实体 `Dept` 上有 `@NotBlank/@Size` 注解，但控制器 `@RequestBody Dept` 未加 `@Valid`，因此长度校验不会在入口处生效。名称唯一性由 Service 层校验，重复时返回 `code:0`。

---

### 3.4 修改部门

> **请求路径**：`/depts`
> **请求方式**：PUT
> **接口描述**：根据部门 ID 修改部门名称。

**请求参数**

请求体（JSON）。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 部门 ID |
| name | String | 是 | 新的部门名称 |

请求示例：

```json
{
  "id": 1,
  "name": "学工部"
}
```

**响应数据**

格式：`Result<Dept>`（返回修改后的部门对象）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "学工部",
    "createTime": null,
    "updateTime": "2026-08-13T00:28:01"
  }
}
```

---

### 3.5 删除部门

> **请求路径**：`/depts/{id}`
> **请求方式**：DELETE
> **接口描述**：根据部门 ID 删除部门。

**请求参数**

路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 待删除的部门 ID |

请求示例：`DELETE /depts/11`

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

## 四、员工管理（/emp）

### 4.1 分页查询员工列表

> **请求路径**：`/emp/getAllEmp`
> **请求方式**：POST
> **接口描述**：分页查询员工列表，支持按性别、部门名称筛选。默认按薪资降序、入职日期升序排序。

**请求参数**

请求体（JSON），`EmpQuery` 继承公共分页参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| pageNo | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |
| sortBy | String | 否 | 排序字段 |
| asc | Boolean | 否 | 是否升序 |
| gender | Integer | 否 | 性别编码：1 男，2 女 |
| deptName | String | 否 | 部门名称（模糊匹配） |

请求示例：

```json
{
  "pageNo": 1,
  "pageSize": 10,
  "gender": 1,
  "deptName": "教研"
}
```

**响应数据**

格式：`Result<PageVo<EmpVo>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| id | Integer | 员工 ID |
| name | String | 员工姓名 |
| gender | String | 性别（中文：男/女） |
| job | String | 职位（中文） |
| image | String | 头像地址 |
| deptName | String | 所属部门名称（LEFT JOIN 查询） |
| entryDate | String | 入职日期（`yyyy-MM-dd`） |
| updateTime | String | 最后修改时间（`yyyy-MM-dd HH:mm:ss`） |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 1,
    "pages": 1,
    "list": [
      {
        "id": 1,
        "name": "张三",
        "gender": "男",
        "job": "班主任",
        "image": "avatar/zs.jpg",
        "deptName": "教研部",
        "entryDate": "2024-01-10",
        "updateTime": "2026-08-06 14:12:01",
        "username": null,
        "password": null,
        "phone": null,
        "salary": null,
        "deptId": null,
        "createTime": null,
        "exprList": null
      }
    ]
  }
}
```

**备注**：列表 SQL 只查了 id、姓名、性别、职位、头像、部门名、入职日期、修改时间这几列，因此 `username / password / phone / salary` 等字段在列表中恒为 `null`；如需完整信息请调用 4.4 详情接口。

---

### 4.2 新增员工

> **请求路径**：`/emp`
> **请求方式**：POST
> **接口描述**：新增员工，可同时提交工作经历。请求体带 `@Valid` 校验。用户名必须唯一，密码为空时默认 `123456`。

**请求参数**

请求体（JSON），`EmpDto`。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | String | 是 | 用户名（`@NotBlank`），不可重复 |
| name | String | 是 | 姓名（`@NotBlank`） |
| gender | Integer | 是 | 性别编码：1 男，2 女（`@NotNull`） |
| phone | String | 是 | 手机号（`@NotBlank` + `@Pattern: ^1[3-9]\d{9}$`） |
| password | String | 否 | 密码，为空则使用默认值 `123456` |
| image | String | 否 | 头像地址 |
| deptId | Integer | 否 | 所属部门 ID |
| entryDate | String | 否 | 入职日期 `yyyy-MM-dd` |
| job | Integer | 否 | 职位编码，见枚举表 |
| salary | Double | 否 | 薪资 |
| experList | Array | 否 | 工作经历列表 |
| └ begin | String | 否 | 经历开始日期 `yyyy-MM-dd` |
| └ end | String | 否 | 经历结束日期 `yyyy-MM-dd` |
| └ company | String | 否 | 公司名称 |
| └ job | String | 否 | 该段经历的职位 |

请求示例：

```json
{
  "username": "lisi001",
  "name": "李四",
  "gender": 1,
  "phone": "13800138002",
  "image": "avatar/ls.jpg",
  "deptId": 1,
  "entryDate": "2025-01-01",
  "job": 2,
  "salary": 9000,
  "experList": [
    {
      "begin": "2022-01-01",
      "end": "2024-12-31",
      "company": "某科技公司",
      "job": "Java工程师"
    }
  ]
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误场景**：用户名已存在时返回 `{"code":0,"msg":"用户名已存在","data":null}`。

---

### 4.3 修改员工

> **请求路径**：`/emp`
> **请求方式**：PUT
> **接口描述**：根据员工 ID 修改员工信息及工作经历。请求体带 `@Valid` 校验。工作经历采用「删旧插新」策略。

**请求参数**

请求体（JSON），`EmpDto`（字段同 4.2，修改时必须携带 `id`）。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 员工 ID |
| 其余字段 | - | - | 同 4.2 新增员工 |

请求示例：

```json
{
  "id": 13,
  "username": "lisi001",
  "name": "李四",
  "gender": 2,
  "phone": "13800138002",
  "deptId": 2,
  "job": 1,
  "salary": 9999,
  "experList": [
    {
      "begin": "2020-01-01",
      "end": "2022-06-30",
      "company": "某互联网公司",
      "job": "主管"
    }
  ]
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**备注**：更新使用 MyBatis-Plus 的 NOT_NULL 字段策略——实体中值为 `null` 的字段不会出现在 UPDATE 的 SET 子句中，数据库保持原值不变。工作经历会先按 `emp_id` 全部删除，再按提交的列表重新插入。

---

### 4.4 根据 ID 查询员工详情

> **请求路径**：`/emp/{id}`
> **请求方式**：GET
> **接口描述**：查询员工完整详情（含工作经历列表），用于修改前回显。密码字段不回传。

**请求参数**

路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 员工 ID |

请求示例：`GET /emp/1`

**响应数据**

格式：`Result<EmpVo>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| id | Integer | 员工 ID |
| username | String | 用户名 |
| name | String | 姓名 |
| gender | String | 性别（中文） |
| phone | String | 手机号 |
| job | String | 职位（中文） |
| salary | Integer | 薪资 |
| image | String | 头像地址 |
| deptName | String | 所属部门名称 |
| entryDate | String | 入职日期（`yyyy-MM-dd`） |
| updateTime | String | 最后修改时间（`yyyy-MM-dd HH:mm:ss`） |
| createTime | String | 创建时间（`yyyy-MM-dd HH:mm:ss`） |
| exprList | Array | 工作经历列表（按开始日期升序） |
| └ id | Integer | 经历 ID |
| └ begin | String | 开始日期（`yyyy-MM-dd`） |
| └ end | String | 结束日期（`yyyy-MM-dd`） |
| └ company | String | 公司名称 |
| └ job | String | 职位 |
| └ empId | Integer | 关联员工 ID |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "name": "张三",
    "gender": "男",
    "phone": "13800138001",
    "job": "班主任",
    "salary": 8500,
    "image": "avatar/zs.jpg",
    "deptName": "教研部",
    "entryDate": "2024-01-10",
    "updateTime": "2026-08-06 14:12:01",
    "createTime": "2024-01-01 00:00:00",
    "password": null,
    "exprList": []
  }
}
```

---

### 4.5 删除员工（批量）

> **请求路径**：`/emp/{ids}`
> **请求方式**：DELETE
> **接口描述**：批量删除员工，先删工作经历子表再删主表，避免孤儿数据。

**请求参数**

路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| ids | List\<Integer\> | 是 | 员工 ID，多个用英文逗号分隔 |

请求示例：`DELETE /emp/13` 或 `DELETE /emp/13,14,15`

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

## 五、班级管理（/clazz）

### 5.1 分页查询班级列表

> **请求路径**：`/clazz`
> **请求方式**：GET
> **接口描述**：分页查询班级列表，支持按学科、开课时间区间筛选。默认按创建时间降序、开课日期升序。

**请求参数**

查询参数（Query String），`ClazzQuery` 继承公共分页参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| pageNo | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |
| sortBy | String | 否 | 排序字段 |
| asc | Boolean | 否 | 是否升序 |
| subject | Integer | 否 | 学科编码：1 语文 ~ 6 生物 |
| beginDate | String | 否 | 开课开始日期 `yyyy-MM-dd` |
| endDate | String | 否 | 开课结束日期 `yyyy-MM-dd` |

请求示例：`GET /clazz?pageNo=1&pageSize=5&subject=1&beginDate=2023-01-01&endDate=2024-12-31`

**响应数据**

格式：`Result<PageVo<ClazzVo>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| id | Integer | 班级 ID |
| name | String | 班级名称 |
| subject | String | 学科（中文） |
| masterId | Integer | 班主任 ID |
| masterName | String | 班主任姓名（LEFT JOIN emp 查询） |
| room | String | 教室 |
| beginDate | String | 开课日期（`yyyy-MM-dd`） |
| endDate | String | 结课日期（`yyyy-MM-dd`） |
| status | Integer | 状态：0 未开班，1 已开班，2 结课 |
| createTime | String | 创建时间（`yyyy-MM-dd HH:mm:ss`） |
| updateTime | String | 修改时间（`yyyy-MM-dd HH:mm:ss`） |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 5,
    "pages": 1,
    "list": [
      {
        "id": 1,
        "name": "大耳朵图图趣味启蒙班",
        "subject": "语文",
        "masterId": 10,
        "masterName": "张三",
        "room": "一号教室",
        "beginDate": "2023-04-30",
        "endDate": "2023-06-29",
        "status": 2,
        "createTime": "2023-06-01 17:08:23",
        "updateTime": "2023-06-01 17:39:58"
      }
    ]
  }
}
```

**备注**：`status` 由 SQL `CASE` 根据 `NOW()` 与开课/结课时间比较得出：`NOW() < begin_date` → 0，`begin_date <= NOW() <= end_date` → 1，`NOW() > end_date` → 2。

---

### 5.2 查询所有班级

> **请求路径**：`/clazz/all`
> **请求方式**：GET
> **接口描述**：查询所有班级（不分页），常用于下拉选择。

**请求参数**

无。

**响应数据**

格式：`Result<List<Clazz>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| id | Integer | 班级 ID |
| name | String | 班级名称 |
| subject | String | 学科（中文） |
| room | String | 教室 |
| masterId | Integer | 班主任 ID |
| beginDate | String | 开课日期 |
| endDate | String | 结课日期 |
| createTime | String | 创建时间 |
| updateTime | String | 修改时间 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "大耳朵图图趣味启蒙班",
      "subject": "语文",
      "room": "一号教室",
      "masterId": 10,
      "beginDate": "2023-04-30T00:00:00.000+08:00",
      "endDate": "2023-06-29T00:00:00.000+08:00",
      "createTime": "2023-06-01T17:08:23.000+08:00",
      "updateTime": "2023-06-01T17:39:58.000+08:00"
    }
  ]
}
```

> 说明：此接口直接返回 `Clazz` 实体列表（非 `ClazzVo`），`subject` 字段因 `@JsonValue` 序列化为中文。时间字段无 `@JsonFormat` 注解，使用 Jackson 默认 ISO-8601 格式。

---

### 5.3 根据 ID 查询班级

> **请求路径**：`/clazz/{id}`
> **请求方式**：GET
> **接口描述**：根据班级 ID 查询单个班级详情。

**请求参数**

路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 班级 ID |

请求示例：`GET /clazz/1`

**响应数据**

格式：`Result<Clazz>`（字段同 5.2 列表项）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "大耳朵图图趣味启蒙班",
    "subject": "语文",
    "room": "一号教室",
    "masterId": 10,
    "beginDate": "2023-04-30T00:00:00.000+08:00",
    "endDate": "2023-06-29T00:00:00.000+08:00",
    "createTime": "2023-06-01T17:08:23.000+08:00",
    "updateTime": "2023-06-01T17:39:58.000+08:00"
  }
}
```

---

### 5.4 新增班级

> **请求路径**：`/clazz`
> **请求方式**：POST
> **接口描述**：新增班级，请求体带 `@Valid` 校验。

**请求参数**

请求体（JSON），`ClazzDto`。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| name | String | 否 | 班级名称（Service 层校验非空） |
| room | String | 是 | 教室（`@NotBlank`） |
| beginDate | String | 是 | 开课日期 `yyyy-MM-dd`（`@NotNull`） |
| endDate | String | 是 | 结课日期 `yyyy-MM-dd`（`@NotNull`） |
| masterId | Integer | 否 | 班主任 ID |
| subject | Integer | 否 | 学科编码（Service 层校验非空） |

请求示例：

```json
{
  "name": "猪猪侠冒险特训一班",
  "room": "二号教室",
  "beginDate": "2025-09-01",
  "endDate": "2026-01-15",
  "masterId": 1,
  "subject": 3
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 5.5 修改班级

> **请求路径**：`/clazz`
> **请求方式**：PUT
> **接口描述**：根据班级 ID 修改班级信息。

**请求参数**

请求体（JSON），`ClazzDto`（字段同 5.4，修改时必须携带 `id` 和 `masterId`）。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 班级 ID（Service 层校验非空） |
| name | String | 否 | 班级名称 |
| room | String | 是 | 教室（`@NotBlank`） |
| beginDate | String | 是 | 开课日期（`@NotNull`） |
| endDate | String | 是 | 结课日期（`@NotNull`） |
| masterId | Integer | 是 | 班主任 ID（Service 层校验非空） |
| subject | Integer | 否 | 学科编码 |

请求示例：

```json
{
  "id": 1,
  "name": "大耳朵图图趣味启蒙班",
  "room": "三号教室",
  "beginDate": "2023-04-30",
  "endDate": "2023-06-29",
  "masterId": 5,
  "subject": 1
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 5.6 删除班级

> **请求路径**：`/clazz/{id}`
> **请求方式**：DELETE
> **接口描述**：根据班级 ID 删除班级。

**请求参数**

路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 待删除的班级 ID |

请求示例：`DELETE /clazz/5`

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

## 六、学员管理（/students）

### 6.1 分页查询学员列表

> **请求路径**：`/students/page`
> **请求方式**：GET
> **接口描述**：分页查询学员列表，支持按姓名、班级、学历筛选。联表查询班级名称。默认按学员 ID 升序排序。

**请求参数**

查询参数（Query String），`StudentQuery` 继承公共分页参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| pageNo | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |
| sortBy | String | 否 | 排序字段 |
| asc | Boolean | 否 | 是否升序 |
| name | String | 否 | 学员姓名（精确匹配） |
| clazzId | Integer | 否 | 班级 ID |
| degree | Integer | 否 | 学历编码：1 初中 ~ 6 博士 |

请求示例：`GET /students/page?pageNo=1&pageSize=10&degree=4`

**响应数据**

格式：`Result<PageVo<StudentVo>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| id | Integer | 学员 ID |
| name | String | 学员姓名 |
| no | String | 学号 |
| gender | String | 性别（中文） |
| phone | String | 手机号 |
| idCard | String | 身份证号 |
| isCollege | Boolean | 是否来自院校 |
| address | String | 联系地址 |
| degree | String | 学历（中文） |
| graduationDate | String | 毕业日期 |
| clazzId | Integer | 班级 ID |
| clazzName | String | 班级名称（LEFT JOIN 查询） |
| violationCount | Integer | 违纪次数 |
| violationScore | Integer | 违纪扣分 |
| createTime | String | 创建时间 |
| updateTime | String | 修改时间 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 20,
    "pages": 2,
    "list": [
      {
        "id": 1,
        "name": "小明",
        "no": "STU2024001",
        "gender": "男",
        "phone": "13900139001",
        "idCard": "440101200501011234",
        "isCollege": true,
        "address": "广州市天河区",
        "degree": "本科",
        "graduationDate": "2024-06-30",
        "clazzId": 1,
        "clazzName": "大耳朵图图趣味启蒙班",
        "violationCount": 0,
        "violationScore": 0,
        "createTime": "2024-09-01 08:00:00",
        "updateTime": "2024-09-01 08:00:00"
      }
    ]
  }
}
```

---

### 6.2 新增学员

> **请求路径**：`/students`
> **请求方式**：POST
> **接口描述**：新增学员，请求体带 `@Valid` 校验。

**请求参数**

请求体（JSON），`StudentDto`。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| name | String | 是 | 学员姓名（`@NotBlank`） |
| no | String | 是 | 学号（`@NotBlank`） |
| gender | Integer | 是 | 性别编码：1 男，2 女（`@NotNull`） |
| phone | String | 是 | 手机号（`@NotBlank`） |
| idCard | String | 否 | 身份证号 |
| isCollege | Boolean | 否 | 是否来自院校 |
| address | String | 否 | 联系地址 |
| degree | Integer | 是 | 学历编码（`@NotNull`） |
| graduationDate | String | 否 | 毕业日期 `yyyy-MM-dd` |
| clazzId | Integer | 是 | 班级 ID（`@NotNull`） |
| violationCount | Integer | 否 | 违纪次数 |
| violationScore | Integer | 否 | 违纪扣分 |

请求示例：

```json
{
  "name": "小明",
  "no": "STU2024001",
  "gender": 1,
  "phone": "13900139001",
  "idCard": "440101200501011234",
  "isCollege": true,
  "address": "广州市天河区",
  "degree": 4,
  "graduationDate": "2024-06-30",
  "clazzId": 1
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 6.3 根据 ID 查询学员

> **请求路径**：`/students/{id}`
> **请求方式**：GET
> **接口描述**：根据学员 ID 查询学员详情。

**请求参数**

路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 学员 ID |

请求示例：`GET /students/1`

**响应数据**

格式：`Result<Student>`（返回数据库实体，字段同 6.1 但不含 `clazzName`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "小明",
    "no": "STU2024001",
    "gender": 1,
    "phone": "13900139001",
    "idCard": "440101200501011234",
    "isCollege": 1,
    "address": "广州市天河区",
    "degree": "本科",
    "graduationDate": "2024-06-30",
    "clazzId": 1,
    "violationCount": 0,
    "violationScore": 0,
    "createTime": "2024-09-01T08:00:00.000+08:00",
    "updateTime": "2024-09-01T08:00:00.000+08:00"
  }
}
```

> 说明：此接口直接返回 `Student` 实体，`gender` 为数据库数字编码（1/2），`isCollege` 为数字（1/0），与分页列表返回的 `StudentVo` 格式不同。

---

### 6.4 修改学员

> **请求路径**：`/students`
> **请求方式**：PUT
> **接口描述**：根据学员 ID 修改学员信息。

**请求参数**

请求体（JSON），`StudentDto`（字段同 6.2，修改时必须携带 `id`）。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 学员 ID |
| 其余字段 | - | - | 同 6.2 新增学员 |

请求示例：

```json
{
  "id": 1,
  "name": "小明",
  "no": "STU2024001",
  "gender": 1,
  "phone": "13900139002",
  "degree": 4,
  "clazzId": 1,
  "violationCount": 1,
  "violationScore": 5
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误场景**：学员不存在返回 `{"code":0,"msg":"学生不存在","data":null}`；违规次数或积分为空返回对应错误提示。

---

### 6.5 删除学员（批量）

> **请求路径**：`/students/{ids}`
> **请求方式**：DELETE
> **接口描述**：批量删除学员，删除前会校验每个 ID 是否存在。

**请求参数**

路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| ids | List\<Integer\> | 是 | 学员 ID，多个用英文逗号分隔 |

请求示例：`DELETE /students/1` 或 `DELETE /students/1,2,3`

**响应数据**

格式：`Result`（`data` 为 `null`）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误场景**：任一学员 ID 不存在返回 `{"code":0,"msg":"学生不存在","data":null}`。

---

## 七、数据统计（/report）

### 7.1 员工性别统计

> **请求路径**：`/report/empGenderData`
> **请求方式**：GET
> **接口描述**：按性别分组统计员工人数，按编码升序排列。

**请求参数**

无。

**响应数据**

格式：`Result<List<Map>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| name | String | 分组名称，如 `男性员工`、`女性员工` |
| value | Integer | 该分组人数 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    { "name": "男性员工", "value": 8 },
    { "name": "女性员工", "value": 5 }
  ]
}
```

---

### 7.2 员工职位统计

> **请求路径**：`/report/empJobData`
> **请求方式**：GET
> **接口描述**：按职位分组统计员工人数，按人数降序排列。

**请求参数**

无。

**响应数据**

格式：`Result<List<Map>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| job | String | 职位名称（中文） |
| value | Integer | 该职位人数 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    { "job": "班主任", "value": 5 },
    { "job": "讲师", "value": 3 },
    { "job": "校长", "value": 1 }
  ]
}
```

---

### 7.3 学员学历统计

> **请求路径**：`/report/studentDegreeData`
> **请求方式**：GET
> **接口描述**：按学历分组统计学员人数，按编码升序排列。

**请求参数**

无。

**响应数据**

格式：`Result<List<Map>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| degree | String | 学历名称（中文） |
| value | Integer | 该学历人数 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    { "degree": "大专", "value": 3 },
    { "degree": "本科", "value": 12 },
    { "degree": "硕士", "value": 5 }
  ]
}
```

---

### 7.4 班级人数统计

> **请求路径**：`/report/studentCountData`
> **请求方式**：GET
> **接口描述**：统计各班级的学员人数，按班级 ID 升序排列。返回结构与其他统计接口不同，包含两个平行数组。

**请求参数**

无。

**响应数据**

格式：`Result<Map>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| clazzNameList | Array\<String\> | 班级名称列表 |
| studentCountList | Array\<Integer\> | 对应的学员人数列表 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "clazzNameList": ["大耳朵图图趣味启蒙班", "猪猪侠冒险特训一班", "猪猪侠冒险特训二班"],
    "studentCountList": [8, 12, 0]
  }
}
```

**备注**：此结构专为前端 ECharts 柱状图设计，`clazzNameList` 作为 X 轴，`studentCountList` 作为数据系列。

---

## 八、操作日志（/log）

### 8.1 操作日志分页查询

> **请求路径**：`/log/page`
> **请求方式**：GET
> **接口描述**：分页查询操作日志，联表查询操作人姓名。无额外筛选条件，仅支持分页。

**请求参数**

查询参数（Query String），使用公共 `PageQuery`。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| pageNo | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |

请求示例：`GET /log/page?pageNo=1&pageSize=10`

**响应数据**

格式：`Result<PageVo<OperateLogVo>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| id | Integer | 日志 ID |
| operateEmpId | Integer | 操作人 ID |
| operateEmpName | String | 操作人姓名（LEFT JOIN emp 查询） |
| operateTime | String | 操作时间 |
| className | String | 操作类名 |
| methodName | String | 方法名 |
| methodParams | String | 方法参数 |
| returnValue | String | 返回值 |
| costTime | Long | 执行耗时（毫秒） |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 50,
    "pages": 5,
    "list": [
      {
        "id": 1,
        "operateEmpId": 1,
        "operateEmpName": "张三",
        "operateTime": "2026-08-13T02:30:00.000+08:00",
        "className": "com.cheny.controller.ClazzController",
        "methodName": "getClazz",
        "methodParams": "ClazzQuery(pageNo=1, pageSize=10, ...)",
        "returnValue": "Result(code=1, msg=success, ...)",
        "costTime": 15
      }
    ]
  }
}
```

---

## 九、用户登录（/user）

### 9.1 用户登录

> **请求路径**：`/user/login`
> **请求方式**：POST
> **接口描述**：用户登录，校验用户名和密码，成功后返回 JWT Token。**无需 Token**。

**请求参数**

请求体（JSON），`EmpLoginDto`。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | String | 是 | 用户名（`@NotBlank`） |
| password | String | 是 | 密码（`@NotBlank`） |

请求示例：

```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

**响应数据**

格式：`Result<String>`（`data` 为 JWT Token 字符串）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInN1YiI6InppaGFuZ3NhbiIsImV4cCI6MTcyODgwMDAwMH0.xxxxx"
}
```

**错误场景**：用户名或密码错误返回 `{"code":0,"msg":"用户名或密码错误","data":null}`。

**使用方式**：登录成功后，将 `data` 中的 Token 字符串存入后续每个请求的请求头 `token` 中。

---

## 十、文件上传（/upload）

### 10.1 文件上传

> **请求路径**：`/upload`
> **请求方式**：POST
> **接口描述**：上传文件到阿里云 OSS，返回文件访问地址。

**请求参数**

`multipart/form-data` 格式。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| file | MultipartFile | 是 | 上传的文件，参数名 `file` |

**响应数据**

格式：`Result<String>`（`data` 为文件在 OSS 上的访问 URL）

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": "https://xxx.oss-cn-xxx.aliyuncs.com/2026/08/13/xxx.jpg"
}
```
