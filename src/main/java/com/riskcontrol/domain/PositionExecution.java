package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.riskcontrol.domain.vo.CommissionAndFeesReportCallbackVo;
import com.riskcontrol.domain.vo.ExecutionCallbackVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 持仓交易
 *
 * @author zpc
 * @date 2026-06-18
 */
@Data
public class PositionExecution extends BaseEntity {

    @Schema(description = "合约id")
    @TableField(value = "conid")
    private Integer conid;

    @Schema(description = "股票简称")
    @TableField(value = "symbol")
    private String symbol;

    @Schema(description = "订单ID orderId")
    @TableField(value = "order_id")
    private Integer orderId;

    @Schema(description = "客户ID clientId")
    @TableField(value = "client_id")
    private Integer clientId;

    @Schema(description = "成交ID execId")
    @TableField(value = "exec_id")
    private String execId;

    @Schema(description = "成交时间字符串")
    @TableField(value = "time")
    private String time;

    @Schema(description = "账户号")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "交易所 exchange")
    @TableField(value = "exchange")
    private String exchange;

    @Schema(description = "买卖方向 side")
    @TableField(value = "side")
    private String side;

    @Schema(description = "本次成交数量 shares")
    @TableField(value = "shares")
    private BigDecimal shares;

    @Schema(description = "成交单价 price")
    @TableField(value = "price")
    private BigDecimal price;

    @Schema(description = "全局唯一permId")
    @TableField(value = "perm_id")
    private Long permId;

    @Schema(description = "清算标识 liquidation")
    @TableField(value = "liquidation")
    private Integer liquidation;

    @Schema(description = "累计成交数量 cumQty")
    @TableField(value = "cum_qty")
    private BigDecimal cumQty;

    @Schema(description = "平均成交价 avgPrice")
    @TableField(value = "avg_price")
    private BigDecimal avgPrice;

    @Schema(description = "订单备注 orderRef")
    @TableField(value = "order_ref")
    private String orderRef;

    @Schema(description = "EV规则 evRule")
    @TableField(value = "ev_rule")
    private String evRule;

    @Schema(description = "EV乘数 evMultiplier")
    @TableField(value = "ev_multiplier")
    private BigDecimal evMultiplier;

    @Schema(description = "模型编码 modelCode")
    @TableField(value = "model_code")
    private String modelCode;

    @Schema(description = "流动性类型 lastLiquidity")
    @TableField(value = "last_liquidity")
    private String lastLiquidity;

    @Schema(description = "是否待价格修订 true=1 false=0")
    @TableField(value = "pending_price_revision")
    private Boolean pendingPriceRevision;

    @Schema(description = "提交人 submitter")
    @TableField(value = "submitter")
    private String submitter;

    @Schema(description = "期权行权/失效类型")
    @TableField(value = "opt_exercise_or_lapse_type")
    private String optExerciseOrLapseType;

    @Schema(description = "佣金及各项费用")
    @TableField(value = "commission_and_fees")
    private BigDecimal commissionAndFees;

    @Schema(description = "结算币种")
    @TableField(value = "currency")
    private String currency;

    @Schema(description = "已实现盈亏")
    @TableField(value = "realized_pnl")
    private BigDecimal realizedPnl;

    @Schema(description = "收益率")
    @TableField(value = "yield")
    private BigDecimal yield;

    @Schema(description = "收益兑付日期")
    @TableField(value = "yield_redemption_date")
    private Long yieldRedemptionDate;

    @Schema(description = "日期")
    @TableField(value = "date")
    private String date;

    @Schema(description = "剩余数量")
    @TableField(value = "remain_qty")
    private BigDecimal remainQty;

    @Schema(description = "入库操作")
    @TableField(value = "opt_type")
    private String optType;

    @Schema(description = "市场价格")
    @TableField(value = "market_price")
    private BigDecimal marketPrice;

    @Schema(description = "本次交易的未实现收益")
    @TableField(value = "cal_execution_unrealized_pnl")
    private BigDecimal calExecutionUnrealizedPnl;

    @Schema(description = "本次交易的已实现收益")
    @TableField(value = "cal_execution_realized_pnl")
    private BigDecimal calExecutionRealizedPnl;

    @Schema(description = "核算状态 0未核算 1已核算")
    @TableField(value = "status")
    private Integer status;

    public PositionExecution(){

    }

    public PositionExecution(ExecutionCallbackVo execution, CommissionAndFeesReportCallbackVo commissionReport, Position position){
        this.conid = execution.getConid();
        this.symbol = execution.getSymbol();
        this.orderId = execution.getOrderId();
        this.clientId = execution.getClientId();
        this.execId = execution.getExecId();
        this.time = execution.getTime();
        this.accountCode = execution.getAcctNumber();
        this.exchange =execution.getExchange();
        this.side = execution.getSide();
        this.shares = execution.getShares().value();
        this.price = BigDecimal.valueOf(execution.getPrice());
        this.permId = execution.getPermId();
        this.liquidation = execution.getLiquidation();
        this.cumQty = execution.getCumQty().value();
        this.avgPrice = BigDecimal.valueOf(execution.getAvgPrice());
        this.orderRef = execution.getOrderRef();
        this.evRule = execution.getEvRule();
        this.evMultiplier = BigDecimal.valueOf(execution.getEvMultiplier());
        this.modelCode = execution.getModelCode();
        this.lastLiquidity = execution.getLastLiquidity() != null ? execution.getLastLiquidity().name() : "";
        this.pendingPriceRevision = execution.isPendingPriceRevision();
        this.submitter = execution.getSubmitter();
        this.optExerciseOrLapseType = execution.getOptExerciseOrLapseType() != null ? execution.getOptExerciseOrLapseType().name() : "";
        this.date = execution.getTime().substring(0,9);
        this.marketPrice = position.getMarketPrice();
        this.status = 0;

        if (commissionReport != null) {
            this.commissionAndFees = BigDecimal.valueOf(commissionReport.getCommissionAndFees());
            this.currency = commissionReport.getCurrency();
            this.realizedPnl = BigDecimal.valueOf(commissionReport.getRealizedPNL());
            this.yield = BigDecimal.valueOf(commissionReport.getYield());
            this.yieldRedemptionDate = (long) commissionReport.getYieldRedemptionDate();
        }
    }
}