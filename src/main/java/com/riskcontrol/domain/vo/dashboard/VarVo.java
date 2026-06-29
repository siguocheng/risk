package com.riskcontrol.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VarVo {

    @Schema(description = "天")
    private int day;

    @Schema(description = "置信度 0.99")
    private double confidence = 0.99;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "总资产占比")
    private BigDecimal totalAssetRatio;


}
