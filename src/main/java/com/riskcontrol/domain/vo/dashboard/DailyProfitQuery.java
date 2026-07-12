package com.riskcontrol.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 收益排行查询条件
 *
 * @author zpc
 * @date 2026-07-11
 */
@Data
public class DailyProfitQuery {

    @Schema(description = "账号集合")
    private List<String> accountCodes;

    @Schema(description = "交易员集合")
    private List<String> traderNames;

    @Schema(description = "策略集合")
    private List<String> strategyNames;

    @Schema(description = "日期（yyyyMMdd格式）")
    private String dailyDate;
}