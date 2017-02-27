package com.mutunus.tutunus.statistics.visualisation.charts;

import com.mutunus.tutunus.structures.MTDate;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collection;


public class TimeSeriesVisualizer {

    public static final double RATIO_CHART_SIZE_TO_TICKS = 0.45;
    public static final int MIN_CHART_WIDTH = 1100;

    static {
        // set a theme using the new shadow generator feature available in
        // 1.0.14 - for backwards compatibility it is not enabled by default
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow", true));
    }

    private static Range getRange(TimeSeriesCollection datasets) {
        double lowest = Double.MAX_VALUE;
        double higest = Double.MIN_VALUE;
        for (int i = 0; i < datasets.getSeriesCount(); i++) {
            lowest = Math.min(lowest, datasets.getSeries(i).getMinY());
            higest = Math.max(higest, datasets.getSeries(i).getMaxY());
        }

        final Range range = new Range(lowest * 0.95, higest * 1.05);
        return range;
    }

    private static JFreeChart createChart(TimeSeriesCollection dataset1, TimeSeriesCollection dataset2) {
        XYPlot plot = new XYPlot();
        plot.setDataset(0, dataset1);
        plot.setDataset(1, dataset2);

        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        //customize the plot with renderers and axis
        plot.setRenderer(0, new StandardXYItemRenderer());//use default fill paint for first series
        StandardXYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setSeriesFillPaint(0, Color.BLUE);
        plot.setRenderer(1, renderer);

        NumberAxis yAxis1 = new NumberAxis("Buy & Hold");
        NumberAxis yAxis2 = new NumberAxis("Strategy");
        plot.setRangeAxis(0, yAxis1);
        plot.setRangeAxis(1, yAxis2);
        plot.setDomainAxis(new DateAxis());
        yAxis2.setRange(getRange(dataset2));
        yAxis1.setRange(getRange(dataset1));
//        yAxis2.setAutoRange(true);

        yAxis1.setAutoRangeStickyZero(false);
        yAxis1.setAutoRangeIncludesZero(false);
        yAxis2.setAutoRangeStickyZero(false);
        yAxis2.setAutoRangeIncludesZero(false);


        //Map the data to the appropriate axis
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);


        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MM-yyyy"));
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(.0051);
        domainAxis.setUpperMargin(.0051);

        //generate the chart
        JFreeChart chart = new JFreeChart("Value of portfolio vs. buy and hold", plot);
        chart.setBackgroundPaint(Color.WHITE);

        return chart;

    }

    public static String get(ChartFeederModel data) throws IOException {
        final verdelhan.ta4j.TimeSeries series = data.getTimeSeries();

        final Collection<Indicator<Decimal>> indicators1 = data.getFirstIndicators();
        final Collection<Indicator<Decimal>> indicators2 = data.getSecondIndicators();

        TimeSeriesCollection dataset1 = createTimeSeriesCollection(indicators1, data);
        TimeSeriesCollection dataset2 = createTimeSeriesCollection(indicators2, data);
//        List<TimeSeriesCollection> datasets = createDatasets(data);

        JFreeChart chart = createChart(dataset1, dataset2);
        chart.getPlot().setInsets(new RectangleInsets() {
            public void trim(Rectangle2D area) {
            }

            ;
        });

        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        System.out.println(series.getTickCount());
        int width = Math.max(MIN_CHART_WIDTH, (int) (series.getTickCount() * RATIO_CHART_SIZE_TO_TICKS));
        ChartUtilities.writeChartAsPNG(bas, chart, width, 600);
        byte[] byteArray = bas.toByteArray();

        String base64 = Base64.getEncoder().encodeToString(byteArray);
        String htmlImageTag = "<img alt=\"Embedded Image\" src=\"data:image/png;base64," + base64 + "\" />";

        return htmlImageTag;
    }

    private static TimeSeriesCollection createTimeSeriesCollection(
            Collection<Indicator<Decimal>> indicators,
            ChartFeederModel data) {
        final verdelhan.ta4j.TimeSeries inputSeries = data.getTimeSeries();


        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (Indicator<Decimal> ind : indicators) {
            TimeSeries ts = new TimeSeries(ind.toString());
            dataset.addSeries(ts);

            for (int i = inputSeries.getBegin(); i < inputSeries.getEnd(); i++) {
                final MTDate mtdate = inputSeries.getDate(i);
                double v1 = ind.getValue(i).toDouble();

                int day = mtdate.getDay();
                int month = mtdate.getMonth();
                int year = mtdate.getYear();

                ts.add(new Day(day, month, year), v1);
            }
        }

        return dataset;
    }

}
