package com.mutunus.tutunus.structures;

import java.util.*;
import java.util.Map.Entry;


// flow niech ma tylko trades z referencjami do transakcji i ew oryginalnych tradow?
public class FlowFactory {

    static class TradesAndTransactions {

        private final List<Trade> trades = new ArrayList<Trade>();
        private final List<Transaction> transactions = new ArrayList<Transaction>();

        private int openPositionSize = 0;

    }

    interface FlowWritable {

        void addTrade(Trade trade);

        void addTransaction(Transaction transaction);

        void seal();
    }

    private final NavigableMap<MTDate, TradesAndTransactions> perDate = new TreeMap<MTDate, TradesAndTransactions>();
    private final String asset;
    private final FlowMetaInfo flowMetaInfo;

    public FlowFactory(final String asset) {
        this(asset, FlowMetaInfo.NO_INFO);
    }

    public FlowFactory(final String asset, final FlowMetaInfo flowMetaInfo) {
        this.asset = asset;
        this.flowMetaInfo = flowMetaInfo;
    }

    public TradingRegister getFlow() {

        final List<Transaction> allTransactions = getAllTransactions();
        final List<Trade> trades = convertToTrades(allTransactions);
        // now 'allTransactions' list contains only unmatched transactions
        final TradingRegisterImpl flow = new TradingRegisterImpl(asset);
        flow.setMetaInfo(flowMetaInfo);

        for (final Trade t : trades) {
            flow.addTrade(t);
        }
        for (final Entry<MTDate, TradesAndTransactions> e : perDate.entrySet()) {
            final MTDate date = e.getKey();
            final List<Trade> tradesForDate = e.getValue().trades;
            for (final Trade t : tradesForDate) {
                if (date.equals(t.getOpenDate())) {
                    flow.addTrade(t);
                }
            }
        }

        for (final Transaction t : allTransactions) {
            flow.addTransaction(t);
        }
        flow.seal();

        return flow;
    }

    private List<Trade> convertToTrades(final List<Transaction> transactions) {
        final List<Trade> result = new ArrayList<Trade>();

        final Iterator<Transaction> i = transactions.iterator();
        final List<Transaction> openSide = new ArrayList<Transaction>();

        while (i.hasNext()) {
            final Transaction t = i.next();
            i.remove();

            if (openSide.isEmpty() || openSide.get(0).getSide() == t.getSide()) {
                openSide.add(t);
                continue;
            }

            // different sides
            makeTrade(result, openSide, t);
        }

        transactions.addAll(openSide);
        return result;
    }

    private void makeTrade(final List<Trade> trades, final List<Transaction> opens, Transaction closeTransaction) {
        while (opens.size() > 0) {

            final Transaction open = opens.remove(0);
            final int openSize = open.getSize();
            final int closeSize = closeTransaction.getSize();

            if (openSize >= closeSize) {
                final Transaction openTraded = open.getPart(closeSize);

                if (openSize > closeSize) {
                    final Transaction openLeft = open.getPart(openSize - closeSize);
                    opens.add(0, openLeft);
                }

                final Trade trade = new Trade(openTraded, closeTransaction);
                trades.add(trade);
                return;
            }

            // open < closeTransaction -> closeTransaction.makeLower
            final Transaction closeTraded = closeTransaction.getPart(openSize);
            closeTransaction = closeTransaction.getPart(closeSize - openSize);
            final Trade trade = new Trade(open, closeTraded);
            trades.add(trade);

        }

        opens.add(closeTransaction);// changes open side!
    }

    private List<Transaction> getAllTransactions() {
        final List<Transaction> list = new ArrayList<Transaction>();
        for (final Entry<MTDate, TradesAndTransactions> e : perDate.entrySet()) {
            list.addAll(e.getValue().transactions);
        }

        return list;
    }

    public void addFifoTransaction(final Transaction transaction) {
        final TradesAndTransactions tt = getOrCreate(transaction.getDate());
        tt.transactions.add(transaction);
        final int sizeSigned = transaction.getSide().getSizeSigned(transaction.getSize());

        final NavigableMap<MTDate, TradesAndTransactions> submap = perDate.tailMap(transaction.getDate(), true);
        for (final Entry<MTDate, TradesAndTransactions> e : submap.entrySet()) {
            final TradesAndTransactions value = e.getValue();
            value.openPositionSize += sizeSigned;
        }
    }

    public int getOpenedPositionSize(final MTDate date) {
        final Entry<MTDate, TradesAndTransactions> floor = perDate.floorEntry(date);
        if (floor != null) {
            return floor.getValue().openPositionSize;
        }

        return 0;
    }

    public void addTrade(final Trade trade) {
        final TradesAndTransactions open = getOrCreate(trade.getOpenDate());
        final TradesAndTransactions close = getOrCreate(trade.getCloseDate());

        open.trades.add(trade);

        if (trade.getOpenDate().equals(trade.getCloseDate()) == false) {
            close.trades.add(trade);
        }
    }

    private TradesAndTransactions getOrCreate(final MTDate date) {
        TradesAndTransactions open = perDate.get(date);
        if (open == null) {
            open = new TradesAndTransactions();
            perDate.put(date, open);

            final Entry<MTDate, TradesAndTransactions> prev = perDate.lowerEntry(date);
            if (prev != null) {
                open.openPositionSize = prev.getValue().openPositionSize;
            }

        }
        return open;
    }

}
