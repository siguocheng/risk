package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@TableName("account_summary")
@EqualsAndHashCode()
public class AccountSummary extends BaseEntity {

    @Schema(description = "账户编号")
    @TableField(value = "account_code")
    private String accountCode;



    @Schema(description = "账户是否就绪")
    @TableField(value = "account_ready")
    private Boolean accountReady;

    @Schema(description = "账户类型")
    @TableField(value = "account_type")
    private String accountType;

    @Schema(description = "应计股息")
    @TableField(value = "accrued_dividend")
    private BigDecimal accruedDividend;

    @Schema(description = "可用资金")
    @TableField(value = "available_funds")
    private BigDecimal availableFunds;

    @Schema(description = "待计费金额")
    @TableField(value = "billable")
    private BigDecimal billable;

    @Schema(description = "购买力/可交易额度")
    @TableField(value = "buying_power")
    private BigDecimal buyingPower;

    @Schema(description = "保证金缓冲比率")
    @TableField(value = "cushion")
    private BigDecimal cushion;

    @Schema(description = "含借贷权益总值")
    @TableField(value = "equity_with_loan_value")
    private BigDecimal equityWithLoanValue;

    @Schema(description = "超额流动性")
    @TableField(value = "excess_liquidity")
    private BigDecimal excessLiquidity;

    @Schema(description = "全额可用资金")
    @TableField(value = "full_available_funds")
    private BigDecimal fullAvailableFunds;

    @Schema(description = "全额超额流动性")
    @TableField(value = "full_excess_liquidity")
    private BigDecimal fullExcessLiquidity;

    @Schema(description = "全额初始保证金要求")
    @TableField(value = "full_init_margin_req")
    private BigDecimal fullInitMarginReq;

    @Schema(description = "全额维持保证金要求")
    @TableField(value = "full_maint_margin_req")
    private BigDecimal fullMaintMarginReq;

    @Schema(description = "持仓总市值")
    @TableField(value = "gross_position_value")
    private BigDecimal grossPositionValue;

    @Schema(description = "担保金额")
    @TableField(value = "guarantee")
    private BigDecimal guarantee;

    @Schema(description = "激励券金额")
    @TableField(value = "incentive_coupons")
    private BigDecimal incentiveCoupons;

    @Schema(description = "印度股票折减额")
    @TableField(value = "indian_stock_haircut")
    private BigDecimal indianStockHaircut;

    @Schema(description = "初始保证金要求")
    @TableField(value = "init_margin_req")
    private BigDecimal initMarginReq;

    @Schema(description = "前瞻可用资金")
    @TableField(value = "look_ahead_available_funds")
    private BigDecimal lookAheadAvailableFunds;

    @Schema(description = "前瞻超额流动性")
    @TableField(value = "look_ahead_excess_liquidity")
    private BigDecimal lookAheadExcessLiquidity;

    @Schema(description = "前瞻初始保证金要求")
    @TableField(value = "look_ahead_init_margin_req")
    private BigDecimal lookAheadInitMarginReq;

    @Schema(description = "前瞻维持保证金要求")
    @TableField(value = "look_ahead_maint_margin_req")
    private BigDecimal lookAheadMaintMarginReq;

    @Schema(description = "前瞻数据下次更新时间戳")
    @TableField(value = "look_ahead_next_change")
    private Long lookAheadNextChange;

    @Schema(description = "维持保证金要求")
    @TableField(value = "maint_margin_req")
    private BigDecimal maintMarginReq;

    @Schema(description = "净资产与保证金是否待复核")
    @TableField(value = "nlv_and_margin_in_review")
    private Boolean nlvAndMarginInReview;

    @Schema(description = "净清算价值(账户净资产)")
    @TableField(value = "net_liquidation")
    private BigDecimal netLiquidation;

    @Schema(description = "净清算价值浮动误差")
    @TableField(value = "net_liquidation_uncertainty")
    private BigDecimal netLiquidationUncertainty;

    @Schema(description = "优先股市值")
    @TableField(value = "pa_shares_value")
    private BigDecimal paSharesValue;

    @Schema(description = "实物凭证资产价值")
    @TableField(value = "physical_certificate_value")
    private BigDecimal physicalCertificateValue;

    @Schema(description = "到期后超额资金")
    @TableField(value = "post_expiration_excess")
    private BigDecimal postExpirationExcess;

    @Schema(description = "到期后保证金")
    @TableField(value = "post_expiration_margin")
    private BigDecimal postExpirationMargin;

    @Schema(description = "当日结算现金")
    @TableField(value = "settled_cash_by_date")
    private String settledCashByDate;

    @Schema(description = "现金总额")
    @TableField(value = "total_cash_value")
    private BigDecimal totalCashValue;

    @Schema(description = "借记卡待扣款总额")
    @TableField(value = "total_debit_card_pending_charges")
    private BigDecimal totalDebitCardPendingCharges;
}
