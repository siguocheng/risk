package com.riskcontrol.domain.vo.contractexecution;

import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 成交明细查询条件VO
 *
 * @author zpc
 * @date 2026-06-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractExecutionQuery extends BasePageQuery {

    @Schema(description = "订单ID")
    private Integer orderId;

    @Schema(description = "客户ID")
    private Integer clientId;

    @Schema(description = "成交ID")
    private String execId;

    @Schema(description = "账户号")
    private String acctNumber;

    @Schema(description = "交易所")
    private String exchange;

    @Schema(description = "买卖方向")
    private String side;

    @Schema(description = "全局唯一permId")
    private Long permId;

    @Schema(description = "模型编码")
    private String modelCode;

    @Schema(description = "提交人")
    private String submitter;

    @Schema(description = "分配状态：0未分配 1部分分配 2已分配")
    private Integer status;

    @Schema(description = "成交时间-开始")
    private LocalDateTime timeStart;

    @Schema(description = "成交时间-结束")
    private LocalDateTime timeEnd;
}
