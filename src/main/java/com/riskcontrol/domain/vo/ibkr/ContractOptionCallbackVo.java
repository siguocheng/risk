package com.riskcontrol.domain.vo.ibkr;

import lombok.Data;

/**
 * 期权希腊值回调数据VO
 *
 * @author zpc
 * @date 2026-06-17
 */
@Data
public class ContractOptionCallbackVo {

    /**
     * 请求ID
     */
    private int tickerId;

    /**
     * 隐含波动率 IV
     */
    private double impliedVol;

    /**
     * 德尔塔
     */
    private double delta;

    /**
     * 期权理论价
     */
    private double optPrice;

    /**
     * 股息现值
     */
    private double pvDividend;

    /**
     * 伽马
     */
    private double gamma;

    /**
     * 维加
     */
    private double vega;

    /**
     * 西塔
     */
    private double theta;

    /**
     * 标的价格
     */
    private double undPrice;
}
