package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.TimeSeries;

public interface TradeCloser {

    Transaction closeTrade(int tickId, TimeSeries timeSeries, Transaction openedTransaction);
}
