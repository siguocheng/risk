package com.riskcontrol.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {

    public static BigDecimal doubleToDecimal(Double num){
        return BigDecimal.valueOf(num).setScale(8, RoundingMode.HALF_UP);
    }
}
