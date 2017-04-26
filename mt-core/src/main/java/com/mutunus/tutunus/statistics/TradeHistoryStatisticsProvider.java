package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.research.indicators.DailyDrawdownIndicator;
import com.mutunus.tutunus.structures.TradingRegister;
import com.mutunus.tutunus.structures.FlowMetaInfo;
import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Trade;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.helpers.HighestValueIndicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TradeHistoryStatisticsProvider extends AbstractStatisticsProvider {

    private static final String H_ID = "#";
    private static final String H_SIDE = "open_side";
    private static final String H_SIZE = "position_size";
    private static final String H_ASSET = "asset";
    private static final String H_DATE_FROM = "date_from";
    private static final String H_DATE_TO = "date_to";
    private static final String H_PROFIT = "profit";
    private static final String H_PROFIT_PERCENT = "profit%";
    private static final String H_TOTAL_PROFIT_PERCENT = "total_profit%";
    private static final String H_TOTAL_PROFIT = "total_profit";
    private static final String H_DRAWDOWN_ABSOLUTE = "drawdown_abs";
    private static final String H_DRAWDOWN_RELATIVE = "drawdown_rel";
    private static final String H_PRICE_OPEN = "price_open";
    private static final String H_PRICE_CLOSE = "price_close";
    private static final String H_CLOSE_REASON = "why_close";

    private static final String[] HEADERS = {H_ID, H_ASSET, H_SIDE, H_SIZE, H_DATE_FROM, H_DATE_TO, H_PRICE_OPEN,
            H_PRICE_CLOSE, H_PROFIT, H_PROFIT_PERCENT, H_TOTAL_PROFIT_PERCENT, H_TOTAL_PROFIT, H_DRAWDOWN_ABSOLUTE, H_DRAWDOWN_RELATIVE, H_CLOSE_REASON};
    private final TimeSeries series;

    public TradeHistoryStatisticsProvider(TimeSeries series) {
        this.series = series;
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
        final List<Trade> trades = tradingRegister.getAllTrades();
        Collections.sort(trades, Comparator.comparing(Trade::getCloseDate));

        final String description = getDescription(tradingRegister);
        final SimpleStatisticsModel m = new SimpleStatisticsModel(description, HEADERS, trades.size() + 1);

        final DailyDrawdownIndicator dailyDrawdown = new DailyDrawdownIndicator(series, tradingRegister);
        final HighestValueIndicator maxDrawdown = new HighestValueIndicator(dailyDrawdown, Integer.MAX_VALUE);
        final DrawdownCalculator drawdown = new DrawdownCalculator(tradingRegister);

        BigDecimal profitPercentSum = new BigDecimal(0);
        for (int row = 0; row < trades.size(); row++) {
            final Trade t = trades.get(row);

            m.set(row, H_ID, String.format("%d", row + 1));
            m.set(row, H_ASSET, tradingRegister.getAsset());
            m.set(row, H_SIDE, t.getOpenSide().toString());
            m.set(row, H_SIZE, Integer.toString(tradingRegister.getOpenedPositionSize(t.getOpenDate())));
            m.set(row, H_DATE_FROM, t.getOpenDate().toString());
            m.set(row, H_DATE_TO, t.getCloseDate().toString());
            m.set(row, H_PRICE_OPEN, String.format("%.4f", t.getOpenPrice().floatValue()));
            m.set(row, H_PRICE_CLOSE, String.format("%.4f", t.getClosePrice().floatValue()));
            m.set(row, H_PROFIT, String.format("%.4f", t.getProfit().floatValue()));
            final BigDecimal profitPercent = computeProfitPercent(t);
            profitPercentSum = profitPercentSum.add(profitPercent);
            m.set(row, H_PROFIT_PERCENT, String.format("%.2f%%", profitPercent.floatValue()));
            m.set(row, H_TOTAL_PROFIT_PERCENT, String.format("%.2f%%", profitPercentSum.floatValue()));

            final int tickId = series.getTickId(t.getCloseDate());
            final BigDecimal totalNetProfit = tradingRegister.getTotalNetProfit(t.getCloseDate());
            m.set(row, H_TOTAL_PROFIT, String.format("%.4f", totalNetProfit.floatValue()));

            final double currentDrawdown = drawdown.getAbsoluteDrawdown(t.getCloseDate());
            final double relativeDrawdown = dailyDrawdown.getValue(tickId).toDouble();
            m.set(row, H_DRAWDOWN_ABSOLUTE, String.format("%.1f", currentDrawdown));
            m.set(row, H_DRAWDOWN_RELATIVE, String.format("%.1f%%", relativeDrawdown));

            m.set(row, H_CLOSE_REASON, t.getCloseComment());
        }

        final MTDate lastDate = tradingRegister.getLastDate();
        final MTDate firstDate = tradingRegister.getFirstDate();
        final int lastRow = trades.size();
        m.set(lastRow, H_ID, String.format("%d", lastRow + 1));
        m.set(lastRow, H_ASSET, tradingRegister.getAsset());
        m.set(lastRow, H_SIDE, "---");
        m.set(lastRow, H_SIZE, Integer.toString(tradingRegister.getOpenedPositionSize(lastDate)));
        m.set(lastRow, H_DATE_FROM, firstDate != null ? firstDate.toString() : "---");
        m.set(lastRow, H_DATE_TO, lastDate != null ? lastDate.toString() : "---");
        m.set(lastRow, H_PRICE_OPEN, "---");
        m.set(lastRow, H_PRICE_CLOSE, "---");
        m.set(lastRow, H_PROFIT, "---");
        m.set(lastRow, H_PROFIT_PERCENT, "---");
        m.set(lastRow, H_DRAWDOWN_ABSOLUTE, String.format("%.1f", drawdown.getMaxAbsoluteDrawdown()));

        final double maxDrawdownValue = maxDrawdown.getValue(series.getEnd()).toDouble();
        m.set(lastRow, H_DRAWDOWN_RELATIVE, String.format("%.1f%%", maxDrawdownValue));
//        m.set(lastRow, H_DRAWDOWN_RELATIVE, String.format("%.2f%%", drawdown.getMaxRelativeDrawdown()));

        final BigDecimal totalNetProfit = tradingRegister.getTotalNetProfit(lastDate);
        m.set(lastRow, H_TOTAL_PROFIT_PERCENT, String.format("%.4f%%", profitPercentSum.floatValue()));
        m.set(lastRow, H_TOTAL_PROFIT, String.format("%.4f", totalNetProfit.floatValue()));
        m.set(lastRow, H_CLOSE_REASON, "Summary");

        return m;
    }

    private BigDecimal computeProfitPercent(final Trade t) {
//        final float openPrice = t.getOpenPrice().floatValue();
//        final float profit = t.getProfit().floatValue();

        final BigDecimal result = BigDecimal.valueOf(100).multiply(t.getProfit()).divide(t.getOpenPrice(), BigDecimal.ROUND_HALF_UP);
//        final float result = 100f * profit / openPrice;

        return result;
    }

    private String getDescription(final TradingRegister f) {
        final FlowMetaInfo metaInfo = f.getMetaInfo();
        final String description =
                String.format("trades_history_%s_%s(%d__%d)", f.getAsset(), metaInfo.getCreatorInfo(), metaInfo.getFromDate()
                        .getYear(), metaInfo.getToDate().getYear());
        return description;
    }

}
