package com.riskcontrol.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 资产类型占比返回结果
 *
 * @author zpc
 * @date 2026-07-11
 */
@Data
public class AssetSecTypeRatio {

    @Schema(description = "资产类型")
    private String secType;

    @Schema(description = "数量")
    private Long count;

    @Schema(description = "占比(%)")
    private BigDecimal ratio;
}