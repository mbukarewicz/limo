package com.mutunus.tutunus.strategies;

class EndOfTradeCloser extends TimeoutCloser {

    public EndOfTradeCloser() {
        super(0);
    }

    @Override
    protected String getCloseReason() {
        return "EndOfTrade";
    }

}
