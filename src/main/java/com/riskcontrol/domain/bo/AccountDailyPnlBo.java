package com.riskcontrol.domain.bo;

import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账户每日盈亏业务对象
 *
 * @author zpc
 * @date 2026-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountDailyPnlBo extends BasePageQuery {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "账户id")
    private String accountCode;

    @Schema(description = "当日盈亏")
    private BigDecimal dailyPnl;

    @Schema(description = "未实现盈亏")
    private BigDecimal unrealizedPnl;

    @Schema(description = "已实现盈亏")
    private BigDecimal realizedPnl;

    @Schema(description = "日期")
    private LocalDate dailyDate;
}
