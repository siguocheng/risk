package com.riskcontrol.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ChartVo {

    private String date;

    private BigDecimal nav;

    private BigDecimal portReturn;

    private List<Benchmark> benchmarks;
}
