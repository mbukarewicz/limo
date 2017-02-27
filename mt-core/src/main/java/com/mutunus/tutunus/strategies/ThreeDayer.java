package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.Side;
import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;


/**
 * Implementation of 'experiment3' trend trading strategy found at http://www.myforexdot.org.uk/trend-trading.html
 */
public class ThreeDayer extends AbstractStrategy {

    private final static double BR = 0.0000d;
    private final static BigDecimal BROKERAGE = bd(BR);

    private static class LongShortOpener implements Opener {

        @Override
        public Transaction openTrade(final int tickId, final TimeSeries timeSeries) {
            final Tick tick = timeSeries.getTick(tickId);
            final Tick tick1 = timeSeries.getTick(tickId - 1);
            final Tick tick2 = timeSeries.getTick(tickId - 2);

            final double c = tick.getClosePrice().toDouble();
            final double c1 = tick1.getClosePrice().toDouble();
            final double c2 = tick2.getClosePrice().toDouble();

            if (c < c1 && c1 < c2) {
                final Transaction t = new Transaction(tickId, Side.LONG, 1, tick.getClosePrice().asBigDecimal(), BROKERAGE, timeSeries.getDate(tickId));
                return t;
            }
            return null;
        }
    }

    private static class LongShortCloser implements Closer {

        @Override
        public Transaction closeTrade(final int tickId, final TimeSeries timeSeries, final Transaction openTransaction) {
            final Tick q = timeSeries.getTick(tickId);
            final double openPrice = openTransaction.getPrice().doubleValue();

            final double c = q.getClosePrice().toDouble();

            if (c > openPrice) {
                final Transaction t =
                        new Transaction(tickId, Side.SHORT, openTransaction.getSize(), q.getClosePrice().asBigDecimal(), BROKERAGE, timeSeries.getDate(tickId));
                return t;
            }
            return null;
        }
    }

    public ThreeDayer(TimeSeries timeSeries) {
        super(timeSeries);
        setAllowIntraday(false);
        maxPositionSize = 1;
    }

    @Override
    protected String getName() {
        return "three_dayer";
    }

    @Override
    protected Opener[] getOpeners() {
        return new Opener[]{new LongShortOpener()};
    }

    @Override
    protected Closer[] getClosers() {
        return new Closer[]{//
//                new LongShortCloser(),
                new TimeoutCloser(10),//
                new TakeProfitCloser(2),//
//                new StopLossCloser(25)//
        };
    }

}
