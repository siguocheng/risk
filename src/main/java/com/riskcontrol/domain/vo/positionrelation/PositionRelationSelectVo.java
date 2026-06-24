package com.riskcontrol.domain.vo.positionrelation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PositionRelationSelectVo {

    @Schema(description = "账号id")
    private String accountCode;

    @Schema(description = "策略名称")
    private String strategyName;

    @Schema(description = "交易员")
    private String traderName;
}
