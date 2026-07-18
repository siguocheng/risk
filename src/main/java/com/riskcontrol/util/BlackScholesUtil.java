package com.riskcontrol.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BlackScholesUtil {

    private static final int MAX_ITERATIONS = 100;
    private static final double PRECISION = 1e-8;

    public static double normCDF(double x) {
        double t = 1.0 / (1.0 + 0.2316419 * Math.abs(x));
        double d = 0.3989423 * Math.exp(-x * x / 2.0);
        double prob = d * t * (0.3193815 + t * (-0.3565638 + t * (1.781478 + t * (-1.821256 + t * 1.330274))));
        return x >= 0 ? 1.0 - prob : prob;
    }

    public static double normPDF(double x) {
        return Math.exp(-x * x / 2.0) / Math.sqrt(2.0 * Math.PI);
    }

    public static double calculateDelta(double spotPrice, double strikePrice, double riskFreeRate,
                                        double timeToExpiry, double volatility, boolean isCall) {
        if (timeToExpiry <= 0 || volatility <= 0) {
            return isCall ? 1.0 : -1.0;
        }

        double sqrtT = Math.sqrt(timeToExpiry);
        double d1 = (Math.log(spotPrice / strikePrice) + (riskFreeRate + volatility * volatility / 2.0) * timeToExpiry)
                / (volatility * sqrtT);

        return isCall ? normCDF(d1) : normCDF(d1) - 1.0;
    }

    public static double calculateOptionPrice(double spotPrice, double strikePrice, double riskFreeRate,
                                               double timeToExpiry, double volatility, boolean isCall) {
        if (timeToExpiry <= 0) {
            double intrinsicValue = Math.max(isCall ? spotPrice - strikePrice : strikePrice - spotPrice, 0);
            return intrinsicValue;
        }

        double sqrtT = Math.sqrt(timeToExpiry);
        double d1 = (Math.log(spotPrice / strikePrice) + (riskFreeRate + volatility * volatility / 2.0) * timeToExpiry)
                / (volatility * sqrtT);
        double d2 = d1 - volatility * sqrtT;

        double price = spotPrice * normCDF(d1) - strikePrice * Math.exp(-riskFreeRate * timeToExpiry) * normCDF(d2);

        if (!isCall) {
            price = price + strikePrice * Math.exp(-riskFreeRate * timeToExpiry) - spotPrice;
        }

        return price;
    }

    public static double calculateVega(double spotPrice, double strikePrice, double riskFreeRate,
                                        double timeToExpiry, double volatility) {
        if (timeToExpiry <= 0 || volatility <= 0) {
            return 0.0;
        }

        double sqrtT = Math.sqrt(timeToExpiry);
        double d1 = (Math.log(spotPrice / strikePrice) + (riskFreeRate + volatility * volatility / 2.0) * timeToExpiry)
                / (volatility * sqrtT);

        return spotPrice * normPDF(d1) * sqrtT;
    }

    public static double calculateImpliedVolatility(double spotPrice, double strikePrice, double riskFreeRate,
                                                     double timeToExpiry, double marketPrice, boolean isCall) {
        if (timeToExpiry <= 0) {
            return Double.NaN;
        }

        if (marketPrice <= 0) {
            return Double.NaN;
        }

        double intrinsicValue = isCall ? Math.max(spotPrice - strikePrice, 0) : Math.max(strikePrice - spotPrice, 0);
        if (marketPrice < intrinsicValue) {
            return Double.NaN;
        }

        double lowVol = 0.001;
        double highVol = 5.0;

        double lowPrice = calculateOptionPrice(spotPrice, strikePrice, riskFreeRate, timeToExpiry, lowVol, isCall);
        double highPrice = calculateOptionPrice(spotPrice, strikePrice, riskFreeRate, timeToExpiry, highVol, isCall);

        if (lowPrice > marketPrice) {
            return Double.NaN;
        }

        while (highVol - lowVol > PRECISION) {
            double midVol = (lowVol + highVol) / 2;
            double midPrice = calculateOptionPrice(spotPrice, strikePrice, riskFreeRate, timeToExpiry, midVol, isCall);

            if (midPrice < marketPrice) {
                lowVol = midVol;
            } else {
                highVol = midVol;
            }
        }

        double guess = (lowVol + highVol) / 2;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            double price = calculateOptionPrice(spotPrice, strikePrice, riskFreeRate, timeToExpiry, guess, isCall);
            double vega = calculateVega(spotPrice, strikePrice, riskFreeRate, timeToExpiry, guess);

            if (Math.abs(price - marketPrice) < PRECISION || Math.abs(vega) < PRECISION) {
                break;
            }

            guess = guess - (price - marketPrice) / vega;

            if (guess <= 0) {
                guess = 0.001;
            }
        }

        return guess;
    }
}