/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014-2016 Marc de Verdelhan & respective authors (see AUTHORS)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package verdelhan.ta4j;


import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

/**
 * End tick of a time period.
 * <p>
 */
public class Tick {

    private static final String CSV_DELIM = ",";
    public static final String CSV_LINE_FORMAT = "%s" + CSV_DELIM
            + "%s" + CSV_DELIM
            + "%s" + CSV_DELIM
            + "%s" + CSV_DELIM
            + "%s" + CSV_DELIM
            + "%d";
    /** Time period (e.g. 1 day, 15 min, etc.) of the tick */
    private Period timePeriod;
    /** End time of the tick */
    private DateTime endTime;
    private LocalDate endDate;
    /** Begin time of the tick */
    private DateTime beginTime;
    /** Open price of the period */
    private Decimal openPrice = null;
    /** Close price of the period */
    private Decimal closePrice = null;
    /** Max price of the period */
    private Decimal maxPrice = null;
    /** Min price of the period */
    private Decimal lowPrice = null;
    /** Traded amount during the period */
    private Decimal amount = Decimal.ZERO;
    /** Volume of the period */
    private Decimal volume = Decimal.ZERO;
    /** Trade count */
    private int trades = 0;

    /**
     * Constructor.
     * @param timePeriod the time period
     * @param endTime the end time of the tick period
     */
    public Tick(Period timePeriod, DateTime endTime) {
        checkTimeArguments(timePeriod, endTime);
        this.timePeriod = timePeriod;
        this.endTime = endTime;
        this.endDate = LocalDate.of(endTime.getYear(), endTime.getMonthOfYear(), endTime.getDayOfMonth());
        this.beginTime = endTime.minus(timePeriod);
        this.endDate = LocalDate.of(endTime.getYear(), endTime.getMonthOfYear(), endTime.getDayOfMonth());
    }

    /**
     * Constructor.
     * @param endTime the end time of the tick period
     * @param openPrice the open price of the tick period
     * @param highPrice the highest price of the tick period
     * @param lowPrice the lowest price of the tick period
     * @param closePrice the close price of the tick period
     * @param volume the volume of the tick period
     */
    public Tick(DateTime endTime, double openPrice, double highPrice, double lowPrice, double closePrice, double volume) {
        this(endTime, Decimal.valueOf(openPrice),
                Decimal.valueOf(highPrice),
                Decimal.valueOf(lowPrice),
                Decimal.valueOf(closePrice),
                Decimal.valueOf(volume));
    }

    /**
     * Constructor.
     * @param endTime the end time of the tick period
     * @param openPrice the open price of the tick period
     * @param highPrice the highest price of the tick period
     * @param lowPrice the lowest price of the tick period
     * @param closePrice the close price of the tick period
     * @param volume the volume of the tick period
     */
    public Tick(DateTime endTime, String openPrice, String highPrice, String lowPrice, String closePrice, String volume) {
        this(endTime, Decimal.valueOf(openPrice),
                Decimal.valueOf(highPrice),
                Decimal.valueOf(lowPrice),
                Decimal.valueOf(closePrice),
                Decimal.valueOf(volume));
    }

    /**
     * Constructor.
     * @param endTime the end time of the tick period
     * @param openPrice the open price of the tick period
     * @param highPrice the highest price of the tick period
     * @param lowPrice the lowest price of the tick period
     * @param closePrice the close price of the tick period
     * @param volume the volume of the tick period
     */
    public Tick(DateTime endTime, Decimal openPrice, Decimal highPrice, Decimal lowPrice, Decimal closePrice, Decimal volume) {
        this(Period.days(1), endTime, openPrice, highPrice, lowPrice, closePrice, volume);
    }

    /**
     * Constructor.
     * @param timePeriod the time period
     * @param endTime the end time of the tick period
     * @param openPrice the open price of the tick period
     * @param highPrice the highest price of the tick period
     * @param lowPrice the lowest price of the tick period
     * @param closePrice the close price of the tick period
     * @param volume the volume of the tick period
     */
    public Tick(Period timePeriod, DateTime endTime, Decimal openPrice, Decimal highPrice, Decimal lowPrice, Decimal closePrice, Decimal volume) {
        checkTimeArguments(timePeriod, endTime);
        this.timePeriod = timePeriod;
        this.endTime = endTime;
        this.endDate = LocalDate.of(endTime.getYear(), endTime.getMonthOfYear(), endTime.getDayOfMonth());
        this.beginTime = endTime.minus(timePeriod);
        this.openPrice = openPrice;
        this.maxPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
    }

    /**
     * @return the close price of the period
     */
    public Decimal getClosePrice() {
        return closePrice;
    }

    /**
     * @return the open price of the period
     */
    public Decimal getOpenPrice() {
        return openPrice;
    }

    /**
     * @return the number of trades in the period
     */
    public int getTrades() {
        return trades;
    }

    /**
     * @return the max price of the period
     */
    public Decimal getMaxPrice() {
        return maxPrice;
    }

    /**
     * @return the whole traded amount of the period
     */
    public Decimal getAmount() {
        return amount;
    }

    /**
     * @return the whole traded volume in the period
     */
    public Decimal getVolume() {
        return volume;
    }

    /**
     * @return the min price of the period
     */
    public Decimal getLowPrice() {
        return lowPrice;
    }

    /**
     * @return the time period of the tick
     */
    public Period getTimePeriod() {
        return timePeriod;
    }

    /**
     * @return the begin timestamp of the tick period
     */
    public DateTime getBeginTime() {
        return beginTime;
    }

    /**
     * @return the end timestamp of the tick period
     */
    public DateTime getEndTime() {
        return endTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return String.format("[time: %1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS, close price: %2$f]",
                endTime.toGregorianCalendar(), closePrice.toDouble());
    }

    /**
     * @param timestamp a timestamp
     * @return true if the provided timestamp is between the begin time and the end time of the current period, false otherwise
     */
    public boolean inPeriod(DateTime timestamp) {
        return timestamp != null
                && !timestamp.isBefore(beginTime)
                && timestamp.isBefore(endTime);
    }

    /**
     * @return true if this is a bearish tick, false otherwise
     */
    public boolean isBearish() {
        return (openPrice != null) && (closePrice != null) && closePrice.isLessThan(openPrice);
    }

    /**
     * @return true if this is a bullish tick, false otherwise
     */
    public boolean isBullish() {
        return (openPrice != null) && (closePrice != null) && openPrice.isLessThan(closePrice);
    }

    /**
     * @return a human-friendly string of the end timestamp
     */
    public String getDateName() {
        return endTime.toString("hh:mm dd/MM/yyyy");
    }

    /**
     * @return a even more human-friendly string of the end timestamp
     */
    public String getSimpleDateName() {
        return endTime.toString("yyyy-MM-dd");
    }

    /**
     * @param timePeriod the time period
     * @param endTime the end time of the tick
     * @throws IllegalArgumentException if one of the arguments is null
     */
    private void checkTimeArguments(Period timePeriod, DateTime endTime) throws IllegalArgumentException {
        if (timePeriod == null) {
            throw new IllegalArgumentException("Time period cannot be null");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
    }

    public boolean isLower(final Tick other) {
        Decimal otherClosePrice = other.getClosePrice();
        return closePrice.isLessThan(otherClosePrice);
    }

    public String toCsvLine() {
        final String lastTickDate = getSimpleDateName();
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern("0.####");

        final String o = df.format(openPrice.toDouble());
        final String h = df.format(maxPrice.toDouble());
        final String l = df.format(lowPrice.toDouble());
        final String c = df.format(closePrice.toDouble());
        final String result = String.format(Locale.US, CSV_LINE_FORMAT, lastTickDate,
                o, h, l, c, (long) volume.toDouble());

        return result;
    }

    public static void main(String[] args) {
        System.out.printf("%.2f: Default locale\n", 3.1415926535);
        System.out.printf(Locale.GERMANY, "%.2f: Germany locale\n", 3.1415926535);
        System.out.printf(Locale.US, "%.2f: US locale\n", 3.1415926535);


//        final DecimalFormat df = new DecimalFormat("0.####");
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern("0.####");
        final String o = df.format(3.1415926535);
        System.out.println("df: " + o);
    }

}
