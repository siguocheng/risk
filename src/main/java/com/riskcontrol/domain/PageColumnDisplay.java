package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 页面列表列展示实体类
 *
 * @author zpc
 * @date 2026-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("page_column_display")
public class PageColumnDisplay extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "页面名称")
    @TableField(value = "page_name")
    private String pageName;

    @Schema(description = "类型")
    @TableField(value = "type")
    private String type;

    @Schema(description = "列名称")
    @TableField(value = "column_name")
    private String columnName;

    @Schema(description = "是否展示")
    @TableField(value = "is_display")
    private Boolean isDisplay;

    @Schema(description = "是否展示")
    @TableField(value = "column_key")
    private String columnKey;
}
