package com.mutunus.tutunus.structures;

import org.apache.commons.lang3.Range;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;


public class QuotationsImpl implements Quotations, QuotationsExt {

    // public enum QuotationsType {
    // OPEN, HIGH, LOW, CLOSE
    // }

    private final String asset;
    // private final double[] open;
    // private final double[] high;
    // private final double[] low;
    private final double[] close;
    // private final long[] volume;
    private final Quotation[] quotations;
    private final MTDate[] dates;
    private final NavigableMap<MTDate, Integer> dateToId;

    public QuotationsImpl(final String asset, final int size) {
        this.asset = asset;
        // open = new double[size];
        // high = new double[size];
        // low = new double[size];
        close = new double[size];
        // volume = new long[size];
        quotations = new Quotation[size];
        dates = new MTDate[size];
        dateToId = new TreeMap<MTDate, Integer>();
    }

    public void setData(final int index, final MTDate date, final Quotation quotation) {
        if (index < 0) {
            throw new MTException(String.format("Negative index: '%d'", index));
        }
        if (index >= this.quotations.length) {
            throw new MTException(String.format("Too large index '%d', highest allowed is '%d'", index,
                    this.quotations.length - 1));
        }

        // try {
        // Quotation.assertValuesCorrect(open.doubleValue(), high.doubleValue(), low.doubleValue(), close.doubleValue());
        // } catch (final MTException e) {
        // System.out.println("Invalid quotations for date '" + date.toString() + "', reason: " + e.getMessage());
        // throw e;
        // }

        this.dates[index] = date;
        this.quotations[index] = quotation;
        // this.open[index] = open;
        // this.high[index] = high;
        // this.low[index] = low;
        this.close[index] = quotation.getClose();
        // this.volume[index] = volume;
        dateToId.put(date, index);
    }

    /*
     * (non-Javadoc)
     * @see com.mutunus.tutunus.structures.Quotations#getAsset()
     */
    @Override
    public String getAsset() {
        return asset;
    }

    /*
     * (non-Javadoc)
     * @see com.mutunus.tutunus.structures.Quotations#getSize()
     */
    @Override
    public int getSize() {
        return dates.length;
    }

    /*
     * (non-Javadoc)
     * @see com.mutunus.tutunus.structures.Quotations#getIds(com.mutunus.tutunus.structures.MTDate,
     * com.mutunus.tutunus.structures.MTDate)
     */
    @Override
    public Range<Integer> getIds(final MTDate fromDate, final MTDate toDate) {
        final Entry<MTDate, Integer> from = dateToId.ceilingEntry(fromDate);
        final Entry<MTDate, Integer> to = dateToId.floorEntry(toDate);

        if (from != null && to != null) {
            return Range.between(from.getValue(), to.getValue());
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.mutunus.tutunus.structures.Quotations#getQuotation(int)
     */
    @Override
    public Quotation getQuotation(final int dateId) {
        // return new Quotation(open[dateId], high[dateId], low[dateId], close[dateId], volume[dateId]);
        return quotations[dateId];
    }

    /*
     * (non-Javadoc)
     * @see com.mutunus.tutunus.structures.Quotations#getDate(int)
     */
    @Override
    public MTDate getDate(final int dateId) {
        return dates[dateId];
    }

    /*
     * (non-Javadoc)
     * @see com.mutunus.tutunus.structures.Quotations#getDateId(com.mutunus.tutunus.structures.MTDate)
     */
    @Override
    public int getDateId(final MTDate date) {
        final Integer i = dateToId.get(date);
        if (i == null) {
            return MISSING_DATE;
        }
        return i;
    }

    @Override
    public double[] getClosePrices() {
        return close;
    }

}
