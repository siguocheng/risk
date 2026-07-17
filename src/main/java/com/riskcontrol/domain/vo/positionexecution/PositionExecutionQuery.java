package com.riskcontrol.domain.vo.positionexecution;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.riskcontrol.domain.bo.BasePageQuery;
import com.riskcontrol.enums.SetTypeEnum;
import com.riskcontrol.util.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * 成交明细查询条件VO
 *
 * @author zpc
 * @date 2026-06-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PositionExecutionQuery extends BasePageQuery {

    @Schema(description = "账户集合")
    private List<String> accountCodes;

    @Schema(description = "交易员集合")
    private List<String> tradeNames;

    @Schema(description = "策略集合")
    private List<String> strategyNames;

    @Schema(description = "开始时间，默认当年第一天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate = DateUtil.localDateToString(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));

    @Schema(description = "结束时间，默认是当天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate = DateUtil.localDateToString(LocalDate.now());

    @Schema(description = "标的")
    private List<Integer> conids;

    @Schema(description = "板块")
    private List<String> sectors;

    @Schema(description = "资产类型")
    private String secType;

    public String getSecType(){
        SetTypeEnum byName = SetTypeEnum.getByName(secType);
        if (byName != null) {
            return byName.getCode();
        } else {
            return secType;
        }
    }
}
