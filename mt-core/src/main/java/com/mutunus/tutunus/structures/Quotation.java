package com.mutunus.tutunus.structures;

import java.io.Serializable;
import java.math.BigDecimal;


// @PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Quotation implements Serializable {

    private static final int MULTIPLICAND = 100000;
    private static final BigDecimal DOUBLE_2_LONG = BigDecimal.valueOf(MULTIPLICAND);

    private static final long serialVersionUID = 1L;

    // public static final Quotation NULL_QUOTATION = new Quotation(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0l);
    // @SuppressWarnings("unused")
    // @PrimaryKey
    // @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    // private Key key;

    // @Persistent
    // @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
    private final long close;

    // @Persistent
    // @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
    private final long low;

    // @Persistent
    // @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
    private final long high;

    // @Persistent
    // @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
    private final long open;

    // @Persistent
    // @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
    private final long volume;

    // public Quotation() {
    // this(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0L);
    // }

    public Quotation(final long open, final long high, final long low, final long close, final long volume) {
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.volume = volume;

        // if (Double.isNaN(close) && (!Double.isNaN(open) && !Double.isNaN(high) && !Double.isNaN(low))) {
        // throw new MTException("Close can not be NaN while other fields have proper values! - " + toString());
        // }
        //
        // if (Double.isNaN(open)) {
        // this.open = close;
        // }
        // if (Double.isNaN(high)) {
        // this.high = close;
        // }
        // if (Double.isNaN(low)) {
        // this.low = close;
        // }
        //
        // assertValuesCorrect(this.open, this.high, this.low, this.close);
    }

    // public Quotation(final Quotation q) {
    // this(q.getOpen(), q.getHigh(), q.getLow(), q.getClose(), q.getVolume());
    // }

    public static void assertValuesCorrect(final double open, final double high, final double low, final double close) {
        if (Double.isNaN(close) && !(Double.isNaN(open) || Double.isNaN(high) || Double.isNaN(low))) {
            throw new MTException("Close can not be NaN while other fields have proper values! - " + " " + open + " " + high
                    + " " + low + " " + close);
        }

        if (low > high) {
            throw new MTException("Low '" + low + "' must not be greater than high '" + high + "'");
        }
        if (low > open) {
            throw new MTException("Open '" + open + "' must not be lower than low '" + low + "'");
        }
        if (low > close) {
            throw new MTException("Close '" + close + "' must not be lower than low '" + low + "'");
        }
        if (open > high) {
            throw new MTException("Open '" + open + "' must not be greater than high '" + high + "'");
        }
        if (close > high) {
            throw new MTException("Close '" + close + "' must not be greater than high '" + high + "'");
        }
    }

    // public void setLow(final double low) {
    // if (high != Double.NaN) {
    // assertValuesCorrect(open, high, low, close);
    // this.low = low;
    // } else {
    // assertValuesCorrect(open, low, low, close);
    // this.low = low;
    // this.high = low;
    // }
    // }

    private double toDouble(final long value) {
        final BigDecimal r = BigDecimal.valueOf(value).divide(DOUBLE_2_LONG);
        return r.doubleValue();
    }

    public double getLow() {
        return toDouble(low);
    }

    // public void setHigh(final double high) {
    // if (low != Double.NaN) {
    // assertValuesCorrect(open, high, low, close);
    // this.high = high;
    // } else {
    // assertValuesCorrect(open, high, high, close);
    // this.low = high;
    // this.high = high;
    // }
    //
    // }

    public double getHigh() {
        return toDouble(high);
    }

    // public void setVolume(final long volume) {
    // this.volume = volume;
    // }

    public long getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return getOpen() + " " + getHigh() + " " + getLow() + " " + getClose() + " " + volume;
    }

    // public void setClose(final double close) {
    // this.close = close;
    // }

    public double getClose() {
        return toDouble(close);
    }

    // public void setOpen(final double open) {
    // this.open = open;
    // }

    public double getOpen() {
        return toDouble(open);
    }

    // public Double getRange() {
    // return MathUtils.computeRange(low, high);
    // }

    @Override
    public boolean equals(final Object o2) {
        if (!(o2 instanceof Quotation)) {
            return false;
        }

        final Quotation q2 = (Quotation) o2;
        return open == q2.open //
                && high == q2.high //
                && low == q2.low //
                && close == q2.close //
                && volume == q2.volume;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(close).hashCode();
    }

    public static Quotation newInstance(final String _open,
                                        final String _high,
                                        final String _low,
                                        final String _close,
                                        final String _volume) {

        final BigDecimal open = new BigDecimal(_open).multiply(DOUBLE_2_LONG);
        final BigDecimal high = new BigDecimal(_high).multiply(DOUBLE_2_LONG);
        final BigDecimal low = new BigDecimal(_low).multiply(DOUBLE_2_LONG);
        final BigDecimal close = new BigDecimal(_close).multiply(DOUBLE_2_LONG);
        final long vol = Long.parseLong(_volume);

        // TODO: moze byc blad w danych,, lapac wyjatek i go obslugiwac
        final Quotation q =
                new Quotation(open.longValueExact(), high.longValueExact(), low.longValueExact(), close.longValueExact(), vol);

        return q;
    }

}
