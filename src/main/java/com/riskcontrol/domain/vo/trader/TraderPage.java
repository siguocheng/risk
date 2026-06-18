package com.riskcontrol.domain.vo.trader;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 交易员分页结果
 *
 * @author zpc
 * @date 2026-06-18
 */
@Data
public class TraderPage {

    @Schema(description = "交易员ID")
    private Long id;

    @Schema(description = "交易员名称")
    private String traderName;
}