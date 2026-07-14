package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易出入库
 *
 * @author zpc
 * @date 2026-07-05
 */
@Data
@TableName("position_execution_in_out")
public class PositionExecutionInOut extends BaseEntity {

    @Schema(description = "入库交易id")
    @TableField(value = "position_execution_in_id")
    private Long positionExecutionInId;

    @Schema(description = "出库交易id")
    @TableField(value = "position_execution_out_id")
    private Long positionExecutionOutId;

    @Schema(description = "出库数量")
    @TableField(value = "qty")
    private BigDecimal qty;

}