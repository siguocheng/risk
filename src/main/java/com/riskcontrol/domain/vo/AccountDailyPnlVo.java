package com.riskcontrol.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账户每日盈亏视图对象
 *
 * @author zpc
 * @date 2026-06-10
 */
@Data
@ColumnWidth(18)
public class AccountDailyPnlVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableField(value = "id")
    private Long id;

    @Schema(description = "账户id")
    @TableField(value = "account_code")
    @ExcelProperty(value = "账户id", index = 0)
    @ColumnWidth(25)
    private String accountCode;

    @Schema(description = "当日盈亏")
    @TableField(value = "daily_pnl")
    @ExcelProperty(value = "当日盈亏", index = 1)
    @ColumnWidth(25)
    private BigDecimal dailyPnl;

    @Schema(description = "未实现盈亏")
    @TableField(value = "unrealized_pnl")
    @ExcelProperty(value = "未实现盈亏", index = 2)
    @ColumnWidth(25)
    private BigDecimal unrealizedPnl;

    @Schema(description = "已实现盈亏")
    @TableField(value = "realized_pnl")
    @ExcelProperty(value = "已实现盈亏", index = 3)
    @ColumnWidth(25)
    private BigDecimal realizedPnl;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "日期")
    @TableField(value = "daily_date")
    @ExcelProperty(value = "日期", index = 4)
    @ColumnWidth(25)
    private LocalDate dailyDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    @TableField(value = "modified_time")
    private LocalDateTime modifiedTime;
}
