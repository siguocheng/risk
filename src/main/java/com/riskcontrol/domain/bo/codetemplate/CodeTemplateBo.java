package com.riskcontrol.domain.bo.codetemplate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代码模板业务对象 code_template
 *
 * @author fallrain
 * @date 2026-04-07
 */
@Tag(name = "代码示例")
@Data
@EqualsAndHashCode(callSuper = true)
public class CodeTemplateBo extends BasePageQuery {

    @Schema(description = "名称", example = "示例名称")
    @TableField(value = "id")
    private Long id;

    @Schema(description = "订阅id")
    @TableField(value = "subscription_id")
    private Long subscriptionId;

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

    @Schema(description = "删除标识(0未删除,1已删除)")
    @TableField(value = "deleted")
    private Boolean deleted;

    @Schema(description = "创建人id")
    @TableField(value = "create_by")
    private Long createBy;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

    @Schema(description = "修改人id")
    @TableField(value = "update_by")
    private Long updateBy;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    @TableField(value = "gmt_modified")
    private LocalDateTime gmtModified;

}
