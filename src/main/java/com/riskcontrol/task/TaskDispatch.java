package com.riskcontrol.task;

import com.riskcontrol.domain.ContractMarketHistory;
import com.riskcontrol.domain.TaskJobLog;
import com.riskcontrol.domain.vo.PositionMarketPriceVo;
import com.riskcontrol.service.IContractMarketHistoryService;
import com.riskcontrol.service.ITaskJobLogService;
import com.riskcontrol.util.DateUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TaskDispatch {

    @Resource
    IbReconnectTask ibReconnectTask;

    @Resource
    CalPositionExecutionTask calPositionExecutionTask;

    @Resource
    IContractMarketHistoryService contractMarketHistoryService;

    @Resource
    ITaskJobLogService taskJobLogService;

    public static final String EXCEPTION_MSG = "异常";

    @Scheduled(cron="0 0 8 * * ?")
    public void execute(){

        String uuid = UUID.randomUUID().toString();

        boolean flag = true;

        try {
            log.info("同步交易日start");
            ibReconnectTask.synTradeDate();
            saveTaskLog("同步交易日", "成功", "");
            log.info("同步交易日end");
        } catch (Exception e) {
            flag = false;
            saveTaskLog("同步交易日", e.getMessage(), "");
            log.error("同步交易日异常", e);
        }

        if (!flag) {
            return;
        }

        // 同步账号
        try {
            log.info("{} 同步账号start", uuid);
            ibReconnectTask.synAccountCurrency();
            saveTaskLog("同步账号", "成功", uuid);
            log.info("{} 同步账号end", uuid);
        } catch (Exception e) {
            log.error("{} 同步账号异常", uuid, e);
            flag = false;
            saveTaskLog("同步账号", EXCEPTION_MSG, uuid);
        }

        if (!flag) {
            return;
        }



        // 同步持仓数据
        try {
            log.info("{} 同步持仓start", uuid);
            ibReconnectTask.synAccount();
            saveTaskLog("同步持仓", "成功", uuid);
            log.info("{} 同步持仓end", uuid);
        } catch (Exception e) {
            flag = false;
            saveTaskLog("同步持仓", EXCEPTION_MSG, uuid);
            log.error("{} 同步持仓异常", uuid, e);
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
            log.error("{} 同步市场异常", uuid, e);
            flag = false;
            saveTaskLog("同步市场", EXCEPTION_MSG, uuid);
        }

        if (!flag) {
            return;
        }

        // 同步交易数据
        try {
            log.info("{} 同步交易start", uuid);
            // TODO
            ibReconnectTask.synExecutions();

            ibReconnectTask.synContractDetails();
            saveTaskLog("同步交易", "成功", uuid);
            log.info("{} 同步交易end", uuid);
        } catch (Exception e) {
            flag = false;
            log.error("{} 同步交易异常", uuid, e);
            saveTaskLog("同步交易", EXCEPTION_MSG, uuid);
        }
        if (!flag) {
            return;
        }

        try {
            log.info("{} 核算start", uuid);
            // 开启核算任务
            calPositionExecutionTask.cal();
            saveTaskLog("核算", "成功", uuid);
            log.info("{} 核算end", uuid);
        } catch (Exception e) {
            log.error("{}  核算异常", uuid, e);
            saveTaskLog("核算", EXCEPTION_MSG, uuid);
        }
    }

    @Scheduled(cron="0 0 7 * * ?")
    public void synTradeDate() {
        try {
            log.info("同步交易日start");
            ibReconnectTask.synTradeDate();
            saveTaskLog("同步交易日", "成功", "");
            log.info("同步交易日end");
        } catch (Exception e) {

            saveTaskLog("同步交易日", e.getMessage(), "");
            log.error("同步交易日异常", e);
        }
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
