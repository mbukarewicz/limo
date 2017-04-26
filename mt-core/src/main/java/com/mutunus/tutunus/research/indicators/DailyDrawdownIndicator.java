package com.mutunus.tutunus.research.indicators;

import com.mutunus.tutunus.structures.TradingRegister;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.CachedIndicator;
import verdelhan.ta4j.indicators.helpers.HighestValueIndicator;

public class DailyDrawdownIndicator extends CachedIndicator<Decimal> {

    final HighestValueIndicator highestValueIndicator;
    final TradingValuationWithDrawdownIndicator tradingValuationWithDrawdownIndicator;

    public DailyDrawdownIndicator(
            TimeSeries series,
            TradingRegister tradingRegister) {
        this(series, tradingRegister, new TradingValuationIndicator(series, tradingRegister));
    }

    public DailyDrawdownIndicator(
            TimeSeries series,
            TradingRegister tradingRegister,
            TradingValuationIndicator tradingValuationIndicator) {
        super(series);
        final TradingValuationIndicator valuationIndicator = tradingValuationIndicator;
        highestValueIndicator = new HighestValueIndicator(valuationIndicator, Integer.MAX_VALUE);
        tradingValuationWithDrawdownIndicator = new TradingValuationWithDrawdownIndicator(series, tradingRegister);
    }

    @Override
    protected Decimal calculate(int index) {
        final Decimal pastHigh = highestValueIndicator.getValue(index - 1);
        final Decimal todaysLowValuation = tradingValuationWithDrawdownIndicator.getValue(index);

        Decimal todaysDrawdown = pastHigh.minus(todaysLowValuation);
        todaysDrawdown = todaysDrawdown.max(Decimal.ZERO);
        return todaysDrawdown;
    }

}
