package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.structures.TradingRegister;
import com.mutunus.tutunus.structures.FlowMetaInfo;
import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.MTException;

import java.math.BigDecimal;
import java.util.*;


public class CashFlowStatisticsProvider extends AbstractStatisticsProvider {

    private static final String H_DATE = "Date";
    private static final String H_PROFIT = "$";
    private static final String H_PROFIT_DAILY = "$daily";
    private static final String H_BROKERAGE = "$brok";
    private static final String H_SIZE_POS = "#pos-size";
    private static final String H_NO_TRADES = "#trades";

    public CashFlowStatisticsProvider() {
    }

    private String[] mergeHeaders(final List<TradingRegister> tradingRegisters) {
        final Set<String> headers = new LinkedHashSet<String>();
        headers.add(H_DATE);
        for (int j = 0; j < tradingRegisters.size(); j++) {
            final TradingRegister f = tradingRegisters.get(j);
            final String flowDescColumn = getFlowColumn(f);
            if (headers.contains(flowDescColumn)) {
                throw new MTException(String.format("Cannot add flow '%s' for the second time.", flowDescColumn));
            }

            headers.add(flowDescColumn);
            headers.add(String.format("%d%s", j + 1, H_PROFIT_DAILY));
            headers.add(String.format("%d%s", j + 1, H_BROKERAGE));
            headers.add(String.format("%d%s", j + 1, H_SIZE_POS));
            headers.add(String.format("%d%s", j + 1, H_NO_TRADES));
        }

        return headers.toArray(new String[]{});
    }

    private String getFlowColumn(final TradingRegister f) {
        final FlowMetaInfo metaInfo = f.getMetaInfo();
        final String description =
                String.format("%s:%s(%d::%d)%s", f.getAsset(), metaInfo.getCreatorInfo(), metaInfo.getFromDate().getYear(),
                        metaInfo.getToDate().getYear(), H_PROFIT);
        return description;
    }

    private NavigableSet<MTDate> mergeAllDates(final List<TradingRegister> tradingRegisters) {
        final TreeSet<MTDate> dates = new TreeSet<MTDate>();
        for (final TradingRegister f : tradingRegisters) {
            final SortedSet<MTDate> ds = f.getAllDates();
            dates.addAll(ds);
        }

        return dates;
    }

    private BigDecimal computeDailyProfit(final TradingRegister tradingRegister, final MTDate date) {
        final MTDate prev = date.clone();
        prev.addDays(-1);

        final BigDecimal day1 = tradingRegister.getTotalNetProfit(date);
        final BigDecimal day0 = tradingRegister.getTotalNetProfit(prev);

        return day1.subtract(day0);
    }

    private int sumPositionSize(final MTDate date, final TradingRegister tradingRegister) {
        final int size = tradingRegister.getOpenedPositionSize(date);

        return size;
    }

    @Override
    public StatisticsModel[] process(final List<TradingRegister> tradingRegisters) {
        final String[] headers = mergeHeaders(tradingRegisters);
        final NavigableSet<MTDate> allDates = mergeAllDates(tradingRegisters);
        final SimpleStatisticsModel m = new SimpleStatisticsModel("Cash TradingRegister", headers, allDates.size());

        int rowOfData = 0;
        for (final MTDate date : allDates) {
            m.set(rowOfData, H_DATE, date.toString());
            for (int j = 0; j < tradingRegisters.size(); j++) {
                final TradingRegister tradingRegister = tradingRegisters.get(j);
                final BigDecimal brokerage = tradingRegister.getTotalBrokerage(date);
                final BigDecimal netProfit = tradingRegister.getTotalNetProfit(date);
                final int noOfTrades = tradingRegister.getNewOrClosedTradesForDate(date).size();
                final int positionSize = sumPositionSize(date, tradingRegister);
                final BigDecimal dailyProfit = computeDailyProfit(tradingRegister, date);

                final String flowDescColumn = getFlowColumn(tradingRegister);
                m.set(rowOfData, flowDescColumn, String.format("%.2f", netProfit.floatValue()));
                m.set(rowOfData, String.format("%d%s", j + 1, H_PROFIT_DAILY),
                        String.format("%.2f", dailyProfit.floatValue()));
                m.set(rowOfData, String.format("%d%s", j + 1, H_BROKERAGE), String.format("%.2f", brokerage.floatValue()));
                m.set(rowOfData, String.format("%d%s", j + 1, H_SIZE_POS), String.format("%d", positionSize));
                m.set(rowOfData, String.format("%d%s", j + 1, H_NO_TRADES), String.format("%d", noOfTrades));
            }
            rowOfData++;
        }

        return new StatisticsModel[]{m};
    }
}
