package com.riskcontrol.domain.vo.codetemplate;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代码模板视图对象 code_template
 *
 * @author fallrain
 * @date 2026-04-07
 */
@Data
public class CodeTemplateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableField(value = "id")
    private Long id;

    @Schema(description = "订阅id")
    @TableField(value = "subscription_id")
    private Long subscriptionId;

    @Schema(description = "代码id")
    @TableField(value = "code_id")
    @ExcelProperty(value = "代码id", index = 0)
    @ColumnWidth(25)
    private Long codeId;

    @Schema(description = "代码状态：1-未保存 2-已保存")
    @TableField(value = "code_state")
    @ExcelProperty(value = "代码状态：1-未保存 2-已保存", index = 1)
    @ColumnWidth(25)
    private Integer codeState;

    @Schema(description = "代码名称")
    @TableField(value = "code_name")
    @ExcelProperty(value = "代码名称", index = 2)
    @ColumnWidth(25)
    private String codeName;

    @Schema(description ="代码代码")
    @TableField(value = "code_code")
    @ExcelProperty(value = "代码代码", index = 3)
    @ColumnWidth(25)
    private String codeCode;

    @Schema(description = "代码状态(0-否，1-是)")
    @TableField(value = "code_status")
    @ExcelProperty(value = "代码状态(0-否，1-是)", index = 4)
    @ColumnWidth(25)
    private Integer codeStatus;

    @Schema(description = "代码类型(0-通用，1-不通用)")
    @TableField(value = "code_type")
    @ExcelProperty(value = "代码类型(0-通用，1-不通用)", index = 5)
    @ColumnWidth(25)
    private Integer codeType;

    @Schema(description = "代码URL")
    @TableField(value = "code_url")
    @ExcelProperty(value = "代码URL", index = 6)
    @ColumnWidth(25)
    private String codeUrl;

    @Schema(description = "代码富文本内容")
    @TableField(value = "code_text")
    @ExcelProperty(value = "代码富文本内容", index = 7)
    @ColumnWidth(25)
    private String codeText;

    @Schema(description ="生产棒数")
    @TableField(value = "number_production_rod")
    @ExcelProperty(value = "生产棒数", index = 8)
    @ColumnWidth(25)
    private BigDecimal numberProductionRod;

    @Schema(description = "备注")
    @TableField(value = "remark")
    @ExcelProperty(value = "备注", index = 9)
    @ColumnWidth(25)
    private String remark;

    @Schema(description = "删除标识(0未删除,1已删除)")
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    @Schema(description = "创建人id")
    @TableField(value = "create_by")
    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

    @Schema(description = "修改人id")
    @TableField(value = "update_by")
    private Long updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    @TableField(value = "gmt_modified")
    private LocalDateTime gmtModified;

}
