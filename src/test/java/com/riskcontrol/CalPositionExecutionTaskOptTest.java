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
//class CalPositionExecutionTaskOptTest {
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
//        position.setId(1L);
//        position.setAccountCode("U123");
//        position.setConid(1001);
//        position.setCalPositionQty(BigDecimal.ZERO);
//        position.setCalAvgCost(BigDecimal.ZERO);
//        position.setCalRealizedPnl(BigDecimal.ZERO);
//        position.setCalUnrealizedPnl(BigDecimal.ZERO);
//
//        contract = new Contract();
//        contract.setConid(1001);
//        contract.setMultiplier("100");
//
//        when(positionService.getPositionByConid("U123", 1001)).thenReturn(position);
//        when(contractMarketHistoryService.getMarketPriceByConidAndDate(1001, "20260701"))
//                .thenReturn(new BigDecimal("2.5"));
//        when(contractMarketHistoryService.getMarketPriceByConidAndDate(1001, "2026-07-01"))
//                .thenReturn(new BigDecimal("2.5"));
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
//        PositionExecution buy = execution(1L, TradeSideEnum.BOT.name(), "2.0", "2", "20260701 09:00:00");
//        PositionExecution sell = execution(2L, TradeSideEnum.SLD.name(), "3.0", "2", "20260701 10:00:00");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTradesOpt",
//                List.of(buy, sell), "U123", "20260701", contract);
//
//        assertEquals(new BigDecimal("200"), sell.getCalExecutionRealizedPnl());
//        assertEquals(new BigDecimal("200"), position.getCalDailyRealizedPnl());
//        assertEquals(BigDecimal.ZERO, position.getCalDailyUnrealizedPnl());
//        assertEquals(BigDecimal.ZERO, position.getCalPositionQty());
//    }
//
//    @Test
//    void newLotHeldToday_dailyUnrealizedFromBuyPrice() {
//        PositionExecution buy = execution(1L, TradeSideEnum.BOT.name(), "2.0", "2", "20260701 09:00:00");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTradesOpt",
//                List.of(buy), "U123", "20260701", contract);
//
//        assertEquals(new BigDecimal("100"), position.getCalDailyUnrealizedPnl());
//        assertEquals(new BigDecimal("100"), position.getCalUnrealizedPnl());
//    }
//
//    @Test
//    void shortOpenAndClose_realizedPnl() {
//        PositionExecution sellOpen = execution(1L, TradeSideEnum.SLD.name(), "1.5", "1", "20260701 09:00:00");
//        PositionExecution buyClose = execution(2L, TradeSideEnum.BOT.name(), "0.5", "1", "20260701 10:00:00");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTradesOpt",
//                List.of(sellOpen, buyClose), "U123", "20260701", contract);
//
//        assertEquals(new BigDecimal("100"), buyClose.getCalExecutionRealizedPnl());
//        assertEquals(new BigDecimal("100"), position.getCalDailyRealizedPnl());
//        assertEquals(BigDecimal.ZERO, position.getCalPositionQty());
//    }
//
//    @Test
//    void longExpireAtZero_realizedLoss() {
//        PositionExecution buy = execution(1L, TradeSideEnum.BOT.name(), "2.0", "1", "20260701 09:00:00");
//        PositionExecution expire = execution(2L, TradeSideEnum.SLD.name(), "0", "1", "20260701 16:00:00");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTradesOpt",
//                List.of(buy, expire), "U123", "20260701", contract);
//
//        assertEquals(new BigDecimal("-200"), expire.getCalExecutionRealizedPnl());
//        assertEquals(BigDecimal.ZERO, position.getCalPositionQty());
//    }
//
//    @Test
//    void shortExpireAtZero_realizedGain() {
//        PositionExecution sellOpen = execution(1L, TradeSideEnum.SLD.name(), "1.0", "1", "20260701 09:00:00");
//        PositionExecution expire = execution(2L, TradeSideEnum.BOT.name(), "0", "1", "20260701 16:00:00");
//
//        ReflectionTestUtils.invokeMethod(task, "handleNoDayTradesOpt",
//                List.of(sellOpen, expire), "U123", "20260701", contract);
//
//        assertEquals(new BigDecimal("100"), expire.getCalExecutionRealizedPnl());
//        assertEquals(BigDecimal.ZERO, position.getCalPositionQty());
//    }
//
//    @Test
//    void resolveOptType_matchesPositionSideRules() {
//        position.setCalPositionQty(new BigDecimal("2"));
//        assertEquals(PositionExecutionOptTypeEnum.IN.name(),
//                ReflectionTestUtils.invokeMethod(task, "resolveOptType", position, execution(1L, TradeSideEnum.BOT.name(), "1", "1", "t1")));
//        assertEquals(PositionExecutionOptTypeEnum.OUT.name(),
//                ReflectionTestUtils.invokeMethod(task, "resolveOptType", position, execution(2L, TradeSideEnum.SLD.name(), "1", "1", "t2")));
//
//        position.setCalPositionQty(new BigDecimal("-1"));
//        assertEquals(PositionExecutionOptTypeEnum.OUT.name(),
//                ReflectionTestUtils.invokeMethod(task, "resolveOptType", position, execution(3L, TradeSideEnum.BOT.name(), "1", "1", "t3")));
//        assertEquals(PositionExecutionOptTypeEnum.IN.name(),
//                ReflectionTestUtils.invokeMethod(task, "resolveOptType", position, execution(4L, TradeSideEnum.SLD.name(), "1", "1", "t4")));
//    }
//
//    private PositionExecution execution(Long id, String side, String price, String shares, String time) {
//        PositionExecution execution = new PositionExecution();
//        execution.setId(id);
//        execution.setAccountCode("U123");
//        execution.setConid(1001);
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
