//package com.mutunus.tutunus.strategies;
//
//import com.mutunus.tutunus.dao.QuotationsProvider;
//import com.mutunus.tutunus.structures.*;
//
//import java.math.BigDecimal;
//
//
///**
// * Implementation of 'experiment3' trend trading strategy found at http://www.myforexdot.org.uk/trend-trading.html
// */
//public class TrendWithDipsAndHighsStrategy extends AbstractStrategy {
//
//    private final static double BR = 0.0000d;
//    private final static BigDecimal BROKERAGE = bd(BR);
//
//    class LongShortTradeOpener implements TradeOpener {
//
//        @Override
//        public Transaction openTrade(final int qId, final Quotations qs) {
//            if (qId < 140) {
//                return null;
//            }
//
//            final Quotation q = qs.getQuotation(qId);
//            final MTDate date = qs.getDate(qId);
//
//            // TODO: 120 or 140?
//            final double[] closes120 = copyCloses(qs, qId, 140);
//            final int indexHigh120 = getMaxIndex(closes120);
//            final int indexLow120 = getMinIndex(closes120);
//            final double[] closes10 = copyCloses(qs, qId, 10);
//            final int indexHigh10 = getMaxIndex(closes10);
//            final int indexLow10 = getMinIndex(closes10);
//            // TODO: 120 or 140?
//            final int daysSinceLow120 = 140 - indexLow120;
//            final int daysSinceHigh120 = 140 - indexHigh120;
//
//            Transaction t = null;
//            if (indexLow10 == 9 // market closes lower than it has ever closed in the previous 10 days
//                    && daysSinceHigh120 <= 20) { // market has made a new 120 day closing price high in the previous 20 days (it
//                // has to be 'true high' in the last 20 days so 140 days in total...)
//                t = new Transaction(Side.LONG, 1, bd(q.getClose()), BROKERAGE, date);
//            } else if (indexHigh10 == 9// price makes a new 10 day closing price high
//                    && daysSinceLow120 <= 20) {// market has made a new 120 day closing price low in the previous 20 days (it has
//                // to be 'true low' in the last 20 days so 140 days in total...)
//                t = new Transaction(Side.SHORT, 1, bd(q.getClose()), BROKERAGE, date);
//            }
//
//            return t;
//        }
//
//        private int getMaxIndex(final double[] values) {
//            double max = Double.MIN_VALUE;
//            int maxIndex = 0;
//
//            for (int i = 0; i < values.length; i++) {
//                final double v = values[i];
//                if (v > max) {
//                    max = v;
//                    maxIndex = i;
//                }
//            }
//
//            return maxIndex;
//        }
//
//        private int getMinIndex(final double[] values) {
//            double min = Double.MAX_VALUE;
//            int minIndex = 0;
//
//            for (int i = 0; i < values.length; i++) {
//                final double v = values[i];
//                if (v < min) {
//                    min = v;
//                    minIndex = i;
//                }
//            }
//
//            return minIndex;
//        }
//
//        private double[] copyCloses(final Quotations qs, final int lastIndexInclusive, final int size) {
//            final int first = lastIndexInclusive - size + 1;
//            final double[] lows = new double[size];
//            for (int i = 0; i < size; i++) {
//                final double low = qs.getQuotation(first + i).getClose();
//                lows[i] = low;
//            }
//            return lows;
//        }
//
//    }
//
//    public TrendWithDipsAndHighsStrategy(final QuotationsProvider quotationsProvider) {
//        super(quotationsProvider);
//        // maxPositionSize = 1;
//        allowCloseTradeInTheSamePeriodAsOpen = false;// -> intraday
//    }
//
//    @Override
//    protected String getName() {
//        return "trend_with_highs_and_lows";
//    }
//
//    @Override
//    protected TradeOpener[] getOpeners() {
//        return new TradeOpener[]{new LongShortTradeOpener()};
//    }
//
//    @Override
//    protected TradeCloser[] getClosers() {
//        return new TradeCloser[]{//
//                new TimeoutTradeCloser(40),//
//                // new TakeProfitTradeCloser(35),//
//                // new StopLossTradeCloser(2.5)//
//        };
//    }
//
//}
