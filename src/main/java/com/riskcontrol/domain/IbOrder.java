package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ib.client.Order;
import com.ib.client.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * IB TWS订单主表 实体
 */
@Data
@TableName("ib_order")
@Schema(description = "IB TWS订单主表")
public class IbOrder extends BaseEntity {

    @TableField(value = "client_id")
    @Schema(description = "客户端ID")
    private Integer clientId;

    @TableField(value = "order_id")
    @Schema(description = "本地订单号")
    private Integer orderId;

    @TableField(value = "perm_id")
    @Schema(description = "IB全局永久订单ID")
    private Long permId;

    @TableField(value = "parent_id")
    @Schema(description = "父单ID")
    private Integer parentId;

    @TableField(value = "parent_perm_id")
    @Schema(description = "父单永久ID")
    private Long parentPermId;

    @TableField(value = "conid")
    @Schema(description = "合约id")
    private int conid;

    @TableField(value = "sl_order_id")
    @Schema(description = "止损关联订单ID")
    private Integer slOrderId;

    @TableField(value = "sl_order_type")
    @Schema(description = "止损单类型")
    private String slOrderType;

    @TableField(value = "pt_order_id")
    @Schema(description = "止盈关联订单ID")
    private Integer ptOrderId;

    @TableField(value = "pt_order_type")
    @Schema(description = "止盈单类型")
    private String ptOrderType;

    @TableField(value = "order_ref")
    @Schema(description = "自定义订单备注")
    private String orderRef;

    @TableField(value = "oca_group")
    @Schema(description = "OCA分组标识")
    private String ocaGroup;

    @TableField(value = "oca_type")
    @Schema(description = "OCA类型")
    private Integer ocaType;

    @TableField(value = "model_code")
    @Schema(description = "模型编码")
    private String modelCode;

    @TableField(value = "ext_operator")
    @Schema(description = "外部操作员")
    private String extOperator;

    @TableField(value = "submitter")
    @Schema(description = "提交人")
    private String submitter;

    @TableField(value = "action")
    @Schema(description = "买卖方向")
    private String action = "BUY";

    @TableField(value = "total_quantity")
    @Schema(description = "总委托数量")
    private BigDecimal totalQuantity;

    @TableField(value = "filled_quantity")
    @Schema(description = "已成交数量")
    private BigDecimal filledQuantity;

    @TableField(value = "suggested_size")
    @Schema(description = "建议下单量")
    private BigDecimal suggestedSize;

    @TableField(value = "cash_qty")
    @Schema(description = "现金交易金额")
    private BigDecimal cashQty;

    @TableField(value = "min_qty")
    @Schema(description = "最小成交数量")
    private Integer minQty;

    @TableField(value = "min_trade_qty")
    @Schema(description = "最小交易数量")
    private Integer minTradeQty;

    @TableField(value = "min_compete_size")
    @Schema(description = "最小竞争数量")
    private Integer minCompeteSize;

    @TableField(value = "display_size")
    @Schema(description = "盘口展示数量")
    private Integer displaySize;

    @TableField(value = "order_type")
    @Schema(description = "订单类型")
    private String orderType;

    @TableField(value = "adjusted_order_type")
    @Schema(description = "调整后订单类型")
    private String adjustedOrderType;

    @TableField(value = "lmt_price")
    @Schema(description = "限价价格")
    private BigDecimal lmtPrice;

    @TableField(value = "lmt_price_offset")
    @Schema(description = "限价偏移值")
    private BigDecimal lmtPriceOffset;

    @TableField(value = "aux_price")
    @Schema(description = "辅助止损价格")
    private BigDecimal auxPrice;

    @TableField(value = "trail_stop_price")
    @Schema(description = "追踪止损价")
    private BigDecimal trailStopPrice;

    @TableField(value = "trailing_percent")
    @Schema(description = "追踪止损百分比")
    private BigDecimal trailingPercent;

    @TableField(value = "trigger_price")
    @Schema(description = "触发价格")
    private BigDecimal triggerPrice;

    @TableField(value = "adjusted_stop_price")
    @Schema(description = "调整止损价")
    private BigDecimal adjustedStopPrice;

    @TableField(value = "adjusted_stop_limit_price")
    @Schema(description = "调整止损限价")
    private BigDecimal adjustedStopLimitPrice;

    @TableField(value = "adjusted_trailing_amount")
    @Schema(description = "调整追踪幅度")
    private BigDecimal adjustedTrailingAmount;

    @TableField(value = "adjustable_trailing_unit")
    @Schema(description = "追踪单位")
    private Integer adjustableTrailingUnit;

    @TableField(value = "percent_offset")
    @Schema(description = "百分比偏移")
    private BigDecimal percentOffset;

    @TableField(value = "discretionary_amt")
    @Schema(description = "自主成交价区间")
    private BigDecimal discretionaryAmt;

    @TableField(value = "compete_against_best_offset")
    @Schema(description = "最优对手偏移量")
    private BigDecimal competeAgainstBestOffset;

    @TableField(value = "mid_offset_at_whole")
    @Schema(description = "整档中间价偏移")
    private BigDecimal midOffsetAtWhole;

    @TableField(value = "mid_offset_at_half")
    @Schema(description = "半档中间价偏移")
    private BigDecimal midOffsetAtHalf;

    @TableField(value = "tif")
    @Schema(description = "订单时效规则")
    private String tif;

    @TableField(value = "good_after_time")
    @Schema(description = "延后生效时间")
    private String goodAfterTime;

    @TableField(value = "good_till_date")
    @Schema(description = "有效截止日期")
    private String goodTillDate;

    @TableField(value = "auto_cancel_date")
    @Schema(description = "自动取消日期")
    private String autoCancelDate;

    @TableField(value = "duration")
    @Schema(description = "订单有效期时长")
    private Integer duration;

    @TableField(value = "active_start_time")
    @Schema(description = "订单激活起始时间")
    private String activeStartTime;

    @TableField(value = "active_stop_time")
    @Schema(description = "订单激活结束时间")
    private String activeStopTime;

    @TableField(value = "account_code")
    @Schema(description = "交易账户")
    private String accountCode;

    @TableField(value = "customer_account")
    @Schema(description = "客户账号")
    private String customerAccount;

    @TableField(value = "settling_firm")
    @Schema(description = "结算公司")
    private String settlingFirm;

    @TableField(value = "clearing_account")
    @Schema(description = "清算账户")
    private String clearingAccount;

    @TableField(value = "clearing_intent")
    @Schema(description = "清算意向")
    private String clearingIntent;

    @TableField(value = "fa_group")
    @Schema(description = "FA分组")
    private String faGroup;

    @TableField(value = "fa_method")
    @Schema(description = "FA分配方式")
    private String faMethod;

    @TableField(value = "fa_percentage")
    @Schema(description = "FA分配比例")
    private BigDecimal faPercentage;

    @TableField(value = "bond_accrued_interest")
    @Schema(description = "债券应计利息")
    private String bondAccruedInterest;

    @TableField(value = "open_close")
    @Schema(description = "开仓平仓标识")
    private String openClose;

    @TableField(value = "origin")
    @Schema(description = "订单来源")
    private Integer origin;

    @TableField(value = "short_sale_slot")
    @Schema(description = "做空渠道")
    private Integer shortSaleSlot;

    @TableField(value = "designated_location")
    @Schema(description = "做空指定地点")
    private String designatedLocation;

    @TableField(value = "exempt_code")
    @Schema(description = "豁免代码")
    private Integer exemptCode;

    @TableField(value = "rule80a")
    @Schema(description = "80A交易规则")
    private String rule80A;

    @TableField(value = "all_or_none")
    @Schema(description = "全部成交否则取消")
    private Boolean allOrNone;

    @TableField(value = "block_order")
    @Schema(description = "大宗订单")
    private Boolean blockOrder;

    @TableField(value = "hidden")
    @Schema(description = "隐藏订单")
    private Boolean hidden;

    @TableField(value = "outside_rth")
    @Schema(description = "允许盘外交易")
    private Boolean outsideRth;

    @TableField(value = "sweep_to_fill")
    @Schema(description = "扫单立即成交")
    private Boolean sweepToFill;

    @TableField(value = "transmit")
    @Schema(description = "是否直接推送交易所")
    private Boolean transmit;

    @TableField(value = "what_if")
    @Schema(description = "仅试算不提交订单")
    private Boolean whatIf;

    @TableField(value = "what_if_type")
    @Schema(description = "试算类型")
    private Integer whatIfType;

    @TableField(value = "override_percentage_constraints")
    @Schema(description = "覆盖比例限制")
    private Boolean overridePercentageConstraints;

    @TableField(value = "opt_out_smart_routing")
    @Schema(description = "关闭智能路由")
    private Boolean optOutSmartRouting;

    @TableField(value = "not_held")
    @Schema(description = "Not Held交易指令")
    private Boolean notHeld;

    @TableField(value = "solicited")
    @Schema(description = "主动推介订单")
    private Boolean solicited;

    @TableField(value = "randomize_size")
    @Schema(description = "随机委托数量")
    private Boolean randomizeSize;

    @TableField(value = "randomize_price")
    @Schema(description = "随机委托价格")
    private Boolean randomizePrice;

    @TableField(value = "dont_use_auto_price_for_hedge")
    @Schema(description = "对冲不使用自动价格")
    private Boolean dontUseAutoPriceForHedge;

    @TableField(value = "is_oms_container")
    @Schema(description = "OMS容器订单")
    private Boolean isOmsContainer;

    @TableField(value = "discretionary_up_to_limit_price")
    @Schema(description = "自主价不超过限价")
    private Boolean discretionaryUpToLimitPrice;

    @TableField(value = "auto_cancel_parent")
    @Schema(description = "父单取消自动撤子单")
    private Boolean autoCancelParent;

    @TableField(value = "imbalance_only")
    @Schema(description = "仅失衡撮合")
    private Boolean imbalanceOnly;

    @TableField(value = "include_overnight")
    @Schema(description = "包含隔夜交易")
    private Boolean includeOvernight;

    @TableField(value = "professional_customer")
    @Schema(description = "专业客户标识")
    private Boolean professionalCustomer;

    @TableField(value = "post_only")
    @Schema(description = "仅挂单不主动成交")
    private Boolean postOnly;

    @TableField(value = "allow_pre_open")
    @Schema(description = "允许盘前交易")
    private Boolean allowPreOpen;

    @TableField(value = "ignore_open_auction")
    @Schema(description = "忽略开盘集合竞价")
    private Boolean ignoreOpenAuction;

    @TableField(value = "deactivate")
    @Schema(description = "暂停订单")
    private Boolean deactivate;

    @TableField(value = "conditions_cancel_order")
    @Schema(description = "条件触发后撤单")
    private Boolean conditionsCancelOrder;

    @TableField(value = "conditions_ignore_rth")
    @Schema(description = "条件忽略盘外时段")
    private Boolean conditionsIgnoreRth;

    @TableField(value = "seek_price_improvement")
    @Schema(description = "寻求价格优化")
    private Boolean seekPriceImprovement;

    @TableField(value = "route_marketable_to_bbo")
    @Schema(description = "市价单路由最优盘口")
    private Boolean routeMarketableToBbo;

    @TableField(value = "use_price_mgmt_algo")
    @Schema(description = "启用价格管理算法")
    private Boolean usePriceMgmtAlgo;

    @TableField(value = "volatility")
    @Schema(description = "波动率数值")
    private BigDecimal volatility;

    @TableField(value = "volatility_type")
    @Schema(description = "波动率类型")
    private Integer volatilityType;

    @TableField(value = "continuous_update")
    @Schema(description = "持续更新开关")
    private Integer continuousUpdate;

    @TableField(value = "reference_price_type")
    @Schema(description = "参考价类型")
    private Integer referencePriceType;

    @TableField(value = "reference_contract_id")
    @Schema(description = "参考合约ID")
    private Integer referenceContractId;

    @TableField(value = "ref_futures_con_id")
    @Schema(description = "参考期货合约ID")
    private Integer refFuturesConId;

    @TableField(value = "starting_price")
    @Schema(description = "起始价格")
    private BigDecimal startingPrice;

    @TableField(value = "stock_ref_price")
    @Schema(description = "股票参考价")
    private BigDecimal stockRefPrice;

    @TableField(value = "delta")
    @Schema(description = "Delta值")
    private BigDecimal delta;

    @TableField(value = "stock_range_lower")
    @Schema(description = "股票区间下限")
    private BigDecimal stockRangeLower;

    @TableField(value = "stock_range_upper")
    @Schema(description = "股票区间上限")
    private BigDecimal stockRangeUpper;

    @TableField(value = "basis_points")
    @Schema(description = "基点数值")
    private BigDecimal basisPoints;

    @TableField(value = "basis_points_type")
    @Schema(description = "基点类型")
    private Integer basisPointsType;

    @TableField(value = "pegged_change_amount")
    @Schema(description = "挂钩变动幅度")
    private BigDecimal peggedChangeAmount;

    @TableField(value = "is_pegged_change_amount_decrease")
    @Schema(description = "挂钩向下变动标识")
    private Boolean isPeggedChangeAmountDecrease;

    @TableField(value = "reference_change_amount")
    @Schema(description = "参考变动幅度")
    private BigDecimal referenceChangeAmount;

    @TableField(value = "reference_exchange_id")
    @Schema(description = "参考交易所")
    private String referenceExchangeId;

    @TableField(value = "trigger_method")
    @Schema(description = "触发方式")
    private Integer triggerMethod;

    @TableField(value = "auction_strategy")
    @Schema(description = "集合竞价策略")
    private Integer auctionStrategy;

    @TableField(value = "post_to_ats")
    @Schema(description = "上报ATS平台")
    private Integer postToAts;

    @TableField(value = "delta_neutral_order_type")
    @Schema(description = "Delta对冲订单类型")
    private String deltaNeutralOrderType;

    @TableField(value = "delta_neutral_aux_price")
    @Schema(description = "Delta对冲辅助价")
    private BigDecimal deltaNeutralAuxPrice;

    @TableField(value = "delta_neutral_con_id")
    @Schema(description = "Delta对冲合约ID")
    private Integer deltaNeutralConId;

    @TableField(value = "delta_neutral_open_close")
    @Schema(description = "Delta对冲开平仓")
    private String deltaNeutralOpenClose;

    @TableField(value = "delta_neutral_short_sale")
    @Schema(description = "Delta对冲做空标识")
    private Boolean deltaNeutralShortSale;

    @TableField(value = "delta_neutral_short_sale_slot")
    @Schema(description = "Delta做空渠道")
    private Integer deltaNeutralShortSaleSlot;

    @TableField(value = "delta_neutral_designated_location")
    @Schema(description = "Delta做空地点")
    private String deltaNeutralDesignatedLocation;

    @TableField(value = "delta_neutral_settling_firm")
    @Schema(description = "Delta结算公司")
    private String deltaNeutralSettlingFirm;

    @TableField(value = "delta_neutral_clearing_account")
    @Schema(description = "Delta清算账户")
    private String deltaNeutralClearingAccount;

    @TableField(value = "delta_neutral_clearing_intent")
    @Schema(description = "Delta清算意向")
    private String deltaNeutralClearingIntent;

    @TableField(value = "scale_init_level_size")
    @Schema(description = "阶梯初始委托数量")
    private Integer scaleInitLevelSize;

    @TableField(value = "scale_subs_level_size")
    @Schema(description = "阶梯后续委托数量")
    private Integer scaleSubsLevelSize;

    @TableField(value = "scale_price_increment")
    @Schema(description = "阶梯价格步长")
    private BigDecimal scalePriceIncrement;

    @TableField(value = "scale_price_adjust_value")
    @Schema(description = "阶梯价格调整值")
    private BigDecimal scalePriceAdjustValue;

    @TableField(value = "scale_price_adjust_interval")
    @Schema(description = "阶梯调整间隔")
    private Integer scalePriceAdjustInterval;

    @TableField(value = "scale_profit_offset")
    @Schema(description = "阶梯止盈偏移量")
    private BigDecimal scaleProfitOffset;

    @TableField(value = "scale_auto_reset")
    @Schema(description = "阶梯自动重置")
    private Boolean scaleAutoReset;

    @TableField(value = "scale_init_position")
    @Schema(description = "阶梯初始持仓")
    private Integer scaleInitPosition;

    @TableField(value = "scale_init_fill_qty")
    @Schema(description = "阶梯初始成交数量")
    private Integer scaleInitFillQty;

    @TableField(value = "scale_random_percent")
    @Schema(description = "阶梯随机比例")
    private Boolean scaleRandomPercent;

    @TableField(value = "scale_table")
    @Schema(description = "阶梯配置表名称")
    private String scaleTable;

    @TableField(value = "hedge_type")
    @Schema(description = "对冲类型")
    private String hedgeType;

    @TableField(value = "hedge_param")
    @Schema(description = "对冲自定义参数")
    private String hedgeParam;

    @TableField(value = "hedge_max_size")
    @Schema(description = "对冲最大数量")
    private Integer hedgeMaxSize;

    @TableField(value = "algo_strategy")
    @Schema(description = "算法交易策略")
    private String algoStrategy;

    @TableField(value = "algo_id")
    @Schema(description = "算法唯一标识")
    private String algoId;

    @TableField(value = "soft_dollar_tier")
    @Schema(description = "软美元层级")
    private String softDollarTier;

    @TableField(value = "mifid2_decision_maker")
    @Schema(description = "MiFID2决策人")
    private String mifid2DecisionMaker;

    @TableField(value = "mifid2_decision_algo")
    @Schema(description = "MiFID2决策算法")
    private String mifid2DecisionAlgo;

    @TableField(value = "mifid2_execution_trader")
    @Schema(description = "MiFID2执行交易员")
    private String mifid2ExecutionTrader;

    @TableField(value = "mifid2_execution_algo")
    @Schema(description = "MiFID2执行算法")
    private String mifid2ExecutionAlgo;

    @TableField(value = "manual_order_time")
    @Schema(description = "人工下单时间")
    private String manualOrderTime;

    @TableField(value = "manual_order_indicator")
    @Schema(description = "人工订单标识")
    private Integer manualOrderIndicator;

    @TableField(value = "advanced_error_override")
    @Schema(description = "高级错误覆盖说明")
    private String advancedErrorOverride;

    @TableField(value = "shareholder")
    @Schema(description = "股东标识")
    private String shareholder;

    @TableField(value = "status")
    @Schema(description = "订单状态")
    private String status = "";

    @TableField(value = "reject_reason")
    @Schema(description = "订单拒绝原因")
    private String rejectReason;

    @TableField(value = "warning_text")
    @Schema(description = "订单警告信息")
    private String warningText;

    @TableField(value = "completed_time")
    @Schema(description = "订单完成时间")
    private String completedTime;

    @TableField(value = "completed_status")
    @Schema(description = "订单完成状态")
    private String completedStatus;

    @TableField(value = "init_margin_before")
    @Schema(description = "下单前初始保证金")
    private String initMarginBefore;

    @TableField(value = "maint_margin_before")
    @Schema(description = "下单前维持保证金")
    private String maintMarginBefore;

    @TableField(value = "equity_with_loan_before")
    @Schema(description = "下单前融资权益")
    private String equityWithLoanBefore;

    @TableField(value = "init_margin_change")
    @Schema(description = "初始保证金变动值")
    private String initMarginChange;

    @TableField(value = "maint_margin_change")
    @Schema(description = "维持保证金变动值")
    private String maintMarginChange;

    @TableField(value = "equity_with_loan_change")
    @Schema(description = "融资权益变动值")
    private String equityWithLoanChange;

    @TableField(value = "init_margin_after")
    @Schema(description = "下单后初始保证金")
    private String initMarginAfter;

    @TableField(value = "maint_margin_after")
    @Schema(description = "下单后维持保证金")
    private String maintMarginAfter;

    @TableField(value = "equity_with_loan_after")
    @Schema(description = "下单后融资权益")
    private String equityWithLoanAfter;

    @TableField(value = "commission_and_fees")
    @Schema(description = "总手续费")
    private BigDecimal commissionAndFees;

    @TableField(value = "min_commission_and_fees")
    @Schema(description = "最低手续费")
    private BigDecimal minCommissionAndFees;

    @TableField(value = "max_commission_and_fees")
    @Schema(description = "最高手续费")
    private BigDecimal maxCommissionAndFees;

    @TableField(value = "commission_and_fees_currency")
    @Schema(description = "手续费币种")
    private String commissionAndFeesCurrency;

    @TableField(value = "margin_currency")
    @Schema(description = "保证金币种")
    private String marginCurrency;

    @TableField(value = "init_margin_before_outside_rth")
    @Schema(description = "盘外下单前初始保证金")
    private BigDecimal initMarginBeforeOutsideRth;

    @TableField(value = "maint_margin_before_outside_rth")
    @Schema(description = "盘外下单前维持保证金")
    private BigDecimal maintMarginBeforeOutsideRth;

    @TableField(value = "equity_with_loan_before_outside_rth")
    @Schema(description = "盘外下单前融资权益")
    private BigDecimal equityWithLoanBeforeOutsideRth;

    @TableField(value = "init_margin_change_outside_rth")
    @Schema(description = "盘外初始保证金变动")
    private BigDecimal initMarginChangeOutsideRth;

    @TableField(value = "maint_margin_change_outside_rth")
    @Schema(description = "盘外维持保证金变动")
    private BigDecimal maintMarginChangeOutsideRth;

    @TableField(value = "equity_with_loan_change_outside_rth")
    @Schema(description = "盘外融资权益变动")
    private BigDecimal equityWithLoanChangeOutsideRth;

    @TableField(value = "init_margin_after_outside_rth")
    @Schema(description = "盘外下单后初始保证金")
    private BigDecimal initMarginAfterOutsideRth;

    @TableField(value = "maint_margin_after_outside_rth")
    @Schema(description = "盘外下单后维持保证金")
    private BigDecimal maintMarginAfterOutsideRth;

    @TableField(value = "equity_with_loan_after_outside_rth")
    @Schema(description = "盘外下单后融资权益")
    private BigDecimal equityWithLoanAfterOutsideRth;

    public IbOrder(Order order, OrderState orderState){
// 基础标识
//        this.setClientId(order.clientId());
//        this.setOrderId(order.orderId());
//        this.setPermId(order.permId());
//        this.setParentId(order.parentId());
//        this.setParentPermId(order.parentPermId());
//        this.setSlOrderId(order.slOrderId());
//        this.setSlOrderType(order.slOrderType());
//        this.setPtOrderId(order.ptOrderId());
//        this.setPtOrderType(order.ptOrderType());
//        this.setOrderRef(order.orderRef());
//        this.setOcaGroup(order.ocaGroup());
//        this.setOcaType(order.ocaType());
//        this.setModelCode(order.modelCode());
//        this.setExtOperator(order.extOperator());
//        this.setSubmitter(order.submitter());
//
//        // 交易方向、数量、价格
//        this.setAction(order.action());
//        this.setTotalQuantity(order.totalQuantity() == null ? null : new BigDecimal(order.totalQuantity().toString()));
//        this.setFilledQuantity(order.filledQuantity() == null ? null : new BigDecimal(order.filledQuantity().toString()));
//        this.setSuggestedSize(order.suggestedSize() == null ? null : new BigDecimal(order.suggestedSize().toString()));
//        this.setCashQty(order.cashQty() == null ? null : new BigDecimal(order.cashQty().toString()));
//        this.setMinQty(order.minQty());
//        this.setMinTradeQty(order.minTradeQty());
//        this.setMinCompeteSize(order.minCompeteSize());
//        this.setDisplaySize(order.displaySize());
//        this.setOrderType(order.orderType().name());
//        this.setAdjustedOrderType(order.adjustedOrderType() == null ? null : order.adjustedOrderType().name());
//
//        this.setLmtPrice(BigDecimal.valueOf(order.lmtPrice()));
//        this.setLmtPriceOffset(BigDecimal.valueOf(order.lmtPriceOffset()));
//        this.setAuxPrice(BigDecimal.valueOf(order.auxPrice()));
//        this.setTrailStopPrice(BigDecimal.valueOf(order.trailStopPrice()));
//        this.setTrailingPercent(BigDecimal.valueOf(order.trailingPercent()));
//        this.setTriggerPrice(BigDecimal.valueOf(order.triggerPrice()));
//        this.setAdjustedStopPrice(BigDecimal.valueOf(order.adjustedStopPrice()));
//        this.setAdjustedStopLimitPrice(BigDecimal.valueOf(order.adjustedStopLimitPrice()));
//        this.setAdjustedTrailingAmount(BigDecimal.valueOf(order.adjustedTrailingAmount()));
//        this.setAdjustableTrailingUnit(order.adjustableTrailingUnit());
//        this.setPercentOffset(BigDecimal.valueOf(order.percentOffset()));
//        this.setDiscretionaryAmt(BigDecimal.valueOf(order.discretionaryAmt()));
//        this.setCompeteAgainstBestOffset(BigDecimal.valueOf(order.competeAgainstBestOffset()));
//        this.setMidOffsetAtWhole(BigDecimal.valueOf(order.midOffsetAtWhole()));
//        this.setMidOffsetAtHalf(BigDecimal.valueOf(order.midOffsetAtHalf()));
//
//        // 时效TIF
//        this.setTif(order.tif());
//        this.setGoodAfterTime(order.goodAfterTime());
//        this.setGoodTillDate(order.goodTillDate());
//        this.setAutoCancelDate(order.autoCancelDate());
//        this.setDuration(order.duration());
//        this.setActiveStartTime(order.activeStartTime());
//        this.setActiveStopTime(order.activeStopTime());
//
//        // 账户清算FA
//        this.setAccount(order.account());
//        this.setCustomerAccount(order.customerAccount());
//        this.setSettlingFirm(order.settlingFirm());
//        this.setClearingAccount(order.clearingAccount());
//        this.setClearingIntent(order.clearingIntent());
//        this.setFaGroup(order.faGroup());
//        this.setFaMethod(order.faMethod());
//        this.setFaPercentage(BigDecimal.valueOf(Double.parseDouble(order.faPercentage() == null ? "0" : order.faPercentage())));
//        this.setBondAccruedInterest(order.bondAccruedInterest());
//
//        // 开仓做空
//        this.setOpenClose(order.openClose());
//        this.setOrigin(order.origin());
//        this.setShortSaleSlot(order.shortSaleSlot());
//        this.setDesignatedLocation(order.designatedLocation());
//        this.setExemptCode(order.exemptCode());
//        this.setRule80A(order.rule80A());
//
//        // 布尔标识
//        this.setAllOrNone(order.allOrNone());
//        this.setBlockOrder(order.blockOrder());
//        this.setHidden(order.hidden());
//        this.setOutsideRth(order.outsideRth());
//        this.setSweepToFill(order.sweepToFill());
//        this.setTransmit(order.transmit());
//        this.setWhatIf(order.whatIf());
//        this.setWhatIfType(order.whatIfType());
//        this.setOverridePercentageConstraints(order.overridePercentageConstraints());
//        this.setOptOutSmartRouting(order.optOutSmartRouting());
//        this.setNotHeld(order.notHeld());
//        this.setSolicited(order.solicited());
//        this.setRandomizeSize(order.randomizeSize());
//        this.setRandomizePrice(order.randomizePrice());
//        this.setDontUseAutoPriceForHedge(order.dontUseAutoPriceForHedge());
//        this.setIsOmsContainer(order.isOmsContainer());
//        this.setDiscretionaryUpToLimitPrice(order.discretionaryUpToLimitPrice());
//        this.setAutoCancelParent(order.autoCancelParent());
//        this.setImbalanceOnly(order.imbalanceOnly());
//        this.setIncludeOvernight(order.includeOvernight());
//        this.setProfessionalCustomer(order.professionalCustomer());
//        this.setPostOnly(order.postOnly());
//        this.setAllowPreOpen(order.allowPreOpen());
//        this.setIgnoreOpenAuction(order.ignoreOpenAuction());
//        this.setDeactivate(order.deactivate());
//        this.setConditionsCancelOrder(order.conditionsCancelOrder());
//        this.setConditionsIgnoreRth(order.conditionsIgnoreRth());
//        this.setSeekPriceImprovement(order.seekPriceImprovement());
//        this.setRouteMarketableToBbo(order.routeMarketableToBbo());
//        this.setUsePriceMgmtAlgo(order.usePriceMgmtAlgo());
//
//        // 波动率、参考价格
//        this.setVolatility(BigDecimal.valueOf(order.volatility()));
//        this.setVolatilityType(order.volatilityType());
//        this.setContinuousUpdate(order.continuousUpdate());
//        this.setReferencePriceType(order.referencePriceType());
//        this.setReferenceContractId(order.referenceContractId());
//        this.setRefFuturesConId(order.refFuturesConId());
//        this.setStartingPrice(BigDecimal.valueOf(order.startingPrice()));
//        this.setStockRefPrice(BigDecimal.valueOf(order.stockRefPrice()));
//        this.setDelta(BigDecimal.valueOf(order.delta()));
//        this.setStockRangeLower(BigDecimal.valueOf(order.stockRangeLower()));
//        this.setStockRangeUpper(BigDecimal.valueOf(order.stockRangeUpper()));
//        this.setBasisPoints(BigDecimal.valueOf(order.basisPoints()));
//        this.setBasisPointsType(order.basisPointsType());
//        this.setPeggedChangeAmount(BigDecimal.valueOf(order.peggedChangeAmount()));
//        this.setIsPeggedChangeAmountDecrease(order.isPeggedChangeAmountDecrease());
//        this.setReferenceChangeAmount(BigDecimal.valueOf(order.referenceChangeAmount()));
//        this.setReferenceExchangeId(order.referenceExchangeId());
//        this.setTriggerMethod(order.triggerMethod());
//        this.setAuctionStrategy(order.auctionStrategy());
//        this.setPostToAts(order.postToAts());
//
//        // Delta Neutral
//        this.setDeltaNeutralOrderType(order.deltaNeutralOrderType());
//        this.setDeltaNeutralAuxPrice(BigDecimal.valueOf(order.deltaNeutralAuxPrice()));
//        this.setDeltaNeutralConId(order.deltaNeutralConId());
//        this.setDeltaNeutralOpenClose(order.deltaNeutralOpenClose());
//        this.setDeltaNeutralShortSale(order.deltaNeutralShortSale());
//        this.setDeltaNeutralShortSaleSlot(order.deltaNeutralShortSaleSlot());
//        this.setDeltaNeutralDesignatedLocation(order.deltaNeutralDesignatedLocation());
//        this.setDeltaNeutralSettlingFirm(order.deltaNeutralSettlingFirm());
//        this.setDeltaNeutralClearingAccount(order.deltaNeutralClearingAccount());
//        this.setDeltaNeutralClearingIntent(order.deltaNeutralClearingIntent());
//
//        // 阶梯Scale
//        this.setScaleInitLevelSize(order.scaleInitLevelSize());
//        this.setScaleSubsLevelSize(order.scaleSubsLevelSize());
//        this.setScalePriceIncrement(BigDecimal.valueOf(order.scalePriceIncrement()));
//        this.setScalePriceAdjustValue(BigDecimal.valueOf(order.scalePriceAdjustValue()));
//        this.setScalePriceAdjustInterval(order.scalePriceAdjustInterval());
//        this.setScaleProfitOffset(BigDecimal.valueOf(order.scaleProfitOffset()));
//        this.setScaleAutoReset(order.scaleAutoReset());
//        this.setScaleInitPosition(order.scaleInitPosition());
//        this.setScaleInitFillQty(order.scaleInitFillQty());
//        this.setScaleRandomPercent(order.scaleRandomPercent());
//        this.setScaleTable(order.scaleTable());
//
//        // 对冲
//        this.setHedgeType(order.hedgeType());
//        this.setHedgeParam(order.hedgeParam());
//        this.setHedgeMaxSize(order.hedgeMaxSize());
//
//        // 算法单
//        this.setAlgoStrategy(order.algoStrategy());
//        this.setAlgoId(order.algoId());
//        this.setSoftDollarTier(order.softDollarTier() == null ? null : order.softDollarTier().name());
//
//        // MiFID2
//        this.setMifid2DecisionMaker(order.mifid2DecisionMaker());
//        this.setMifid2DecisionAlgo(order.mifid2DecisionAlgo());
//        this.setMifid2ExecutionTrader(order.mifid2ExecutionTrader());
//        this.setMifid2ExecutionAlgo(order.mifid2ExecutionAlgo());
//
//        // 人工订单
//        this.setManualOrderTime(order.manualOrderTime());
//        this.setManualOrderIndicator(order.manualOrderIndicator());
//        this.setAdvancedErrorOverride(order.advancedErrorOverride());
//        this.setShareholder(order.shareholder());
    }
}