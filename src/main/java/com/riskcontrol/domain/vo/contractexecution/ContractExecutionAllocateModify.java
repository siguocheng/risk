package com.riskcontrol.domain.vo.contractexecution;

import com.riskcontrol.domain.ContractExecutionHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成交分配请求VO
 *
 * @author zpc
 * @date 2026-06-21
 */
@Data
public class ContractExecutionAllocateModify {

    @Schema(description = "交易数据主键id")
    private Long id;

    @Schema(description = "成交ID")
    private String execId;

    @Schema(description = "分配明细列表")
    private List<ContractExecutionHistory> details;
}
