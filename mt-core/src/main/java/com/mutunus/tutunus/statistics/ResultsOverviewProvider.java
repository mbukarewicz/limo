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
    public static final String H_DATE = "date";
    public static final String H_TOTAL_PROFIT = "total_profit";
    private static final String H_TOTAL_PROFIT_PERCENT = "total_profit%";
    private static final String H_DRAWDOWN_ABSOLUTE = "drawdown_abs";
    private static final String H_DRAWDOWN_RELATIVE = "drawdown_rel";
    public static final String H_CLOSE = "_close";

    private static final String[] HEADERS = {H_ID, H_ASSET, H_DATE, H_TOTAL_PROFIT_PERCENT, H_TOTAL_PROFIT, H_CLOSE, H_DRAWDOWN_ABSOLUTE,
            H_DRAWDOWN_RELATIVE};

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

        final DrawdownCalculator drawdown = new DrawdownCalculator(tradingRegister);

        int row = 0;
        for (int i = startDateId; i <= endDateId; i++, row++) {
            final MTDate date = timeSeries.getDate(i);

            final Tick tick1 = timeSeries.getTick(i);

            m.set(row, H_ID, String.format("%d", row + 1));
            m.set(row, H_ASSET, asset);
            m.set(row, H_DATE, date.toString());

            final BigDecimal totalNetProfit = tradingRegister.getTotalNetProfit(date);
            m.set(row, H_TOTAL_PROFIT, String.format("%.4f", totalNetProfit.floatValue()));
            final BigDecimal totalProfitPercent = tradingRegister.getTotalProfitPercent(date);
            m.set(row, H_TOTAL_PROFIT_PERCENT, String.format("%.2f%%", totalProfitPercent.floatValue()));

            m.set(row, "_close", String.format("%.4f", tick1.getClosePrice().toDouble()));

            final double currentDrawdown = drawdown.getAbsoluteDrawdown(date);
            final double relativeDrawdown = drawdown.getRelativeDrawdown(date);
            m.set(row, H_DRAWDOWN_ABSOLUTE, String.format("%.2f", currentDrawdown));
            m.set(row, H_DRAWDOWN_RELATIVE, String.format("%.1f%%", relativeDrawdown));
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
