package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.math.MathUtils;
import com.mutunus.tutunus.structures.Side;
import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.helpers.HighestValueIndicator;
import verdelhan.ta4j.indicators.helpers.LowestValueIndicator;
import verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import verdelhan.ta4j.indicators.simple.MinPriceIndicator;
import verdelhan.ta4j.indicators.trackers.SMAIndicator;

import java.math.BigDecimal;


/**
 *
 */
public class MomentumStrategy extends AbstractStrategy {
    private final static double BR = 0.0000d;
    private final static BigDecimal BROKERAGE = bd(BR);

    private static class LongShortTradeOpenerTradeCloser implements TradeOpener, TradeCloser {


        private final SMAIndicator smaIndicator;

        public LongShortTradeOpenerTradeCloser(SMAIndicator smaIndicator) {
            this.smaIndicator = smaIndicator;
        }

        @Override
        public Transaction openTrade(final int tickId, final TimeSeries timeSeries) {
            final Decimal smaValue = smaIndicator.getValue(tickId);
            final Decimal pastSmaValue = smaIndicator.getValue(tickId - 40);


            MinPriceIndicator minPriceInd = new MinPriceIndicator(timeSeries);
            LowestValueIndicator lowestValueIndicator = new LowestValueIndicator(minPriceInd, 80);

            final Decimal lowest = lowestValueIndicator.getValue(tickId);

            final Tick tick = timeSeries.getTick(tickId);
            final Tick tick1 = timeSeries.getTick(tickId - 20);
            final Tick tick2 = timeSeries.getTick(tickId - 40);
            final Decimal closePrice = tick.getClosePrice();

            final double upmove = MathUtils.computeChange(lowest.toDouble(), closePrice.toDouble());

//            boolean uptrend = tick1.isLower(tick);
//            uptrend &= tick2.isLower(tick);

            boolean uptrend = smaValue.isLessThan(closePrice);
            uptrend &= smaValue.isGreaterThan(pastSmaValue);
            if (upmove > 20 && uptrend) {
                final Transaction t = new Transaction(
                        tickId, Side.LONG, 1, tick.getClosePrice().asBigDecimal(), BROKERAGE, timeSeries.getDate(tickId));
                return t;
            }
            return null;
        }

        @Override
        public Transaction closeTrade(int tickId, TimeSeries timeSeries, Transaction openedTransaction) {
            ClosePriceIndicator closePriceInd = new ClosePriceIndicator(timeSeries);
            final int openTickId = openedTransaction.getTickId();
            int diff = tickId - openTickId;
            HighestValueIndicator highValueIndicator = new HighestValueIndicator(closePriceInd, Math.min(diff, 80));

            final Decimal pastHigh = highValueIndicator.getValue(tickId);
            final Tick tick = timeSeries.getTick(tickId);
            final Decimal closePrice = tick.getClosePrice();
            final double changeFromTop = MathUtils.computeChange(pastHigh.toDouble(), closePrice.toDouble());

            if (changeFromTop < -10) {
                final String formatedValue = String.format("%.2f", changeFromTop);
                final String comment = "MovingTP: " + formatedValue + "%";
                final Transaction t = new Transaction(
                        tickId, openedTransaction.getSide().revert(), openedTransaction.getSize(),
                        tick.getClosePrice().asBigDecimal(), BROKERAGE, timeSeries.getDate(tickId), comment);
                return t;
            }
            return null;
        }
    }

    final SMAIndicator smaIndicator;

    public MomentumStrategy(TimeSeries timeSeries, SMAIndicator smaIndicator) {
        super(timeSeries);
        this.smaIndicator = smaIndicator;
        setAllowIntraday(false);
        maxPositionSize = 1;
    }

    @Override
    protected String getName() {
        return "MomentumStrategy";
    }

    @Override
    protected TradeOpener[] getOpeners() {
        return new TradeOpener[]{new LongShortTradeOpenerTradeCloser(smaIndicator)};
    }

    @Override
    protected TradeCloser[] getClosers() {
        return new TradeCloser[]{//
                new TimeoutTradeCloser(80),//
//                new TakeProfitTradeCloser(40),//
                new StopLossTradeCloser(10),//
                new LongShortTradeOpenerTradeCloser(smaIndicator)
        };
    }

}
