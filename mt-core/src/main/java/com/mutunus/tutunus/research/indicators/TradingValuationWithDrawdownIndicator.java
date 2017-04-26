package com.mutunus.tutunus.research.indicators;

import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Trade;
import com.mutunus.tutunus.structures.TradingRegister;
import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.CachedIndicator;

import java.math.BigDecimal;
import java.util.List;

public class TradingValuationWithDrawdownIndicator extends CachedIndicator<Decimal> {

    private final TradingRegister tradingRegister;
    final RealizedProfitIndicator realizedProfitIndicator;

    public TradingValuationWithDrawdownIndicator(
            TimeSeries series,
            TradingRegister tradingRegister) {
        super(series);
        this.tradingRegister = tradingRegister;
        realizedProfitIndicator = new RealizedProfitIndicator(series, tradingRegister);
    }

    @Override
    protected Decimal calculate(int index) {
        //pobrac profit (zrealizowany + niezrealizowany) dla dnia poprzedniego
        //policzyc max strate dla dzisiejszego
        //wynik = profit - strata

        Decimal pastProfitValuation = realizedProfitIndicator.getValue(index - 1);

        MTDate today = getTimeSeries().getDate(index);
        final List<Trade> activeTrades = tradingRegister.getTrades2(today);

        BigDecimal drawdownPerDay = BigDecimal.ZERO;
        final Tick tick = getTimeSeries().getTick(index);
        for (Trade t : activeTrades) {
            final BigDecimal openPrice = t.getOpenPrice();
            final Transaction open = new Transaction(0, t.getOpenSide(), t.getSize(), openPrice, t.getOpenBrokerage(), t.getOpenDate());

            BigDecimal closePrice = tick.getLowPrice().asBigDecimal();
            if (t.getOpenSide().isShort()) {
                closePrice = tick.getMaxPrice().asBigDecimal();
            }
            final Transaction close = new Transaction(1, t.getOpenSide().revert(), t.getSize(), closePrice, t.getCloseBrokerage(), t.getCloseDate());

            final Trade phonyTrade = new Trade(open, close);
            final BigDecimal profitPercent = phonyTrade.getProfitPercent();

            drawdownPerDay = drawdownPerDay.add(profitPercent);
        }

        final Decimal result = pastProfitValuation.plus(Decimal.valueOf(drawdownPerDay));
        return result;
    }

}
