//package com.mutunus.tutunus.strategies;
//
//import com.mutunus.tutunus.dao.QuotationsProvider;
//import com.mutunus.tutunus.structures.*;
//
//import java.math.BigDecimal;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// */
//public class ElectionTestStrategy extends AbstractStrategy {
//
//    private final static double BR = 0.00d;
//    private final static BigDecimal BROKERAGE = bd(BR);
//    private final int month;
//
//    class LongShortOpener implements Opener {
//
//        @Override
//        public Transaction openTrade(final int qId, final Quotations qs) {
//            final Quotation q = qs.getQuotation(qId);
//            final MTDate date = qs.getDate(qId);
//
//            Transaction t = null;
//            if (canGoShort(qs, qId)) {
//                t = new Transaction(Side.SHORT, 1, bd(q.getOpen()), BROKERAGE, date);
//            }
//            return t;
//        }
//
//    }
////
////    class LongShortCloser implements Closer {
////
////        @Override
////        public Transaction closeTrade(final int qId, final Quotations qs, final Transaction openTransaction) {
////            final Side side = openTransaction.getSide();
////            final int size = openTransaction.getSize();
////
////            final Quotation q = qs.getQuotation(qId);
////            final double close = q.getClose();
////            final MTDate date = qs.getDate(qId);
////
////            Transaction t = null;
////
////            if (side == Side.LONG && shouldCloseLongs(qs, qId)) {
////                t = new Transaction(Side.SHORT, size, close, BR * size, date);
//////            } else if (side == Side.SHORT && shouldCloseShorts(qs, qId)) {
//////                t = new Transaction(Side.LONG, size, close, BR * size, date);
////            }
////
////            return t;
////        }
////    }
//
//    private final Set<MTDate> dates = new HashSet<>();
//
//    public ElectionTestStrategy(int month, int day, final QuotationsProvider quotationsProvider, Quotations quotations) {
//        super(quotationsProvider);
//        this.month = month;
//        maxPositionSize = 1;
//        allowCloseTradeInTheSamePeriodAsOpen = false;// -> no intraday
//
//        addValidDate(new MTDate(1961, month, day), quotations);
//        addValidDate(new MTDate(1969, month, day), quotations);
//        addValidDate(new MTDate(1977, month, day), quotations);
//        addValidDate(new MTDate(1981, month, day), quotations);
//        addValidDate(new MTDate(1989, month, day), quotations);
//        addValidDate(new MTDate(1993, month, day), quotations);
////        addValidDate(new MTDate(1997, month, 20), quotations);
//        addValidDate(new MTDate(2001, month, day), quotations);
////        addValidDate(new MTDate(2005, month, 20), quotations);
//        addValidDate(new MTDate(2009, month, day), quotations);
////        addValidDate(new MTDate(2013, month, 21), quotations);
//    }
//
//    private void addValidDate(MTDate date, Quotations qs) {
//
//        int counter = 0;
//        while (qs.getDateId(date) == Quotations.MISSING_DATE) {
//            date.addDays(1);
//            counter++;
//            if (counter > 5) {
//                throw new RuntimeException("No quotations around " + date);
//            }
//        }
//
//        dates.add(date);
//    }
//
//    @Override
//    protected String getName() {
//        return "ElectionTest(" + month + ")";
//    }
//
//    private boolean canGoShort(final Quotations qs, final int qId) {
//        MTDate date = qs.getDate(qId);
//        if (dates.contains(date)) {
//            return true;
//        }
//
//        return false;
//    }
//
//
//    @Override
//    protected Opener[] getOpeners() {
//        return new Opener[]{new LongShortOpener()};
//    }
//
//    @Override
//    protected Closer[] getClosers() {
//        return new Closer[]{
////                new LongShortCloser()
//                new TimeoutCloser(50),//
//                new TakeProfitCloser(5),//
////                new StopLossCloser(3)//
//        };
//    }
//
//}
