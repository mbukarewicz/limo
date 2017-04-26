package com.mutunus.tutunus.structures;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class Transaction {

    private static final String DEF_COMMENT = "---";

    private static long NEXT_ID = 1;

    private final long id;
    private final Side side;
    private final BigDecimal price;
    private final MTDate date;
    private final BigDecimal brokerage;
    private final int size;
    private final int tickId;
    private final String comment;

    public Transaction(final int tickId,
                       final Side side,
                       final int size,
                       final BigDecimal price,
                       final BigDecimal brokerage,
                       final MTDate date,
                       final long id,
                       final String comment) {
        this.tickId = tickId;
        this.comment = comment;
        if (size <= 0) {
            throw new RuntimeException("Invalid size: " + size);
        }
        if (price.signum() != 1) {
            throw new RuntimeException("Invalid price: " + price);
        }
        if (brokerage.signum() == -1) {
            throw new RuntimeException("Invalid brokerage: " + brokerage);
        }
        if (comment == null) {
            throw new RuntimeException("Comment cannot be null");
        }

        this.side = side;
        this.size = size;
        this.price = price;
        this.brokerage = brokerage;
        this.date = date;
        this.id = id;
    }

    public Transaction(final int tickId, final Side side, final int size, final BigDecimal price, final BigDecimal brokerage, final MTDate date) {
        this(tickId, side, size, price, brokerage, date, getNextId(), DEF_COMMENT);
    }

    public Transaction(final int tickId, final Side side, final int size, final double price, final double brokerage, final MTDate date) {
        this(tickId, side, size, new BigDecimal(Double.toString(price)), new BigDecimal(Double.toString(brokerage)), date,
                getNextId(), DEF_COMMENT);
    }

    public Transaction(final int tickId,
                       final Side side,
                       final int size,
                       final BigDecimal price,
                       final BigDecimal brokerage,
                       final MTDate date,
                       final String comment) {
        this(tickId, side, size, price, brokerage, date, getNextId(), comment);
    }

    public Transaction(final int tickId,
                       final Side side,
                       final int size,
                       final double price,
                       final double brokerage,
                       final MTDate date,
                       final String comment) {
        this(tickId, side, size, new BigDecimal(Double.toString(price)), new BigDecimal(Double.toString(brokerage)), date,
                getNextId(), comment);
    }

    public static synchronized long getNextId() {
        return ++NEXT_ID;
    }

    public Transaction getPart(final int reqSize) {
        if (reqSize <= 0 || reqSize > this.size) {
            throw new MTException(String.format("Cannot compute part of size '%d' from object of size '%d'", reqSize,
                    this.size));
        }

        if (reqSize == size) {
            return this;
        }

        final BigDecimal brokerageNew = getBrokerageForSize(reqSize);

        final Transaction posNew = new Transaction(tickId, side, reqSize, price, brokerageNew, date);
        return posNew;
    }

    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public Side getSide() {
        return side;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MTDate getDate() {
        return date;
    }

    public int getSize() {
        return size;
    }

    protected BigDecimal getBrokerageForSize(final int reqSize) {
        final BigDecimal m = new BigDecimal(reqSize).divide(new BigDecimal(size), 5, RoundingMode.HALF_EVEN);
        final BigDecimal brokerageNew = brokerage.multiply(m);
        return brokerageNew;
    }

    public String getComment() {
        return comment;
    }

    public int getTickId() {
        return tickId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "side=" + side +
                ", date=" + date +
                ", tickId=" + tickId +
                '}';
    }
}
