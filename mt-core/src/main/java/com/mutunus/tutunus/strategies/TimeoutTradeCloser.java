package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.*;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;


public class TimeoutTradeCloser implements TradeCloser {

    private final int businessDaysToClose;

    public TimeoutTradeCloser(final int businessDaysToClose) {
        this.businessDaysToClose = businessDaysToClose;
    }

    @Override
    public Transaction closeTrade(final int tickId, final TimeSeries timeSeries, final Transaction openTransaction) {
        final MTDate today = timeSeries.getDate(tickId);
        final MTDate openDate = timeSeries.getDate(openTransaction.getTickId());
        final int openDateId = openTransaction.getTickId();
        if (tickId < openDateId) {
            throw new MTException(String.format(
                    "Can not close trade with dateId '%d' (%s) since that trade was opened after - open dateId '%d' (%s)", tickId,
                    today, openDateId, openDate));
        }

        if (canClose(tickId, openDateId)) {
            final Side side = openTransaction.getSide();
            final int size = openTransaction.getSize();
            final Tick q = timeSeries.getTick(tickId);
            final double close = q.getClosePrice().toDouble();
            final Transaction t = new Transaction(tickId, side.revert(), size, close, close * size * 0, today, getCloseReason());
            return t;
        }

        return null;
    }

    protected String getCloseReason() {
        return "Timeout_" + businessDaysToClose;
    }

    private boolean canClose(final int qId, final int openDateId) {
        final int diff = qId - openDateId;
        if (businessDaysToClose <= diff) {
            return true;
        }

        return false;
    }

}
