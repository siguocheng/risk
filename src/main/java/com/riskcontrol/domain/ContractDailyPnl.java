package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractDailyPnl extends BaseEntity {

    @Schema(description = "账户id")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "合约唯一 ID")
    @TableField(value = "conid")
    private int conid;

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
