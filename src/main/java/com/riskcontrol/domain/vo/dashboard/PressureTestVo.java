package com.riskcontrol.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PressureTestVo {

    @Schema(description = "场景")
    private String scene;

    @Schema(description = "金额")
    private BigDecimal amount;
}
