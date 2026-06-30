package com.riskcontrol.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalTrimZeroSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        // 去除末尾多余0
        BigDecimal twoScale = value.setScale(4, RoundingMode.HALF_UP);

        BigDecimal trimVal = twoScale.stripTrailingZeros();
        // toPlainString 杜绝科学计数
        String plain = trimVal.toPlainString();
        gen.writeNumber(plain);
    }
}
