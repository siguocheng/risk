package com.riskcontrol.domain.vo.trader;

import com.riskcontrol.domain.TraderModifiedHistory;
import com.riskcontrol.domain.vo.TraderModifiedHistoryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TraderDetail {

    @Schema(description = "交易员ID")
    private Long id;

    @Schema(description = "交易员名称")
    private String traderName;

    @Schema(description = "本金")
    private BigDecimal capital;

    @Schema(description = "修改历史记录")
    private List<TraderModifiedHistoryVo> modifiedHistoryList;
}