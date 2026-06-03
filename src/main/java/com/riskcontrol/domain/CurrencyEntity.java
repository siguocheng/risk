package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @DESCRIPTION: 共有的
 * @USER: fallrain
 * @DATE: 2023/5/12 9:22
 */
@Data
public abstract  class CurrencyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;

    @Schema(description = "删除标识(0未删除,1已删除)", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    protected Boolean isDeleted;

    @Schema(description = "创建人ID", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    protected Long createBy;

    @Schema(description = "创建时间", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime gmtCreate;

    @Schema(description = "修改人ID", hidden = true)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    protected Long updateBy;

    @Schema(description = "更新时间", hidden = true)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    protected LocalDateTime gmtModified;
}
