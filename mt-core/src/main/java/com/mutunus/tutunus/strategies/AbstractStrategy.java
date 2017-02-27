package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.*;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


interface Opener {

    Transaction openTrade(int tickId, TimeSeries timeSeries);
}

interface Closer {

    Transaction closeTrade(int tickId, TimeSeries timeSeries, Transaction openedTransaction);
}

public abstract class AbstractStrategy {

    protected final TimeSeries timeSeries;
    protected boolean allowCloseTradeInTheSamePeriodAsOpen = false;
    protected long maxPositionSize = 1;

    public AbstractStrategy(TimeSeries timeSeries) {
        this.timeSeries = timeSeries;
    }


    public void setAllowIntraday(boolean allowIntraday) {
        allowCloseTradeInTheSamePeriodAsOpen = allowIntraday;
    }

    public TradingRegister run(TimeSeries ts) {
        String asset = ts.getName();

        MTDate beginDate = timeSeries.getDate(ts.getBegin());
        MTDate endDate = timeSeries.getDate(ts.getEnd());
        final FlowFactory flowFactory = new FlowFactory(asset, new FlowMetaInfo(beginDate, endDate, getName()));

        final List<Transaction> allOpened = new ArrayList<>();

        final Opener[] openers = getOpeners();
        final Closer[] closers = getClosers();
        long currentPositionSize = 0;

        for (int qId = ts.getBegin(); qId <= ts.getEnd(); qId++) {
            @SuppressWarnings("unchecked")
            List<Transaction> newOpens = Collections.EMPTY_LIST;
            if (currentPositionSize < maxPositionSize) {
                newOpens = tryToOpenTrade(timeSeries, openers, qId);
                currentPositionSize += sumPositionSize(newOpens);
            }

            if (allowCloseTradeInTheSamePeriodAsOpen) {
                allOpened.addAll(newOpens);
                currentPositionSize -= tryToCloseTrades(timeSeries, flowFactory, allOpened, closers, qId);
            } else {
                currentPositionSize -= tryToCloseTrades(timeSeries, flowFactory, allOpened, closers, qId);
                allOpened.addAll(newOpens);
            }
        }

        closeAllTransactions(timeSeries, flowFactory, allOpened, ts.getEnd());

        return flowFactory.getFlow();
    }

    private long sumPositionSize(final List<Transaction> newOpens) {
        long sum = 0;

        for (final Transaction t : newOpens) {
            sum += Math.abs(t.getSize());
        }

        return sum;
    }

    @SuppressWarnings("unchecked")
    private List<Transaction> tryToOpenTrade(final TimeSeries ts, final Opener[] openers, final int qId) {
        List<Transaction> newOpens = null;
        for (final Opener o : openers) {
            final Transaction newOpen = o.openTrade(qId, ts);
            if (newOpen != null) {
                if (newOpens == null) {
                    newOpens = new ArrayList<>();
                }
                newOpens.add(newOpen);
                break;// TODO: add if(multiOpensAtOneTime == false)?
            }
        }

        return newOpens != null ? newOpens : Collections.EMPTY_LIST;
    }

    private long tryToCloseTrades(final TimeSeries ts,
                                  final FlowFactory flowFactory,
                                  final List<Transaction> allOpened,
                                  final Closer[] closers,
                                  final int qId) {
        long totalClosed = 0;

        final Iterator<Transaction> openedIter = allOpened.iterator();
        while (openedIter.hasNext()) {
            final Transaction tOpen = openedIter.next();
            for (final Closer c : closers) {
                final Transaction tClose = c.closeTrade(qId, ts, tOpen);
                if (tClose != null) {
                    final Trade trade = new Trade(tOpen, tClose);
                    flowFactory.addTrade(trade);
                    openedIter.remove();
                    totalClosed += Math.abs(tClose.getSize());
                    break;
                }
            }
        }

        return totalClosed;
    }

    private void closeAllTransactions(final TimeSeries ts,
                                      final FlowFactory flowFactory,
                                      final List<Transaction> allOpened,
                                      final int lastDayId) {
        final Iterator<Transaction> openedIter = allOpened.iterator();
        final Closer endTestPeriodCloser = getEndTestPeriodCloser();
        while (openedIter.hasNext()) {
            final Transaction tOpen = openedIter.next();
            final Transaction tClose = endTestPeriodCloser.closeTrade(lastDayId, ts, tOpen);

            if (tClose != null) {
                final Trade trade = new Trade(tOpen, tClose);
                flowFactory.addTrade(trade);
                openedIter.remove();
            }
        }

        if (allOpened.isEmpty() == false) {
            throw new RuntimeException("all transactions should be closed");
        }
    }

    protected Closer getEndTestPeriodCloser() {
        return new EndOfTradeCloser();
    }

    protected abstract Opener[] getOpeners();

    protected abstract Closer[] getClosers();

    protected abstract String getName();

    protected static BigDecimal bd(final double d) {
        return new BigDecimal(Double.toString(d));
    }

}
