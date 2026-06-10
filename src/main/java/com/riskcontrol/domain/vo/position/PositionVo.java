package com.riskcontrol.domain.vo.position;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 持仓列表视图对象
 *
 * @author zpc
 * @date 2026-06-10
 */
@Data
@ColumnWidth(18)
public class PositionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableField(value = "id")
    private Long id;

    @Schema(description = "合约id")
    @TableField(value = "conid")
    @ExcelProperty(value = "合约id", index = 0)
    @ColumnWidth(25)
    private Long conId;

    @Schema(description = "账号编号")
    @TableField(value = "account_code")
    @ExcelProperty(value = "账号编号", index = 1)
    @ColumnWidth(25)
    private String accountCode;

    @Schema(description = "模型代码")
    @TableField(value = "model_code")
    @ExcelProperty(value = "模型代码", index = 2)
    @ColumnWidth(25)
    private String modelCode;

    @Schema(description = "持仓股数")
    @TableField(value = "position")
    @ExcelProperty(value = "持仓股数", index = 3)
    @ColumnWidth(25)
    private BigDecimal positionQty;

    @Schema(description = "平均成本价")
    @TableField(value = "avg_cost")
    @ExcelProperty(value = "平均成本价", index = 4)
    @ColumnWidth(25)
    private BigDecimal avgCost;

    @Schema(description = "未实现盈亏")
    @TableField(value = "unrealized_pnl")
    @ExcelProperty(value = "未实现盈亏", index = 5)
    @ColumnWidth(25)
    private BigDecimal unrealizedPnl;

    @Schema(description = "市场价格")
    @TableField(value = "market_price")
    @ExcelProperty(value = "市场价格", index = 6)
    @ColumnWidth(25)
    private BigDecimal marketPrice;

    @Schema(description = "市场值")
    @TableField(value = "market_value")
    @ExcelProperty(value = "市场值", index = 7)
    @ColumnWidth(25)
    private BigDecimal marketValue;

    @Schema(description = "实现盈亏")
    @TableField(value = "realized_pnl")
    @ExcelProperty(value = "实现盈亏", index = 8)
    @ColumnWidth(25)
    private BigDecimal realizedPnl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
