package com.riskcontrol.domain.vo.positionrelation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.riskcontrol.domain.bo.BasePageQuery;
import com.riskcontrol.enums.SetTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 策略和交易员和账号和持仓之间的关系历史分页查询条件
 *
 * @author zpc
 * @date 2026-07-11
 */
@Data
public class PositionRelationHistoryQuery extends BasePageQuery {

    @Schema(description = "yyyyMMdd格式日期")
    private String dailyDate;

    @Schema(description = "账号id")
    private List<String> accountCodes;

    @Schema(description = "合约id")
    private List<Integer> conids;

    @Schema(description = "策略名称")
    private List<String> strategyNames;

    @Schema(description = "交易员")
    private List<String> tradeNames;

    @Schema(description = "资产类型")
    private String secType;

    @Schema(description = "区域")
    private String zoneType;

    private Integer dateType;

    @Schema(description = "开始时间，默认当年第一天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    @Schema(description = "结束时间，默认是当天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    public String getSecType(){
        SetTypeEnum byName = SetTypeEnum.getByName(secType);
        if (byName != null) {
            return byName.getCode();
        } else {
            return secType;
        }
    }
}