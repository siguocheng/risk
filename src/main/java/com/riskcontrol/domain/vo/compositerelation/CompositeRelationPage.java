package com.riskcontrol.domain.vo.compositerelation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 综合关系分页结果
 *
 * @author zpc
 * @date 2026-06-19
 */
@Data
public class CompositeRelationPage {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "账号id")
    private String accountCode;

    @Schema(description = "合约id")
    private Integer conid;

    @Schema(description = "策略名称")
    private String strategyName;

    @Schema(description = "交易员")
    private String traderName;

    @Schema(description = "持仓股数")
    private BigDecimal positionQty;
}
