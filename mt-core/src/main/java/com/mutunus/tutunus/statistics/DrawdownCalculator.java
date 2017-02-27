package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.structures.TradingRegister;
import com.mutunus.tutunus.structures.MTDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;


/**
 * Computes Drawdown according to the formula from {@link {http://comisef.wikidot.com/tutorial:drawdowns}}
 */
public class DrawdownCalculator {

    private final NavigableMap<MTDate, BigDecimal> maxProfitUpToDay = new TreeMap<MTDate, BigDecimal>();
    private final NavigableMap<MTDate, BigDecimal> profits = new TreeMap<MTDate, BigDecimal>();

    public DrawdownCalculator(final TradingRegister tradingRegister) {
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
