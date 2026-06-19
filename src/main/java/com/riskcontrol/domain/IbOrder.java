package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ib.client.Contract;
import com.riskcontrol.domain.vo.ibkr.IbOrderCallbackVo;
import com.riskcontrol.util.IbValueUtil;
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

    @TableField(value = "conid")
    @Schema(description = "合约ID")
    private Integer conid;

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
    private String faPercentage;

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
    private Boolean allOrNone = false;

    @TableField(value = "block_order")
    @Schema(description = "大宗订单")
    private Boolean blockOrder = false;

    @TableField(value = "hidden")
    @Schema(description = "隐藏订单")
    private Boolean hidden = false;

    @TableField(value = "outside_rth")
    @Schema(description = "允许盘外交易")
    private Boolean outsideRth = false;

    @TableField(value = "sweep_to_fill")
    @Schema(description = "扫单立即成交")
    private Boolean sweepToFill = false;

    @TableField(value = "transmit")
    @Schema(description = "是否直接推送交易所")
    private Boolean transmit = true;

    @TableField(value = "what_if")
    @Schema(description = "仅试算不提交订单")
    private Boolean whatIf = false;

    @TableField(value = "what_if_type")
    @Schema(description = "试算类型")
    private Integer whatIfType;

    @TableField(value = "override_percentage_constraints")
    @Schema(description = "覆盖比例限制")
    private Boolean overridePercentageConstraints = false;

    @TableField(value = "opt_out_smart_routing")
    @Schema(description = "关闭智能路由")
    private Boolean optOutSmartRouting = false;

    @TableField(value = "not_held")
    @Schema(description = "Not Held交易指令")
    private Boolean notHeld = false;

    @TableField(value = "solicited")
    @Schema(description = "主动推介订单")
    private Boolean solicited = false;

    @TableField(value = "randomize_size")
    @Schema(description = "随机委托数量")
    private Boolean randomizeSize = false;

    @TableField(value = "randomize_price")
    @Schema(description = "随机委托价格")
    private Boolean randomizePrice = false;

    @TableField(value = "dont_use_auto_price_for_hedge")
    @Schema(description = "对冲不使用自动价格")
    private Boolean dontUseAutoPriceForHedge = false;

    @TableField(value = "is_oms_container")
    @Schema(description = "OMS容器订单")
    private Boolean isOmsContainer = false;

    @TableField(value = "discretionary_up_to_limit_price")
    @Schema(description = "自主价不超过限价")
    private Boolean discretionaryUpToLimitPrice = false;

    @TableField(value = "auto_cancel_parent")
    @Schema(description = "父单取消自动撤子单")
    private Boolean autoCancelParent = false;

    @TableField(value = "imbalance_only")
    @Schema(description = "仅失衡撮合")
    private Boolean imbalanceOnly = false;

    @TableField(value = "include_overnight")
    @Schema(description = "包含隔夜交易")
    private Boolean includeOvernight = false;

    @TableField(value = "professional_customer")
    @Schema(description = "专业客户标识")
    private Boolean professionalCustomer = false;

    @TableField(value = "post_only")
    @Schema(description = "仅挂单不主动成交")
    private Boolean postOnly = false;

    @TableField(value = "allow_pre_open")
    @Schema(description = "允许盘前交易")
    private Boolean allowPreOpen = false;

    @TableField(value = "ignore_open_auction")
    @Schema(description = "忽略开盘集合竞价")
    private Boolean ignoreOpenAuction = false;

    @TableField(value = "deactivate")
    @Schema(description = "暂停订单")
    private Boolean deactivate = false;

    @TableField(value = "conditions_cancel_order")
    @Schema(description = "条件触发后撤单")
    private Boolean conditionsCancelOrder = false;

    @TableField(value = "conditions_ignore_rth")
    @Schema(description = "条件忽略盘外时段")
    private Boolean conditionsIgnoreRth = false;

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
    private String volatilityType;

    @TableField(value = "continuous_update")
    @Schema(description = "持续更新开关")
    private Integer continuousUpdate;

    @TableField(value = "reference_price_type")
    @Schema(description = "参考价类型")
    private String referencePriceType;

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
    private Boolean isPeggedChangeAmountDecrease = false;

    @TableField(value = "reference_change_amount")
    @Schema(description = "参考变动幅度")
    private BigDecimal referenceChangeAmount;

    @TableField(value = "reference_exchange_id")
    @Schema(description = "参考交易所")
    private String referenceExchangeId;

    @TableField(value = "trigger_method")
    @Schema(description = "触发方式")
    private String triggerMethod;

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
    private Boolean deltaNeutralShortSale = false;

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
    private Boolean scaleAutoReset = false;

    @TableField(value = "scale_init_position")
    @Schema(description = "阶梯初始持仓")
    private Integer scaleInitPosition;

    @TableField(value = "scale_init_fill_qty")
    @Schema(description = "阶梯初始成交数量")
    private Integer scaleInitFillQty;

    @TableField(value = "scale_random_percent")
    @Schema(description = "阶梯随机比例")
    private Boolean scaleRandomPercent = false;

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
    private String ocaType;

    @TableField(value = "model_code")
    @Schema(description = "模型编码")
    private String modelCode;

    @TableField(value = "ext_operator")
    @Schema(description = "外部操作员")
    private String extOperator;

    @TableField(value = "submitter")
    @Schema(description = "提交人")
    private String submitter;

    @TableField(value = "status")
    @Schema(description = "订单状态")
    private String status;

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

    public IbOrder() {
    }

    public IbOrder(IbOrderCallbackVo ibOrderCallbackVo) {
        Contract contract = ibOrderCallbackVo.getContract();
        this.conid = contract.conid();

        com.ib.client.Order order = ibOrderCallbackVo.getOrder();
        com.ib.client.OrderState orderState = ibOrderCallbackVo.getOrderState();

        // 基础信息
        this.orderId = ibOrderCallbackVo.getOrderId();
        this.clientId = order.clientId();
        this.permId = order.permId();
        this.parentId = order.parentId();
        this.parentPermId = order.parentPermId();
        this.slOrderId = order.slOrderId();
        this.slOrderType = order.slOrderType();
        this.ptOrderId = order.ptOrderId();
        this.ptOrderType = order.ptOrderType();
        this.orderRef = order.orderRef();
        this.ocaGroup = order.ocaGroup();
        this.ocaType = order.ocaType().getApiString();
        this.modelCode = order.modelCode();
        this.extOperator = order.extOperator();
        this.submitter = order.submitter();

        // 交易方向、数量、价格
        this.action = order.action().getApiString();
        this.totalQuantity = order.totalQuantity() == null ? null : new BigDecimal(order.totalQuantity().toString());
        this.filledQuantity = order.filledQuantity() == null ? null : new BigDecimal(order.filledQuantity().toString());

        this.cashQty = IbValueUtil.trimDouble(order.cashQty());
        this.minQty = IbValueUtil.trimInt(order.minQty());
        this.minTradeQty = order.minTradeQty();
        this.minCompeteSize = order.minCompeteSize();
        this.displaySize = order.displaySize();
        this.orderType = order.orderType() == null ? null : order.orderType().name();
        this.adjustedOrderType = order.adjustedOrderType() == null ? null : order.adjustedOrderType().name();

        this.lmtPrice = IbValueUtil.trimDouble(order.lmtPrice());
        this.lmtPriceOffset = IbValueUtil.trimDouble(order.lmtPriceOffset());
        this.auxPrice = IbValueUtil.trimDouble(order.auxPrice());
        this.trailStopPrice = IbValueUtil.trimDouble(order.trailStopPrice());
        this.trailingPercent = IbValueUtil.trimDouble(order.trailingPercent());
        this.triggerPrice = IbValueUtil.trimDouble(order.triggerPrice());
        this.adjustedStopPrice = IbValueUtil.trimDouble(order.adjustedStopPrice());
        this.adjustedStopLimitPrice = IbValueUtil.trimDouble(order.adjustedStopLimitPrice());
        this.adjustedTrailingAmount = IbValueUtil.trimDouble(order.adjustedTrailingAmount());
        this.adjustableTrailingUnit = order.adjustableTrailingUnit();
        this.percentOffset = IbValueUtil.trimDouble(order.percentOffset());
        this.discretionaryAmt = IbValueUtil.trimDouble(order.discretionaryAmt());
        this.competeAgainstBestOffset = IbValueUtil.trimDouble(order.competeAgainstBestOffset());
        this.midOffsetAtWhole = IbValueUtil.trimDouble(order.midOffsetAtWhole());
        this.midOffsetAtHalf = IbValueUtil.trimDouble(order.midOffsetAtHalf());

        // 时效TIF
        this.tif = order.tif().getApiString();
        this.goodAfterTime = order.goodAfterTime();
        this.goodTillDate = order.goodTillDate();
        this.autoCancelDate = order.autoCancelDate();
        this.duration = order.duration();
        this.activeStartTime = order.activeStartTime();
        this.activeStopTime = order.activeStopTime();

        // 账户清算FA
        this.accountCode = order.account();
        this.customerAccount = order.customerAccount();
        this.settlingFirm = order.settlingFirm();
        this.clearingAccount = order.clearingAccount();
        this.clearingIntent = order.clearingIntent();
        this.faGroup = order.faGroup();
        this.faMethod = order.faMethod().getApiString();
        this.faPercentage = order.faPercentage();
        this.bondAccruedInterest = order.bondAccruedInterest();

        // 开仓做空
        this.openClose = order.openClose();
        this.origin = order.origin();
        this.shortSaleSlot = order.shortSaleSlot();
        this.designatedLocation = order.designatedLocation();
        this.exemptCode = order.exemptCode();
        this.rule80A = order.rule80A().getApiString();

        // 布尔标识
        this.allOrNone = order.allOrNone();
        this.blockOrder = order.blockOrder();
        this.hidden = order.hidden();
        this.outsideRth = order.outsideRth();
        this.sweepToFill = order.sweepToFill();
        this.transmit = order.transmit();
        this.whatIf = order.whatIf();
        this.whatIfType = order.whatIfType();
        this.overridePercentageConstraints = order.overridePercentageConstraints();
        this.optOutSmartRouting = order.optOutSmartRouting();
        this.notHeld = order.notHeld();
        this.solicited = order.solicited();
        this.randomizeSize = order.randomizeSize();
        this.randomizePrice = order.randomizePrice();
        this.dontUseAutoPriceForHedge = order.dontUseAutoPriceForHedge();
        this.isOmsContainer = order.isOmsContainer();
        this.discretionaryUpToLimitPrice = order.discretionaryUpToLimitPrice();
        this.autoCancelParent = order.autoCancelParent();
        this.imbalanceOnly = order.imbalanceOnly();
        this.includeOvernight = order.includeOvernight();
        this.professionalCustomer = order.professionalCustomer();
        this.postOnly = order.postOnly();
        this.allowPreOpen = order.allowPreOpen();
        this.ignoreOpenAuction = order.ignoreOpenAuction();
        this.deactivate = order.deactivate();
        this.conditionsCancelOrder = order.conditionsCancelOrder();
        this.conditionsIgnoreRth = order.conditionsIgnoreRth();
        this.seekPriceImprovement = order.seekPriceImprovement();
        this.routeMarketableToBbo = order.routeMarketableToBbo();
        this.usePriceMgmtAlgo = order.usePriceMgmtAlgo();

        // 波动率、参考价格
        this.volatility = IbValueUtil.trimDouble(order.volatility());
        this.volatilityType = order.volatilityType().getApiString();
        this.continuousUpdate = order.continuousUpdate();
        this.referencePriceType = order.referencePriceType().getApiString();
        this.referenceContractId = order.referenceContractId();
        this.refFuturesConId = order.refFuturesConId();
        this.startingPrice = IbValueUtil.trimDouble(order.startingPrice());
        this.stockRefPrice = IbValueUtil.trimDouble(order.stockRefPrice());
        this.delta = IbValueUtil.trimDouble(order.delta());
        this.stockRangeLower = IbValueUtil.trimDouble(order.stockRangeLower());
        this.stockRangeUpper = IbValueUtil.trimDouble(order.stockRangeUpper());
        this.basisPoints = IbValueUtil.trimDouble(order.basisPoints());
        this.basisPointsType = order.basisPointsType();
        this.peggedChangeAmount = IbValueUtil.trimDouble(order.peggedChangeAmount());
        this.isPeggedChangeAmountDecrease = order.isPeggedChangeAmountDecrease();
        this.referenceChangeAmount = IbValueUtil.trimDouble(order.referenceChangeAmount());
        this.referenceExchangeId = order.referenceExchangeId();
        this.triggerMethod = order.triggerMethod().getApiString();
        this.auctionStrategy = order.auctionStrategy();
        this.postToAts = order.postToAts();

        // Delta Neutral
        this.deltaNeutralOrderType = order.deltaNeutralOrderType().getApiString();
        this.deltaNeutralAuxPrice = IbValueUtil.trimDouble(order.deltaNeutralAuxPrice());
        this.deltaNeutralConId = order.deltaNeutralConId();
        this.deltaNeutralOpenClose = order.deltaNeutralOpenClose();
        this.deltaNeutralShortSale = order.deltaNeutralShortSale();
        this.deltaNeutralShortSaleSlot = order.deltaNeutralShortSaleSlot();
        this.deltaNeutralDesignatedLocation = order.deltaNeutralDesignatedLocation();
        this.deltaNeutralSettlingFirm = order.deltaNeutralSettlingFirm();
        this.deltaNeutralClearingAccount = order.deltaNeutralClearingAccount();
        this.deltaNeutralClearingIntent = order.deltaNeutralClearingIntent();

        // 阶梯Scale
        this.scaleInitLevelSize = order.scaleInitLevelSize();
        this.scaleSubsLevelSize = order.scaleSubsLevelSize();
        this.scalePriceIncrement = IbValueUtil.trimDouble(order.scalePriceIncrement());
        this.scalePriceAdjustValue = IbValueUtil.trimDouble(order.scalePriceAdjustValue());
        this.scalePriceAdjustInterval = order.scalePriceAdjustInterval();
        this.scaleProfitOffset = IbValueUtil.trimDouble(order.scaleProfitOffset());
        this.scaleAutoReset = order.scaleAutoReset();
        this.scaleInitPosition = order.scaleInitPosition();
        this.scaleInitFillQty = order.scaleInitFillQty();
        this.scaleRandomPercent = order.scaleRandomPercent();
        this.scaleTable = order.scaleTable();

        // 对冲
        this.hedgeType = order.hedgeType().getApiString();
        this.hedgeParam = order.hedgeParam();
        this.hedgeMaxSize = IbValueUtil.trimInt(order.hedgeMaxSize());

        // 算法单
        this.algoStrategy = order.algoStrategy().getApiString();
        this.algoId = order.algoId();
        this.softDollarTier = order.softDollarTier() == null ? null : order.softDollarTier().name();

        // MiFID2
        this.mifid2DecisionMaker = order.mifid2DecisionMaker();
        this.mifid2DecisionAlgo = order.mifid2DecisionAlgo();
        this.mifid2ExecutionTrader = order.mifid2ExecutionTrader();
        this.mifid2ExecutionAlgo = order.mifid2ExecutionAlgo();

        // 人工订单
        this.manualOrderTime = order.manualOrderTime();
        this.manualOrderIndicator = order.manualOrderIndicator();
        this.advancedErrorOverride = order.advancedErrorOverride();
        this.shareholder = order.shareholder();

        // OrderState
        if (orderState != null) {
            this.status = orderState.status().name();
            this.initMarginBefore = orderState.initMarginBefore();
            this.maintMarginBefore = orderState.maintMarginBefore();
            this.equityWithLoanBefore = orderState.equityWithLoanBefore();
            this.initMarginChange = orderState.initMarginChange();
            this.maintMarginChange = orderState.maintMarginChange();
            this.equityWithLoanChange = orderState.equityWithLoanChange();
            this.initMarginAfter = orderState.initMarginAfter();
            this.maintMarginAfter = orderState.maintMarginAfter();
            this.equityWithLoanAfter = orderState.equityWithLoanAfter();
            this.commissionAndFees = IbValueUtil.trimDouble(orderState.commissionAndFees());
            this.minCommissionAndFees = IbValueUtil.trimDouble(orderState.minCommissionAndFees());
            this.maxCommissionAndFees = IbValueUtil.trimDouble(orderState.maxCommissionAndFees());
            this.commissionAndFeesCurrency = orderState.commissionAndFeesCurrency();
            this.marginCurrency = orderState.marginCurrency();
            this.warningText = orderState.warningText();
            this.completedTime = orderState.completedTime();
            this.completedStatus = orderState.completedStatus();
            this.rejectReason = orderState.rejectReason();
            this.suggestedSize = orderState.suggestedSize() == null ? null : IbValueUtil.trimBigDec(orderState.suggestedSize().value());
        }
    }
}