package com.riskcontrol.domain.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ContractBo extends BasePageQuery {

    @Schema(description = "合约简称")
    private String symbol;
}
