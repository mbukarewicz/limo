package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.statistics.visualisation.StatisticsVisualiser;
import com.mutunus.tutunus.structures.TradingRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StatisticsManager {

    private final List<TradingRegister> tradingRegisters = new ArrayList<>();
    private final List<StatisticsProcessor> statisticsProcessors = new ArrayList<StatisticsProcessor>();
    private final List<StatisticsVisualiser> statisticsVisualisers = new ArrayList<StatisticsVisualiser>();

    public StatisticsManager(TradingRegister tradingRegister) {
        this.tradingRegisters.add(tradingRegister);
    }

    public void addStatisticsProcessor(final StatisticsProcessor processor) {
        statisticsProcessors.add(processor);
    }

    public void addStatisticsVisualiser(final StatisticsVisualiser visualiser) {
        statisticsVisualisers.add(visualiser);
    }

    public void process() {
        final List<StatisticsModel> allStats = new ArrayList<StatisticsModel>();
        for (final StatisticsProcessor p : statisticsProcessors) {
            final StatisticsModel[] stats = p.process(tradingRegisters);
            allStats.addAll(Arrays.asList(stats));
        }

        for (final StatisticsVisualiser visualiser : statisticsVisualisers) {
            if (visualiser instanceof StatisticsVisualiser.StatisticsVisualiserExt) {
                ((StatisticsVisualiser.StatisticsVisualiserExt) visualiser).print();
                continue;
            }

            for (final StatisticsModel stat : allStats) {
                try {
                    visualiser.print(stat);
                } catch (final Exception e) {
                    System.out.println(String.format("'%s' failed to process statistics given by '%s', reason: %s",
                            visualiser.getClass().getSimpleName(), //
                            stat.getDescription(),//
                            e.toString()));
                    e.printStackTrace();
                }
            }
        }
    }
}
