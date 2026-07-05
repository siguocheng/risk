package com.riskcontrol.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.domain.AccountCurrency;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.Position;
import com.riskcontrol.domain.PositionExecution;
import com.riskcontrol.enums.PositionExecutionOptTypeEnum;
import com.riskcontrol.enums.SetTypeEnum;
import com.riskcontrol.enums.TradeSideEnum;
import com.riskcontrol.service.IAccountCurrencyService;
import com.riskcontrol.service.IContractService;
import com.riskcontrol.service.IPositionExecutionService;
import com.riskcontrol.service.IPositionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CalPositionExecutionTask {

    @Resource
    IPositionExecutionService positionExecutionService;

    @Resource
    IPositionService positionService;

    @Resource
    IContractService contractService;

    @Resource
    IAccountCurrencyService accountCurrencyService;


    public void cal(){

        List<AccountCurrency> list = accountCurrencyService.list();
        for (AccountCurrency accountCurrency : list) {

            String accountCode = accountCurrency.getAccountCode();

            List<PositionExecution> positionExecutionsAccountCode = this.listPositionExecution(accountCode);
            if (positionExecutionsAccountCode.isEmpty()) {
                continue;
            }

            Map<String, List<PositionExecution>> positionExecutionDateGroup = positionExecutionsAccountCode.stream()
                    .collect(Collectors.groupingBy(pe -> pe.getDate()));

            for (String date : positionExecutionDateGroup.keySet()) {
                List<PositionExecution> positionExecutionsDate = positionExecutionDateGroup.get(date);

                Map<Integer, List<PositionExecution>> positionExecutionDateConidGroup = positionExecutionsDate.stream()
                        .collect(Collectors.groupingBy(pe -> pe.getConid()));

                Boolean isDayTrade = false;
                for (Integer conid : positionExecutionDateConidGroup.keySet()) {
                    List<PositionExecution> positionExecutionsDateConid = positionExecutionDateConidGroup.get(conid);

                    Set<String> intradayGroupKeys = new HashSet<>();

                    BigDecimal buyQty = positionExecutionsDateConid.stream()
                            .filter(t -> TradeSideEnum.BOT.name().equals(t.getSide()))
                            .map(PositionExecution::getShares)
                            .filter(qty -> qty != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal sellQty = positionExecutionsDateConid.stream()
                            .filter(t -> TradeSideEnum.SLD.name().equals(t.getSide()))
                            .map(PositionExecution::getShares)
                            .filter(qty -> qty != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    if (buyQty.compareTo(sellQty) == 0 && buyQty.compareTo(BigDecimal.ZERO) > 0) {
                        isDayTrade = true;
                    }

                    // 如果是日内交易
                    if (isDayTrade){
                        this.hanlenTradayTrades(positionExecutionsDateConid, accountCode, date);
                    } else {
                        
                    }
                }
            }
        }
    }

    private void hanlenTradayTrades(List<PositionExecution> intradayTrades, String accountCode,String date){

        for (PositionExecution intradayTrade : intradayTrades) {
            
        }
    }



    private List<PositionExecution> listPositionExecution(String accountCode){
        LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionExecution::getStatus, 0);
        queryWrapper.eq(PositionExecution::getAccountCode, accountCode);

        queryWrapper.orderByAsc(PositionExecution::getTime);

        return positionExecutionService.list(queryWrapper);
    }
}
