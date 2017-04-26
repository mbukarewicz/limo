package com.mutunus.tutunus.strategies;

import com.mutunus.tutunus.structures.Transaction;
import verdelhan.ta4j.TimeSeries;

public interface TradeOpener {

    Transaction openTrade(int tickId, TimeSeries timeSeries);
}