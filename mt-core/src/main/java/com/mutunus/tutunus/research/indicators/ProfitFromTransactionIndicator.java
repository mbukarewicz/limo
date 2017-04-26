package com.mutunus.tutunus.research.indicators;

import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Trade;
import com.mutunus.tutunus.structures.TradingRegister;
import verdelhan.ta4j.Decimal;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.indicators.CachedIndicator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pokazuje zysk z tradow dla danego okresu (dla kazdego dnia w okresie tradu wynik bedzie identyczny).
 */
public class ProfitFromTransactionIndicator extends CachedIndicator<Decimal> {

    private interface ProfitPerDayIndicatorModeFilter {
        Decimal filter(Decimal input);
    }

    public enum ProfitPerDayIndicatorMode implements ProfitPerDayIndicatorModeFilter {
        SHOW_ALL(d -> d),
        ONLY_PROFITS(d -> d.isPositive() ? d : Decimal.ZERO),
        ONLY_LOSES(d -> d.isNegative() ? d : Decimal.ZERO);


        private final ProfitPerDayIndicatorModeFilter filter;

        ProfitPerDayIndicatorMode(ProfitPerDayIndicatorModeFilter filter) {
            this.filter = filter;
        }

        @Override
        public Decimal filter(Decimal input) {
            return filter.filter(input);
        }

    }

    private final TradingRegister tradingRegister;
    private final ProfitPerDayIndicatorMode mode;

    public ProfitFromTransactionIndicator(TimeSeries series, TradingRegister tradingRegister, ProfitPerDayIndicatorMode mode) {
        super(series);
        this.tradingRegister = tradingRegister;
        this.mode = mode;
    }

    public ProfitFromTransactionIndicator(TimeSeries series, TradingRegister tradingRegister) {
        this(series, tradingRegister, ProfitPerDayIndicatorMode.SHOW_ALL);
    }

    @Override
    protected Decimal calculate(int index) {
        MTDate date = getTimeSeries().getDate(index);
        final List<Trade> trades = tradingRegister.getTrades2(date);

        BigDecimal result = BigDecimal.ZERO;
        for (Trade t : trades) {
            result = result.add(t.getProfitPercent());
        }

        Decimal value = mode.filter(Decimal.valueOf(result));
        return value;
    }

}
