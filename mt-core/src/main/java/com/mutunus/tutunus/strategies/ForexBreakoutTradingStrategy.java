//package com.mutunus.tutunus.strategies;
//
//import com.mutunus.tutunus.dao.QuotationsProvider;
//import com.mutunus.tutunus.structures.*;
//
//import java.math.BigDecimal;
//
//
///**
// * Implementation of gold and silver trading strategy found at http://www.myforexdot.org.uk/commodities-trading-system.html
// */
//public class ForexBreakoutTradingStrategy extends AbstractStrategy {
//
//    private final static double BR = 0.0000d;
//    private final static BigDecimal BROKERAGE = bd(BR);
//
//    class LongShortOpener implements Opener {
//
//        @Override
//        public Transaction openTrade(final int qId, final Quotations qs) {
//            final Quotation q = qs.getQuotation(qId);
//            final MTDate date = qs.getDate(qId);
//
//            Transaction t = null;
//            if (hasNewHigh(qs, qId, 120)) { // new 120 day closing price high happened no less than 60 days ago
//                t = new Transaction(Side.LONG, 1, bd(q.getClose()), BROKERAGE, date);
//            } else if (hasNewLow(qs, qId, 120)) {// new 120 day closing price low happened no less than 60 days ago
//                t = new Transaction(Side.SHORT, 1, bd(q.getClose()), BROKERAGE, date);
//            }
//
//            return t;
//        }
//
//    }
//
//    public ForexBreakoutTradingStrategy(final QuotationsProvider quotationsProvider) {
//        super(quotationsProvider);
//        maxPositionSize = 1;
//        allowCloseTradeInTheSamePeriodAsOpen = false;// -> intraday
//    }
//
//    public boolean hasNewHigh(final Quotations qs, final int qId, final int range) {
//        final double close = qs.getQuotation(qId).getClose();
//        for (int i = 1; i <= range; i++) {
//            final double pastClose = qs.getQuotation(qId - i).getClose();
//
//            if (pastClose > close) {
//                return false;
//            }
//
//        }
//
//        return true;
//    }
//
//    public boolean hasNewLow(final Quotations qs, final int qId, final int range) {
//        final double close = qs.getQuotation(qId).getClose();
//        for (int i = 1; i <= range; i++) {
//            final double pastClose = qs.getQuotation(qId - i).getClose();
//
//            if (pastClose > close) {
//                return false;
//            }
//
//        }
//
//        return true;
//    }
//
//    @Override
//    protected String getName() {
//        return "forex_breakout";
//    }
//
//    @Override
//    protected Opener[] getOpeners() {
//        return new Opener[]{new LongShortOpener()};
//    }
//
//    @Override
//    protected Closer[] getClosers() {
//        return new Closer[]{//
//                new TimeoutCloser(40),//
//                // new TakeProfitCloser(38),//
//                // new StopLossCloser(2.5)//
//        };
//    }
//
//}
