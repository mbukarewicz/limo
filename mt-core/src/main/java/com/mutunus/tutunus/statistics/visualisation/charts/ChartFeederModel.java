package com.mutunus.tutunus.statistics.visualisation.charts;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.TimeSeries;

import java.util.Arrays;
import java.util.Collection;

public class ChartFeederModel {
    private final TimeSeries timeSeries;
    private final Collection<Indicator<Decimal>> indicators1;
    private final Collection<Indicator<Decimal>> indicators2;

    public ChartFeederModel(TimeSeries timeSeries,
                            Indicator<Decimal> indicators1,
                            Indicator<Decimal> indicators2) {
        this(timeSeries, Arrays.asList(indicators1), Arrays.asList(indicators2));
    }
    public ChartFeederModel(TimeSeries timeSeries,
                            Collection<Indicator<Decimal>> indicators1,
                            Collection<Indicator<Decimal>> indicators2) {
        this.timeSeries = timeSeries;
        this.indicators1 = indicators1;
        this.indicators2 = indicators2;
    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public Collection<Indicator<Decimal>> getFirstIndicators() {
        return indicators1;
    }

    public Collection<Indicator<Decimal>> getSecondIndicators() {
        return indicators2;
    }
}