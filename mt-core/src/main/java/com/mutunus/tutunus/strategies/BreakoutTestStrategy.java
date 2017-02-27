package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.math.MathUtils;
import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Side;
import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;

/**
 */
public class BreakoutTestStrategy extends AbstractStrategy {

    private final static double BR = 0.00d;
    private final static BigDecimal BROKERAGE = bd(BR);
    private final double breakoutThreshold;

    class LongShortOpener implements Opener {

        @Override
        public Transaction openTrade(final int tickId, final TimeSeries timeSeries) {
            final Tick tick = timeSeries.getTick(tickId);
            final MTDate date = timeSeries.getDate(tickId);

            Transaction t = null;
            if (canGoLong(timeSeries, tickId)) {
                t = new Transaction(tickId, Side.LONG, 1, bd(tick.getClosePrice().toDouble()), BROKERAGE, date);
//            } else if (canGoShort(timeSeries, tickId)) {
//                t = new Transaction(Side.SHORT, 1, bd(tick.getClose()), BROKERAGE, date);
            }
            return t;
        }

    }

    public BreakoutTestStrategy(TimeSeries timeSeries, double breakoutThreshold) {
        super(timeSeries);
        this.breakoutThreshold = breakoutThreshold;
        setAllowIntraday(false);
    }

    @Override
    protected String getName() {
        return "BreakoutTest(" + breakoutThreshold + ")";
    }


    private boolean canGoLong(final TimeSeries qs, final int qId) {
        final double c0 = qs.getTick(qId).getClosePrice().toDouble();
        final double c1 = qs.getTick(qId - 1).getClosePrice().toDouble();

        final double change = MathUtils.computeChange(c1, c0);

        if (change > breakoutThreshold) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    protected Opener[] getOpeners() {
        return new Opener[]{new LongShortOpener()};
    }

    @Override
    protected Closer[] getClosers() {
        return new Closer[]{
//                new LongShortCloser()
                new TimeoutCloser(20),//
                new TakeProfitCloser(10),//
                new StopLossCloser(10)//
        };
    }

}
