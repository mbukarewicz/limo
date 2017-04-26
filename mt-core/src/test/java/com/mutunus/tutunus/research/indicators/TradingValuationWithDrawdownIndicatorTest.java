package com.mutunus.tutunus.research.indicators;

import com.mutunus.tutunus.strategies.AbstractStrategy;
import com.mutunus.tutunus.strategies.TradeCloser;
import com.mutunus.tutunus.strategies.TradeOpener;
import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Side;
import com.mutunus.tutunus.structures.TradingRegister;
import com.mutunus.tutunus.structures.Transaction;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.helpers.HighestValueIndicator;
import verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


public class TradingValuationWithDrawdownIndicatorTest {

    private static class DummyStrategy extends AbstractStrategy
            implements TradeOpener {


        public DummyStrategy(TimeSeries timeSeries) {
            super(timeSeries);
            maxPositionSize=1;
        }

//        @Override
//        protected TradeOpener[] getOpeners() {
//            return new TradeOpener[]{};
//        }
//
//        @Override
//        protected TradeCloser[] getClosers() {
//            return null;
//        }

        @Override
        protected TradeOpener[] getOpeners() {
            return new TradeOpener[]{this};
        }

        @Override
        protected TradeCloser[] getClosers() {
            return new TradeCloser[0];
        }

        @Override
        protected String getName() {
            return "Dummy";
        }

        @Override
        public Transaction openTrade(int tickId, TimeSeries timeSeries) {
            final Tick tick = timeSeries.getTick(tickId);
            final MTDate date = timeSeries.getDate(tickId);
            final Decimal openPrice = tick.getOpenPrice();
            return new Transaction(tickId, Side.LONG, 1, openPrice.asBigDecimal(),
                    BigDecimal.ZERO, date);
        }

//        @Override
//        public Transaction closeTrade(int tickId, TimeSeries timeSeries, Transaction openedTransaction) {
//            return null;
//        }
    }

    @Test
    public void test1() {
        Tick tick1 = new Tick(dt("2000-01-01"), 50, 52, 48, 51, 1);
        Tick tick2 = new Tick(dt("2000-01-02"), 60, 62, 58, 61, 1);
        Tick tick3 = new Tick(dt("2000-01-03"), 40, 42, 38, 41, 1);
        Tick tick4 = new Tick(dt("2000-01-04"), 30, 32, 28, 31, 1);
        final List<Tick> ticks = Arrays.asList(tick1, tick2, tick3, tick4);

        TimeSeries series = new TimeSeries("SPX", ticks);


        AbstractStrategy strategy = new DummyStrategy(series);
        final TradingRegister tradingRegister = strategy.run(series);

        TradingValuationWithDrawdownIndicator drawdownIndicator = new TradingValuationWithDrawdownIndicator(series, tradingRegister);

        final Decimal value1 = drawdownIndicator.getValue(0);
        final Decimal value2 = drawdownIndicator.getValue(1);
        final Decimal value3 = drawdownIndicator.getValue(2);
        final Decimal value4 = drawdownIndicator.getValue(3);

        System.out.println(value1 + " exp: " + ( -2.0/50 * 100));
        System.out.println(value2 + " exp: " + ( 8.0/50 * 100));
        System.out.println(value3 + " exp: " + ( -12.0/50 * 100));
        System.out.println(value4 + " exp: " + ( -22.0/50 * 100));


        final RealizedProfitIndicator realizedProfitIndicator = new RealizedProfitIndicator(series, tradingRegister);
        final UnrealizedProfitIndicator unrealizedProfitIndicator = new UnrealizedProfitIndicator(series, tradingRegister, new ClosePriceIndicator(series));
        final TradingValuationIndicator valuationIndicator = new TradingValuationIndicator(series, tradingRegister);
        final HighestValueIndicator highestValueIndicator = new HighestValueIndicator(valuationIndicator, Integer.MAX_VALUE);
        final TradingValuationWithDrawdownIndicator tradingValuationWithDrawdownIndicator = new TradingValuationWithDrawdownIndicator(series, tradingRegister);
        final DailyDrawdownIndicator dailyDrawdownIndicator = new DailyDrawdownIndicator(series, tradingRegister);

        //unrealized profit!!!
        //maxUnrealizedProfitIndicator
        for (int i = series.getBegin(); i<=series.getEnd(); i++) {
            final MTDate date = series.getDate(i);
            final Decimal realizedProfit = realizedProfitIndicator.getValue(i);
            final Decimal unrealizedProfit = unrealizedProfitIndicator.getValue(i);
            final Decimal valuation = valuationIndicator.getValue(i);
            final Decimal highestValuation = highestValueIndicator.getValue(i);
            final Decimal drawdownValuationValue = tradingValuationWithDrawdownIndicator.getValue(i);
            final Decimal drawdownForDay = dailyDrawdownIndicator.getValue(i);
//            final Decimal highestValue = highestValueIndicator.getValue(i);

            System.out.println(i
                    + ", realizedProfit " + realizedProfit
                    + ", unrealizedProfit " + unrealizedProfit
                    + ", valuation " + valuation
                    + ", highestValuation " + highestValuation
                    + ", drawdownValuation " + drawdownValuationValue
                    + ", drawdownForDay " + drawdownForDay
//                    + " " +  highestValue
            );
        }
    }

    private DateTime dt(String date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        return formatter.parseDateTime(date);
    }

}