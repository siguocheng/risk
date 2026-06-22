package com.riskcontrol.domain.vo.trader;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交易员维护VO
 *
 * @author zpc
 * @date 2026-06-18
 */
@Data
public class TraderModify {

    @Schema(description = "交易员ID")
    private Long id;

    @Schema(description = "交易员名称")
    private String traderName;

    @Schema(description = "本金")
    @TableField(value = "本金")
    private BigDecimal capital;

    @Schema(description = "关联的账号列表")
    private List<String> accountCodes;
}