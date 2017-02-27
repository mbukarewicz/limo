package com.mutunus.tutunus.research.indicators;


import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.TradingRegister;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.CachedIndicator;

import java.math.BigDecimal;

public class TotalPercentageProfitIndicator extends CachedIndicator<Decimal> {

    private final TradingRegister tradingRegister;

    public TotalPercentageProfitIndicator(TimeSeries series, TradingRegister tradingRegister) {
        super(series);
        this.tradingRegister = tradingRegister;
    }

    @Override
    protected Decimal calculate(int index) {
        MTDate date = getTimeSeries().getDate(index);
        BigDecimal result = tradingRegister.getTotalProfitPercent(date);

        return Decimal.valueOf(result);
    }

}
