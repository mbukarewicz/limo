/*package com.mutunus.tutunus.research.misc;

import com.mutunus.tutunus.Main;
import com.mutunus.tutunus.dao.QuotationsProvider;
import com.mutunus.tutunus.dao.StooqQuotationsProvider;
import com.mutunus.tutunus.math.MathUtils;
import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Quotation;
import com.mutunus.tutunus.structures.Quotations;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class Tdw {

    public static class TdwHistogram {

        private Map<Integer, List<Double>> results;

        private TdwHistogram() {

            final Factory factory = new Factory() {

                @Override
                public Object create() {
                    return new ArrayList<Double>();
                }
            };
            results = LazyMap.decorate(new TreeMap<Integer, List<Double>>(), factory);
        }

        public void add(final int dayOfWeek, final double range) {
            results.get(dayOfWeek).add(range);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();

            for (final Entry<Integer, List<Double>> e : results.entrySet()) {
                final Integer day = e.getKey();
                final Double[] valuesD = e.getValue().toArray(new Double[]{});
                final double[] values = ArrayUtils.toPrimitive(valuesD);
                // new EmpiricalDistributionImpl();
                final double mean = StatUtils.mean(values);
                final double p10 = StatUtils.percentile(values, 10);
                final double p50 = StatUtils.percentile(values, 50);
                final double p95 = StatUtils.percentile(values, 95);

                final String f = String.format("%d (%d) mean: %.2f", day, values.length, mean);
                sb.append(f);
                // sb.append(day).append(" (").append(values.length).append(") mean: ").append(mean);
                sb.append(String.format(" p10: %.2f ", p10));
                sb.append(String.format(" p50: %.2f ", p50));
                sb.append(String.format(" p95: %.2f ", p95));
                // sb.append(" p50: ").append(p50);
                // sb.append(" p95: ").append(p95);
                sb.append("\r\n");
            }

            return sb.toString();
        }

    }

    private static final String DAX = "^dax";
    private static final String SP500 = "^spx";
    private static final String ASSET = SP500;

    private static final String ROOT_FOLDER = Main.STOOQ_FOLDER;
    private final QuotationsProvider quotationsProvider;

    public Tdw(final QuotationsProvider quotationsProvider) {
        this.quotationsProvider = quotationsProvider;
    }

    public static void main(final String[] args) {
        final QuotationsProvider quotationsProvider = new StooqQuotationsProvider(ROOT_FOLDER);

        final int YEAR_FROM = 2000;
        final int YEAR_TO = 2012;
        final Tdw tdw = new Tdw(quotationsProvider);
        final TdwHistogram hist = tdw.getHistogram(ASSET, new MTDate(YEAR_FROM, 1, 1), new MTDate(YEAR_TO, 12, 31));
        System.out.println(hist);

        final TdwHistogram hist2 = tdw.getHistogram(ASSET, new MTDate(2010, 1, 1), new MTDate(YEAR_TO, 12, 31));
        System.out.println(hist2);
    }

    private TdwHistogram getHistogram(final String asset, final MTDate from, final MTDate to) {
        final Quotations qs = quotationsProvider.getQuotations(asset);

        final TdwHistogram hist = new TdwHistogram();

        final Range<Integer> allDateIds = qs.getIds(from, to);
        for (int qId = allDateIds.getMinimum(); qId <= allDateIds.getMaximum(); qId++) {
            final Quotation qPrev = qs.getQuotation(qId - 1);
            final Quotation q = qs.getQuotation(qId);
            final MTDate date = qs.getDate(qId);
            final int dayOfWeek = MTDate.getDayOfWeek(date);
            // final double range = MathUtils.computeRange(q.getLow(), q.getHigh());
            final double change = MathUtils.computeChange(qPrev.getClose(), q.getClose());
            // hist.add(dayOfWeek, range);
            if (change > 0) {
                hist.add(dayOfWeek, change);
            } else if (change < 0) {
                hist.add(-dayOfWeek, -change);
            }
        }

        return hist;
    }
}
*/