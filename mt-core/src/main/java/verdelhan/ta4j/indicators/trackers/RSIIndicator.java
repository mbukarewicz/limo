package verdelhan.ta4j.indicators.trackers;

import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.indicators.CachedIndicator;
import verdelhan.ta4j.indicators.helpers.AverageGainIndicator;
import verdelhan.ta4j.indicators.helpers.AverageLossIndicator;

import static verdelhan.ta4j.Decimal.*;

/**
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:relative_strength_index_rsi
 */
public class RSIIndicator extends CachedIndicator<Decimal> {

    private AverageGainIndicator averageGainIndicator;

    private AverageLossIndicator averageLossIndicator;

    private final int timeFrame;

    public RSIIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator.getTimeSeries());
//        super(indicator);
        this.timeFrame = timeFrame;
        averageGainIndicator = new AverageGainIndicator(indicator, timeFrame);
        averageLossIndicator = new AverageLossIndicator(indicator, timeFrame);
    }

    /**
     *                100
     *  RSI = 100 - --------
     *               1 + RS
     *
     * RS = Average Gain / Average Loss
     * @param index
     * @return
     */
//    @Override
    protected Decimal calculate(int index) {
        Decimal rs = relativeStrength(index);
        if (rs.isNaN()) {
            return HUNDRED;
        }
        return HUNDRED.minus(HUNDRED.dividedBy(ONE.plus(rs)));
    }

    @Override
    public String toString() {
        return super.toString() + " timeFrame(" + timeFrame + ")";
    }

    /**
     * @param index
     * @return the relative strength
     */
    private Decimal relativeStrength(int index) {
        if (index == 0) {
            return ZERO;
        }
        Decimal averageGain = averageGainIndicator.getValue(index);
        Decimal averageLoss = averageLossIndicator.getValue(index);
        return averageGain.dividedBy(averageLoss);
    }

}
