package com.riskcontrol;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.domain.Position;
import com.riskcontrol.service.IContractMarketHistoryService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class CalTest {

    @Resource
    IContractMarketHistoryService contractMarketHistoryService;

    @Test
    public void calvar(){

        int conid = 265598;
        System.out.printf("conid:" + conid);
        // 取得日价格
        double[] prices = contractMarketHistoryService.queryContractMarketPriceCloseByConid(conid);

        double var = calculate(prices,191,0.95);

        System.out.println(var);
    }

    public static double[] logReturns(double[] prices) {

        double[] returns = new double[prices.length - 1];

        for (int i = 1; i < prices.length; i++) {
            returns[i - 1] = Math.log(prices[i] / prices[i - 1]);
        }

        return returns;
    }

    public static double[] historicalPnL(double[] prices, int shares) {

        double currentPrice = prices[prices.length - 1];

        double positionValue = currentPrice * shares;

        double[] returns = logReturns(prices);

        double[] pnl = new double[returns.length];

        for (int i = 0; i < returns.length; i++) {
            pnl[i] = positionValue * returns[i];
        }

        return pnl;
    }

    public static double historicalVaR(double[] prices,
                                       int shares,
                                       double confidence) {

        double[] pnl = historicalPnL(prices, shares);

        Arrays.sort(pnl);

        int index = (int)Math.floor((1.0 - confidence) * pnl.length);

        return -pnl[index];
    }

    public static double expectedShortfall(double[] prices,
                                           int shares,
                                           double confidence) {

        double[] pnl = historicalPnL(prices, shares);

        Arrays.sort(pnl);

        int cutoff = (int)Math.floor((1.0 - confidence) * pnl.length);

        double sum = 0;

        for (int i = 0; i <= cutoff; i++) {
            sum += pnl[i];
        }

        return -sum / (cutoff + 1);
    }

    public static double calculate(double[] prices,
                                   int shares,
                                   double confidence){

        double currentPrice = prices[prices.length-1];

        double positionValue = currentPrice * shares;

        double[] pnl = new double[prices.length-1];

        for(int i=1;i<prices.length;i++){

            double r = Math.log(prices[i]/prices[i-1]);

            pnl[i-1] = positionValue * r;
        }

        Arrays.sort(pnl);

        int index=(int)Math.floor((1-confidence)*pnl.length);

        return -pnl[index];
    }

    public static void main(String[] args) {

    }
}
