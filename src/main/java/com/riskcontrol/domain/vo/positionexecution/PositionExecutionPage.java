package com.riskcontrol.domain.vo.positionexecution;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成交明细分页VO
 *
 * @author zpc
 * @date 2026-06-20
 */
@Data
public class PositionExecutionPage {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单ID")
    private Integer orderId;

    @Schema(description = "客户ID")
    private Integer clientId;

    @Schema(description = "成交ID")
    private String execId;

    @Schema(description = "成交时间")
    private String time;

    @Schema(description = "账户号")
    private String acctNumber;

    @Schema(description = "交易所")
    private String exchange;

    @Schema(description = "买卖方向")
    private String side;

    @Schema(description = "本次成交数量")
    private String shares;

    @Schema(description = "成交单价")
    private String price;

    @Schema(description = "全局唯一permId")
    private Long permId;

    @Schema(description = "清算标识")
    private Integer liquidation;

    @Schema(description = "累计成交数量")
    private String cumQty;

    @Schema(description = "平均成交价")
    private String avgPrice;

    @Schema(description = "订单备注")
    private String orderRef;

    @Schema(description = "EV规则")
    private String evRule;

    @Schema(description = "EV乘数")
    private String evMultiplier;

    @Schema(description = "模型编码")
    private String modelCode;

    @Schema(description = "流动性类型")
    private String lastLiquidity;

    @Schema(description = "是否待价格修订")
    private Boolean pendingPriceRevision;

    @Schema(description = "提交人")
    private String submitter;

    @Schema(description = "期权行权/失效类型")
    private String optExerciseOrLapseType;

    @Schema(description = "佣金及各项费用")
    private String commissionAndFees;

    @Schema(description = "结算币种")
    private String currency;

    @Schema(description = "已实现盈亏")
    private String realizedPnl;

    @Schema(description = "收益率")
    private String yield;

    @Schema(description = "收益兑付日期")
    private Long yieldRedemptionDate;

    @Schema(description = "分配状态：0未分配 1部分分配 2已分配")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime modifiedTime;
}
