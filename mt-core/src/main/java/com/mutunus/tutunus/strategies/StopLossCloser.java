package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.Side;
import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;


public class StopLossCloser extends TakeProfitCloser {

    public StopLossCloser(final double maxLossInPercent) {
        super(maxLossInPercent);
    }

    @Override
    protected BigDecimal getClosePrice(
            final int tickId,
            final TimeSeries timeSeries,
            final Transaction openTransaction,
            final Side side) {
        final Side sideReverted = side.revert();

        final BigDecimal closePrice = super.getClosePrice(tickId, timeSeries, openTransaction, sideReverted);
        return closePrice;
    }

    @Override
    protected String getCloseReason() {
        return "StopLoss";
    }

}
