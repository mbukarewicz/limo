package com.mutunus.tutunus.research.indicators;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.indicators.CachedIndicator;
import verdelhan.ta4j.indicators.helpers.HighestValueIndicator;

public class WilliamsVixFixIndicator extends CachedIndicator<Decimal> {

    private final HighestValueIndicator highestHigh;

    public WilliamsVixFixIndicator(Indicator priceIndicator) {
        super(priceIndicator);
        highestHigh = new HighestValueIndicator(priceIndicator, 22);
    }

    @Override
    protected Decimal calculate(int tickId) {
        final Tick tick = getTimeSeries().getTick(tickId);

        final Decimal highestValue = highestHigh.getValue(tickId);
        Decimal vix = highestValue.minus(tick.getLowPrice());
        vix = vix.dividedBy(highestValue);
        vix = vix.multipliedBy(Decimal.HUNDRED);

        return vix;
    }
}