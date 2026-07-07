//package com.riskcontrol;
//
//import com.riskcontrol.domain.Contract;
//import com.riskcontrol.domain.Position;
//import com.riskcontrol.domain.PositionExecution;
//import com.riskcontrol.enums.PositionExecutionOptTypeEnum;
//import com.riskcontrol.enums.TradeSideEnum;
//import com.riskcontrol.service.*;
//import com.riskcontrol.task.CalPositionExecutionTask;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class CalPositionExecutionTaskCostBasisTest {
//
//    @Mock
//    private IPositionExecutionService positionExecutionService;
//    @Mock
//    private IPositionService positionService;
//    @Mock
//    private IPositionHistoryService positionHistoryService;
//    @Mock
//    private IContractMarketHistoryService contractMarketHistoryService;
//
//    private CalPositionExecutionTask task;
//    private Position position;
//    private Contract contract;
//    private final List<PositionExecution> inLots = new ArrayList<>();
//
//    @BeforeEach
//    void setUp() {
//        inLots.clear();
//        task = new CalPositionExecutionTask();
//        ReflectionTestUtils.setField(task, "positionExecutionService", positionExecutionService);
//        ReflectionTestUtils.setField(task, "positionService", positionService);
//        ReflectionTestUtils.setField(task, "positionHistoryService", positionHistoryService);
//        ReflectionTestUtils.setField(task, "contractMarketHistoryService", contractMarketHistoryService);
//
//        position = new Position();
//        position.setAccountCode("U123");
//        position.setConid(1001);
//
//        contract = new Contract();
//        contract.setConid(1001);
//        contract.setMultiplier("100");
//
//        when(positionService.getPositionByConid("U123", 1001)).thenReturn(position);
//        when(contractMarketHistoryService.getMarketPriceByConidAndDate(1001, "20260701"))
//                .thenReturn(new BigDecimal("17"));
//        when(positionExecutionService.list(any())).thenAnswer(invocation -> new ArrayList<>(inLots));
//        when(positionExecutionService.updateById(any())).thenAnswer(invocation -> {
//            PositionExecution pe = invocation.getArgument(0);
//            inLots.removeIf(l -> l.getId().equals(pe.getId()));
//            if (PositionExecutionOptTypeEnum.IN.name().equals(pe.getOptType())
//                    && pe.getRemainQty() != null
//                    && pe.getRemainQty().compareTo(BigDecimal.ZERO) > 0) {
//                inLots.add(pe);
//            }
//            return true;
//        });
//    }
//
//    @Test
//    void costBasis_sumsRemainingLots() {
//        PositionExecution lot1 = lot(1L, "16.1", "7");
//        PositionExecution lot2 = lot(2L, "15", "5");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTradesOpt",
//                List.of(lot1, lot2), "U123", "20260701", contract);
//
//        assertEquals(new BigDecimal("18770"), position.getCalCostBasis());
//        assertEquals(new BigDecimal("15.6417"), position.getCalAvgCost());
//        assertEquals(new BigDecimal("12"), position.getCalPositionQty());
//    }
//
//    @Test
//    void costBasis_zeroWhenAllClosed() {
//        PositionExecution buy = lot(1L, "16.1", "7");
//        PositionExecution sell = execution(2L, TradeSideEnum.SLD.name(), "17", "7");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTradesOpt",
//                List.of(buy, sell), "U123", "20260701", contract);
//
//        assertEquals(BigDecimal.ZERO, position.getCalCostBasis());
//        assertEquals(BigDecimal.ZERO, position.getCalAvgCost());
//    }
//
//    private PositionExecution lot(Long id, String price, String shares) {
//        return execution(id, TradeSideEnum.BOT.name(), price, shares);
//    }
//
//    private PositionExecution execution(Long id, String side, String price, String shares) {
//        PositionExecution execution = new PositionExecution();
//        execution.setId(id);
//        execution.setAccountCode("U123");
//        execution.setConid(1001);
//        execution.setSymbol("AAPL");
//        execution.setSide(side);
//        execution.setPrice(new BigDecimal(price));
//        execution.setShares(new BigDecimal(shares));
//        execution.setTime("20260701 09:00:00");
//        execution.setExecutionDate("20260701");
//        execution.setStatus(0);
//        return execution;
//    }
//}
