package com.riskcontrol.domain.vo.position;

import com.riskcontrol.domain.PositionAllocateHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 持仓分配请求VO
 *
 * @author zpc
 * @date 2026-06-22
 */
@Data
public class PositionAllocateRequest {

    @Schema(description = "持仓id或者交易id")
    private Long id;

    @Schema(description = "操作类型 1持仓分配 2交易分配")
    private Integer operateType;

    @Schema(description = "分配明细列表")
    private List<PositionAllocateHistory> details;

}
