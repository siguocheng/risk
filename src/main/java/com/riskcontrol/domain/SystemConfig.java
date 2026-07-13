package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体类
 *
 * @author zpc
 * @date 2026-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_config")
public class SystemConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "配置键")
    @TableField(value = "item_key")
    private String itemKey;

    @Schema(description = "配置值")
    @TableField(value = "item_value")
    private String itemValue;

    @Schema(description = "描述")
    @TableField(value = "display")
    private String display;
}
