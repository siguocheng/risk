package com.riskcontrol.domain.vo.positionrelation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 策略和交易员和账号和持仓之间的关系历史分页结果
 *
 * @author zpc
 * @date 2026-07-11
 */
@Data
public class PositionRelationHistoryPage {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "yyyyMMdd格式日期")
    private String dailyDate;

    @Schema(description = "账号id")
    private String accountCode;

    @Schema(description = "合约id")
    private Integer conid;

    @Schema(description = "策略名称")
    private String strategyName;

    @Schema(description = "交易员")
    private String traderName;

    @Schema(description = "持仓股数")
    private BigDecimal positionQty;

    @Schema(description = "未实现盈亏")
    private BigDecimal unrealizedPnl;

    @Schema(description = "实现盈亏")
    private BigDecimal realizedPnl;

    @Schema(description = "佣金及各项费用")
    private BigDecimal commissionAndFees;

    @Schema(description = "市场价")
    private BigDecimal marketPrice;

    @Schema(description = "修改时间")
    private String modifiedTime;

    @Schema(description = "合约类型")
    private String secType;

    @Schema(description = "合约乘数")
    private String multiplier;

    @Schema(description = "合约简称")
    private String symbol;
}