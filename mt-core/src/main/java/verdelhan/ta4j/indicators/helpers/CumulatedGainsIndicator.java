package verdelhan.ta4j.indicators.helpers;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.indicators.CachedIndicator;

public class CumulatedGainsIndicator extends CachedIndicator<Decimal> {

    private final Indicator<Decimal> indicator;

    private final int timeFrame;

    public CumulatedGainsIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator);
        this.indicator = indicator;
        this.timeFrame = timeFrame;
    }

    @Override
    protected Decimal calculate(int index) {
        Decimal sumOfGains = Decimal.ZERO;
        for (int i = Math.max(1, index - timeFrame + 1); i <= index; i++) {
            if (indicator.getValue(i).isGreaterThan(indicator.getValue(i - 1))) {
                sumOfGains = sumOfGains.plus(indicator.getValue(i).minus(indicator.getValue(i - 1)));
            }
        }
        return sumOfGains;
    }
}