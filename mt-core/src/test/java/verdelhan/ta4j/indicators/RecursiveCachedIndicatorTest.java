package verdelhan.ta4j.indicators;


import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;

import java.util.ArrayList;

public class RecursiveCachedIndicatorTest {

    private static class SimpleRecursiveIndicator extends RecursiveCachedIndicator<Long> {
        public int noOfCalls = 0;

        public SimpleRecursiveIndicator(TimeSeries series) {
            super(series);
        }

        @Override
        protected Long calculate(int index) {
            noOfCalls++;
            if (index <= 0) {
                return Long.valueOf(0);
            }
            final Long prev = getValue(index - 1);
            return prev + index;
        }
    }

    @Test
    public void simpleTest() {
        final int maxIndex = 20000;

        final Tick tick = new Tick(Period.days(1), DateTime.now());
        final ArrayList<Tick> ticks = new ArrayList<>();
        for (int i = 0; i < maxIndex; i++) {
            ticks.add(tick);
        }

        final TimeSeries timeSeries = new TimeSeries(ticks);

        SimpleRecursiveIndicator ind = new SimpleRecursiveIndicator(timeSeries);

        for (int i = 10000; i < maxIndex; i++) {
            final long v1 = ind.getValue(i);
            final long sumOf = sumOf(i);
            Assert.assertEquals("Sum of first " + i + " elements", sumOf, v1);
            Assert.assertEquals(i + 1, ind.noOfCalls);
        }
        Assert.assertEquals(maxIndex, ind.noOfCalls);
    }

    private long sumOf(int maxIndex) {
        long sum = 0;
        for (int i = 1; i <= maxIndex; i++ ) {
            sum += i;
        }
        return sum;
    }

}
