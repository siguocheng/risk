package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 成交明细实体类
 *
 * @author zpc
 * @date 2026-06-18
 */
@Data
public class ContractExecution extends BaseEntity {

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

    @Schema(description = "账户号 acctNumber")
    @TableField(value = "acct_number")
    private String acctNumber;

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
}