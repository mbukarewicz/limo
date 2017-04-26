package com.mutunus.tutunus.structures;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;


public interface TradingRegister {

    String getAsset();

    FlowMetaInfo getMetaInfo();

    SortedSet<MTDate> getAllDates();

    List<Trade> getNewOrClosedTradesForDate(MTDate date);
    List<Trade> getTrades2(final MTDate date);
    List<Trade> getNonTerminatingTrades(final MTDate date);

    int getOpenedPositionSize(MTDate date);

    MTDate getLastDate();

    MTDate getFirstDate();

    BigDecimal getTotalNetProfit(MTDate date);

    BigDecimal getTotalBrokerage(MTDate date);

    List<Trade> getAllTrades();

    BigDecimal getRealizedProfit(MTDate date);
}
