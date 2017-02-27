package com.mutunus.tutunus.research.indicators;


import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Trade;
import com.mutunus.tutunus.structures.TradingRegister;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.CachedIndicator;

import java.math.BigDecimal;
import java.util.List;

public class ProfitPerDayIndicator extends CachedIndicator<Decimal> {

    private final TradingRegister tradingRegister;
    private final boolean showProfits;

    public ProfitPerDayIndicator(TimeSeries series, TradingRegister tradingRegister, boolean showProfits) {
        super(series);
        this.tradingRegister = tradingRegister;
        this.showProfits = showProfits;
    }

    @Override
    protected Decimal calculate(int index) {
        MTDate date = getTimeSeries().getDate(index);
        final List<Trade> trades = tradingRegister.getTrades2(date);
//        final List<Trade> trades1 = tradingRegister.getNewOrClosedTradesForDate(date);
//        if (!trades1.isEmpty()) {
//            System.out.println("#" + index + " " + trades1);
//        }

//        if (trades.isEmpty()) {////
//            return Decimal.NaN;
//        }

        BigDecimal result = BigDecimal.ZERO;
        for(Trade t : trades) {
            result = result.add(t.getProfitPercent());
        }

//        if (result.equals(BigDecimal.ZERO)) {
//            return Decimal.ZERO;
//        }
        if (result.signum() == 1 && showProfits) {
            return Decimal.valueOf(result);
        }
        if (result.signum() == -1 && !showProfits) {
            return Decimal.valueOf(result);
        }
        return Decimal.ZERO;
    }

}
