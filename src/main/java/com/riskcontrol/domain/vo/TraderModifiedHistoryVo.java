package com.riskcontrol.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TraderModifiedHistoryVo {

    @Schema(description = "原交易员名称")
    private String orgTraderName;

    @Schema(description = "最新本金")
    private BigDecimal orgCapital;

    @Schema(description = "当前交易员名称")
    private String currentTraderName;

    @Schema(description = "当前本金")
    private BigDecimal currentCapital;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    protected LocalDateTime modifiedTime;
}
