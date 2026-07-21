package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.riskcontrol.domain.vo.ibkr.PositionCallbackVo;
import com.riskcontrol.util.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

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
    private String positionDate;

    @Schema(description = "合约id")
    @TableField(value = "conid")
    private Integer conid;

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

    @Schema(description = "日收益")
    @TableField(value = "daily_pnl")
    private BigDecimal dailyPnl;

    @Schema(description = "持仓股数")
    @TableField(value = "cal_position_qty")
    private BigDecimal calPositionQty;

    @Schema(description = "平均成本价")
    @TableField(value = "cal_avg_cost")
    private BigDecimal calAvgCost;

    @Schema(description = "持仓成本（当前持有 Lot 成本合计）")
    @TableField(value = "cal_cost_basis")
    private BigDecimal calCostBasis;

    @Schema(description = "未实现盈亏")
    @TableField(value = "cal_unrealized_pnl")
    private BigDecimal calUnrealizedPnl;

    @Schema(description = "实现盈亏")
    @TableField(value = "cal_realized_pnl")
    private BigDecimal calRealizedPnl;

    @Schema(description = "计算日未实现收益")
    @TableField(value = "cal_daily_unrealized_pnl")
    private BigDecimal calDailyUnrealizedPnl;

    @Schema(description = "计算日已实现收益")
    @TableField(value = "cal_daily_realized_pnl")
    private BigDecimal calDailyRealizedPnl;

    @Schema(description = "最后一次交易操作的id")
    @TableField(value = "position_execution_id")
    private Long positionExecutionId;

    @Schema(description = "累计佣金及各项费用")
    @TableField(value = "acc_commission_and_fees")
    private BigDecimal accCommissionAndFees;

    @Schema(description = "类型 STK=股票、OPT=期权、FUT=期货、FX=外汇")
    @TableField(value = "sec_type")
    private String secType;

    @Schema(description = "合约乘数")
    @TableField(value = "multiplier")
    private String multiplier;

    @Schema(description = "当日未实现收益mtm")
    @TableField(value = "cal_daily_unrealized_pnl_mtm")
    private BigDecimal calDailyUnrealizedPnlMtm;

    public PositionHistory(){

    }

    public PositionHistory(Position position, String date){
        BeanUtils.copyProperties(position, this);
        this.positionDate = date;
    }

    public PositionHistory(PositionCallbackVo positionCallbackVo){
        if (positionCallbackVo == null) {
            return;
        }
        this.positionDate = DateUtil.localDateToString(LocalDate.now());
        this.accountCode = positionCallbackVo.getAccountCode();
        this.modelCode = positionCallbackVo.getModelCode();
        this.conid = positionCallbackVo.getConid() != null ? positionCallbackVo.getConid() : null;
        this.symbol = positionCallbackVo.getSymbol();
        this.positionQty = positionCallbackVo.getPosition();
        this.avgCost = positionCallbackVo.getAvgCost() != null ? BigDecimal.valueOf(positionCallbackVo.getAvgCost()) : null;
        this.unrealizedPnl = positionCallbackVo.getUnrealizedPnl() != null ? BigDecimal.valueOf(positionCallbackVo.getUnrealizedPnl()) : null;
        this.marketPrice = positionCallbackVo.getMarketPrice() != null ? BigDecimal.valueOf(positionCallbackVo.getMarketPrice()) : null;
        this.marketValue = positionCallbackVo.getMarketValue() != null ? BigDecimal.valueOf(positionCallbackVo.getMarketValue()) : null;
        this.realizedPnl = positionCallbackVo.getRealizedPnl() != null ? BigDecimal.valueOf(positionCallbackVo.getRealizedPnl()) : null;
    }
}
