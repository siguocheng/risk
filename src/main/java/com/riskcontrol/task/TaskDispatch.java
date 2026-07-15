package com.riskcontrol.task;

import com.riskcontrol.domain.ContractMarketHistory;
import com.riskcontrol.domain.TaskJobLog;
import com.riskcontrol.domain.vo.PositionMarketPriceVo;
import com.riskcontrol.service.IContractMarketHistoryService;
import com.riskcontrol.service.ITaskJobLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
public class TaskDispatch {

    @Resource
    IbReconnectTask ibReconnectTask;

    @Resource
    CalPositionExecutionTask calPositionExecutionTask;

    @Resource
    IContractMarketHistoryService contractMarketHistoryService;

    @Resource
    ITaskJobLogService taskJobLogService;


    public void execute(){

        String uuid = UUID.randomUUID().toString();

        boolean flag = true;

        // 同步账号
        try {
            log.info("{} 同步账号start", uuid);
            ibReconnectTask.synAccountCurrency();
            saveTaskLog("同步账号", "成功", uuid);
            log.info("{} 同步账号end", uuid);
        } catch (Exception e) {
            log.error("{} 同步账号异常", e);
            flag = false;
            saveTaskLog("同步账号", e.getMessage(), uuid);
        }

        if (!flag) {
            return;
        }

        // 同步市场数据
        try {
            log.info("{} 同步市场start", uuid);
            ibReconnectTask.synContractMarket();
            saveTaskLog("同步市场", "成功", uuid);
            log.info("{} 同步市场end", uuid);
        } catch (Exception e) {
            log.error("{} 同步市场异常", e);
            saveTaskLog("同步市场", e.getMessage(), uuid);
        }

        if (!flag) {
            return;
        }

        // 同步持仓数据
        try {
            log.info("{} 同步持仓start", uuid);
            List<PositionMarketPriceVo>  marketPriceList = ibReconnectTask.synAccount();

            for (PositionMarketPriceVo positionMarketPriceVo : marketPriceList) {
                ContractMarketHistory contractMarketHistory = new ContractMarketHistory();
                contractMarketHistory.setConid(positionMarketPriceVo.getConid());
                contractMarketHistory.setSymbol(positionMarketPriceVo.getSymbol());
                contractMarketHistory.setPositionMarketPrice(positionMarketPriceVo.getMarketPrice());
                contractMarketHistoryService.saveOrUpdateContractMarket(contractMarketHistory);
            }
            saveTaskLog("同步持仓", "成功", uuid);
            log.info("{} 同步持仓end", uuid);
        } catch (Exception e) {
            saveTaskLog("同步持仓", e.getMessage(), uuid);
            log.error("{} 同步持仓异常", e);
        }

        if (!flag) {
            return;
        }

        // 同步交易数据
        try {
            log.info("{} 同步交易start", uuid);
            // TODO
            ibReconnectTask.synExecutions();
            saveTaskLog("同步交易", "成功", uuid);
            log.info("{} 同步交易end", uuid);
        } catch (Exception e) {
            log.error("{} 同步交易异常", e);
            saveTaskLog("同步交易", e.getMessage(), uuid);
        }
        if (!flag) {
            return;
        }

        try {
            log.info("{} 核算start", uuid);
            // 开启核算任务
            calPositionExecutionTask.cal();
            log.info("{} 核算end", uuid);
        } catch (Exception e) {
            log.error("{}  核算异常", e);
            saveTaskLog("核算", e.getMessage(), uuid);
        }
    }


    public void firstSyn(){

    }

    private void saveTaskLog(String jobName, String result, String uuid){
        TaskJobLog taskJobLog = new TaskJobLog();
        taskJobLog.setJobName(jobName);
        taskJobLog.setExecuteResult(result);
        taskJobLog.setExecuteTime(LocalDateTime.now());
        taskJobLog.setExecuteId(uuid);
        taskJobLogService.save(taskJobLog);
    }
}
