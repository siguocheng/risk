package com.riskcontrol.domain.vo.investmentstrategy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 投资策略分页结果
 *
 * @author zpc
 * @date 2026-06-19
 */
@Data
public class InvestmentStrategyPage {

    @Schema(description = "投资策略ID")
    private Long id;

    @Schema(description = "策略名称")
    private String strategyName;
}
