package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.structures.TradingRegister;
import com.mutunus.tutunus.structures.FlowMetaInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


public class LarryWilliamsStatisticsProvider extends AbstractStatisticsProvider {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final String H_ID = "#id";
    private static final String H_STRATEGY_NAME = "strategy";
    private static final String H_ASSET = "asset";
    private static final String H_DATE_FROM = "date_from";
    private static final String H_DATE_TO = "date_to";
    private static final String H_PROFIT = "profit";
    private static final String H_PROFIT_PERCENT = "profit%";
    private static final String H_TOTAL_PROFITS = "total_profits";
    private static final String H_TOTAL_LOSSES = "total_losses";
    private static final String H_TRADES = "#trades";
    private static final String H_WINS = "#wins";
    private static final String H_WINS_PERCENT = "%wins";
    private static final String H_LOSES = "#loses";
    private static final String H_LOSES_PERCENT = "%loses";
    private static final String H_AVG_TRADE = "avg_trade";
    private static final String H_AVG_WIN = "avg_win";
    private static final String H_MEDIAN_WIN = "median_win";
    private static final String H_AVG_LOSS = "avg_loss";
    private static final String H_MEDIAN_LOSS = "median_loss";
    private static final String H_LARGEST_POSITION = "largest_position";
    private static final String H_PROFIT_LOSS_RATIO = "profit/loss_ratio";
    private static final String H_DRAWDOWN_ABSOLUTE_MAX = "max_drawdown_abs";
    private static final String H_DRAWDOWN_RELATIVE_MAX = "max_drawdown_rel";
    private static final String[] HEADERS = {H_ID, H_STRATEGY_NAME, H_ASSET, H_DATE_FROM, H_DATE_TO, H_PROFIT,
            H_PROFIT_PERCENT, H_TOTAL_PROFITS, H_TOTAL_LOSSES, H_PROFIT_LOSS_RATIO, H_TRADES, H_WINS, H_WINS_PERCENT, H_LOSES,
            H_LOSES_PERCENT, H_AVG_TRADE, H_AVG_WIN, H_MEDIAN_WIN, H_AVG_LOSS, H_MEDIAN_LOSS, H_LARGEST_POSITION,
            H_DRAWDOWN_ABSOLUTE_MAX, H_DRAWDOWN_RELATIVE_MAX};

    public LarryWilliamsStatisticsProvider() {
    }

    private String toString(final String format, final BigDecimal value) {
        if (value == null) {
            return "---";
        }
        return String.format(format, value.floatValue());
    }

    // TODO: what with appt, remove for good?
    // Average Profitability Per Trade = (Probability of Win x Average Win) -
    // (Probability of Loss x Average Loss)
    // Read more:
    // http://www.investopedia.com/articles/forex/07/profit_loss.asp#ixzz1b4Cbh2DN
    // private BigDecimal computeAppt(final float probabilityOfWin,
    // final BigDecimal avgWin,
    // final float probabilityOfLoss,
    // final BigDecimal avgLoss) {
    // final BigDecimal v1 = avgWin == null ? ZERO : avgWin.multiply(new BigDecimal(probabilityOfWin));
    // final BigDecimal v2 = avgLoss == null ? ZERO : avgLoss.multiply(new BigDecimal(probabilityOfLoss));
    // return v1.add(v2).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
    // }

    private BigDecimal divide(final BigDecimal val, final BigDecimal divisor) {
        if (val.signum() == 0) {
            return ZERO;
        }

        if (divisor.signum() != 0) {
            return val.divide(divisor, 4, RoundingMode.HALF_EVEN);
        } else {
            return null;
        }
    }

    @Override
    public StatisticsModel[] process(final List<TradingRegister> tradingRegisters) {
        final SimpleStatisticsModel m = new SimpleStatisticsModel("LW_stats", HEADERS);

        for (int row = 0; row < tradingRegisters.size(); row++) {
            final TradingRegister f = tradingRegisters.get(row);
            final LarryWilliamsCalculator summarizer = new LarryWilliamsCalculator(f);
            final FlowMetaInfo flowInfo = f.getMetaInfo();
            final DrawdownCalculator drawdown = new DrawdownCalculator(f);

            m.set(row, H_ID, String.format("%d", row + 1));
            m.set(row, H_STRATEGY_NAME, f.getMetaInfo().getCreatorInfo());
            m.set(row, H_DATE_FROM, flowInfo.getFromDate().toString());
            m.set(row, H_DATE_TO, flowInfo.getToDate().toString());
            m.set(row, H_ASSET, f.getAsset());

            final BigDecimal netProfit = summarizer.getTotalNetProfit();
            m.set(row, H_PROFIT, String.format("%.4f", netProfit.floatValue()));
//            BigDecimal profitPercent = divide(netProfit, f.getInitialMoney());
//            if (profitPercent != null) {
//                profitPercent = profitPercent.multiply(new BigDecimal(100));
//            }
//            m.set(row, H_PROFIT_PERCENT, toString("%.1f%%", profitPercent));
            m.set(row, H_PROFIT_PERCENT, toString("%.1f%%", null));

            final int noOfTrades = summarizer.getNumberOfTrades();
            final int numberOfWinningTrades = summarizer.getNumberOfWinningTrades();
            final int numberOfLosingTrades = noOfTrades - numberOfWinningTrades;
            final float probabilityOfWin = numberOfWinningTrades * 100f / noOfTrades;
            final float probabilityOfLoss = numberOfLosingTrades * 100f / noOfTrades;
            final BigDecimal totalProfits = summarizer.getSumOfAllWinningTrades();
            final BigDecimal totalLosses = summarizer.getSumOfAllLosingTrades();
            final BigDecimal grossProfitAndLoss = totalProfits.add(totalLosses);
            final int largestPosition = summarizer.getLargestPosition();
            final BigDecimal avgWin = divide(totalProfits, new BigDecimal(numberOfWinningTrades));
            final BigDecimal avgLoss = divide(totalLosses, new BigDecimal(numberOfLosingTrades));
            final BigDecimal winLossProfitRatio = divide(totalProfits, totalLosses.abs());
            final BigDecimal medianWin = summarizer.getMedianWin();
            final BigDecimal medianLoss = summarizer.getMedianLoss();
            // final double roi = summarizer.getRoi();

            m.set(row, H_TRADES, String.format("%d", noOfTrades));
            m.set(row, H_WINS, String.format("%d", numberOfWinningTrades));
            if (Float.isNaN(probabilityOfWin)) {
                m.set(row, H_WINS_PERCENT, "---");
            } else {
                m.set(row, H_WINS_PERCENT, String.format("%03.1f%%", probabilityOfWin));
            }
            m.set(row, H_LOSES, String.format("%d", numberOfLosingTrades));
            if (Float.isNaN(probabilityOfLoss)) {
                m.set(row, H_LOSES_PERCENT, "---");
            } else {
                m.set(row, H_LOSES_PERCENT, String.format("%03.1f%%", probabilityOfLoss));
            }
            m.set(row, H_TOTAL_PROFITS, toString("%.4f", totalProfits));
            m.set(row, H_TOTAL_LOSSES, String.format("%.4f", totalLosses));
            m.set(row, H_PROFIT_LOSS_RATIO, toString("%.4f", winLossProfitRatio));

            m.set(row, H_AVG_WIN, toString("%.4f", avgWin));
            m.set(row, H_MEDIAN_WIN, toString("%.4f", medianWin));
            m.set(row, H_AVG_LOSS, toString("%.4f", avgLoss));
            m.set(row, H_MEDIAN_LOSS, toString("%.4f", medianLoss));
            m.set(row, H_AVG_TRADE, toString("%.4f", divide(grossProfitAndLoss, new BigDecimal(noOfTrades))));
            m.set(row, H_LARGEST_POSITION, Integer.toString(largestPosition));

            m.set(row, H_DRAWDOWN_ABSOLUTE_MAX, String.format("%.2f", drawdown.getMaxAbsoluteDrawdown()));
            m.set(row, H_DRAWDOWN_RELATIVE_MAX, String.format("%.2f%%", drawdown.getMaxRelativeDrawdown()));

            // m.set(row, H_APPT, toString("%.4f", appt));
        }

        return new StatisticsModel[]{m};
    }
}
