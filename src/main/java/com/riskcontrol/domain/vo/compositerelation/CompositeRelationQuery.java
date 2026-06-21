package com.riskcontrol.domain.vo.compositerelation;

import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 综合关系分页查询条件
 *
 * @author zpc
 * @date 2026-06-19
 */
@Data
public class CompositeRelationQuery extends BasePageQuery {

    @Schema(description = "账号id")
    private String accountCode;

    @Schema(description = "合约id")
    private Integer conid;

    @Schema(description = "策略名称")
    private String strategyName;

    @Schema(description = "交易员")
    private String traderName;
}
