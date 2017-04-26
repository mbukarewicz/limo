package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.research.indicators.DailyDrawdownIndicator;
import com.mutunus.tutunus.research.indicators.RealizedProfitIndicator;
import com.mutunus.tutunus.research.indicators.TradingValuationIndicator;
import com.mutunus.tutunus.research.indicators.UnrealizedProfitIndicator;
import com.mutunus.tutunus.structures.TradingRegister;
import com.mutunus.tutunus.structures.MTDate;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.helpers.HighestValueIndicator;
import verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;


/**
 */
public class DrawdownCalculator {

    private final NavigableMap<MTDate, BigDecimal> maxProfitUpToDay = new TreeMap<MTDate, BigDecimal>();
    private final NavigableMap<MTDate, BigDecimal> profits = new TreeMap<MTDate, BigDecimal>();

    final DailyDrawdownIndicator dailyDrawdown;
    final HighestValueIndicator maxDrawdown;

    final RealizedProfitIndicator realizedProfitIndicator;
    final UnrealizedProfitIndicator unrealizedProfitIndicator;
    final TradingValuationIndicator valuationIndicator;


    public DrawdownCalculator(TradingRegister tradingRegister, TimeSeries series) {

        realizedProfitIndicator = new RealizedProfitIndicator(series, tradingRegister);
        unrealizedProfitIndicator = new UnrealizedProfitIndicator(series, tradingRegister, new ClosePriceIndicator(series));
        valuationIndicator = new TradingValuationIndicator(
                series,
                tradingRegister,
                realizedProfitIndicator,
                unrealizedProfitIndicator);

        dailyDrawdown = new DailyDrawdownIndicator(series, tradingRegister, valuationIndicator);
        maxDrawdown = new HighestValueIndicator(dailyDrawdown, Integer.MAX_VALUE);
    }

    public DrawdownCalculator(final TradingRegister tradingRegister) {
        dailyDrawdown = null;
        maxDrawdown = null;
        realizedProfitIndicator = null;
        unrealizedProfitIndicator = null;
        valuationIndicator = null;

        final SortedSet<MTDate> dates = tradingRegister.getAllDates();

        BigDecimal max = BigDecimal.ZERO;
        for (final MTDate date : dates) {
            final BigDecimal profit = tradingRegister.getTotalNetProfit(date);
            final BigDecimal brokerage = tradingRegister.getTotalBrokerage(date);

            final BigDecimal current = profit.subtract(brokerage);
            if (max.compareTo(current) < 0) {
                max = current;
            }

            profits.put(date, current);
            maxProfitUpToDay.put(date, max);
        }
    }

    public double getRealizedProfit(int index) {
        final Decimal value = realizedProfitIndicator.getValue(index);
        return value.toDouble();
    }

    public double getUnrealizedProfit(int index) {
        final Decimal value = unrealizedProfitIndicator.getValue(index);
        return value.toDouble();
    }

    public double getMaxDrawdown(int index) {
        final Decimal value = maxDrawdown.getValue(index);
        return value.toDouble();
    }

    public double getDailyDrawdown(int index) {
        final Decimal value = dailyDrawdown.getValue(index);
        return value.toDouble();
    }



    private BigDecimal getValue(final MTDate date, final NavigableMap<MTDate, BigDecimal> values) {
        // final Entry<MTDate, BigDecimal> floorEntry = values.floorEntry(date);
        // if (floorEntry == null) {
        // return null;
        // }
        // return floorEntry.getValue();
        final Entry<MTDate, BigDecimal> floorEntry = values.floorEntry(date);
        if (floorEntry == null) {
            return BigDecimal.ZERO;
        }
        return floorEntry.getValue();
    }

    public double getRelativeDrawdown(final MTDate date) {
        final BigDecimal max = getValue(date, maxProfitUpToDay);
        final BigDecimal current = getValue(date, profits);

        // if (max == null || current == null) {
        // return 0;
        // }
        // if (max.equals(BigDecimal.ZERO)) {
        // return 0; // TODO: jesli max == 1 a current == -1 to rel == 200%
        // }

        BigDecimal drawdown = max.subtract(current);
        drawdown = drawdown.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN);
        if (max.equals(BigDecimal.ZERO)) {
            return drawdown.doubleValue();
        }
        final BigDecimal relative = drawdown.divide(max, RoundingMode.HALF_EVEN);
//        return relative.doubleValue();
        return 0d;
    }

    public double getAbsoluteDrawdown(final MTDate date) {
        final BigDecimal max = getValue(date, maxProfitUpToDay);
        final BigDecimal current = getValue(date, profits);

        // if (max == null || current == null) {
        // return 0;
        // }
        final BigDecimal drawdown = max.subtract(current);
        return drawdown.doubleValue();
    }

    public double getMaxAbsoluteDrawdown() {
        final Set<MTDate> allDates = profits.keySet();

        double currentMax = 0;
        for (final MTDate date : allDates) {
            final double d = getAbsoluteDrawdown(date);
            currentMax = Math.max(currentMax, d);
        }

        return currentMax;
    }

    public double getMaxRelativeDrawdown() {
        final Set<MTDate> allDates = profits.keySet();

        double currentMax = 0;
        for (final MTDate date : allDates) {
            final double d = getRelativeDrawdown(date);
            currentMax = Math.max(currentMax, d);
        }

        return currentMax;
    }

}
