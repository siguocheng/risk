package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@TableName("contract_sector")
@Schema(description = "股票合约板块信息表")
public class ContractSector extends BaseEntity {

    @TableField("conid")
    @Schema(description = "IB合约ID")
    private Integer conid;

    @TableField("type")
    @Schema(description = "标的类型")
    private Integer type;

    @TableField("sector")
    @Schema(description = "行业板块")
    private String sector;

    @TableField("sector_value")
    @Schema(description = "行业板块中文")
    private String sectorValue;

    public static Map<String,String> sectorMap = new HashMap<>();

    static {
        sectorMap.put("Technology", "信息技术(科技)");
        sectorMap.put("Indices", "指数");
        sectorMap.put("Consumer", "消费");

    }

}
