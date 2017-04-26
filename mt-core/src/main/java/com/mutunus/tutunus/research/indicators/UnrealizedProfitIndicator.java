package com.mutunus.tutunus.research.indicators;

import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Trade;
import com.mutunus.tutunus.structures.TradingRegister;
import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.Indicator;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.CachedIndicator;
import verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

import java.math.BigDecimal;
import java.util.List;


public class UnrealizedProfitIndicator extends CachedIndicator<Decimal> {

    private final TradingRegister tradingRegister;
    private final Indicator<Decimal> priceSource;

    public UnrealizedProfitIndicator(TimeSeries series, TradingRegister tradingRegister, Indicator<Decimal> priceSource) {
        super(series);
        this.tradingRegister = tradingRegister;
        this.priceSource = priceSource;
    }

    @Override
    protected Decimal calculate(int index) {
        MTDate date = getTimeSeries().getDate(index);
        final List<Trade> activeTrades = tradingRegister.getNonTerminatingTrades(date);
        final Tick tick = getTimeSeries().getTick(index);

        BigDecimal profit = BigDecimal.ZERO;
        for (Trade t : activeTrades) {
            final BigDecimal openPrice = t.getOpenPrice();
            final Decimal value = priceSource.getValue(index);
//            final BigDecimal closePrice = tick.getClosePrice().asBigDecimal();

            final Transaction open = new Transaction(0, t.getOpenSide(), t.getSize(), openPrice, t.getOpenBrokerage(), t.getOpenDate());
            final Transaction close = new Transaction(1, t.getOpenSide().revert(), t.getSize(), value.asBigDecimal(), t.getCloseBrokerage(), date);

            final Trade phonyTrade = new Trade(open, close);
            final BigDecimal profitPercent = phonyTrade.getProfitPercent();

            profit = profit.add(profitPercent);
        }

        return Decimal.valueOf(profit);
    }

}
