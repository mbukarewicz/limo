package com.mutunus.tutunus.structures;

import com.mutunus.tutunus.Utils;


public class Asset {

    public enum AssetType {
        SHARE, INDEX, FX, ETF, COMMODITY
    }

    public enum AssetDomain {
        NONE, USA, EU, POL
    }

    private final String name;
    private final String tickerStooq;
    private final AssetType type;
    private final AssetDomain domain;
    private final String aliorName;
    public static final String ALIOR_NAME_NOT_PRESENT = "---";

    public Asset(final String name,
                 final String tickerStooq,
                 final AssetType type,
                 final AssetDomain domain,
                 final String aliorName) {
        Utils.assertNotEmpty("name", name);
        Utils.assertNotEmpty("tickerStooq", tickerStooq);
        Utils.assertNotNull("type", type);
        Utils.assertNotNull("domain", domain);
        this.name = name;
        this.tickerStooq = tickerStooq;
        this.type = type;
        this.domain = domain;
        this.aliorName = aliorName;
    }

    public String getName() {
        return name;
    }

    public String getTickerStooq() {
        return tickerStooq;
    }

    @Override
    public String toString() {
        return name + " " + tickerStooq + " " + type + " " + domain;
    }

}
