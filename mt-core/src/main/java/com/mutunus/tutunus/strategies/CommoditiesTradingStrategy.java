package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Side;
import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;


/**
 * Implementation of gold and silver trading strategy found at http://www.myforexdot.org.uk/commodities-trading-system.html
 */
public class CommoditiesTradingStrategy extends AbstractStrategy {

    private final static double BR = 0.0001d;
    private final static BigDecimal BROKERAGE = bd(BR);

    class LongShortTradeOpener implements TradeOpener {

        @Override
        public Transaction openTrade(final int tickId, final TimeSeries timeSeries) {
            final Tick tick = timeSeries.getTick(tickId);
            final MTDate date = timeSeries.getDate(tickId);

            final double[] closes120 = copyCloses(timeSeries, tickId, 120);
            final int indexMax120 = getMaxIndex(closes120);
            final int indexMin120 = getMinIndex(closes120);
            final double[] closes10 = copyCloses(timeSeries, tickId, 10);
            final int indexMax10 = getMaxIndex(closes10);
            final int indexMin10 = getMinIndex(closes10);
            final int daysSinceMin120 = 120 - indexMin120;
            final int daysSinceMax120 = 120 - indexMax120;

            Transaction t = null;
            if (indexMin10 == 9 // market closes lower than it has ever closed in the previous 10 days
                    && indexMax120 > indexMin120 // made a new 120 day closing price high more recently than it made a new 120
                    // day closing price low
                    && daysSinceMax120 < 60) { // new 120 day closing price high happened no less than 60 days ago
                t = new Transaction(tickId, Side.LONG, 1, bd(tick.getClosePrice().toDouble()), BROKERAGE, date);
            } else if (indexMax10 == 9// price makes a new 10 day closing price high
                    && indexMin120 > indexMax120// made new 120 day closing price low more recently than it made a new 120 day
                    // closing price high
                    && daysSinceMin120 < 60) {// new 120 day closing price low happened no less than 60 days ago
                t = new Transaction(tickId, Side.SHORT, 1, bd(tick.getClosePrice().toDouble()), BROKERAGE, date);
            }

            return t;
        }

        private int getMaxIndex(final double[] values) {
            double max = Double.MIN_VALUE;
            int maxIndex = 0;

            for (int i = 0; i < values.length; i++) {
                final double v = values[i];
                if (v > max) {
                    max = v;
                    maxIndex = i;
                }
            }

            return maxIndex;
        }

        private int getMinIndex(final double[] values) {
            double min = Double.MAX_VALUE;
            int minIndex = 0;

            for (int i = 0; i < values.length; i++) {
                final double v = values[i];
                if (v < min) {
                    min = v;
                    minIndex = i;
                }
            }

            return minIndex;
        }

        private double[] copyCloses(final TimeSeries qs, final int lastIndexInclusive, final int size) {
            final int first = lastIndexInclusive - size + 1;
            final double[] lows = new double[size];
            for (int i = 0; i < size; i++) {
                final double low = qs.getTick(first + i).getClosePrice().toDouble();
                lows[i] = low;
            }
            return lows;
        }

    }

    public CommoditiesTradingStrategy(TimeSeries timeSeries) {
        super(timeSeries);
//        maxPositionSize = 4;
        setAllowIntraday(false);
    }

    @Override
    protected String getName() {
        return "commodities_trader";
    }

    @Override
    protected TradeOpener[] getOpeners() {
        return new TradeOpener[]{new LongShortTradeOpener()};
    }

    @Override
    protected TradeCloser[] getClosers() {
        return new TradeCloser[]{//
                new TimeoutTradeCloser(40),//
                // new TakeProfitTradeCloser(38),//
//            new StopLossTradeCloser(2.5)//
        };
    }

}
