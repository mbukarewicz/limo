package verdelhan.ta4j.indicators.simple;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.CachedIndicator;

/**
 * Maximum price indicator.
 * <p>
 */
public class MaxPriceIndicator extends CachedIndicator<Decimal> {

    private TimeSeries series;

    public MaxPriceIndicator(TimeSeries series) {
        super(series);
        this.series = series;
    }

    @Override
    protected Decimal calculate(int index) {
        return series.getTick(index).getMaxPrice();
    }
}
