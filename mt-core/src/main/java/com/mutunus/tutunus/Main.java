package com.mutunus.tutunus;

import com.mutunus.tutunus.logging.LoggerProducer;
import com.mutunus.tutunus.research.indicators.TradingValuationWithDrawdownIndicator;
import com.mutunus.tutunus.research.indicators.ProfitFromTransactionIndicator;
import com.mutunus.tutunus.research.indicators.RealizedProfitIndicator;
import com.mutunus.tutunus.research.indicators.WilliamsVixFixIndicator;
import com.mutunus.tutunus.statistics.LarryWilliamsStatisticsProvider;
import com.mutunus.tutunus.statistics.ResultsOverviewProvider;
import com.mutunus.tutunus.statistics.StatisticsManager;
import com.mutunus.tutunus.statistics.TradeHistoryStatisticsProvider;
import com.mutunus.tutunus.statistics.visualisation.ConsoleVisualiser;
import com.mutunus.tutunus.statistics.visualisation.HtmlStatisticsVisualiser;
import com.mutunus.tutunus.statistics.visualisation.charts.ChartFeederModel;
import com.mutunus.tutunus.strategies.AbstractStrategy;
import com.mutunus.tutunus.strategies.MomentumStrategy;
import com.mutunus.tutunus.structures.TradingRegister;
import org.slf4j.Logger;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.TimeSeriesStooqCsvLoader;
import verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import verdelhan.ta4j.indicators.trackers.SMAIndicator;

import java.util.Arrays;

import static com.mutunus.tutunus.research.indicators.ProfitFromTransactionIndicator.ProfitPerDayIndicatorMode.ONLY_LOSES;
import static com.mutunus.tutunus.research.indicators.ProfitFromTransactionIndicator.ProfitPerDayIndicatorMode.ONLY_PROFITS;


public class Main {
    private static final Logger LOG = LoggerProducer.getLogger(Main.class);

    // http://stooq.pl/q/d/?s=^spx

    public static final String DAX = "^dax";
    public static final String SP500 = "^spx";
    public static final String USD_PLN = "usdpln";
    public static final String EUR_USD = "eurusd";
    public static final String FKGH = "fkgh";
    public static final String FW20 = "fw20";
    public static final String KGH = "kgh";
    public static final String GOLD_SPOT = "xauusd";
    public static final String SILVER_SPOT = "xagusd";

//        private static final String ASSET = SP500;
//    private static final String ASSET = FW20;
    private static final String ASSET = FKGH;

    private static final String ROOT_FOLDER = "src/main/resources";
    private static final String OUT_FOLDER = ROOT_FOLDER + "/tmp";
    public static final String FOLDER_TIMESERIES = ROOT_FOLDER + "/stooq";


    public static void main(final String[] args) throws Exception {
        LOG.info("Hello {}, {}, {}, {}", "ssk", 19, null);

        TimeSeries series = new TimeSeriesStooqCsvLoader(FOLDER_TIMESERIES).loadData(ASSET);

        AbstractStrategy strategy;
        TimeSeries subsries = series.subseries2(20050101);

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        WilliamsVixFixIndicator wvf = new WilliamsVixFixIndicator(closePrice);
//        SMAIndicator sma = new SMAIndicator(closePrice, 50);
        SMAIndicator sma100 = new SMAIndicator(closePrice, 100);
//        SMAIndicator sma = new SMAIndicator(wvf, 3);
//        strategy = new WilliamsVixStrategy(series, wvf);
//        strategy = new ThreeDownDaysAndGapUpStrategy(series);
        strategy = new MomentumStrategy(series, sma100);

        TradingRegister tradingRegister = strategy.run(subsries);


        RealizedProfitIndicator realizedProfitIndicator = new RealizedProfitIndicator(series, tradingRegister);
        TradingValuationWithDrawdownIndicator tradingValuationWithDrawdownIndicator = new TradingValuationWithDrawdownIndicator(series, tradingRegister);
        ChartFeederModel chartModel1 = new ChartFeederModel(subsries,
                Arrays.asList(closePrice, sma100),
                Arrays.asList(realizedProfitIndicator/*, tradingValuationWithDrawdownIndicator*/));

//        ChartFeederModel chartModel2 = new ChartFeederModel(subsries, closePrice, wvf);
        ChartFeederModel chartModel2 = new ChartFeederModel(subsries,
                Arrays.asList(closePrice, sma100),
                Arrays.asList(new ProfitFromTransactionIndicator(series, tradingRegister, ONLY_LOSES),
                        new ProfitFromTransactionIndicator(series, tradingRegister, ONLY_PROFITS)));
//        ChartFeederModel chartModelSma = new ChartFeederModel(subsries,
//                Arrays.asList(realizedProfitIndicator),
//                Arrays.asList(closePrice, sma100));
        ChartFeederModel chartModelVix = new ChartFeederModel(subsries,
                Arrays.asList(wvf),
                Arrays.asList(closePrice));

        final StatisticsManager statsMan = new StatisticsManager(tradingRegister);
        statsMan.addStatisticsVisualiser(new ConsoleVisualiser());
//        statsMan.addStatisticsVisualiser(new CsvStatisticsVisualiser(OUT_FOLDER));

        // statsMan.addStatisticsProcessor(new CashFlowStatisticsProvider());
        ResultsOverviewProvider resultsOverviewProvider = new ResultsOverviewProvider(series, subsries.getBegin(), subsries.getEnd());
        TradeHistoryStatisticsProvider tradeHistoryStatisticsProvider = new TradeHistoryStatisticsProvider(series);
        statsMan.addStatisticsProcessor(resultsOverviewProvider);
        statsMan.addStatisticsProcessor(tradeHistoryStatisticsProvider);
        statsMan.addStatisticsProcessor(new LarryWilliamsStatisticsProvider());

        HtmlStatisticsVisualiser htmlStatisticsVisualiser = new HtmlStatisticsVisualiser(
                OUT_FOLDER,
                tradeHistoryStatisticsProvider.process(tradingRegister),
                Arrays.asList(
                        chartModel1,
                        chartModel2
//                        chartModelVix
//                    ,chartModelSma
                ));
        statsMan.addStatisticsVisualiser(htmlStatisticsVisualiser);

        statsMan.process();
    }

}
