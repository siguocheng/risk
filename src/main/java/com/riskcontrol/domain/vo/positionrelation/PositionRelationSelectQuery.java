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

    @Schema(description = "查询类型 1:交易员 2:策略")
    private Integer queryType;
}
