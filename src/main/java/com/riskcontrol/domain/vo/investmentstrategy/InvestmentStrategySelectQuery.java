package com.riskcontrol.domain.vo.investmentstrategy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 投资策略查询条件VO（下拉选择用）
 *
 * @author zpc
 * @date 2026-06-20
 */
@Data
public class InvestmentStrategySelectQuery {

    @Schema(description = "账号代码列表")
    private List<String> accountCodes;

    @Schema(description = "交易员名称列表")
    private List<String> traderNames;
}
