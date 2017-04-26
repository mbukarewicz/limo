package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.Side;
import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;


/**

 */
public class ThreeDownDaysAndGapUpStrategy extends AbstractStrategy {
    private final static double BR = 0.0000d;
    private final static BigDecimal BROKERAGE = bd(BR);

    private static class LongShortTradeOpenerTradeCloser implements TradeOpener, TradeCloser {

        @Override
        public Transaction openTrade(final int tickId, final TimeSeries timeSeries) {

            final Tick tick = timeSeries.getTick(tickId);
            final Tick tick1 = timeSeries.getTick(tickId - 1);
            final Tick tick2 = timeSeries.getTick(tickId - 2);

            if (tick.getOpenPrice().isLessThan(tick1.getClosePrice())
                    && tick1.getClosePrice().isGreaterThan(tick2.getMaxPrice())) {
                final Transaction t = new Transaction(
                        tickId, Side.SHORT, 1, tick.getOpenPrice().asBigDecimal(), BROKERAGE, timeSeries.getDate(tickId));
                return t;
            }
            return null;
        }

        @Override
        public Transaction closeTrade(int tickId, TimeSeries timeSeries, Transaction openedTransaction) {
            final Tick tick = timeSeries.getTick(tickId);

            final Transaction t = new Transaction(
                    tickId, openedTransaction.getSide().revert(), 1, tick.getClosePrice().asBigDecimal(), BROKERAGE, timeSeries.getDate(tickId));
            return t;
        }
    }

    public ThreeDownDaysAndGapUpStrategy(TimeSeries timeSeries) {
        super(timeSeries);
        setAllowIntraday(true);
        maxPositionSize = 1;
    }

    @Override
    protected String getName() {
        return "ThreeDownDaysAndGapUp";
    }

    @Override
    protected TradeOpener[] getOpeners() {
        return new TradeOpener[]{new LongShortTradeOpenerTradeCloser()};
    }

    @Override
    protected TradeCloser[] getClosers() {
        return new TradeCloser[]{//
                new LongShortTradeOpenerTradeCloser()
//                new TimeoutTradeCloser(80),//
//                new TakeProfitTradeCloser(8),//
//                new StopLossTradeCloser(10)//
        };
    }

}
