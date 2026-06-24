package com.riskcontrol.domain.vo.positionrelation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PositionRelationSelectQuery {

    @Schema(description = "账号集合")
    private List<String> accountCodes;

    @Schema(description = "策略集合")
    private List<String> strategyNames;

    @Schema(description = "交易员集合")
    private List<String> traderNames;
}
