package com.riskcontrol.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RiskControlVarModule {

    @Schema(description = "var")
    private VarVo var;

    @Schema(description = "es")
    private BigDecimal es;

    @Schema(description = "最大回撤")
    private BigDecimal maxDrawdown;

    @Schema(description = "组合加权隐含波动率")
    private BigDecimal iv;

    @Schema(description = "保证金比率")
    private BigDecimal marginRatio;

    @Schema(description = "市场VIX对标的数值")
    private BigDecimal vix;

    @Schema(description = "压力测试预估损失")
    private List<PressureTestVo> pressureTestVo;
}
