package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账户每日盈亏实体类
 *
 * @author zpc
 * @date 2026-06-10
 */
@Data
@TableName("account_daily_pnl")
@EqualsAndHashCode(callSuper = true)
public class AccountDailyPnl extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "账户id")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "当日盈亏")
    @TableField(value = "daily_pnl")
    private BigDecimal dailyPnl;

    @Schema(description = "未实现盈亏")
    @TableField(value = "unrealized_pnl")
    private BigDecimal unrealizedPnl;

    @Schema(description = "已实现盈亏")
    @TableField(value = "realized_pnl")
    private BigDecimal realizedPnl;

    @Schema(description = "日期")
    @TableField(value = "daily_date")
    private LocalDate dailyDate;
}
