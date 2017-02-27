package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.structures.TradingRegister;

import java.util.List;

public interface StatisticsProcessor {

    StatisticsModel[] process(List<TradingRegister> tradingRegisters);

    StatisticsModel process(TradingRegister tradingRegister);

}