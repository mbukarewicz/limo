package verdelhan.ta4j.indicators.trackers;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.indicators.CachedIndicator;

/**
 * Simple moving average (SMA) indicator.
 * <p>
 */
public class SMAIndicator extends CachedIndicator<Decimal> {

    private final Indicator<Decimal> indicator;

    private final int timeFrame;

    public SMAIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator);
        this.indicator = indicator;
        this.timeFrame = timeFrame;
    }

    @Override
    protected Decimal calculate(int index) {
        Decimal sum = Decimal.ZERO;
        for (int i = Math.max(0, index - timeFrame + 1); i <= index; i++) {
            sum = sum.plus(indicator.getValue(i));
        }

        final int realTimeFrame = Math.min(timeFrame, index + 1);
        return sum.dividedBy(Decimal.valueOf(realTimeFrame));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " timeFrame: " + timeFrame;
    }

}