package com.riskcontrol.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易员风险指标返回结果
 *
 * @author zpc
 * @date 2026-07-13
 */
@Data
public class TraderRiskMetricsVo {

    @Schema(description = "交易员名称")
    private String traderName;

    @Schema(description = "夏普比率(Sharpe Ratio)")
    private BigDecimal sharpeRatio;

    @Schema(description = "索提诺比率(Sortino Ratio)")
    private BigDecimal sortinoRatio;

    @Schema(description = "卡玛比率(Calmar Ratio)")
    private BigDecimal calmarRatio;

    @Schema(description = "盈亏比(Win/Loss Ratio)")
    private BigDecimal winLossRatio;

    @Schema(description = "风险占比(95% VaR / 总资产)")
    private BigDecimal riskRatio;

    @Schema(description = "波动率溢价(组合IV-市场VIX)")
    private BigDecimal volatilityPremium;
}
