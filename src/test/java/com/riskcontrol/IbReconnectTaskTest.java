package com.riskcontrol;

import com.riskcontrol.task.IbReconnectTask;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@SpringBootTest
public class IbReconnectTaskTest {

    @Resource
    IbReconnectTask ibReconnectTask;

    @Test
    public void accountCurrency() throws ExecutionException, InterruptedException, TimeoutException {
        ibReconnectTask.synAccountCurrency();
    }

    @Test
    public void synAccount() throws ExecutionException, InterruptedException, TimeoutException {
        ibReconnectTask.synAccount();
    }

    @Test
    public void synPnl() throws ExecutionException, InterruptedException, TimeoutException {
        ibReconnectTask.synPnl("DUQ346350","");
    }

    @Test
    public void synSinglePnl() throws ExecutionException, InterruptedException, TimeoutException {
        ibReconnectTask.synSinglePnl("DUQ346350","", 265598);
    }

    @Test
    public void synContractHistory() throws ExecutionException, InterruptedException, TimeoutException {
        ibReconnectTask.synContractHistory();
    }

}
