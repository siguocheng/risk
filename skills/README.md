# Skills 目录说明文档

## 目录结构

```
skills/
├── README.md                    # 本说明文档
├── code/                        # 代码生成相关技能
│   └── CodeSingleTableCRUD.md  # 单表 CRUD 代码生成技能
└── [其他分类目录]/              # 按需创建其他分类
```

## 目录说明

### skills/ 根目录

用于存放各类技能（Skills）文档。每个子目录代表一个技能分类，便于管理和维护。

### skills/code/ 目录

存放与代码生成相关的技能文档。目前包含单表 CRUD 代码生成功能。

## 文件说明

| 文件路径 | 说明 |
|---------|------|
| `README.md` | skills 目录说明文档，记录目录结构和使用方法 |
| `code/CodeSingleTableCRUD.md` | 单表 CRUD 代码生成技能，使用 LoadSkill 工具调用 |

## 使用方法

### 1. 调用 Skills

使用 `LoadSkill` 工具加载并执行 skills：

```java
// 方式一：直接使用技能名称
LoadSkill(skill_name="code:单表CRUD")

// 方式二：使用完整路径
LoadSkill(skill_name="code:CodeSingleTableCRUD")
```

### 2. 添加新的 Skill

1. 在 `skills/` 目录下创建分类目录（如 `code/`、`docs/`、`config/` 等）
2. 在分类目录下创建 `.md` 文件，编写技能说明
3. 更新本 `README.md` 文档，添加新技能的说明

### 3. Skill 文件格式

Skill 文件使用 Markdown 格式编写，包含以下部分：

```markdown
# Skill 名称

## 简介
简要描述这个技能的作用

## 使用方法
详细说明如何使用这个技能

## 参数说明
| 参数名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| param1 | String | 参数1说明 | "value1" |

## 示例
展示具体使用示例
```

## 示例

### 调用单表 CRUD 代码生成技能

```java
LoadSkill(skill_name="code:单表CRUD")
```

执行后，系统会提示你输入相关信息（如表名、实体类名等），然后自动生成对应的 CRUD 代码。

### 查看技能列表

在 IDE 中浏览 `skills/` 目录，查看所有可用的技能文档。

## 维护指南

- **新增技能**：在对应分类目录下创建 `.md` 文件，并更新本 README
- **更新技能**：直接编辑对应的 `.md` 文件
- **删除技能**：删除对应的 `.md` 文件，并从本 README 中移除说明
