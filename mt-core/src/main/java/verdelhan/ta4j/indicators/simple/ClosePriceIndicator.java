package verdelhan.ta4j.indicators.simple;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.CachedIndicator;

public class ClosePriceIndicator extends CachedIndicator<Decimal> {

    private TimeSeries series;

    public ClosePriceIndicator(TimeSeries series) {
        super(series);
        this.series = series;
    }

    @Override
    protected Decimal calculate(int index) {
        return series.getTick(index).getClosePrice();
    }
}