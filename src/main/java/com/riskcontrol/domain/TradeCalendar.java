package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 美股交易日历实体类
 *
 * @author zpc
 * @date 2026-07-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("trade_calendar")
public class TradeCalendar extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "类型1:美股")
    @TableField(value = "type")
    private Integer type;

    @Schema(description = "美股交易日,yyyy-MM-dd")
    @TableField(value = "trade_date")
    private String tradeDate;

    @Schema(description = "上一交易日记录ID")
    @TableField(value = "pre_id")
    private Long preId;

}