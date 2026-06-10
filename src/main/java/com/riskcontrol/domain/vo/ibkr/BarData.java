package com.riskcontrol.domain.vo.ibkr;

import lombok.Data;

@Data
public class BarData {

    public String time;
    public double open; // 开盘价
    public double high; // 周期内最高价
    public double low; // 周期内最低价
    public double close; // 收盘价
    public long volume; // 成交量 股票：单位为股 期货：单位为手
    public double wap; // 成交量加权平均价 周期内总成交金额 ÷ 总成交量，常用于判断市场平均持仓成本。
    public int count; // 成交笔数
}
