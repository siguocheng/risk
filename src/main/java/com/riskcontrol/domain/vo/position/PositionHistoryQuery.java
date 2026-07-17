package com.riskcontrol.domain.vo.position;

import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PositionHistoryQuery extends BasePageQuery {

    @Schema(description = "yyyyMMdd格式日期")
    private String positionDate;

    @Schema(description = "账号编号")
    private List<String> accountCodes;

    @Schema(description = "合约id")
    private List<Integer> conids;

    @Schema(description = "模型代码")
    private List<String> modelCodes;

    @Schema(description = "合约简称")
    private String symbol;
}