package com.mutunus.tutunus.structures;

import org.apache.commons.lang3.Range;


public interface Quotations {

    int MISSING_DATE = -1;

    String getAsset();

    int getSize();

    Range<Integer> getIds(final MTDate fromDate, final MTDate toDate);

    Quotation getQuotation(final int dateId);

    MTDate getDate(final int dateId);

    int getDateId(final MTDate date);

}
