package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 代码模板对象 code_template
 *
 * @author fallrain
 * @date 2026-04-07
 */
@Data
@TableName("code_template")
@EqualsAndHashCode()
public class CodeTemplate extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "代码id")
    @TableField(value = "code_id")
    private Long codeId;

    @Schema(description = "代码状态：1-未保存 2-已保存")
    @TableField(value = "code_state")
    private Integer codeState;

    @Schema(description = "代码名称")
    @TableField(value = "code_name")
    private String codeName;

    @Schema(description = "代码代码")
    @TableField(value = "code_code")
    private String codeCode;

    @Schema(description = "代码状态(0-否，1-是)")
    @TableField(value = "code_status")
    private Integer codeStatus;

    @Schema(description = "代码类型(0-通用，1-不通用)")
    @TableField(value = "code_type")
    private Integer codeType;

    @Schema(description = "代码URL")
    @TableField(value = "code_url")
    private String codeUrl;

    @Schema(description = "代码富文本内容")
    @TableField(value = "code_text")
    private String codeText;

    @Schema(description = "生产棒数")
    @TableField(value = "number_production_rod")
    private BigDecimal numberProductionRod;

    @Schema(description = "备注")
    @TableField(value = "remark")
    private String remark;

}
