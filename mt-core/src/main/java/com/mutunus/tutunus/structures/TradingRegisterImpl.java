package com.mutunus.tutunus.structures;

import com.mutunus.tutunus.structures.FlowFactory.FlowWritable;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;


public class TradingRegisterImpl implements TradingRegister, FlowWritable {

    // TODO:
    // // !!!please decide
    // // trade powinien zawiarac id/obiekt open oraz close
    // // ////// czy np open powinine miec wielkosc oryginalna czy faktyczna?
    // // czy storowac wszystkie transakcje z danego dnia czy tylko trady i pozycje na koniec?
    //
    // // jesli duzy ilosc na raz to jedno id transakcji,, wtedy nie rozbijac tego na kilka malych
    // // 100+ //id1
    // // 200+ //id2
    // // 100+ //id3
    // // 400- //id4 a nie id4 id5 id6,, ale trzeba trzymac referencje do kilku transakcji
    // // nie wystarczy po prostu id transakcji ustawiac na ta sama wartosc,, czyli
    // // 100+ 100- id1
    // // 200+ 200- id2
    //
    // // tak samo jak ustawi sie kupno na duzo ilosc i wchodzi w turach,, spr jak to wyglada w csv'kach
    // // czy to jest najwazneisjze?
    //

    private class Cache {

        private final NavigableSet<MTDate> allDates = new TreeSet<MTDate>();
        private final NavigableMap<MTDate, Integer> openedPositionSize = new TreeMap<>();
        private final NavigableMap<MTDate, BigDecimal> totalNetProfit = new TreeMap<MTDate, BigDecimal>();
        private final NavigableMap<MTDate, BigDecimal> totalProfitPercent = new TreeMap<>();
        private final NavigableMap<MTDate, BigDecimal> totalBrokerage = new TreeMap<MTDate, BigDecimal>();
    }

    private final String asset;
    private FlowMetaInfo metaInfo = FlowMetaInfo.NO_INFO;
    private Cache cache = null;

    private final NavigableMap<MTDate, List<Trade>> trades = new TreeMap<MTDate, List<Trade>>();
    private final NavigableMap<MTDate, List<Transaction>> transactions = new TreeMap<MTDate, List<Transaction>>();
    private BigDecimal initialMoney = BigDecimal.ZERO;

    public TradingRegisterImpl(final String asset) {
        this.asset = asset;
    }

    @Override
    public String getAsset() {
        return asset;
    }

    @Override
    public FlowMetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(final FlowMetaInfo flowInfo) {
        metaInfo = flowInfo;
    }

    @Override
    public void addTransaction(final Transaction t) {
        final MTDate date = t.getDate();
        List<Transaction> list = transactions.get(date);
        if (list == null) {
            list = new ArrayList<>();
            transactions.put(date, list);
        }
        list.add(t);
    }

    @Override
    public void addTrade(final Trade trade) {
        final MTDate openDate = trade.getOpenDate();
        final MTDate closeDate = trade.getCloseDate();

        List<Trade> tradeList = trades.get(openDate);
        if (tradeList == null) {
            tradeList = new ArrayList<>();
            trades.put(openDate, tradeList);
        }
        tradeList.add(trade);

        if (openDate.equals(closeDate)) {
            return;
        }

        tradeList = trades.get(closeDate);
        if (tradeList == null) {
            tradeList = new ArrayList<>();
            trades.put(closeDate, tradeList);
        }
        tradeList.add(trade);
    }

    @Override
    public void seal() {
        cache = new Cache();
        cache.allDates.addAll(trades.keySet());
        cache.allDates.addAll(transactions.keySet());

        int totalSize = 0;
        BigDecimal totalNetProfit = BigDecimal.ZERO;
        BigDecimal totalProfitPercent = BigDecimal.ZERO;
        BigDecimal totalBrokerage = BigDecimal.ZERO;
        for (final MTDate date : cache.allDates) {
            List<Transaction> transactionsForDay = Collections.emptyList();
            List<Trade> tradesForDay = Collections.emptyList();
            if (transactions.containsKey(date)) {
                transactionsForDay = transactions.get(date);
            }
            if (trades.containsKey(date)) {
                tradesForDay = trades.get(date);
            }

            totalSize += sumPositionSizes(transactionsForDay, tradesForDay, date);
            totalNetProfit = totalNetProfit.add(sumProfit(transactionsForDay, tradesForDay, date));
            totalProfitPercent = totalProfitPercent.add(sumProfitPercent(transactionsForDay, tradesForDay, date));
            totalBrokerage = totalBrokerage.add(sumBrokerage(transactionsForDay, tradesForDay, date));


            cache.openedPositionSize.put(date, totalSize);
            cache.totalNetProfit.put(date, totalNetProfit);
            cache.totalProfitPercent.put(date, totalProfitPercent);
            cache.totalBrokerage.put(date, totalBrokerage);
        }

    }

    private BigDecimal sumProfitPercent(final List<Transaction> transactionsForDay,
                                        final List<Trade> tradesForDay,
                                        final MTDate date) {
        BigDecimal result = BigDecimal.ZERO;

        for (final Trade t : tradesForDay) {
            final MTDate dClose = t.getCloseDate();

            if (date.equals(dClose)) {
                final BigDecimal profitPercent = t.getProfitPercent();

                result = result.add(profitPercent);
            }
        }

        return result;
    }

    private BigDecimal sumProfit(final List<Transaction> transactionsForDay,
                                 final List<Trade> tradesForDay,
                                 final MTDate date) {
        BigDecimal result = BigDecimal.ZERO;
        for (final Transaction t : transactionsForDay) {
            result = result.subtract(t.getBrokerage());
        }
        for (final Trade t : tradesForDay) {
            final MTDate dClose = t.getCloseDate();
            final MTDate dOpen = t.getOpenDate();
            if (date.equals(dOpen)) {
                result = result.subtract(t.getOpenBrokerage());
            }

            if (date.equals(dClose)) {
                result = result.add(t.getGrossProfit()).subtract(t.getCloseBrokerage());
            }
        }

        return result;
    }

    private BigDecimal sumBrokerage(final List<Transaction> transactionsForDay,
                                    final List<Trade> tradesForDay,
                                    final MTDate date) {
        BigDecimal result = BigDecimal.ZERO;
        for (final Transaction t : transactionsForDay) {
            result = result.add(t.getBrokerage());
        }
        for (final Trade t : tradesForDay) {
            final MTDate dOpen = t.getOpenDate();
            final MTDate dClose = t.getCloseDate();
            if (date.equals(dClose)) {
                result = result.add(t.getCloseBrokerage());
            }
            if (date.equals(dOpen)) {
                result = result.add(t.getOpenBrokerage());
            }
        }

        return result;
    }

    private int sumPositionSizes(final List<Transaction> transactionsForDay,
                                 final List<Trade> tradesForDay,
                                 final MTDate date) {
        int totalSize = 0;
        for (final Transaction t : transactionsForDay) {
            totalSize += t.getSide().getSizeSigned(t.getSize());
        }
        for (final Trade t : tradesForDay) {
            final MTDate dOpen = t.getOpenDate();
            final MTDate dClose = t.getCloseDate();
            if (dOpen.equals(dClose)) {// open/close during one day
                continue;
            }
            final Side side = t.getOpenSide();
            final int size = side.getSizeSigned(t.getSize());
            if (date.equals(dOpen)) {
                totalSize += size;
            } else {// close
                totalSize -= size;
            }
        }

        return totalSize;
    }

    @Override
    public SortedSet<MTDate> getAllDates() {
        if (cache == null) {
            seal();
        }

        return Collections.unmodifiableSortedSet(cache.allDates);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Trade> getNewOrClosedTradesForDate(final MTDate date) {
        if (trades.containsKey(date)) {
            return Collections.unmodifiableList(trades.get(date));
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Trade> getTrades2(final MTDate date) {
        final Entry<MTDate, List<Trade>> floorEntry = trades.floorEntry(date);
        if (floorEntry == null) {
            return Collections.EMPTY_LIST;
        }
        final List<Trade> tradeList = floorEntry.getValue();
        final ArrayList<Trade> result = new ArrayList<>();
        for (Trade t : tradeList) {
            if (t.getCloseDate().compareTo(date) > 0) {
                result.add(t);
            }
        }


        return result;
    }

    @Override
    public int getOpenedPositionSize(final MTDate date) {
        if (cache == null) {
            seal();
        }

        final Entry<MTDate, Integer> floorEntry = cache.openedPositionSize.floorEntry(date);
        if (floorEntry == null) {
            return 0;
        }
        return floorEntry.getValue();
    }

    @Override
    public MTDate getLastDate() {
        if (cache == null) {
            seal();
        }

        if (cache.allDates.isEmpty()) {
            return null;
        }

        return cache.allDates.last();
    }

    @Override
    public MTDate getFirstDate() {
        if (cache == null) {
            seal();
        }

        if (cache.allDates.isEmpty()) {
            return null;
        }

        return cache.allDates.first();
    }

    @Override
    public BigDecimal getTotalNetProfit(final MTDate date) {
        if (cache == null) {
            seal();
        }

        final Entry<MTDate, BigDecimal> floorEntry = cache.totalNetProfit.floorEntry(date);
        if (floorEntry == null) {
            return BigDecimal.ZERO;
        }
        return floorEntry.getValue();
    }

    @Override
    public BigDecimal getTotalBrokerage(final MTDate date) {
        if (cache == null) {
            seal();
        }

        final Entry<MTDate, BigDecimal> floorEntry = cache.totalBrokerage.floorEntry(date);
        if (floorEntry == null) {
            return BigDecimal.ZERO;
        }
        return floorEntry.getValue();
    }

    @Override
    public List<Trade> getAllTrades() {
        final List<Trade> result = new ArrayList<Trade>(trades.size() * 2);

        final Set<Long> ids = new HashSet<Long>();

        for (final Entry<MTDate, List<Trade>> e : trades.entrySet()) {
            final List<Trade> list = e.getValue();
            for (final Trade t : list) {
                if (!ids.contains(t.getId())) {
                    ids.add(t.getId());
                    result.add(t);
                }
            }
        }

        return result;
    }

    @Override
    public BigDecimal getInitialMoney() {
        return initialMoney;
    }

    @Override
    public BigDecimal getTotalProfitPercent(MTDate date) {
        if (cache == null) {
            seal();
        }

        final Entry<MTDate, BigDecimal> floorEntry = cache.totalProfitPercent.floorEntry(date);
        if (floorEntry == null) {
            return BigDecimal.ZERO;
        }
        return floorEntry.getValue();
    }

    public void setInitialMoney(final BigDecimal initialMoney) {
        this.initialMoney = initialMoney;
    }
}
