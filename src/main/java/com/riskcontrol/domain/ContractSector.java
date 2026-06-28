package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("contract_sector")
@Schema(description = "股票合约板块信息表")
public class ContractSector extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "自增主键")
    private Integer id;

    @TableField("conid")
    @Schema(description = "IB合约ID")
    private Integer conid;

    @TableField("type")
    @Schema(description = "标的类型")
    private Integer type;

    @TableField("sector")
    @Schema(description = "行业板块")
    private String sector;

    @TableField("deleted")
    @Schema(description = "删除标识")
    private Integer deleted;

    @TableField("create_id")
    @Schema(description = "创建人")
    private Long createId;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private java.time.LocalDateTime createTime;

    @TableField("modified_id")
    @Schema(description = "更新人")
    private Long modifiedId;

    @TableField("modified_time")
    @Schema(description = "更新时间")
    private java.time.LocalDateTime modifiedTime;
}
