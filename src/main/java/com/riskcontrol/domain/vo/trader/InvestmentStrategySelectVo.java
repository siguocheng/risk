package com.riskcontrol.domain.vo.trader;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class InvestmentStrategySelectVo {

    @Schema(description = "策略名称")
    private String strategyName;
}
