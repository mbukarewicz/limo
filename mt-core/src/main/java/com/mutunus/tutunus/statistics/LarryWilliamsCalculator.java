package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.structures.TradingRegister;
import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Trade;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.math.BigDecimal;
import java.util.List;


public class LarryWilliamsCalculator {

    private final TradingRegister tradingRegister;

    public LarryWilliamsCalculator(final TradingRegister tradingRegister) {
        this.tradingRegister = tradingRegister;
    }

    public BigDecimal getTotalNetProfit() {
        final MTDate lastDate = tradingRegister.getLastDate();
        final BigDecimal money = tradingRegister.getTotalNetProfit(lastDate);
        return money;
    }

    public int getNumberOfTrades() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        return trades.size();
    }

    // open - close - brokerage > 0
    public int getNumberOfWinningTrades() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        int sum = 0;
        for (final Trade t : trades) {
            if (t.getProfit().signum() > 0) {
                sum++;
            }
        }

        return sum;
    }

    public int getNumberOfLosingTrades() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        int sum = 0;
        for (final Trade t : trades) {
            if (t.getProfit().signum() < 0) {
                sum++;
            }
        }

        return sum;
    }

    public BigDecimal getSumOfAllWinningTrades() {
        BigDecimal sum = BigDecimal.ZERO;
        final List<Trade> trades = tradingRegister.getAllTrades();

        for (final Trade t : trades) {
            if (t.getProfit().signum() > 0) {
                sum = sum.add(t.getProfit());
            }
        }

        return sum;
    }

    public BigDecimal getSumOfAllLosingTrades() {
        BigDecimal sum = BigDecimal.ZERO;
        final List<Trade> trades = tradingRegister.getAllTrades();

        for (final Trade t : trades) {
            if (t.getProfit().signum() < 0) {
                sum = sum.add(t.getProfit());
            }
        }

        return sum;
    }

    public int getLargestPosition() {
        int largest = 0;
        for (final MTDate date : tradingRegister.getAllDates()) {
            final int size = tradingRegister.getOpenedPositionSize(date);
            largest = Math.max(largest, size);
        }

        return largest;
    }

    private int getMaxNoOfConsecutiveWins() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        int max = 0;
        int current = 0;
        for (final Trade trade : trades) {
            final BigDecimal profit = trade.getProfit();
            if (profit.signum() > 0) {
                current++;
            } else {
                if (current > max) {
                    max = current;
                }
                current = 0;
            }
        }
        if (current > max) {
            max = current;
        }

        return max;
    }

    private BigDecimal getLargestConsecutiveWin() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        BigDecimal maxWin = BigDecimal.ZERO;
        BigDecimal currentWin = BigDecimal.ZERO;
        for (final Trade trade : trades) {
            final BigDecimal profit = trade.getProfit();
            if (profit.signum() > 0) {
                currentWin = currentWin.add(profit);
            } else {
                if (currentWin.compareTo(maxWin) > 0) {
                    maxWin = currentWin;
                }
                currentWin = BigDecimal.ZERO;
            }
        }
        if (currentWin.compareTo(maxWin) > 0) {
            maxWin = currentWin;
        }

        return maxWin;
    }

    private BigDecimal getLargestConsecutiveLoss() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        BigDecimal maxLoss = BigDecimal.ZERO;
        BigDecimal currentLoss = BigDecimal.ZERO;
        for (final Trade trade : trades) {
            final BigDecimal profit = trade.getProfit();
            if (profit.signum() < 0) {
                currentLoss = currentLoss.add(profit);
            } else {
                if (currentLoss.abs().compareTo(maxLoss.abs()) > 0) {
                    maxLoss = currentLoss;
                }
                currentLoss = BigDecimal.ZERO;
            }
        }
        if (currentLoss.abs().compareTo(maxLoss.abs()) > 0) {
            maxLoss = currentLoss;
        }

        return maxLoss;
    }

    // TODO: not in use
    private int getMaxNoOfConsecutiveLoses() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        int max = 0;
        int current = 0;
        for (final Trade trade : trades) {
            final BigDecimal profit = trade.getProfit();
            if (profit.signum() < 0) {
                current++;
            } else {
                if (current > max) {
                    max = current;
                }
                current = 0;
            }
        }
        if (current > max) {
            max = current;
        }

        return max;
    }

    private BigDecimal getLargestSingleWin() {
        final List<Trade> trades = tradingRegister.getAllTrades();
        BigDecimal maxWin = null;

        for (final Trade trade : trades) {
            if (maxWin == null) {
                maxWin = trade.getProfit();
            } else if (maxWin.compareTo(trade.getProfit()) < 0) {
                maxWin = trade.getProfit();
            }
        }

        return maxWin;
    }

    private BigDecimal getLargestSingleLoss() {
        final List<Trade> trades = tradingRegister.getAllTrades();
        BigDecimal maxLoss = null;

        for (final Trade trade : trades) {
            if (maxLoss == null) {
                maxLoss = trade.getProfit();
            } else if (maxLoss.compareTo(trade.getProfit()) > 0) {
                maxLoss = trade.getProfit();
            }
        }

        return maxLoss;
    }

    public BigDecimal getMedianWin() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        final double[] data = new double[trades.size()];

        int index = 0;
        for (final Trade t : trades) {
            if (t.getProfit().signum() > 0) {
                data[index] = t.getProfit().doubleValue();
                index++;
            }
        }

        if (index == 0) {
            return null;
        }

        final Median median = new Median();
        median.setData(data, 0, index);
        final double result = median.evaluate();

        return new BigDecimal(result);
    }

    public BigDecimal getMedianLoss() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        final double[] data = new double[trades.size()];

        int index = 0;
        for (final Trade t : trades) {
            if (t.getProfit().signum() < 0) {
                data[index] = t.getProfit().doubleValue();
                index++;
            }
        }

        if (index == 0) {
            return null;
        }

        final Median median = new Median();
        median.setData(data, 0, index);
        final double result = median.evaluate();

        return new BigDecimal(result);
    }

    // TODO: todo
    public double getRoi() {
        final List<Trade> trades = tradingRegister.getAllTrades();

        for (final Trade t : trades) {
            // t.get

        }

        return 0;
    }
}
