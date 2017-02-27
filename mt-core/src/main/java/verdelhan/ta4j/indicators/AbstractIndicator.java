package verdelhan.ta4j.indicators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.TimeSeries;

public abstract class AbstractIndicator<T> implements Indicator<T> {

    /** The logger */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private TimeSeries series;

    /**
     * Constructor.
     * @param series the related time series
     */
    public AbstractIndicator(TimeSeries series) {
        this.series = series;
    }

    @Override
    public TimeSeries getTimeSeries() {
        return series;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}