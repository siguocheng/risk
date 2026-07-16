package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("exchange_zone")
public class ExchangeZone extends BaseEntity {

    @Schema(description = "交易所，NASDAQ 纳斯达克")
    @TableField(value = "exchange")
    private String exchange;

    @Schema(description = "地区")
    @TableField(value = "zone")
    private String zone;

    @Schema(description = "地区值")
    @TableField(value = "zone_value")
    private String zoneValue;

    @Schema(description = "地区ID")
    @TableField(value = "zone_id")
    private String zoneId;
}