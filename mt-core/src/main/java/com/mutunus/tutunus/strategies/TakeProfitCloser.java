package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.*;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;


public class TakeProfitCloser implements Closer {

    private static final BigDecimal _100 = new BigDecimal("100");
    private final BigDecimal profitInPercent;

    public TakeProfitCloser(final double profitInPercent) {
        this.profitInPercent = new BigDecimal(Double.toString(profitInPercent));
    }

    @Override
    public Transaction closeTrade(final int tickId, final TimeSeries timeSeries, final Transaction openTransaction) {

        final MTDate today = timeSeries.getDate(tickId);
        MTDate openDate = timeSeries.getDate(openTransaction.getTickId());
        final int openDateId = openTransaction.getTickId();
        if (tickId < openDateId) {
            throw new MTException(String.format(
                    // TODO: napisac lepszy komentarz
                    "Can not close trade with dateId '%d' (%s) since that trade was opened after - open dateId '%d' (%s)", tickId,
                    today, openDateId, openDate));
        }

        final Side side = openTransaction.getSide();
        final BigDecimal targetPrice = getClosePrice(tickId, timeSeries, openTransaction, side);

        if (targetPrice != null) {
            final int size = openTransaction.getSize();
            final double close = targetPrice.doubleValue();
            final Transaction t = new Transaction(tickId, side.revert(), size, close, close * size * 0, today, getCloseReason());
            return t;
        }

        return null;
    }

    protected String getCloseReason() {
        return "TakeProfit";
    }

    protected BigDecimal getClosePrice(final int tickId, final TimeSeries timeSeries, final Transaction openTransaction, final Side side) {
        BigDecimal targetPrice = null;
        if (side == Side.LONG) {
            BigDecimal targetPercent = profitInPercent.add(_100);// 2.5% -> 102.5
            targetPercent = targetPercent.divide(_100);// 102.5 -> 1.025

            final BigDecimal expectedPrice = openTransaction.getPrice().multiply(targetPercent);
            final BigDecimal high = timeSeries.getTick(tickId).getMaxPrice().asBigDecimal();

            if (high.compareTo(expectedPrice) >= 0) {
                targetPrice = expectedPrice;
            }
        } else if (side == Side.SHORT) {
            BigDecimal targetPercent = _100.subtract(profitInPercent); // 2.5% -> 97.5
            targetPercent = targetPercent.divide(_100); // 97.5 -> 0.975

            final BigDecimal expectedPrice = openTransaction.getPrice().multiply(targetPercent);
            final BigDecimal low = timeSeries.getTick(tickId).getMinPrice().asBigDecimal();

            if (low.compareTo(expectedPrice) <= 0) {
                targetPrice = expectedPrice;
            }
        }
        return targetPrice;
    }

}
