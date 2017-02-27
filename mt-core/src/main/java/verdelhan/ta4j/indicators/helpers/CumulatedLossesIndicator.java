package verdelhan.ta4j.indicators.helpers;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.indicators.CachedIndicator;

public class CumulatedLossesIndicator extends CachedIndicator<Decimal> {

    private final Indicator<Decimal> indicator;

    private final int timeFrame;

    public CumulatedLossesIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator);
        this.indicator = indicator;
        this.timeFrame = timeFrame;
    }

    @Override
    protected Decimal calculate(int index) {
        Decimal sumOfLosses = Decimal.ZERO;
        for (int i = Math.max(1, index - timeFrame + 1); i <= index; i++) {
            if (indicator.getValue(i).isLessThan(indicator.getValue(i - 1))) {
                sumOfLosses = sumOfLosses.plus(indicator.getValue(i - 1).minus(indicator.getValue(i)));
            }
        }
        return sumOfLosses;
    }
}