package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.structures.*;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class ResultsOverviewProvider extends AbstractStatisticsProvider {

    private static final String H_ID = "#";
    private static final String H_ASSET = "asset";
    private static final String H_DATE = "date";
    private static final String H_TOTAL_PROFIT = "total_profit%";
    private static final String H_REALIZED_PROFIT = "realized_profit%";
    private static final String H_UNREALIZED_PROFIT = "unrealized_profit%";
    private static final String H_COUNT = "count";
    private static final String H_DAILY_DRAWDOWN = "daily_drawdown";
    private static final String H_MAX_DRAWDOWN = "max_drawdown";
    private static final String H_CLOSE = "_close";
    private static final String H_HIGH = "_high";
    private static final String H_LOW = "_low";

    private static final String[] HEADERS = {
            H_ID,
            H_ASSET,
            H_DATE,
            H_TOTAL_PROFIT,
            H_REALIZED_PROFIT,
            H_UNREALIZED_PROFIT,
            H_COUNT,
            H_HIGH,
            H_LOW,
            H_CLOSE,
            H_DAILY_DRAWDOWN,
            H_MAX_DRAWDOWN};

//            total_profit
//                    total_realized
//            total_unrealized
//    #longs/shorts


    private final TimeSeries timeSeries;
    private final int begin;
    private final int end;


    public ResultsOverviewProvider(final TimeSeries timeSeries, int begin, int end) {
        this.timeSeries = timeSeries;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public StatisticsModel[] process(final List<TradingRegister> tradingRegisters) {
        final List<SimpleStatisticsModel> models = new ArrayList<SimpleStatisticsModel>();

        for (final TradingRegister tradingRegister : tradingRegisters) {
            final SimpleStatisticsModel m = processFlow(tradingRegister);
            models.add(m);
        }

        return models.toArray(new SimpleStatisticsModel[]{});
    }

    private SimpleStatisticsModel processFlow(final TradingRegister tradingRegister) {
        final MTDate fromDate = tradingRegister.getMetaInfo().getFromDate();
        final MTDate toDate = tradingRegister.getMetaInfo().getToDate();
        final String asset = tradingRegister.getAsset();

//        final Range<Integer> idsRange = qs.getIds(fromDate, toDate);
//        final int startDateId = idsRange.getMinimum();
//        final int endDateId = idsRange.getMaximum();
        final int startDateId = begin;
        final int endDateId = end;

        final String description = getDescription(tradingRegister);
        final SimpleStatisticsModel m = new SimpleStatisticsModel(description, HEADERS, endDateId - startDateId + 1);


        final DrawdownCalculator drawdown = new DrawdownCalculator(tradingRegister, timeSeries);

        int row = 0;
        for (int i = startDateId; i <= endDateId; i++, row++) {
            final MTDate date = timeSeries.getDate(i);

            final Tick tick1 = timeSeries.getTick(i);
            final List<Trade> tradesForDay = tradingRegister.getTrades2(date);
            final long countLongs = tradesForDay.stream().filter(t -> t.getOpenSide().isLong()).count();
            final long countShorts = tradesForDay.stream().filter(t -> t.getOpenSide().isShort()).count();
            String count = "0";
            if (countLongs > 0 && countShorts > 0) {
              count = countLongs + " / " + countShorts;
            } else if (countLongs > 0) {
                count = Long.toString(countLongs);
            } else {
                count = Long.toString(countShorts);
            }


            m.set(row, H_ID, String.format("%d", row + 1));
            m.set(row, H_ASSET, asset);
            m.set(row, H_DATE, date.toString());

            final BigDecimal totalProfitPercent = tradingRegister.getRealizedProfit(date);
            final double realizedProfit = drawdown.getRealizedProfit(i);
            final double unrealizedProfit = drawdown.getUnrealizedProfit(i);

            m.set(row, H_TOTAL_PROFIT, String.format("%.2f%%", totalProfitPercent.floatValue()));
            m.set(row, H_REALIZED_PROFIT, String.format("%.2f%%", realizedProfit));
            m.set(row, H_UNREALIZED_PROFIT, String.format("%.2f%%", unrealizedProfit));

            m.set(row, H_COUNT, count);
            m.set(row, H_CLOSE, String.format("%.4f", tick1.getClosePrice().toDouble()));
            m.set(row, H_HIGH, String.format("%.4f", tick1.getMaxPrice().toDouble()));
            m.set(row, H_LOW, String.format("%.4f", tick1.getLowPrice().toDouble()));

            final double dailyDrawdownDouble = drawdown.getDailyDrawdown(i);
            final double maxDrawdownDouble = drawdown.getMaxDrawdown(i);
            m.set(row, H_DAILY_DRAWDOWN, String.format("%.2f%%", dailyDrawdownDouble));
            m.set(row, H_MAX_DRAWDOWN, String.format("%.2f%%", maxDrawdownDouble));
        }

        return m;
    }

    private String getDescription(final TradingRegister f) {
        final FlowMetaInfo metaInfo = f.getMetaInfo();
        final String description =
                String.format("results_overview_%s_%s(%d__%d)", f.getAsset(), metaInfo.getCreatorInfo(), metaInfo.getFromDate()
                        .getYear(), metaInfo.getToDate().getYear());
        return description;
    }

}
