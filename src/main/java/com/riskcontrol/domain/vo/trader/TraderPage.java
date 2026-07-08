package com.riskcontrol.domain.vo.trader;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易员分页结果
 *
 * @author zpc
 * @date 2026-06-18
 */
@Data
public class TraderPage {

    @Schema(description = "交易员ID")
    private Long id;

    @Schema(description = "交易员名称")
    private String traderName;

    @Schema(description = "本金")
    private BigDecimal capital;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedTime;
}