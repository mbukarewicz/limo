package verdelhan.ta4j.indicators.helpers;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.indicators.CachedIndicator;

public class AverageLossIndicator extends CachedIndicator<Decimal> {

    private final CumulatedLossesIndicator cumulatedLosses;

    private final int timeFrame;

    public AverageLossIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator);
        this.cumulatedLosses = new CumulatedLossesIndicator(indicator, timeFrame);
        this.timeFrame = timeFrame;
    }

    @Override
    protected Decimal calculate(int index) {
        final int realTimeFrame = Math.min(timeFrame, index + 1);
        return cumulatedLosses.getValue(index).dividedBy(Decimal.valueOf(realTimeFrame));
    }
}