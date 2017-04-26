package com.mutunus.tutunus.research.indicators;

import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.TradingRegister;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.CachedIndicator;
import verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

/**
 * pobrac zrealizowany profit dla dnia poprzedniego
 * jesli sa otwarte trans to dodac profit na zamknieciu
 *
 * wynikiem jest profit w procentach
 */
public class TradingValuationIndicator extends CachedIndicator<Decimal> {

    private final TradingRegister tradingRegister;
    private final RealizedProfitIndicator realizedProfitIndicator;
    private final UnrealizedProfitIndicator unrealizedProfitIndicator;

    public TradingValuationIndicator(TimeSeries series, TradingRegister tradingRegister) {
        this(series, tradingRegister, new RealizedProfitIndicator(series, tradingRegister), new UnrealizedProfitIndicator(series, tradingRegister, new ClosePriceIndicator(series)));
    }

    public TradingValuationIndicator(TimeSeries series, TradingRegister tradingRegister, RealizedProfitIndicator realizedProfitIndicator, UnrealizedProfitIndicator unrealizedProfitIndicator) {
        super(series);
        this.tradingRegister = tradingRegister;
        this.realizedProfitIndicator = realizedProfitIndicator;
        this.unrealizedProfitIndicator = unrealizedProfitIndicator;
    }

    @Override
    protected Decimal calculate(int index) {
        MTDate date = getTimeSeries().getDate(index);
        if (date.isLt(tradingRegister.getFirstDate())) {
            return Decimal.ZERO;
        }

        final Decimal realized = realizedProfitIndicator.getValue(index);
        final Decimal unrealized = unrealizedProfitIndicator.getValue(index);

        final Decimal result = realized.plus(unrealized);

        return result;
    }


}
