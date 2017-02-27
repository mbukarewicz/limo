//package com.mutunus.tutunus.strategies;
//
//import com.mutunus.tutunus.dao.QuotationsProvider;
//import com.mutunus.tutunus.structures.*;
//import org.apache.commons.math3.stat.StatUtils;
//
//import java.math.BigDecimal;
//
///**
// */
//public class SMATestStrategy extends AbstractStrategy {
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
////            } else if (canGoShort(qs, qId)) {
////                t = new Transaction(Side.SHORT, 1, bd(q.getClose()), BROKERAGE, date);
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
////            } else if (side == Side.SHORT && shouldCloseShorts(qs, qId)) {
////                t = new Transaction(Side.LONG, size, close, BR * size, date);
//            }
//
//            return t;
//        }
//    }
//
//    public SMATestStrategy(final QuotationsProvider quotationsProvider) {
//        super(quotationsProvider);
//    }
//
//    @Override
//    protected String getName() {
//        return "SMATest";
//    }
//
////    private boolean shouldCloseShorts(final Quotations qs, final int qId) {
////        final double c0 = qs.getQuotation(qId).getClose();
////
////        final double[] values5 = getValues(qs, qId - 4, qId);
////        final double mean5 = StatUtils.mean(values5);
////
////        if (c0 < mean5) {
////            return true;
////        } else {
////            return false;
////        }
////    }
//
//    private boolean shouldCloseLongs(final Quotations qs, final int qId) {
//        final double c0 = qs.getQuotation(qId).getClose();
//
//        final double[] values5 = getValues(qs, qId - 4, qId);
//        final double mean5 = StatUtils.mean(values5);
//
//        if (c0 > mean5) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
////    private boolean canGoShort(final Quotations qs, final int qId) {
////        final double c0 = qs.getQuotation(qId).getClose();
////        final double[] values200 = getValues(qs, qId - 199, qId);
////        final double mean200 = StatUtils.mean(values200);
////
////        final double[] values5 = getValues(qs, qId - 4, qId);
////        final double mean5 = StatUtils.mean(values5);
////
////        if (c0 < mean200 && c0 > mean5) {
////            return true;
////        } else {
////            return false;
////        }
////    }
//
//    private boolean canGoLong(final Quotations qs, final int qId) {
//        /*
//         * If ClosingPrice > 200-DaySMA AND ClosingPrice < 5-DaySMA GoLong. If ClosingPrice > 5-DaySMA CloseLong. If
//         * ClosingPrice < 200-DaySMA AND ClosingPrice > 5-DaySMA GoShort. If ClosingPrice < 5-DaySMA CloseShort.
//         */
//        final double c0 = qs.getQuotation(qId).getClose();
//        final double[] values200 = getValues(qs, qId - 199, qId);
//        final double mean200 = StatUtils.mean(values200);
//
//        final double[] values5 = getValues(qs, qId - 4, qId);
//        final double mean5 = StatUtils.mean(values5);
//
//        if (c0 > mean200 && c0 < mean5) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }
//
//    private double[] getValues(final Quotations qs, final int begin, final int end) {
//        final double[] values = new double[end - begin + 1];
//        for (int i = begin; i <= end; i++) {
//            values[i - begin] = qs.getQuotation(i).getClose();
//        }
//        return values;
//    }
//
//    @Override
//    protected Opener[] getOpeners() {
//        return new Opener[]{new LongShortOpener()};
//    }
//
//    @Override
//    protected Closer[] getClosers() {
//        return new Closer[]{new LongShortCloser()};
//    }
//
//}
