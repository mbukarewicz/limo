package com.mutunus.tutunus.structures;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;


@XmlAccessorType(XmlAccessType.NONE)
public class MTDate implements Serializable, Comparable<MTDate>, Cloneable {

    public static final MTDate DAY_ONE = new MTDate(0, 1, 1);
    private static final long serialVersionUID = 1L;
    private static final Set<Integer> IGNORED = new TreeSet<Integer>();

    static {
        IGNORED.add(1 * 100 + 1);
        IGNORED.add(5 * 100 + 1);
        IGNORED.add(5 * 100 + 3);
        IGNORED.add(8 * 100 + 15);
        IGNORED.add(11 * 100 + 1);
        IGNORED.add(11 * 100 + 11);
        IGNORED.add(12 * 100 + 24);
        IGNORED.add(12 * 100 + 25);
        IGNORED.add(12 * 100 + 26);
    }

    // yyyymmdd
    @XmlValue
    private int date;

    public MTDate() {
        this(new Date(System.currentTimeMillis()));
    }

    public MTDate(final int year, final int month, final int day) {
        setAndCheckDate(year, month, day);
    }

    @SuppressWarnings("deprecation")
    public MTDate(final Date d) {
        this(d.getYear() + 1900, d.getMonth() + 1, d.getDate());
    }

    public MTDate(final MTDate d) {
        this(d.getDate());
    }

    private MTDate(final int date) {
        this.date = date;
        assertMonthDayCorrect(getYear(), getMonth(), getDay());
    }

    private void setAndCheckDate(final int year, final int month, final int day) {
        date = createDate(year, month, day);
        assertMonthDayCorrect(year, month, day);
    }

    private int getDate() {
        return date;
    }

    // public void setDate(int newDate) {
    // date = newDate;
    // }

    private static int createDate(final int year, final int month, final int day) {
        return year * 100 * 100 + month * 100 + day;
    }

    private static void assertMonthDayCorrect(final int y, final int m, final int d) {
        if (m < 1 || m > 12) {
            throw new MTException("Invalid month '" + m + "'");
        }
        final int maxDaysInMonth = getDaysInMonthNoCheck(m, y);
        if (d < 1 || d > maxDaysInMonth) {
            throw new MTException("Invalid number of days '" + d + "' for month '" + m + "' and year '" + y + "' "
                    + " - must be between '0' and '" + maxDaysInMonth + "'");
        }
    }

    /**
     * Return format is: yyyy-MM-dd
     */
    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", getYear(), getMonth(), getDay());
    }

    // output: dd-MM-yy
    // public String toOnetString() {
    // int year = getYear();
    // int onetYear = year - 2000;
    // if (year < 2000) {
    // onetYear = year - 1900;
    // }
    // return UtilsClient.leadWithZero(getDay())
    // + "-" + UtilsClient.leadWithZero(getMonth())
    // + "-" + UtilsClient.leadWithZero(onetYear);
    // }

    // output: dd.MM.yyyy
    // public String toOnetStringCurrency() {
    // return UtilsClient.leadWithZero(getDay())
    // + "." + UtilsClient.leadWithZero(getMonth())
    // + "." + UtilsClient.leadWithZero(getYear());
    // }

    /**
     * Input must be follow this pattern yyyy-MM-dd, e.g. 2005-03-26
     *
     * @return
     */
    public static MTDate parse(final String date) {
        if (date.length() != 10 || date.charAt(4) != '-' || date.charAt(7) != '-') {
            throw new MTException("Invalid date '" + date + "' to parse.");
        }
        final String[] yyyyMMdd = date.trim().split("-");
        final int year = Integer.parseInt(yyyyMMdd[0]);
        final int month = Integer.parseInt(yyyyMMdd[1]);
        final int day = Integer.parseInt(yyyyMMdd[2]);

        return new MTDate(year, month, day);
    }

    /**
     * Input must be follow this pattern dd-MM-yyyy, e.g. 10-15-2009
     *
     * @return
     */
    public static MTDate parseDdMMYyyy(final String date) {
        if (date.length() != 10 || date.charAt(2) != '-' || date.charAt(5) != '-') {
            throw new MTException("Invalid date '" + date + "' to parse.");
        }
        final String[] ddMMyyyy = date.trim().split("-");
        final int year = Integer.parseInt(ddMMyyyy[2]);
        final int month = Integer.parseInt(ddMMyyyy[1]);
        final int day = Integer.parseInt(ddMMyyyy[0]);

        return new MTDate(year, month, day);
    }

    public static boolean isIgnored(final MTDate date) {
        final int monthDay = date.getDate() % (100 * 100);

        return IGNORED.contains(monthDay);
    }

    public void addMonths(final int change) {
        int year = getYear();
        int month = getMonth();
        int day = getDay();
        final int oldMaxDaysInMonth = getDaysInMonth(month, year);

        final int fullYears = change / 12;
        year += fullYears;
        month += change % 12;

        if (month > 12) {
            year++;
            month -= 12;
        } else if (month <= 0) {
            year--;
            month += 12;
        }

        final int newMaxDaysInMonth = getDaysInMonth(month, year);
        if (newMaxDaysInMonth < oldMaxDaysInMonth && day > newMaxDaysInMonth) {
            day = newMaxDaysInMonth;
        }

        setAndCheckDate(year, month, day);
    }

    public void addDays(final int change) {
        if (change > 0) {
            addDaysPositive(change);
        } else {
            addDaysNegative(change);
        }
    }

    private void addDaysNegative(final int change) {
        int year = getYear();
        int month = getMonth();
        int day = getDay();

        int changeLeft = change * (-1);
        while (changeLeft > 0) {
            if (day > changeLeft) {
                day -= changeLeft;
                changeLeft = 0;
            } else {
                changeLeft -= (day);
                day = 1;

                // TODO: glupie - najpierw ustawiam date zeby za chwile odczytac
                setAndCheckDate(year, month, day);
                addMonths(-1);
                year = getYear();
                month = getMonth();
                final int lastDay = getDaysInMonthNoCheck(month, year);
                day = lastDay;
            }
        }

        setAndCheckDate(year, month, day);
    }

    private void addDaysPositive(final int change) {
        int year = getYear();
        int month = getMonth();
        int day = getDay();

        int changeLeft = change;
        while (changeLeft > 0) {
            final int daysInMonth = getDaysInMonthNoCheck(month, year);
            final int daysToEndOfMonth = daysInMonth - day;

            if (daysToEndOfMonth >= changeLeft) {
                day += changeLeft;
                changeLeft = 0;
            } else {
                day = 1;
                changeLeft -= (daysToEndOfMonth + 1);
                // TODO: glupie - najpierw ustawiam date zeby za chwile odczytac
                setAndCheckDate(year, month, day);
                addMonths(1);
                year = getYear();
                month = getMonth();
            }
        }

        setAndCheckDate(year, month, day);
    }

    public int getYear() {
        return date / (100 * 100);
    }

    public int getMonth() {
        return (date / 100) % 100;
    }

    public int getDay() {
        return date % 100;
    }

    public void setDay(final int day) {
        setAndCheckDate(getYear(), getMonth(), day);
    }

    public void setDayMonth(final int day, final int month) {
        setAndCheckDate(getYear(), month, day);
    }

    @SuppressWarnings("deprecation")
    public Date toDate() {
        return new Date(getYear() - 1900, getMonth() - 1, getDay());
    }

    public static MTDate today() {
        return new MTDate(new Date());
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof MTDate && equals((MTDate) other));
    }

    private boolean equals(final MTDate second) {
        return date == second.getDate();
    }

    @Override
    public int hashCode() {
        return date;
    }

    @Override
    public int compareTo(final MTDate d2) {
        return date - d2.date;
    }

    public int toIntYearMonth() {
        return date / 100;
    }

    public int toIntYearMonthDay() {
        // return year * 10000 + month * 100 + day;
        return date;
    }

    // TODO: zaokragla lata do 365 miesiace do 30
    // obecny wiekszy niz drugi
    public int diff(final MTDate other) {
        if (other.compareTo(this) > 0) {
            return other.diff(this);
        }

        int dYear = getYear() - other.getYear();
        int dMonth = getMonth() - other.getMonth();
        int dDay = getDay() - other.getDay();

        if (dDay < 0) {
            dMonth--;
            dDay += 30;
        }
        if (dMonth < 0) {
            dYear--;
            dMonth += 12;
        }

        return dYear * 365 + dMonth * 30 + dDay;
    }

    public static int getDaysInMonth(final int m, final int y) {
        assertMonthDayCorrect(y, m, 1);
        return getDaysInMonthNoCheck(m, y);
    }

    private static int getDaysInMonthNoCheck(final int m, final int y) {
        switch (m) {
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (y % 4 == 0 && y % 100 != 0 || y % 400 == 0) {
                    return 29;
                }
                return 28;

            default:
                return 31;
        }
    }

    public boolean isWorking() {
        return !(isIgnored(this) || isWeekend());
    }

    public boolean isWeekend() {
        final int dayOfWeek = toDate().getDay();
        return dayOfWeek == 0 || dayOfWeek == 6;
    }

    public static int getDayOfWeek(final MTDate date) {
        final int dayOfWeek = date.toDate().getDay();
        return dayOfWeek;
    }

    public static MTDate parseIntDateYyyyMmDd(final int yyyyMmdd) {
        return new MTDate(yyyyMmdd);
    }

    @Override
    public MTDate clone() {
        return new MTDate(this);
    }
}
