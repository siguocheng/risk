package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ContractMarketHistoryQuery {

    @Schema(description = "合约ID")
    private Integer conid;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页展示数", example = "10")
    private Integer pageSize = 10;

}