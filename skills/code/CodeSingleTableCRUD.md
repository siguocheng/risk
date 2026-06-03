# CodeSingleTableCRUD

## 概述

根据数据库表结构自动生成完整的 CRUD 代码文件，包括 Controller、Service、Mapper、Entity、VO、BO 等。

## 触发关键词

- 生成代码
- 代码生成
- 生成CRUD
- 生成增删改查
- 单表CRUD

## 输入参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| tableName | string | 是 | 数据库表名（英文） |
| tableComment | string | 是 | 表的中文注释/描述 |
| columns | array | 是 | 表字段信息数组 |
| basePackage | string | 否 | 基础包路径，默认为 com.riskcontrol |

### columns 数组项结构

| 字段 | 类型 | 说明 |
|------|------|------|
| name | string | 字段名（英文） |
| comment | string | 字段中文注释 |
| type | string | Java类型（Long, String, Integer, BigDecimal, LocalDateTime） |
| dbType | string | 数据库类型（bigint, varchar, int, decimal, datetime） |
| nullable | boolean | 是否可为空 |
| primaryKey | boolean | 是否为主键 |
| autoIncrement | boolean | 是否自增 |

## 生成文件列表

根据模板，会生成以下 8 个文件：

| 文件类型 | 生成路径 | 说明 |
|---------|---------|------|
| Controller | `controller/{moduleName}/{EntityName}Controller.java` | 继承 BaseController，包含完整的 CRUD 接口 |
| Service接口 | `service/I{EntityName}Service.java` | 定义业务方法接口 |
| Service实现 | `service/impl/{EntityName}ServiceImpl.java` | 实现业务逻辑，继承 BaseMapperPlus |
| Mapper接口 | `dao/{EntityName}Mapper.java` | 继承 BaseMapperPlus |
| Mapper XML | `mapper/{EntityName}Mapper.xml` | MyBatis 映射文件（位于 src/main/java/com/wuyuan/industry/mapper 目录） |
| Entity | `domain/{EntityName}.java` | 继承 CurrencyEntity |
| VO | `domain/vo/{moduleName}/{EntityName}Vo.java` | 视图对象，支持 Excel 导出 |
| BO | `domain/bo/{moduleName}/{EntityName}Bo.java` | 业务对象，继承 BasePageQuery，支持分页 |

## 文件夹规则

- **Controller、VO、BO**：放在以表名转换后的模块文件夹下（如 `codetemplate`）
- **Service、Mapper接口、Entity**：放在根目录
- **Mapper XML**：放在 `src/main/java/com/wuyuan/industry/mapper` 目录下（不是 resources 目录）

### 命名转换规则

**1. 包路径和文件夹名称**（用于 Java package 和文件夹命名）：
- 表名：`code_template` → 模块名：`codetemplate`
- 表名：`mold_device` → 模块名：`molddevice`
- 规则：去除下划线，全部小写，直接拼接

**2. 请求路径**（用于 `@RequestMapping` 注解）：
- 表名：`code_template` → 路径：`code-template`
- 表名：`mold_device` → 路径：`mold-device`
- 规则：下划线替换为中划线，全部小写
- 格式：**小写字母 + 中划线**

**3. 实体名称**（用于类名）：
- 表名：`code_template` → 实体名：`CodeTemplate`
- 规则：使用驼峰命名，首字母大写，去除下划线

## 已有文件处理规则

**在生成代码前，必须检查目标文件是否已存在：**

1. 如果文件不存在，直接生成
2. 如果文件已存在，**必须询问用户**选择：
   - **覆盖**：完全替换原有文件（适用于全新生成）
   - **修改**：在原有基础上进行增量修改，保留原有业务逻辑，只更新字段等元信息（适用于在已有代码上添加新字段）

## 代码模板规范

### 1. Controller 规范

**重要说明：**
- Controller 必须继承 `BaseController`
- **优先使用项目中的类**，包括：
  - `com.riskcontrol.enums.BusinessType`（业务类型枚举）
  - `com.riskcontrol.controller.BaseController`（基础控制器）
- **返回值优化**：不再使用 `ResultBean<T>` 包装，直接返回业务数据
  - 分页查询：返回 `IPage<{EntityName}Vo>`
  - 详情查询：返回 `{EntityName}Vo`
  - 新增/修改/删除：返回 `Boolean`
  - 导出：返回 `void`

```java
package com.riskcontrol.controller.{moduleName};

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.annotation.Log;
import com.riskcontrol.enums.BusinessType;
import com.riskcontrol.controller.BaseController;
import com.riskcontrol.domain.bo.{moduleName}.{EntityName}Bo;
import com.riskcontrol.domain.vo.{moduleName}.{EntityName}Vo;
import com.riskcontrol.service.I{EntityName}Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * {tableComment}控制器
 *
 * @author zpc
 * @date {currentDate}
 */
@Tag(name =  "{tableComment}管理")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/{请求路径}")  // 例如：code-template（小写字母 + 中划线）
public class {EntityName}Controller extends BaseController {

    private final I{EntityName}Service {entityName}Service;

    /**
     * 分页查询{tableComment}列表
     *
     * @param request HTTP请求对象
     * @param bo 查询条件
     * @return 分页结果
     */
    @PostMapping("/pc/query-page")
    @Operation(summary = "获取{tableComment}分页列表")
    @ResourceMethod(btnCode = "btn-pc-{entityName}-query-page", level = 3)
    public IPage<{EntityName}Vo> list(HttpServletRequest request, 
            @Validated(QueryGroup.class) @RequestBody {EntityName}Bo bo) {
        setSubscriptionIdAndUserId(bo, request);
        return {entityName}Service.queryPageList(bo);
    }

    /**
     * 导出{tableComment}列表
     *
     * @param request HTTP请求对象
     * @param bo 查询条件
     * @param response HTTP响应对象
     */
    @Log(title = "{tableComment}", businessType = BusinessType.EXPORT)
    @PostMapping("/pc/export-excel")
    @Operation(summary = "导出{tableComment}列表")
    @ResourceMethod(btnCode = "btn-pc-{entityName}-export-excel", level = 3)
    public void export(HttpServletRequest request, {EntityName}Bo bo, HttpServletResponse response) {
        setSubscriptionIdAndUserId(bo, request);
        List<{EntityName}Vo> list = {entityName}Service.queryList(bo);
        try (OutputStream outputStream = response.getOutputStream()) {
            setResponseToExcel(response, URLEncoder.encode("{tableComment}.xlsx", "UTF-8"));
            EasyExcel.write(outputStream, {EntityName}Vo.class).sheet().doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException("导出失败");
        }
    }

    /**
     * 获取{tableComment}详细信息
     *
     * @param request HTTP请求对象
     * @param id 主键ID
     * @return {tableComment}详情
     */
    @GetMapping("/pc/query-detail")
    @Operation(summary = "获取{tableComment}详细信息")
    @ResourceMethod(btnCode = "btn-pc-{entityName}-query-detail", level = 3)
    public {EntityName}Vo getInfo(HttpServletRequest request, 
            @NotNull(message = "主键不能为空") @RequestParam("id") Long id) {
        return {entityName}Service.queryById(id);
    }

    /**
     * 新增{tableComment}
     *
     * @param request HTTP请求对象
     * @param bo {tableComment}信息
     * @return 操作结果
     */
    @Log(title = "{tableComment}", businessType = BusinessType.INSERT)
    @PostMapping("/pc/create")
    @Operation(summary = "新增{tableComment}")
    @ResourceMethod(btnCode = "btn-pc-{entityName}-create", level = 3)
    public Boolean add(HttpServletRequest request, 
            @Validated(AddGroup.class) @RequestBody {EntityName}Bo bo) {
        setSubscriptionIdAndUserId(bo, request);
        return {entityName}Service.insertByBo(bo);
    }

    /**
     * 修改{tableComment}
     *
     * @param request HTTP请求对象
     * @param bo {tableComment}信息
     * @return 操作结果
     */
    @Log(title = "{tableComment}", businessType = BusinessType.UPDATE)
    @PostMapping("/pc/update")
    @Operation(summary = "修改{tableComment}")
    @ResourceMethod(btnCode = "btn-pc-{entityName}-update", level = 3)
    public Boolean edit(HttpServletRequest request, 
            @Validated(EditGroup.class) @RequestBody {EntityName}Bo bo) {
        setSubscriptionIdAndUserId(bo, request);
        return {entityName}Service.updateByBo(bo);
    }

    /**
     * 删除{tableComment}
     *
     * @param request HTTP请求对象
     * @param bo 删除条件
     * @return 操作结果
     */
    @Log(title = "{tableComment}", businessType = BusinessType.BATCH_DELETE)
    @PostMapping("/pc/remove-batch")
    @Operation(summary = "删除{tableComment}")
    @ResourceMethod(btnCode = "btn-pc-{entityName}-remove-batch", level = 3)
    public Boolean remove(HttpServletRequest request, 
            @Validated(DeleteGroup.class) @RequestBody {EntityName}Bo bo) {
        setSubscriptionIdAndUserId(bo, request);
        return {entityName}Service.deleteWithValidByIds(bo, true);
    }
}
```

**关键点：**
- 继承 `BaseController`（来自 `com.riskcontrol.controller.BaseController`）
- 使用 `@Validated` + 分组校验 (`AddGroup`, `EditGroup`, `QueryGroup`, `DeleteGroup`)
- 使用 `@ResourceMethod` 做权限控制
- 使用 `@Log` 记录操作日志，**BusinessType 来自 `com.riskcontrol.enums.BusinessType`**
- **必须为所有方法添加 `@Operation` 注解**，用于 Swagger 文档展示
- 使用 `@Tag` 定义 Controller 级别的标签
- **返回值不使用 ResultBean 包装**，直接返回业务数据类型
- 删除操作使用 `BusinessType.BATCH_DELETE` 而非 `BusinessType.DELETE`

### 2. Service 接口规范

**重要说明：**
- 优先使用项目中的类：
  - `com.baomidou.mybatisplus.core.metadata.IPage`（分页接口）
  - `com.riskcontrol.domain.bo.{moduleName}.{EntityName}Bo`（业务对象）
  - `com.riskcontrol.domain.vo.{moduleName}.{EntityName}Vo`（视图对象）

```java
package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.domain.bo.{moduleName}.{EntityName}Bo;
import com.riskcontrol.domain.vo.{moduleName}.{EntityName}Vo;

import java.util.List;

/**
 * {tableComment}Service接口
 *
 * @author zpc
 * @date {currentDate}
 */
public interface I{EntityName}Service {
    
    /**
     * 查询{tableComment}
     *
     * @param id 主键ID
     * @return {tableComment}
     */
    {EntityName}Vo queryById(Long id);
    
    /**
     * 查询{tableComment}分页列表
     *
     * @param bo 查询条件
     * @return 分页结果
     */
    IPage<{EntityName}Vo> queryPageList({EntityName}Bo bo);
    
    /**
     * 查询{tableComment}列表
     *
     * @param bo 查询条件
     * @return 列表数据
     */
    List<{EntityName}Vo> queryList({EntityName}Bo bo);
    
    /**
     * 新增{tableComment}
     *
     * @param bo {tableComment}信息
     * @return 是否成功
     */
    Boolean insertByBo({EntityName}Bo bo);
    
    /**
     * 修改{tableComment}
     *
     * @param bo {tableComment}信息
     * @return 是否成功
     */
    Boolean updateByBo({EntityName}Bo bo);
    
    /**
     * 批量删除{tableComment}
     *
     * @param bo 删除条件
     * @param isValid 是否校验
     * @return 是否成功
     */
    Boolean deleteWithValidByIds({EntityName}Bo bo, Boolean isValid);
}
```

### 3. Service 实现规范

**重要说明：**
- 优先使用项目中的工具类和实体类：
  - `com.riskcontrol.util.WYObjectUtils`（对象转换工具）
  - `com.riskcontrol.util.WYStringUtils`（字符串工具）
  - `com.riskcontrol.entity.CurrencyEntity`（基础实体）
  - `com.riskcontrol.dao.{EntityName}Mapper`（Mapper接口位于dao包）

```java
package com.riskcontrol.service.impl;

{EntityName}Mapper;
{EntityName};
{moduleName}.{EntityName}Bo;
{moduleName}.{EntityName}Vo;
import com.riskcontrol.entity.CurrencySubEntity;
import com.riskcontrol.service.I{EntityName}Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * {tableComment}Service业务层处理
 *
 * @author zpc
 * @date {currentDate}
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class {EntityName}ServiceImpl implements I{EntityName}Service{

private final {EntityName}Mapper baseMapper;

/**
 * 查询{tableComment}
 *
 * @param id 主键ID
 * @return {tableComment}
 */
@Override
public {EntityName}Vo queryById(Long id){
        return baseMapper.selectVoById(id);
        }

/**
 * 查询{tableComment}分页列表
 *
 * @param bo 查询条件
 * @return 分页结果
 */
@Override
public IPage<{EntityName}Vo>queryPageList({EntityName}Bo bo){
        LambdaQueryWrapper<{EntityName}>lqw=buildQueryWrapper(bo);
        Page<{EntityName}Vo>result=baseMapper.selectVoPage(bo.build(),lqw);
        return result;
        }

/**
 * 查询{tableComment}列表
 *
 * @param bo 查询条件
 * @return 列表数据
 */
@Override
public List<{EntityName}Vo>queryList({EntityName}Bo bo){
        LambdaQueryWrapper<{EntityName}>lqw=buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
        }

/**
 * 构建查询条件
 *
 * @param bo 查询条件
 * @return LambdaQueryWrapper
 */
private LambdaQueryWrapper<{EntityName}>buildQueryWrapper({EntityName}Bo bo){
        Map<String, Object> params=bo.getParams();
        LambdaQueryWrapper<{EntityName}>lqw=Wrappers.lambdaQuery();
        lqw.orderByAsc({EntityName}::getId);

        lqw.eq({EntityName}::getSubscriptionId,bo.getSubscriptionId());
        lqw.eq({EntityName}::getIsDeleted,Boolean.FALSE);
        // 动态条件构建示例：
        // lqw.eq(WYObjectUtils.isNotNull(bo.getXxx()), {EntityName}::getXxx, bo.getXxx());
        // lqw.like(WYStringUtils.isNotBlank(bo.getXxx()), {EntityName}::getXxx, bo.getXxx());
        // lqw.between(WYObjectUtils.isNotNull(params.get("beginTime")) && WYObjectUtils.isNotNull(params.get("endTime")), 
        //             {EntityName}::getGmtCreate, params.get("beginTime"), params.get("endTime"));
        return lqw;
        }

/**
 * 新增{tableComment}
 *
 * @param bo {tableComment}信息
 * @return 是否成功
 */
@Override
public Boolean insertByBo({EntityName}Bo bo){
        {EntityName}add=WYObjectUtils.convert(bo,{EntityName}.class);
        validEntityBeforeSave(add);
        boolean flag=baseMapper.insert(add)>0;
        if(flag){
        bo.setId(add.getId());
        }
        return flag;
        }

/**
 * 修改{tableComment}
 *
 * @param bo {tableComment}信息
 * @return 是否成功
 */
@Override
public Boolean updateByBo({EntityName}Bo bo){
        {EntityName}update=WYObjectUtils.convert(bo,{EntityName}.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update)>0;
        }

/**
 * 保存前的数据校验
 *
 * @param entity 实体对象
 */
private void validEntityBeforeSave({EntityName}entity){
        // TODO 做一些数据校验,如唯一约束
        }

/**
 * 批量删除{tableComment}
 *
 * @param bo 删除条件
 * @param isValid 是否校验
 * @return 是否成功
 */
@Override
public Boolean deleteWithValidByIds({EntityName}Bo bo,Boolean isValid){
        if(isValid){
        // TODO 做一些业务上的校验,判断是否需要校验
        }
        // 逻辑删除
        return ChainWrappers.lambdaUpdateChain(baseMapper)
        .eq(CurrencySubEntity::getSubscriptionId,bo.getSubscriptionId())
        .in({EntityName}::getId,bo.getIdList())
        .set(CurrencyEntity::getIsDeleted,Boolean.TRUE)
        .update();
        }
        }
```

**关键点：**
- 使用 `BaseMapperPlus` 提供的增强方法 (`selectVoById`, `selectVoPage`, `selectVoList`)
- 使用 `LambdaQueryWrapper` 构建类型安全的查询条件
- **使用 `WYObjectUtils.convert()` 进行 BO → Entity 转换**（项目工具类）
- **使用 `WYStringUtils.isNotBlank()` 进行字符串判空**（项目工具类）
- **使用 `WYObjectUtils.isNotNull()` 进行对象判空**（项目工具类）
- 逻辑删除通过 `is_deleted` 字段实现
- **Mapper 接口位于 `com.riskcontrol.dao` 包**

### 4. Mapper 接口规范

**重要说明：**
- Mapper 接口位于 `com.riskcontrol.dao` 包（不是 mapper 包）
- 使用项目中的 `BaseMapperPlus` 基类

```java
package com.riskcontrol.dao;

import com.riskcontrol.domain.{EntityName};
import com.riskcontrol.domain.vo.{moduleName}.{EntityName}Vo;

/**
 * {tableComment}Mapper接口
 *
 * @author zpc
 * @date {currentDate}
 */
public interface {EntityName}Mapper extends BaseMapperPlus<{EntityName}, {EntityName}Vo> {
    // 继承 BaseMapperPlus，自动获得 CRUD 方法
    // 泛型：Entity类型, VO类型
}
```

### 5. Mapper XML 规范

**重要说明：**
- XML 文件位于 `src/main/java/com/wuyuan/industry/mapper` 目录（不是 resources 目录）
- namespace 指向 dao 包的 Mapper 接口

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.riskcontrol.dao.{EntityName}Mapper">
    <resultMap id="BaseResultMap" type="com.riskcontrol.domain.{EntityName}">
        <id column="id" jdbcType="BIGINT" property="id" />
        <!-- 更多字段映射... -->
    </resultMap>

    <sql id="Base_Column_List">
        id, subscription_id, ...
    </sql>
</mapper>
```

### 6. Entity 规范

**重要说明：**
- 继承项目中的 `CurrencyEntity`（包含 公共字段）
- 使用 Lombok 的 `@EqualsAndHashCode(callSuper = true)` 包含父类字段

```java
package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * {tableComment}实体类
 *
 * @author zpc
 * @date {currentDate}
 */
@Data
@TableName("{tableName}")
@EqualsAndHashCode(callSuper = true)
public class {EntityName} extends CurrencyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字段注释")
    @TableField(value = "字段名")
    private Long/String/Integer fieldName;

    // 更多字段...
}
```

**关键点：**
- 使用 `@TableName` 注解指定表名
- **必须添加 `@EqualsAndHashCode(callSuper = true)` 以包含父类字段**

### 7. VO 规范

**重要说明：**
- VO 直接实现 `Serializable`，不继承任何基类
- 支持 Excel 导出注解

```java
package com.riskcontrol.domain.vo.{moduleName};

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * {tableComment}视图对象
 *
 * @author zpc
 * @date {currentDate}
 */
@Data
@ExcelIgnoreUnannotated
@ColumnWidth(18)
public class {EntityName}Vo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableField(value = "id")
    private Long id;

    @Schema(description = "字段注释")
    @TableField(value = "字段名")
    @ExcelProperty(value = "字段中文名", index = 0)
    @ColumnWidth(25)
    private Long/String/Integer fieldName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    @TableField(value = "gmt_modified")
    private LocalDateTime gmtModified;

    // 支持 Excel 导出注解
}
```

**关键点：**
- 直接实现 `Serializable`
- 支持 `@ExcelProperty` 和 `@ColumnWidth` 注解用于 Excel 导出
- **必须导入** `com.fasterxml.jackson.annotation.JsonFormat` 用于日期字段格式化
- 所有 `LocalDateTime` 类型字段都需要添加 `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` 注解

### 8. BO 规范

**重要说明：**
- 继承项目中的 `BasePageQuery`（位于 `com.riskcontrol.query` 包）
- 使用 Lombok 的 `@EqualsAndHashCode(callSuper = true)` 包含父类字段

```java
package com.riskcontrol.domain.bo.{moduleName};

import com.baomidou.mybatisplus.annotation.TableField;
import com.riskcontrol.query.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {tableComment}业务对象
 *
 * @author zpc
 * @date {currentDate}
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class {EntityName}Bo extends BasePageQuery {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "字段注释")
    private Long/String/Integer fieldName;

    // 继承 BasePageQuery，获得分页能力
    // idList 字段已在父类 BaseCommand 中定义，无需重复定义
}
```

**关键点：**
- 继承 `com.riskcontrol.query.BasePageQuery`（包含分页参数：pageNum, pageSize, orderColumn, orderType）
- **必须添加 `@EqualsAndHashCode(callSuper = true)` 以包含父类字段**

## 继承关系

```
{EntityName} (Entity)
    ↑
    CurrencyEntity (包含公共字段: id, isDeleted, createBy, gmtCreate, updateBy, gmtModified)
{EntityName}Bo
    ↑
    BasePageQuery (包含分页参数)

{EntityName}Vo - 直接实现 Serializable
```

## 示例输入

```
生成代码，表名：code_template
表注释：代码模板
字段：
- id (bigint, 主键, 自增)
- code_id (bigint, 代码ID)
- code_name (varchar(100), 代码名称)
- code_content (text, 代码内容)
- code_state (int, 代码状态：1-未保存 2-已保存)
- is_deleted (bit, 是否删除)
- create_by (varchar(64), 创建人)
- gmt_create (datetime, 创建时间)
- update_by (varchar(64), 更新人)
- gmt_modified (datetime, 更新时间)
```

## 输出示例

以表名 `code_template` 为例，生成后文件结构：

```
src/main/java/com/wuyuan/industry/
├── controller/
│   └── codetemplate/                    # 模块文件夹（去除下划线，全部小写）
│       └── CodeTemplateController.java
├── service/
│   ├── ICodeTemplateService.java
│   └── impl/
│       └── CodeTemplateServiceImpl.java
├── dao/
│   └── CodeTemplateMapper.java
├── domain/
│   ├── CodeTemplate.java
│   ├── vo/codetemplate/                 # 模块文件夹（去除下划线，全部小写）
│   │   └── CodeTemplateVo.java
│   └── bo/codetemplate/                 # 模块文件夹（去除下划线，全部小写）
│       └── CodeTemplateBo.java
└── mapper/
    └── CodeTemplateMapper.xml
```

**请求路径示例：**
- Controller 的 `@RequestMapping("/code-template")`  // 小写字母 + 中划线

## 项目类引用规范

**在生成代码时，必须优先使用项目中的类，确保引用正确：**

### Controller 层
- `com.riskcontrol.controller.BaseController` - 基础控制器
- `com.riskcontrol.annotation.Log` - 日志注解
- `com.riskcontrol.enums.BusinessType` - 业务类型枚举
- `com.riskcontrol.annotation.ResourceMethod` - 资源方法注解

### Service 层
- `com.riskcontrol.util.WYObjectUtils` - 对象转换工具（替代 ObjectUtils）
- `com.riskcontrol.util.WYStringUtils` - 字符串工具
- `com.riskcontrol.entity.CurrencyEntity` - 基础实体

### DAO/Mapper 层
- `com.riskcontrol.dao.{EntityName}Mapper` - Mapper 接口位于 **dao 包**（不是 mapper 包）
- `com.riskcontrol.dao.BaseMapperPlus` - Mapper 基类

### Domain 层
- `com.riskcontrol.query.BasePageQuery` - 分页查询基类
- Entity 继承 `CurrencyEntity`
- VO 直接实现 `Serializable`
- BO 继承 `BasePageQuery`

## 注意事项

1. **路径确定**：生成代码前，首先确定项目的代码目录（`src/main/java/com/riskcontrol`）
2. **通用性**：代码生成逻辑要通用，适应不同的表结构
3. **已有文件**：必须检查文件是否存在，询问用户是覆盖还是修改
4. **字段映射**：正确处理 Java 类型与数据库类型的映射
5. **公共字段**：自动包含继承自父类的公共字段（id, isDeleted, createBy, gmtCreate, updateBy, gmtModified）
6. **作者信息**：所有生成的类必须添加 `@author zpc` 注释
7. **日期信息**：所有生成的类必须添加 `@date {currentDate}` 注释（使用当前日期）
8. **方法注释**：所有 public 和 protected 方法必须添加完整的 Javadoc 注释，包括：
   - 方法功能描述
   - @param 参数说明
   - @return 返回值说明
9. **类注释**：所有类必须添加类级别的 Javadoc 注释，包括：
   - 类的功能描述
   - @author zpc
   - @date 当前日期
10. **Swagger注解**：Controller 层的所有方法**必须**添加 `@ApiOperation` 注解，确保 Swagger 文档完整展示
11. **版本号规范**：Controller 的 `@Tag` 注解中
12. **返回值规范**：Controller 层**不使用 ResultBean 包装**，直接返回业务数据类型（IPage、VO、Boolean、void）
13. **项目工具类**：优先使用项目工具类 `WYObjectUtils`、`WYStringUtils`，而非通用工具类
14. **Lombok 注解**：Entity 和 BO 必须添加 `@EqualsAndHashCode(callSuper = true)` 以正确处理继承关系
15. **删除类型**：批量删除使用 `BusinessType.BATCH_DELETE` 而非 `BusinessType.DELETE`
16. **命名规则**：
    - **包路径/文件夹名**：去除下划线，全部小写（如 `codetemplate`）
    - **请求路径**：下划线替换为中划线，全部小写（如 `code-template`）
