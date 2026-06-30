package com.riskcontrol.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Benchmark {

    private String key;

    private String name;

    private BigDecimal value;

    @JsonIgnore
    private BigDecimal value1;
}
