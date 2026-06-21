package com.riskcontrol.domain.vo.investmentstrategy;

import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 投资策略分页查询条件
 *
 * @author zpc
 * @date 2026-06-19
 */
@Data
public class InvestmentStrategyQuery extends BasePageQuery {

    @Schema(description = "策略名称")
    private String strategyName;
}
