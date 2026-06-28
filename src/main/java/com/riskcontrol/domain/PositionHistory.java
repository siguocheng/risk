package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.riskcontrol.domain.vo.ibkr.PositionCallbackVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 持仓列表历史实体类
 *
 * @author zpc
 * @date 2026-06-20
 */
@Data
@TableName("position_history")
@EqualsAndHashCode(callSuper = true)
public class PositionHistory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "持仓日期")
    @TableField(value = "position_date")
    private LocalDate positionDate;

    @Schema(description = "合约id")
    @TableField(value = "conid")
    private Long conid;

    @Schema(description = "股票简称")
    @TableField(value = "symbol")
    private String symbol;

    @Schema(description = "账号编号")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "模型代码")
    @TableField(value = "model_code")
    private String modelCode;

    @Schema(description = "持仓股数")
    @TableField(value = "position_qty")
    private BigDecimal positionQty;

    @Schema(description = "平均成本价")
    @TableField(value = "avg_cost")
    private BigDecimal avgCost;

    @Schema(description = "未实现盈亏")
    @TableField(value = "unrealized_pnl")
    private BigDecimal unrealizedPnl;

    @Schema(description = "市场价格")
    @TableField(value = "market_price")
    private BigDecimal marketPrice;

    @Schema(description = "市场值")
    @TableField(value = "market_value")
    private BigDecimal marketValue;

    @Schema(description = "实现盈亏")
    @TableField(value = "realized_pnl")
    private BigDecimal realizedPnl;

    public PositionHistory(){

    }

    public PositionHistory(PositionCallbackVo positionCallbackVo){
        if (positionCallbackVo == null) {
            return;
        }
        this.positionDate = LocalDate.now();
        this.accountCode = positionCallbackVo.getAccountCode();
        this.modelCode = positionCallbackVo.getModelCode();
        this.conid = positionCallbackVo.getConid() != null ? positionCallbackVo.getConid().longValue() : null;
        this.symbol = positionCallbackVo.getSymbol();
        this.positionQty = positionCallbackVo.getPosition();
        this.avgCost = positionCallbackVo.getAvgCost() != null ? BigDecimal.valueOf(positionCallbackVo.getAvgCost()) : null;
        this.unrealizedPnl = positionCallbackVo.getUnrealizedPnl() != null ? BigDecimal.valueOf(positionCallbackVo.getUnrealizedPnl()) : null;
        this.marketPrice = positionCallbackVo.getMarketPrice() != null ? BigDecimal.valueOf(positionCallbackVo.getMarketPrice()) : null;
        this.marketValue = positionCallbackVo.getMarketValue() != null ? BigDecimal.valueOf(positionCallbackVo.getMarketValue()) : null;
        this.realizedPnl = positionCallbackVo.getRealizedPnl() != null ? BigDecimal.valueOf(positionCallbackVo.getRealizedPnl()) : null;
    }
}
