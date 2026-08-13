# tlias 教学管理系统 API 接口文档

> 项目：`web-project`（tlias-web-management 多模块工程）
> 技术栈：Spring Boot 4.1.0 + MyBatis-Plus 3.5.17 + MySQL
> 服务端口：`8080`，基础地址：`http://localhost:8080`
> 文档版本：v1.0　　更新日期：2026-08-13　　依据：启动服务实测全部接口，示例均为真实响应

---

## 一、接口总览

系统共实测 **28 个已实现接口**，按业务划分为 6 个模块。其中需求清单中的「员工查询全部」「学员违纪处理」目前没有独立接口，处理方式见对应模块说明。

| 模块 | 模块前缀 | 
| --- | --- | 
| 1. 部门管理 | `/depts` | 
| 2. 员工管理 | `/emp` | 
| 3. 班级管理 | `/clazz` | 
| 4. 学员管理 | `/students` | 
| 5. 数据统计 | `/report`、`/log` | 
| 6. 其他接口 | `/user`、`/upload` | 

### 全量接口清单

| 序号 | 模块 | 接口名称 | 方式 | 路径 | 是否需 Token |
| --- | --- | --- | --- | --- | --- |
| 3.1 | 部门 | 查询部门列表 | GET | `/depts` | 是 |
| 3.2 | 部门 | 根据 ID 查询部门 | GET | `/depts/{id}` | 是 |
| 3.3 | 部门 | 新增部门 | POST | `/depts` | 是 |
| 3.4 | 部门 | 修改部门 | PUT | `/depts` | 是 |
| 3.5 | 部门 | 删除部门 | DELETE | `/depts/{id}` | 是 |
| 4.1 | 员工 | 分页查询员工列表 | POST | `/emp/getAllEmp` | 是 |
| 4.2 | 员工 | 新增员工 | POST | `/emp` | 是 |
| 4.3 | 员工 | 修改员工 | PUT | `/emp` | 是 |
| 4.4 | 员工 | 根据 ID 查询员工详情 | GET | `/emp/{id}` | 是 |
| 4.5 | 员工 | 删除员工（批量） | DELETE | `/emp/{ids}` | 是 |
| 5.1 | 班级 | 分页查询班级列表 | GET | `/clazz` | 是 |
| 5.2 | 班级 | 查询所有班级 | GET | `/clazz/all` | 是 |
| 5.3 | 班级 | 根据 ID 查询班级 | GET | `/clazz/{id}` | 是 |
| 5.4 | 班级 | 新增班级 | POST | `/clazz` | 是 |
| 5.5 | 班级 | 修改班级 | PUT | `/clazz` | 是 |
| 5.6 | 班级 | 删除班级 | DELETE | `/clazz/{id}` | 是 |
| 6.1 | 学员 | 分页查询学员列表 | GET | `/students/page` | 是 |
| 6.2 | 学员 | 新增学员 | POST | `/students` | 是 |
| 6.3 | 学员 | 根据 ID 查询学员 | GET | `/students/{id}` | 是 |
| 6.4 | 学员 | 修改学员 | PUT | `/students` | 是 |
| 6.5 | 学员 | 删除学员（批量） | DELETE | `/students/{ids}` | 是 |
| 7.1 | 统计 | 员工性别统计 | GET | `/report/empGenderData` | 是 |
| 7.2 | 统计 | 员工职位统计 | GET | `/report/empJobData` | 是 |
| 7.3 | 统计 | 学员学历统计 | GET | `/report/studentDegreeData` | 是 |
| 7.4 | 统计 | 班级人数统计 | GET | `/report/studentCountData` | 是 |
| 7.5 | 统计 | 操作日志分页查询 | GET | `/log/page` | 是 |
| 8.1 | 其他 | 用户登录 | POST | `/user/login` | 否 |
| 8.2 | 其他 | 文件上传 | POST | `/upload` | 是 |

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

> 说明：服务端发生未捕获异常时，由全局异常处理器统一返回 `{"code":0,"msg":"程序出错，赶快找后端","data":null}`（HTTP 仍为 200）。仅登录拦截器在 Token 缺失/非法时直接返回 HTTP `401`（纯文本）。

### 2.2 统一分页结构 `PageVo`

所有分页接口的 `data` 均为该结构（按代码实际，字段为 `total / pages / list`）：

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
- **需要 Token**：`/depts/**`、`/emp/**`（`getAllEmp` 除外）、`/clazz/**`、`/report/**`、`/log/**`、`/upload`。
- **免 Token**（拦截器放行）：`/user/login`、`/user/register`
- Token 缺失或非法时，拦截器直接返回 HTTP `401`。

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

班级状态 `ClazzVo.status`：`0` 未开班、`1` 已开班、`2` 结课。

### 2.6 日期格式

请求体 / 查询参数中的日期统一使用 `yyyy-MM-dd`（如 `2025-01-01`）。响应中的时间字段为 ISO-8601 字符串，时区为东八区（`GMT+8`）。

---

## 三、部门管理（/depts）

### 3.1 查询部门列表

> **请求路径**：`/depts`
> **请求方式**：GET
> **接口描述**：查询所有部门（数据量少不分页），按最后修改时间倒序排序。

**请求参数**

格式：无请求参数。

**响应数据**

格式：`Result<List<Dept>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Array | 部门列表 |
| &#124;- id | Integer | 部门 ID |
| &#124;- name | String | 部门名称 |
| &#124;- createTime | String | 创建时间 |
| &#124;- updateTime | String | 最后修改时间 |

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

---

### 3.2 根据 ID 查询部门

> **请求路径**：`/depts/{id}`
> **请求方式**：GET
> **接口描述**：根据部门 ID 查询单个部门详情，常用于修改前回显。

**请求参数**

格式：路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 部门 ID |

请求示例：`GET /depts/1`

**响应数据**

格式：`Result<Dept>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 部门对象 |
| &#124;- id | Integer | 部门 ID |
| &#124;- name | String | 部门名称 |
| &#124;- createTime | String | 创建时间 |
| &#124;- updateTime | String | 最后修改时间 |

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

格式：请求体（JSON）。

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

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 3.4 修改部门

> **请求路径**：`/depts`
> **请求方式**：PUT
> **接口描述**：根据部门 ID 修改部门名称。

**请求参数**

格式：请求体（JSON）。

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

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 修改后的部门对象 |
| &#124;- id | Integer | 部门 ID |
| &#124;- name | String | 部门名称 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "学工部",
    "createTime": null,
    "updateTime": "2026-08-13T00:28:01.8508088"
  }
}
```

---

### 3.5 删除部门

> **请求路径**：`/depts/{id}`
> **请求方式**：DELETE
> **接口描述**：根据部门 ID 删除部门。

**请求参数**

格式：路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 待删除的部门 ID |

请求示例：`DELETE /depts/11`

**响应数据**

格式：`Result`（`data` 为 `null`）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

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
> **接口描述**：分页查询员工列表，支持按性别、部门名称筛选。免 Token 接口。

**请求参数**

格式：请求体（JSON），`EmpQuery` 继承公共分页参数。

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
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 分页对象 |
| &#124;- total | Integer | 总记录数 |
| &#124;- pages | Integer | 总页数 |
| &#124;- list | Array | 员工列表 |
| &#124;&#124;- name | String | 员工姓名 |
| &#124;&#124;- gender | String | 性别（中文：男/女） |
| &#124;&#124;- job | String | 职位（中文） |
| &#124;&#124;- image | String | 头像地址 |
| &#124;&#124;- deptName | String | 所属部门名称 |
| &#124;&#124;- entryDate | String | 入职日期 |
| &#124;&#124;- updateTime | String | 最后修改时间 |

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
        "name": "张三",
        "gender": "男",
        "job": "班主任",
        "image": "avatar/zs.jpg",
        "deptName": "教研部",
        "entryDate": "2024-01-10T00:00:00.000+08:00",
        "updateTime": "2026-08-06T14:12:01.000+08:00",
        "id": null,
        "username": null,
        "phone": null,
        "salary": null
      }
    ]
  }
}
```


---

### 4.2 新增员工

> **请求路径**：`/emp`
> **请求方式**：POST
> **接口描述**：新增员工，可同时提交工作经历。请求体带 `@Valid` 校验。

**请求参数**

格式：请求体（JSON），`EmpDto`。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | String | 是 | 用户名（`@NotBlank`） |
| name | String | 是 | 姓名（`@NotBlank`） |
| gender | Integer | 是 | 性别编码：1 男，2 女（`@NotNull`） |
| phone | String | 是 | 手机号（`@NotBlank`） |
| image | String | 否 | 头像地址 |
| deptId | Integer | 否 | 所属部门 ID |
| entryDate | String | 否 | 入职日期 `yyyy-MM-dd` |
| job | Integer | 否 | 职位编码，见枚举表 |
| salary | Double | 否 | 薪资 |
| experList | Array | 否 | 工作经历列表 |
| &#124;- begin | String | 否 | 经历开始日期 `yyyy-MM-dd` |
| &#124;- end | String | 否 | 经历结束日期 `yyyy-MM-dd` |
| &#124;- company | String | 否 | 公司名称 |
| &#124;- job | String | 否 | 该段经历的职位 |

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

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息，失败时为具体原因 |
| data | Null | 固定为 null |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 4.3 修改员工

> **请求路径**：`/emp`
> **请求方式**：PUT
> **接口描述**：根据员工 ID 修改员工信息及工作经历。请求体带 `@Valid` 校验。

**请求参数**

格式：请求体（JSON），`EmpDto`（字段同 4.2，修改时必须携带 `id`）。

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

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 4.4 根据 ID 查询员工详情

> **请求路径**：`/emp/{id}`
> **请求方式**：GET
> **接口描述**：查询员工完整详情（含工作经历列表），用于修改前回显。

**请求参数**

格式：路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 员工 ID |

请求示例：`GET /emp/1`

**响应数据**

格式：`Result<EmpVo>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 员工详情对象 |
| &#124;- id | Integer | 员工 ID |
| &#124;- username | String | 用户名 |
| &#124;- name | String | 姓名 |
| &#124;- gender | String | 性别（中文） |
| &#124;- phone | String | 手机号 |
| &#124;- job | String | 职位（中文） |
| &#124;- salary | Integer | 薪资 |
| &#124;- image | String | 头像地址 |
| &#124;- deptName | String | 所属部门名称 |
| &#124;- entryDate | String | 入职日期 |
| &#124;- updateTime | String | 最后修改时间 |
| &#124;- exprList | Array | 工作经历列表 |
| &#124;&#124;- id | Integer | 经历 ID |
| &#124;&#124;- begin | String | 开始日期 |
| &#124;&#124;- end | String | 结束日期 |
| &#124;&#124;- company | String | 公司名称 |
| &#124;&#124;- job | String | 职位 |
| &#124;&#124;- empId | Integer | 关联员工 ID |

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
    "entryDate": "2024-01-10T00:00:00.000+08:00",
    "updateTime": "2026-08-06T14:12:01.000+08:00",
    "exprList": []
  }
}
```

---

### 4.5 删除员工（批量）

> **请求路径**：`/emp/{ids}`
> **请求方式**：DELETE
> **接口描述**：批量删除员工，路径中可传一个或多个 ID（逗号分隔）。

**请求参数**

格式：路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| ids | List&lt;Integer&gt; | 是 | 员工 ID，多个用英文逗号分隔 |

请求示例：`DELETE /emp/13`　或　`DELETE /emp/13,14,15`

**响应数据**

格式：`Result`（`data` 为 `null`）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 4.6 查询全部员工（待开发）

> **接口描述**：需求中的「查询全部员工（不分页下拉列表）」目前**未实现独立接口**。当前仅有 4.1 的分页查询。


---

## 五、班级管理（/clazz）

### 5.1 分页查询班级列表

> **请求路径**：`/clazz`
> **请求方式**：GET
> **接口描述**：分页查询班级列表，支持按学科、开课时间区间筛选。接口带 `@Log` 操作日志记录。

**请求参数**

格式：查询参数（Query String），`ClazzQuery` 继承公共分页参数。

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
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 分页对象 |
| &#124;- total | Integer | 总记录数 |
| &#124;- pages | Integer | 总页数 |
| &#124;- list | Array | 班级列表 |
| &#124;&#124;- name | String | 班级名称 |
| &#124;&#124;- subject | String | 学科（中文） |
| &#124;&#124;- masterId | Integer | 班主任 ID |
| &#124;&#124;- masterName | String | 班主任姓名 |
| &#124;&#124;- room | String | 教室 |
| &#124;&#124;- beginDate | String | 开课日期 |
| &#124;&#124;- endDate | String | 结课日期 |
| &#124;&#124;- status | Integer | 状态：0 未开班，1 已开班，2 结课 |
| &#124;&#124;- createTime | String | 创建时间 |
| &#124;&#124;- updateTime | String | 修改时间 |

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
        "name": "猪猪侠冒险特训二班",
        "subject": "语文",
        "masterId": 20,
        "masterName": null,
        "room": null,
        "beginDate": "2023-07-20T00:00:00.000+08:00",
        "endDate": "2024-02-20T00:00:00.000+08:00",
        "status": 2,
        "createTime": "2023-06-01T17:46:10.000+08:00",
        "updateTime": "2023-06-01T17:46:10.000+08:00",
        "id": null
      }
    ]
  }
}
```



---

### 5.2 查询所有班级

> **请求路径**：`/clazz/all`
> **请求方式**：GET
> **接口描述**：查询所有班级（不分页），常用于下拉选择。接口带 `@Log` 记录。

**请求参数**

格式：无请求参数。

**响应数据**

格式：`Result<List<Clazz>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Array | 班级列表 |
| &#124;- id | Integer | 班级 ID |
| &#124;- name | String | 班级名称 |
| &#124;- subject | String | 学科（中文） |
| &#124;- room | String | 教室 |
| &#124;- masterId | Integer | 班主任 ID |
| &#124;- beginDate | String | 开课日期 |
| &#124;- endDate | String | 结课日期 |
| &#124;- createTime | String | 创建时间 |
| &#124;- updateTime | String | 修改时间 |

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

---

### 5.3 根据 ID 查询班级

> **请求路径**：`/clazz/{id}`
> **请求方式**：GET
> **接口描述**：根据班级 ID 查询单个班级详情。接口带 `@Log` 记录。

**请求参数**

格式：路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 班级 ID |

请求示例：`GET /clazz/1`

**响应数据**

格式：`Result<Clazz>`（字段同 5.2 列表项）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 班级对象 |
| &#124;- id | Integer | 班级 ID |
| &#124;- name | String | 班级名称 |
| &#124;- subject | String | 学科（中文） |
| &#124;- room | String | 教室 |
| &#124;- masterId | Integer | 班主任 ID |
| &#124;- beginDate | String | 开课日期 |
| &#124;- endDate | String | 结课日期 |

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
> **接口描述**：新增班级。请求体带 `@Valid` 校验。接口带 `@Log` 记录。

**请求参数**

格式：请求体（JSON），`ClazzDto`。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| name | String | 否 | 班级名称（Service 校验非空） |
| room | String | 是 | 教室（`@NotBlank`） |
| beginDate | String | 是 | 开课日期 `yyyy-MM-dd`（`@NotNull`） |
| endDate | String | 是 | 结课日期 `yyyy-MM-dd`（`@NotNull`） |
| masterId | Integer | 否 | 班主任 ID |
| subject | Integer | 否 | 学科编码：1 语文 ~ 6 生物 |

请求示例：

```json
{
  "name": "JavaEE就业168期",
  "room": "101教室",
  "beginDate": "2026-01-01",
  "endDate": "2026-06-30",
  "masterId": 1,
  "subject": 1
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

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
> **接口描述**：根据班级 ID 修改班级信息。接口带 `@Log` 记录。

**请求参数**

格式：请求体（JSON），`ClazzDto`（修改时必须携带 `id` 和 `masterId`）。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 班级 ID |
| masterId | Integer | 是 | 班主任 ID |
| 其余字段 | - | - | 同 5.4 新增班级 |

请求示例：

```json
{
  "id": 12,
  "name": "JavaEE就业168期",
  "room": "102教室",
  "beginDate": "2026-02-01",
  "endDate": "2026-07-31",
  "masterId": 2,
  "subject": 2
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

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
> **接口描述**：根据班级 ID 删除班级。接口带 `@Log` 记录。

**请求参数**

格式：路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 待删除的班级 ID |

请求示例：`DELETE /clazz/12`

**响应数据**

格式：`Result`（`data` 为 `null`）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

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

> 说明：`/students/**` 全部接口在 Token 拦截器中放行，**无需 Token** 即可访问。

### 6.1 分页查询学员列表

> **请求路径**：`/students/page`
> **请求方式**：GET
> **接口描述**：分页查询学员列表，支持按姓名、班级、学历筛选。

**请求参数**

格式：查询参数（Query String），`StudentQuery` 继承公共分页参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| pageNo | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |
| sortBy | String | 否 | 排序字段 |
| asc | Boolean | 否 | 是否升序 |
| name | String | 否 | 学员姓名（精确匹配） |
| clazzId | Integer | 否 | 班级 ID |
| degree | Integer | 否 | 学历编码：1 初中 ~ 6 博士 |

请求示例：`GET /students/page?pageNo=1&pageSize=5&degree=4`　（也可 `?name=胡图图`、`?clazzId=1`）

**响应数据**

格式：`Result<PageVo<StudentVo>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 分页对象 |
| &#124;- total | Integer | 总记录数 |
| &#124;- pages | Integer | 总页数 |
| &#124;- list | Array | 学员列表 |
| &#124;&#124;- id | Integer | 学员 ID |
| &#124;&#124;- name | String | 姓名 |
| &#124;&#124;- no | String | 学号 |
| &#124;&#124;- gender | String | 性别（中文） |
| &#124;&#124;- phone | String | 手机号 |
| &#124;&#124;- idCard | String | 身份证号 |
| &#124;&#124;- isCollege | Boolean | 是否来自院校 |
| &#124;&#124;- address | String | 联系地址 |
| &#124;&#124;- degree | String | 学历（中文） |
| &#124;&#124;- graduationDate | String | 毕业时间 |
| &#124;&#124;- clazzId | Integer | 班级 ID |
| &#124;&#124;- violationCount | Integer | 违纪次数 |
| &#124;&#124;- violationScore | Integer | 违纪扣分 |
| &#124;&#124;- createTime | String | 创建时间 |
| &#124;&#124;- updateTime | String | 修改时间 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 17,
    "pages": 4,
    "list": [
      {
        "id": 1,
        "name": "胡图图",
        "no": "2022000001",
        "gender": "男",
        "phone": "18800000001",
        "idCard": "110120000300200001",
        "isCollege": true,
        "address": "翻斗花园1号楼",
        "degree": "初中",
        "graduationDate": "2021-07-01T00:00:00.000+08:00",
        "clazzId": 1,
        "violationCount": 0,
        "violationScore": 0,
        "createTime": "2023-11-14T21:22:19.000+08:00",
        "updateTime": "2023-11-15T16:20:59.000+08:00"
      }
    ]
  }
}
```

---

### 6.2 新增学员

> **请求路径**：`/students`
> **请求方式**：POST
> **接口描述**：新增学员。请求体带 `@Valid` 校验。接口带 `@Log` 记录。

**请求参数**

格式：请求体（JSON），`StudentDto`。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| name | String | 是 | 姓名（`@NotBlank`） |
| no | String | 是 | 学号（`@NotBlank`） |
| gender | Integer | 是 | 性别编码：1 男，2 女（`@NotNull`） |
| phone | String | 是 | 手机号（`@NotBlank`） |
| idCard | String | 否 | 身份证号 |
| isCollege | Boolean | 否 | 是否来自院校，`true` 存 1、`false` 存 0 |
| address | String | 否 | 联系地址 |
| degree | Integer | 是 | 学历编码：1 初中 ~ 6 博士（`@NotNull`） |
| graduationDate | String | 否 | 毕业时间 `yyyy-MM-dd` |
| clazzId | Integer | 是 | 所属班级 ID（`@NotNull`） |
| violationCount | Integer | 否 | 违纪次数 |
| violationScore | Integer | 否 | 违纪扣分 |

请求示例：

```json
{
  "name": "王小美",
  "no": "2022000099",
  "gender": 2,
  "phone": "13900000099",
  "idCard": "110101199001011234",
  "isCollege": true,
  "address": "翻斗花园9号楼",
  "degree": 4,
  "graduationDate": "2022-07-01",
  "clazzId": 1,
  "violationCount": 0,
  "violationScore": 0
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

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
> **接口描述**：根据学员 ID 查询单个学员详情。

**请求参数**

格式：路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 学员 ID |

请求示例：`GET /students/1`

**响应数据**

格式：`Result<Student>`（返回实体对象，注意此处 `gender`/`isCollege` 为**数字编码**而非中文/布尔）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 学员对象 |
| &#124;- id | Integer | 学员 ID |
| &#124;- name | String | 姓名 |
| &#124;- no | String | 学号 |
| &#124;- gender | Integer | 性别编码：1 男，2 女 |
| &#124;- phone | String | 手机号 |
| &#124;- idCard | String | 身份证号 |
| &#124;- isCollege | Integer | 是否来自院校：1 是，0 否 |
| &#124;- address | String | 联系地址 |
| &#124;- degree | String | 学历（中文） |
| &#124;- graduationDate | String | 毕业时间 |
| &#124;- clazzId | Integer | 班级 ID |
| &#124;- violationCount | Integer | 违纪次数 |
| &#124;- violationScore | Integer | 违纪扣分 |
| &#124;- createTime | String | 创建时间 |
| &#124;- updateTime | String | 修改时间 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "胡图图",
    "no": "2022000001",
    "gender": 1,
    "phone": "18800000001",
    "idCard": "110120000300200001",
    "isCollege": 1,
    "address": "翻斗花园1号楼",
    "degree": "初中",
    "graduationDate": "2021-07-01T00:00:00.000+08:00",
    "clazzId": 1,
    "violationCount": 0,
    "violationScore": 0,
    "createTime": "2023-11-14T21:22:19.000+08:00",
    "updateTime": "2023-11-15T16:20:59.000+08:00"
  }
}
```


---

### 6.4 修改学员（含违纪处理）

> **请求路径**：`/students`
> **请求方式**：PUT
> **接口描述**：根据学员 ID 修改学员信息。违纪处理即通过本接口更新 `violationCount` 和 `violationScore` 实现。接口带 `@Log` 记录。

**请求参数**

格式：请求体（JSON），`StudentDto`（修改时必须携带 `id`、`violationCount`、`violationScore`）。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Integer | 是 | 学员 ID |
| violationCount | Integer | 是 | 违纪次数（Service 判空） |
| violationScore | Integer | 是 | 违纪扣分（Service 判空） |
| 其余字段 | - | - | 同 6.2 新增学员 |

请求示例（违纪处理：违纪次数 +1、扣分 +5）：

```json
{
  "id": 1,
  "name": "胡图图",
  "no": "2022000001",
  "gender": 1,
  "phone": "18800000001",
  "idCard": "110120000300200001",
  "isCollege": true,
  "address": "翻斗花园1号楼",
  "degree": 1,
  "graduationDate": "2021-07-01",
  "clazzId": 1,
  "violationCount": 1,
  "violationScore": 5
}
```

**响应数据**

格式：`Result`（`data` 为 `null`）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 6.5 删除学员（批量）

> **请求路径**：`/students/{ids}`
> **请求方式**：DELETE
> **接口描述**：批量删除学员，路径中可传一个或多个 ID（逗号分隔）。接口带 `@Log` 记录。

**请求参数**

格式：路径参数。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| ids | List&lt;Integer&gt; | 是 | 学员 ID，多个用英文逗号分隔 |

请求示例：`DELETE /students/22`　或　`DELETE /students/22,23`

**响应数据**

格式：`Result`（`data` 为 `null`）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Null | 固定为 null |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 6.6 学员违纪处理（实现方式说明）

> **接口描述**：需求中的「学员违纪处理」**没有独立接口**，其功能通过 **6.4 修改学员** 实现：更新目标学员的 `violationCount`（违纪次数）与 `violationScore`（违纪扣分）两个字段即可。

---

## 七、数据统计（/report、/log）

统计接口分为两种数据组织模式：

- **对象数组模式**：`data` 为对象数组，每项含「分类名 + 数量」，如 `[{ "name": "男性员工", "value": 3 }]`，适用于性别、职位、学历统计。
- **双数组模式**：`data` 为对象，内含两个等长数组（分类名数组 + 数量数组），按下标一一对应，适用于班级人数统计（便于 ECharts 双轴渲染）。

### 7.1 员工性别统计

> **请求路径**：`/report/empGenderData`
> **请求方式**：GET
> **接口描述**：统计各性别的员工人数（对象数组模式）。

**请求参数**

格式：无请求参数。

**响应数据**

格式：`Result<List<Object>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Array | 统计列表 |
| &#124;- name | String | 分类名称（男性员工/女性员工） |
| &#124;- value | Integer | 该分类人数 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    { "name": "男性员工", "value": 3 },
    { "name": "女性员工", "value": 2 }
  ]
}
```

---

### 7.2 员工职位统计

> **请求路径**：`/report/empJobData`
> **请求方式**：GET
> **接口描述**：统计各职位的员工人数（对象数组模式）。

**请求参数**

格式：无请求参数。

**响应数据**

格式：`Result<List<Object>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Array | 统计列表 |
| &#124;- job | String | 职位名称（中文） |
| &#124;- value | Integer | 该职位人数 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    { "job": "讲师", "value": 3 },
    { "job": "班主任", "value": 2 }
  ]
}
```

---

### 7.3 学员学历统计

> **请求路径**：`/report/studentDegreeData`
> **请求方式**：GET
> **接口描述**：统计各学历的学员人数（对象数组模式）。

**请求参数**

格式：无请求参数。

**响应数据**

格式：`Result<List<Object>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Array | 统计列表 |
| &#124;- degree | String | 学历名称（中文） |
| &#124;- value | Integer | 该学历人数 |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    { "degree": "初中", "value": 1 },
    { "degree": "高中", "value": 4 },
    { "degree": "大专", "value": 3 },
    { "degree": "本科", "value": 7 },
    { "degree": "硕士", "value": 2 }
  ]
}
```

---

### 7.4 班级人数统计

> **请求路径**：`/report/studentCountData`
> **请求方式**：GET
> **接口描述**：统计每个班级的学员人数（双数组模式）。

**请求参数**

格式：无请求参数。

**响应数据**

格式：`Result<Object>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 统计对象 |
| &#124;- clazzNameList | Array&lt;String&gt; | 班级名称数组 |
| &#124;- studentCountList | Array&lt;Integer&gt; | 对应班级人数数组（与名称数组下标对应） |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "clazzNameList": [
      "大耳朵图图趣味启蒙班",
      "甜心格格国风成长班",
      "猪猪侠冒险特训一班",
      "猪猪侠冒险特训二班",
      "JavaEE就业166期"
    ],
    "studentCountList": [3, 4, 0, 0, 1]
  }
}
```

---

### 7.5 操作日志分页查询

> **请求路径**：`/log/page`
> **请求方式**：GET
> **接口描述**：分页查询操作日志（由 `@Log` + AOP 切面自动记录增删改操作）。

**请求参数**

格式：查询参数（Query String），`PageQuery`。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| pageNo | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |
| sortBy | String | 否 | 排序字段 |
| asc | Boolean | 否 | 是否升序 |

请求示例：`GET /log/page?pageNo=1&pageSize=10`

**响应数据**

格式：`Result<PageVo<OperateLogVo>>`

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | Object | 分页对象 |
| &#124;- total | Integer | 总记录数 |
| &#124;- pages | Integer | 总页数 |
| &#124;- list | Array | 日志列表 |
| &#124;&#124;- id | Integer | 日志 ID |
| &#124;&#124;- operateEmpId | Integer | 操作人 ID |
| &#124;&#124;- operateEmpName | String | 操作人姓名（关联 emp 查询） |
| &#124;&#124;- operateTime | Date | 操作时间 |
| &#124;&#124;- className | String | 操作的类名 |
| &#124;&#124;- methodName | String | 操作的方法名 |
| &#124;&#124;- methodParams | String | 方法参数 |
| &#124;&#124;- returnValue | String | 方法返回值 |
| &#124;&#124;- costTime | Long | 耗时（毫秒） |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 41,
    "pages": 21,
    "list": [
      {
        "id": 1,
        "operateEmpId": null,
        "operateEmpName": null,
        "operateTime": null,
        "className": null,
        "methodName": null,
        "methodParams": null,
        "returnValue": "Result(code=1, msg=success, data=null)",
        "costTime": 657
      }
    ]
  }
}
```

---

## 八、其他接口

### 8.1 用户登录

> **请求路径**：`/user/login`
> **请求方式**：POST
> **接口描述**：员工登录，校验用户名密码，成功返回 JWT Token。

**请求参数**

格式：请求体（JSON），`EmpLoginDto`，带 `@Valid` 校验。

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

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息，失败为「用户名或密码错误」 |
| data | String | JWT Token，后续请求放入请求头 `token` |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3ODY1NTkyODE1ODQsInVzZXJJZCI6MX0.wtKJAj-u3D56ZigGxggf-B68lTAq-m4ZY7imgkvBM-c"
}
```

**备注说明**：Token 有效期由 `jwt.expire` 配置（当前 7200000 毫秒 = 2 小时）。

---

### 8.2 文件上传

> **请求路径**：`/upload`
> **请求方式**：POST
> **接口描述**：以 `multipart/form-data` 上传文件到阿里云 OSS，返回文件访问 URL。

**请求参数**

格式：表单（multipart/form-data）。

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| file | File | 是 | 上传的文件，单文件最大 10MB |

请求示例（curl）：

```bash
curl -X POST "http://localhost:8080/upload" \
  -H "token: <登录后获取的Token>" \
  -F "file=@/path/to/test.txt"
```

**响应数据**

格式：`Result<String>`（`data` 为 OSS 文件访问地址）

| 参数名 | 类型 | 说明 |
| --- | --- | --- |
| code | Integer | 响应码：1 成功，0 失败 |
| msg | String | 提示信息 |
| data | String | 文件在 OSS 上的访问 URL |

响应示例：

```json
{
  "code": 1,
  "msg": "success",
  "data": "https://java-cheny.oss-cn-beijing.aliyuncs.com/2026/08_test.txt"
}
```

**备注说明**：OSS 凭证从环境变量 `OSS_ACCESS_KEY_ID` / `OSS_ACCESS_KEY_SECRET` 读取，文件大小限制由 `spring.servlet.multipart`（10MB）控制。

---

## 附录：待完善 / 注意事项清单

1. **员工「查询全部（不分页）」**：未实现，见 4.6。
2. **学员「违纪处理」独立接口**：未实现，当前由 6.4 修改学员代替，见 6.6。
3. **员工列表字段缺失**：4.1 列表中 `id / username / phone / salary` 恒为 `null`（SQL 未查询这些列）。
4. **学员详情返回实体**：6.3 返回实体而非 VO，`gender` 为数字、`isCollege` 为 `1/0`，与列表接口不一致。
5. **部门新增校验**：3.3 控制器未加 `@Valid`，实体上的 `@NotBlank/@Size` 暂不生效。
6. **班级修改校验**：5.5 控制器未加 `@Valid`，依赖 Service 手动判空。
7. **操作日志字段不全**：7.5 中 `className / methodName / methodParams / operateTime` 尚未由 AOP 填充；Token 放行路径上的操作 `operateEmpId` 为 `null`。
8. **班主任姓名**：5.1 班级列表 `masterName` 恒为 `null`（SQL 未联查）。
9. **Token 放行范围**：`/students/**` 与 `/emp/getAllEmp` 免 Token，如需收紧安全策略可在 `WebConfig` 调整。
