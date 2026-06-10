package com.riskcontrol.domain.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 持仓列表业务对象
 *
 * @author zpc
 * @date 2026-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PositionBo extends BasePageQuery {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "合约id")
    private Long conId;

    @Schema(description = "账号编号")
    private String accountCode;

    @Schema(description = "模型代码")
    private String modelCode;

    @Schema(description = "持仓股数")
    @TableField(value = "position")
    private BigDecimal positionQty;

    @Schema(description = "平均成本价")
    private BigDecimal avgCost;

    @Schema(description = "未实现盈亏")
    private BigDecimal unrealizedPnl;

    @Schema(description = "市场价格")
    private BigDecimal marketPrice;

    @Schema(description = "市场值")
    private BigDecimal marketValue;

    @Schema(description = "实现盈亏")
    private BigDecimal realizedPnl;
}
