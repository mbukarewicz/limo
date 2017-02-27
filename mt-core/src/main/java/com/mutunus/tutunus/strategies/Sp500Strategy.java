//package com.mutunus.tutunus.strategies;
//
//import com.mutunus.tutunus.dao.QuotationsProvider;
//import com.mutunus.tutunus.structures.*;
//
//import java.math.BigDecimal;
//
///**
// * Implementation of S&P500 trading strategy found at http://www.myforexdot.org.uk/SP500TradingSystem.html
// */
//public class Sp500Strategy extends AbstractStrategy {
//
//    private final static double BR = 0.01d;
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
//            if (canGoLong(qs, qId)) {
//                t = new Transaction(Side.LONG, 1, bd(q.getClose()), BROKERAGE, date);
//            } else if (canGoShort(qs, qId)) {
//                t = new Transaction(Side.SHORT, 1, bd(q.getClose()), BROKERAGE, date);
//            }
//            return t;
//        }
//
//    }
//
//    class LongShortCloser implements Closer {
//
//        @Override
//        public Transaction closeTrade(final int qId, final Quotations qs, final Transaction openTransaction) {
//            final Side side = openTransaction.getSide();
//            final int size = openTransaction.getSize();
//
//            final Quotation q = qs.getQuotation(qId);
//            final double close = q.getClose();
//            final MTDate date = qs.getDate(qId);
//
//            Transaction t = null;
//
//            if (side == Side.LONG && shouldCloseLongs(qs, qId)) {
//                t = new Transaction(Side.SHORT, size, close, BR * size, date);
//            } else if (side == Side.SHORT && shouldCloseShorts(qs, qId)) {
//                t = new Transaction(Side.LONG, size, close, BR * size, date);
//            }
//
//            return t;
//        }
//    }
//
//    public Sp500Strategy(final QuotationsProvider quotationsProvider) {
//        super(quotationsProvider);
//        maxPositionSize = 4;
//    }
//
//    @Override
//    protected String getName() {
//        return "sp500Killer";
//    }
//
//    @Override
//    protected Opener[] getOpeners() {
//        return new Opener[]{new LongShortOpener()};
//    }
//
//    @Override
//    protected Closer[] getClosers() {
//        return new Closer[]{new LongShortCloser()};// , new TimeoutCloser()};
//    }
//
//    private boolean canGoLong(final Quotations qs, final int qId) {
//        final double c0 = qs.getQuotation(qId).getClose();
//        final double c1 = qs.getQuotation(qId - 1).getClose();
//        final double c2 = qs.getQuotation(qId - 2).getClose();
//        final double c3 = qs.getQuotation(qId - 3).getClose();
//        final double c60 = qs.getQuotation(qId - 60).getClose();
//
//        if (c0 < c1 && c0 < c2 && c0 < c3 && c0 > c60) {
//            return true;
//        }
//
//        return false;
//    }
//
//    private boolean canGoShort(final Quotations qs, final int qId) {
//        final double c0 = qs.getQuotation(qId).getClose();
//        final double c1 = qs.getQuotation(qId - 1).getClose();
//        final double c2 = qs.getQuotation(qId - 2).getClose();
//        final double c3 = qs.getQuotation(qId - 3).getClose();
//        final double c60 = qs.getQuotation(qId - 60).getClose();
//
//        if (c0 > c1 && c0 > c2 && c0 > c3 && c0 < c60) {
//            return true;
//        }
//
//        return false;
//    }
//
//    private boolean shouldCloseShorts(final Quotations qs, final int qId) {
//        final double c0 = qs.getQuotation(qId).getClose();
//        final double c1 = qs.getQuotation(qId - 1).getClose();
//        final double c2 = qs.getQuotation(qId - 2).getClose();
//        final double c3 = qs.getQuotation(qId - 3).getClose();
//        final double c60 = qs.getQuotation(qId - 60).getClose();
//
//        if ((c0 < c1 && c0 < c2 && c0 < c3) || c0 > c60) {
//            return true;
//        }
//
//        return false;
//    }
//
//    private boolean shouldCloseLongs(final Quotations qs, final int qId) {
//        final double c0 = qs.getQuotation(qId).getClose();
//        final double c1 = qs.getQuotation(qId - 1).getClose();
//        final double c2 = qs.getQuotation(qId - 2).getClose();
//        final double c3 = qs.getQuotation(qId - 3).getClose();
//        final double c60 = qs.getQuotation(qId - 60).getClose();
//
//        if ((c0 > c1 && c0 > c2 && c0 > c3) || c0 < c60) {
//            return true;
//        }
//
//        return false;
//    }
//
//}
