package com.riskcontrol.domain.vo.positionexecution;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
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

    @Schema(description = "合约id")
    private Integer conid;

    @Schema(description = "股票简称")
    private String symbol;

    @Schema(description = "成交时间")
    private String time;

    @Schema(description = "账户号")
    private String accountCode;

    @Schema(description = "交易所")
    private String exchange;

    @Schema(description = "买卖方向")
    private String side;

    @Schema(description = "本次成交数量")
    private BigDecimal shares;

    @Schema(description = "成交单价")
    private String price;

    @Schema(description = "佣金及各项费用")
    private String commissionAndFees;

    @Schema(description = "结算币种")
    private String currency;

    @Schema(description = "已实现盈亏")
    private BigDecimal realizedPnl;

    @Schema(description = "未分配数量")
    private BigDecimal remainQty;
}
