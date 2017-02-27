package verdelhan.ta4j.indicators.trackers;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.indicators.RecursiveCachedIndicator;

/**
 * Exponential moving average indicator.
 * <p>
 */
public class EMAIndicator extends RecursiveCachedIndicator<Decimal> {

    private final Indicator<Decimal> indicator;

    private final int timeFrame;

    private final Decimal multiplier;

    /**
     * Constructor.
     *
     * @param indicator an indicator
     * @param timeFrame the EMA time frame
     */
    public EMAIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator);
        this.indicator = indicator;
        this.timeFrame = timeFrame;
        multiplier = Decimal.TWO.dividedBy(Decimal.valueOf(timeFrame + 1));
    }

    @Override
    protected Decimal calculate(int index) {
        if (index + 1 < timeFrame) {
            // Starting point of the EMA
            return new SMAIndicator(indicator, timeFrame).getValue(index);
        }
        if (index == 0) {
            // If the timeframe is bigger than the indicator's value count
            return indicator.getValue(0);
        }
        Decimal emaPrev = getValue(index - 1);
        return indicator.getValue(index).minus(emaPrev).multipliedBy(multiplier).plus(emaPrev);
    }
}