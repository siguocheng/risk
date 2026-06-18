package com.riskcontrol.domain.vo.trader;

import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 交易员分页查询条件
 *
 * @author zpc
 * @date 2026-06-18
 */
@Data
public class TraderQuery extends BasePageQuery {

    @Schema(description = "交易员名称")
    private String traderName;
}