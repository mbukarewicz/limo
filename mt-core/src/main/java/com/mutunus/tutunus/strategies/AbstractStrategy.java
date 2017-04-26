package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.*;
import verdelhan.ta4j.TimeSeries;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


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

        final TradeOpener[] tradeOpeners = getOpeners();
        final TradeCloser[] tradeClosers = getClosers();
        long currentPositionSize = 0;

        for (int qId = ts.getBegin(); qId <= ts.getEnd(); qId++) {
            @SuppressWarnings("unchecked")
            List<Transaction> newOpens = Collections.EMPTY_LIST;
            if (currentPositionSize < maxPositionSize) {
                newOpens = tryToOpenTrade(timeSeries, tradeOpeners, qId);
                currentPositionSize += sumPositionSize(newOpens);
            }

            if (allowCloseTradeInTheSamePeriodAsOpen) {
                allOpened.addAll(newOpens);
                currentPositionSize -= tryToCloseTrades(timeSeries, flowFactory, allOpened, tradeClosers, qId);
            } else {
                currentPositionSize -= tryToCloseTrades(timeSeries, flowFactory, allOpened, tradeClosers, qId);
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
    private List<Transaction> tryToOpenTrade(final TimeSeries ts, final TradeOpener[] tradeOpeners, final int qId) {
        List<Transaction> newOpens = null;
        for (final TradeOpener o : tradeOpeners) {
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
                                  final TradeCloser[] tradeClosers,
                                  final int qId) {
        long totalClosed = 0;

        final Iterator<Transaction> openedIter = allOpened.iterator();
        while (openedIter.hasNext()) {
            final Transaction tOpen = openedIter.next();
            for (final TradeCloser c : tradeClosers) {
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
        final TradeCloser endTestPeriodTradeCloser = getEndTestPeriodCloser();
        while (openedIter.hasNext()) {
            final Transaction tOpen = openedIter.next();
            final Transaction tClose = endTestPeriodTradeCloser.closeTrade(lastDayId, ts, tOpen);

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

    protected TradeCloser getEndTestPeriodCloser() {
        return new EndOfTradeTradeCloser();
    }

    protected abstract TradeOpener[] getOpeners();

    protected abstract TradeCloser[] getClosers();

    protected abstract String getName();

    protected static BigDecimal bd(final double d) {
        return new BigDecimal(Double.toString(d));
    }

}
