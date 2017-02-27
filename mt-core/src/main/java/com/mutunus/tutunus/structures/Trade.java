package com.mutunus.tutunus.structures;

import java.math.BigDecimal;


public class Trade {

    private static long NEXT_ID = 1;
    private final long id;

    private final Transaction open;
    private final Transaction close;

    public Trade(final Transaction open, final Transaction close) {
        assertInputOk(open, close);
        this.open = open;
        this.close = close;
        id = getNextId();
    }

    public void assertInputOk(final Transaction open, final Transaction close) {
        if (open.getSize() != close.getSize()) {
            throw new RuntimeException(String.format("Open size '%d' does not match close size '%d'", open.getSize(),
                    close.getSize()));
        }
        if (open.getSide().equals(close.getSide())) {
            throw new RuntimeException("Open and close sides must differ: " + open.getSide());
        }
        if (open.getDate().compareTo(close.getDate()) > 0) {
            throw new RuntimeException(String.format("Open date '%s' must not exceed close date '%s'", open.getDate()
                    .toString(), close.getDate().toString()));
        }
    }

    public static synchronized long getNextId() {
        return NEXT_ID++;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getProfitPercent() {
        final BigDecimal netProfit = getProfit();
        final BigDecimal openPrice = getOpenPrice();
        final BigDecimal profitPercent = BigDecimal.valueOf(100).multiply(netProfit).divide(openPrice, BigDecimal.ROUND_HALF_UP);

        return profitPercent;
    }

    public BigDecimal getProfit() {
        final BigDecimal grossProfit = getGrossProfit();
        BigDecimal profit = grossProfit.subtract(open.getBrokerage()).subtract(close.getBrokerage());
        return profit;
    }

    public BigDecimal getGrossProfit() {
        BigDecimal profit = calculateProfit();
        return profit;
    }

    // p 1000/1.5 k
    // t 1000/2.0 s
    private BigDecimal calculateProfit() {
        final BigDecimal dSize = new BigDecimal(open.getSize());
        final BigDecimal dPrice = close.getPrice().subtract(open.getPrice());
        final int mulitplier = open.getSide().getModifier() * Side.REVERT;

        final BigDecimal profit = dSize.multiply(dPrice).multiply(new BigDecimal(mulitplier));
        return profit;
    }

    public MTDate getOpenDate() {
        return open.getDate();
    }

    public MTDate getCloseDate() {
        return close.getDate();
    }

    public int getSize() {
        return open.getSize();
    }

    public Side getOpenSide() {
        return open.getSide();
    }

    public BigDecimal getCloseBrokerage() {
        return close.getBrokerage();
    }

    public BigDecimal getOpenBrokerage() {
        return open.getBrokerage();
    }

    public BigDecimal getOpenPrice() {
        return open.getPrice();
    }

    public BigDecimal getClosePrice() {
        return close.getPrice();
    }

    public String getCloseComment() {
        return close.getComment();
    }

    @Override
    public String toString() {
        return "Trade{" +
                "open=" + open +
                ", close=" + close +
                '}';
    }
}
