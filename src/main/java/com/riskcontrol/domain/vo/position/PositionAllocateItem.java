package com.riskcontrol.domain.vo.position;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionAllocateItem {

    @Schema(description = "分配记录id")
    private Long id;

    @Schema(description = "账号代码")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "合约ID")
    @TableField(value = "conid")
    private Integer conid;

    @Schema(description = "策略名称")
    @TableField(value = "strategy_name")
    private String strategyName;

    @Schema(description = "交易员名称")
    @TableField(value = "trader_name")
    private String traderName;

    @Schema(description = "分配数量（正数增加，负数减少）")
    @TableField(value = "allocate_qty")
    private BigDecimal allocateQty;
}
