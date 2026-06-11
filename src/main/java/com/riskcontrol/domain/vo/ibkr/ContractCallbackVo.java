package com.riskcontrol.domain.vo.ibkr;

import com.ib.client.ComboLeg;
import com.ib.client.DeltaNeutralContract;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ContractCallbackVo {

    @Schema(description = "合约唯一 ID")
    private int conId;

    @Schema(description = "股票简称")
    private String symbol;

    @Schema(description = "类型 STK = 股票（Stock）、OPT期权、FUT期货、FX外汇")
    private String secType;

    // SMART （IB 智能路由）
    @Schema(description = "交易所，NASDAQ 纳斯达克")
    private String exchange;

    @Schema(description = "结算币种")
    private String currency;

    // ==========================================期权
    @Schema(description = "行权价，仅期权 / 权证有效；股票固定为 0.0")
    private Double strike;

    @Schema(description = "期权类型：C看涨 / P看跌；股票留空字符串")
    private String right;

    @Schema(description = "到期日 / 合约月份，仅期货、期权有效；股票留空")
    private String lastTradeDateOrContractMonth;

    private String lastTradeDate;

    // 股票期权：代表 1 张期权对应多少股股票，美股常规 100
    // 期货：合约规格乘数
    // 示例：乘数 = 100 → 行权 1 张期权，交割 100 股标的
    @Schema(description = "合约乘数（每手合约对应标的数量）")
    private String multiplier;

    // ==========================================交易所
    // 主上市交易所（标的原生交易所）
    // exchange：你实际交易的市场
    // primaryExch：标的原生挂牌市场
    @Schema(description = "主上市交易所")
    private String primaryExch;

    // 交易所本地代码
    @Schema(description = "交易所本地代码")
    private String localSymbol;

    @Schema(description = "交易品类分组")
    private String tradingClass;

    // ==========================================证券身份标识
    // 外部证券编码类型
    @Schema(description = "外部证券编码类型")
    private String secIdType;

    private String secId;

    // 合约中文
    @Schema(description = "合约中文")
    private String description;

    // 发行方 ID
    @Schema(description = "发行方 ID")
    private String issuerId;

    // =========================================组合合约、Delta 对冲、过期合约
    // 做Delta 中性组合交易时，绑定的对冲标的合约对象。
    @Schema(description = "Delta 中性对冲合约")
    private DeltaNeutralContract deltaNeutralContract;

    // 是否包含已到期合约
    @Schema(description = "是否包含已到期合约")
    private boolean includeExpired;

    // 组合合约文字描述
    @Schema(description = "组合合约文字描述")
    private String comboLegsDescrip;

    // 组合合约持仓腿明细列表。
    @Schema(description = "组合合约持仓腿明细列表")
    private List<ComboLeg> comboLegs;
}
