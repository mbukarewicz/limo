package com.mutunus.tutunus.strategies;

class EndOfTradeTradeCloser extends TimeoutTradeCloser {

    public EndOfTradeTradeCloser() {
        super(0);
    }

    @Override
    protected String getCloseReason() {
        return "EndOfTrade";
    }

}
