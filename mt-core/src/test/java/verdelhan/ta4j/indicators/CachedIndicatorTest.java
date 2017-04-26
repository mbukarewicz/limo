package verdelhan.ta4j.indicators;


import org.junit.Assert;
import org.junit.Test;
import verdelhan.ta4j.TimeSeries;

import java.util.ArrayList;

public class CachedIndicatorTest {

    private static class SimpleCachedIndicator extends CachedIndicator {
        public int noOfCalls = 0;

        public SimpleCachedIndicator(TimeSeries series) {
            super(series);
        }

        @Override
        protected Object calculate(int index) {
            noOfCalls++;
            return index;
        }
    }

    @Test
    public void simpleTest() {
        final TimeSeries timeSeries = new TimeSeries(new ArrayList<>());


        SimpleCachedIndicator ind = new SimpleCachedIndicator(timeSeries);

        for (int i = 0; i < 100; i++) {
            final int v1 = (int) ind.getValue(i);
            Assert.assertEquals(i, v1);
            Assert.assertEquals(i + 1, ind.noOfCalls);
        }

        final int v1 = (int) ind.getValue(1);
        Assert.assertEquals(1, v1);
        final int v10 = (int) ind.getValue(10);
        Assert.assertEquals(10, v10);

        Assert.assertEquals(100, ind.noOfCalls);
    }

}
