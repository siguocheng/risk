package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TraderSelectVo {

    @Schema(description = "交易员")
    private String traderName;
}
