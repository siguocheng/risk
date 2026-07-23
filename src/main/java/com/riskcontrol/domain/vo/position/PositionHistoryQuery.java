package com.riskcontrol.domain.vo.position;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.riskcontrol.domain.bo.BasePageQuery;
import com.riskcontrol.enums.SetTypeEnum;
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

    @Schema(description = "资产类型")
    private String secType;

    @Schema(description = "开始时间，默认当年第一天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    @Schema(description = "结束时间，默认是当天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    private Integer dateType;

    public String getSecType(){
        SetTypeEnum byName = SetTypeEnum.getByName(secType);
        if (byName != null) {
            return byName.getCode();
        } else {
            return secType;
        }
    }
}