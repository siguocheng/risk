//package com.riskcontrol;
//
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
//class CalPositionExecutionTaskStkTest {
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
//        position.setId(1L);
//        position.setAccountCode("U123");
//        position.setConid(2001);
//        position.setCalPositionQty(BigDecimal.ZERO);
//        position.setCalAvgCost(BigDecimal.ZERO);
//        position.setCalRealizedPnl(BigDecimal.ZERO);
//        position.setCalUnrealizedPnl(BigDecimal.ZERO);
//
//        when(positionService.getPositionByConid("U123", 2001)).thenReturn(position);
//        when(contractMarketHistoryService.getMarketPriceByConidAndDate(2001, "20260701"))
//                .thenReturn(new BigDecimal("12"));
//        when(contractMarketHistoryService.getMarketPriceByConidAndDate(2001, "2026-07-01"))
//                .thenReturn(new BigDecimal("12"));
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
//    void longOpenAndClose_realizedPnl() {
//        PositionExecution buy = execution(1L, TradeSideEnum.BOT.name(), "10", "100", "20260701 09:00:00");
//        PositionExecution sell = execution(2L, TradeSideEnum.SLD.name(), "15", "100", "20260701 10:00:00");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTrades",
//                List.of(buy, sell), "U123", "20260701", 2001);
//
//        assertEquals(new BigDecimal("500"), sell.getCalExecutionRealizedPnl());
//        assertEquals(new BigDecimal("500"), position.getCalDailyRealizedPnl());
//        assertEquals(BigDecimal.ZERO, position.getCalDailyUnrealizedPnl());
//        assertEquals(BigDecimal.ZERO, position.getCalPositionQty());
//    }
//
//    @Test
//    void shortOpenAndClose_realizedPnl() {
//        PositionExecution sellOpen = execution(1L, TradeSideEnum.SLD.name(), "20", "50", "20260701 09:00:00");
//        PositionExecution buyClose = execution(2L, TradeSideEnum.BOT.name(), "12", "50", "20260701 10:00:00");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTrades",
//                List.of(sellOpen, buyClose), "U123", "20260701", 2001);
//
//        assertEquals(new BigDecimal("400"), buyClose.getCalExecutionRealizedPnl());
//        assertEquals(BigDecimal.ZERO, position.getCalPositionQty());
//    }
//
//    @Test
//    void longAddMore_weightedAvgCost() {
//        PositionExecution buy1 = execution(1L, TradeSideEnum.BOT.name(), "10", "100", "20260701 09:00:00");
//        PositionExecution buy2 = execution(2L, TradeSideEnum.BOT.name(), "16", "100", "20260701 11:00:00");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTrades",
//                List.of(buy1, buy2), "U123", "20260701", 2001);
//
//        assertEquals(new BigDecimal("13.0000"), position.getCalAvgCost());
//        assertEquals(new BigDecimal("200"), position.getCalPositionQty());
//        assertEquals(new BigDecimal("-200"), position.getCalUnrealizedPnl());
//        assertEquals(new BigDecimal("-200"), position.getCalDailyUnrealizedPnl());
//    }
//
//    @Test
//    void carryOverLot_dailyUnrealizedUsesYesterdayClose() {
//        PositionExecution carryLot = execution(1L, TradeSideEnum.BOT.name(), "10", "100", "20260630 09:00:00");
//        carryLot.setDate("20260630");
//        carryLot.setOptType(PositionExecutionOptTypeEnum.IN.name());
//        carryLot.setRemainQty(new BigDecimal("100"));
//        inLots.add(carryLot);
//        position.setCalPositionQty(new BigDecimal("100"));
//
//        when(contractMarketHistoryService.getMarketPriceByConidAndDate(2001, "20260630"))
//                .thenReturn(new BigDecimal("11"));
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTrades",
//                List.of(), "U123", "20260701", 2001);
//
//        assertEquals(new BigDecimal("100"), position.getCalDailyUnrealizedPnl());
//        assertEquals(new BigDecimal("200"), position.getCalUnrealizedPnl());
//    }
//
//    private PositionExecution execution(Long id, String side, String price, String shares, String time) {
//        PositionExecution execution = new PositionExecution();
//        execution.setId(id);
//        execution.setAccountCode("U123");
//        execution.setConid(2001);
//        execution.setSymbol("AAPL");
//        execution.setSide(side);
//        execution.setPrice(new BigDecimal(price));
//        execution.setShares(new BigDecimal(shares));
//        execution.setTime(time);
//        execution.setDate("20260701");
//        execution.setStatus(0);
//        return execution;
//    }
//}
