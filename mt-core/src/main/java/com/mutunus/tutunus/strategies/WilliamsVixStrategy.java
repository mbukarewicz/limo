package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.research.indicators.WilliamsVixFixIndicator;
import com.mutunus.tutunus.structures.Side;
import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;


/**
 * See:
 * Larry Williams, “The VIX fix” (Active Trader, 2007)
 * https://www.ireallytrade.com/newsletters/VIXFix.pdf
 * Amber Hestla-Barnhart, “Fixing the VIX: An Indicator to Beat Fear” (Technically Speaking, 2015)
 * http://docs.mta.org/technically-speaking/15-march/
 * Formula:
 * VIX Fix = (Highest (Close,22) – Low) / (Highest (Close,22)) * 100
 */
public class WilliamsVixStrategy extends AbstractStrategy {
    private final static double BR = 0.0000d;
    private final static BigDecimal BROKERAGE = bd(BR);

    private static class LongShortTradeOpener implements TradeOpener {


        private final WilliamsVixFixIndicator wvx;

        public LongShortTradeOpener(WilliamsVixFixIndicator wvx) {
            this.wvx = wvx;
        }

        @Override
        public Transaction openTrade(final int tickId, final TimeSeries timeSeries) {

            final Tick tick = timeSeries.getTick(tickId);
            final Decimal vix = wvx.getValue(tickId);
//            System.out.println(tickId + " " + timeSeries.getDate(tickId) + ", hghs: " + highestValue + ", min: " + tick.getLowPrice()
//                    + ", vix1 " + vix1
//                    + ", vix2 " + vix2
//                    + " -> " + " vix: " + vix);
//
            if (vix.isGreaterThan(Decimal.valueOf(18))) {
                final Transaction t = new Transaction(
                        tickId, Side.LONG, 1, tick.getClosePrice().asBigDecimal(), BROKERAGE, timeSeries.getDate(tickId));
                return t;
            }
            return null;
        }
    }

    final WilliamsVixFixIndicator wvf;

    public WilliamsVixStrategy(TimeSeries timeSeries, WilliamsVixFixIndicator wvf) {
        super(timeSeries);
        this.wvf = wvf;
        setAllowIntraday(false);
        maxPositionSize = 1;
    }

    @Override
    protected String getName() {
        return "WilliamsVixFix";
    }

    @Override
    protected TradeOpener[] getOpeners() {
        return new TradeOpener[]{new LongShortTradeOpener(wvf)};
    }

    @Override
    protected TradeCloser[] getClosers() {
        return new TradeCloser[]{//
                new TimeoutTradeCloser(80),//
                new TakeProfitTradeCloser(8),//
//                new StopLossTradeCloser(10)//
        };
    }

}
