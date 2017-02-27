package com.mutunus.tutunus.dao;

import com.mutunus.tutunus.structures.Asset;
import com.mutunus.tutunus.structures.Asset.AssetDomain;
import com.mutunus.tutunus.structures.Asset.AssetType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AssetProvider {

    public static final List<Asset> ALL = init();

    public List<Asset> getAll() {
        return ALL;
    }

    private static List<Asset> init() {
        final List<Asset> l = new ArrayList<Asset>();
        add(l, "EUR/USD", "EURUSD", AssetType.FX, null);
        add(l, "EUR/AUD", "EURAUD", AssetType.FX, null);
        add(l, "EUR/CAD", "EURCAD", AssetType.FX, null);
        add(l, "EUR/CHF", "EURCHF", AssetType.FX, null);
        add(l, "EUR/GBP", "EURGBP", AssetType.FX, null);
        add(l, "EUR/PLN", "EURPLN", AssetType.FX, null);
        add(l, "EUR/SEK", "EURSEK", AssetType.FX, null);

        add(l, "GBP/AUD", "GBPAUD", AssetType.FX, null);
        add(l, "GBP/CHF", "GBPCHF", AssetType.FX, null);
        add(l, "GBP/PLN", "GBPPLN", AssetType.FX, null);
        add(l, "GBP/USD", "GBPUSD", AssetType.FX, null);

        add(l, "USD/CHF", "USDCHF", AssetType.FX, null);
        add(l, "USD/PLN", "USDPLN", AssetType.FX, null);
        add(l, "AUD/CAD", "AUDCAD", AssetType.FX, null);
        add(l, "AUD/USD", "AUDUSD", AssetType.FX, null);
        add(l, "CHF/PLN", "CHFPLN", AssetType.FX, null);

        add(l, "BP", "BP.UK", AssetType.SHARE, AssetDomain.EU);
        add(l, "SIEMENS", "SIE.DE", AssetType.SHARE, AssetDomain.EU);
        add(l, "AMAZON", "AMZN.US", AssetType.SHARE, AssetDomain.USA);
        add(l, "APPLE", "AAPL.US", AssetType.SHARE, AssetDomain.USA);
        add(l, "GOOGLE", "GOOG.US", AssetType.SHARE, AssetDomain.USA);
        add(l, "GOLDMAN", "GS.US", AssetType.SHARE, AssetDomain.USA);
        add(l, "MICROSOFT", "MSFT.US", AssetType.SHARE, AssetDomain.USA, "MSOFT");

        add(l, "FKGH", "FKGH", AssetType.SHARE, AssetDomain.POL, Asset.ALIOR_NAME_NOT_PRESENT);// TODO: ??? nie ma w aliorze
        add(l, "KGHM", "KGH", AssetType.SHARE, AssetDomain.POL);
        add(l, "LCC", "LCC", AssetType.SHARE, AssetDomain.POL, Asset.ALIOR_NAME_NOT_PRESENT);
        add(l, "FW20", "FW20", AssetType.INDEX, AssetDomain.POL, "POL20.M2");
        add(l, "IBEX35 - Spain", "^IBEX", AssetType.INDEX, null, "ESP35");
        add(l, "CAC40 - France", "^CAC", AssetType.INDEX, null, "FRA40");
        add(l, "FTSE250 - UK", "^FTM", AssetType.INDEX, null, "GBR250");
        add(l, "DAX", "^DAX", AssetType.INDEX, null, "GER30");
        // ITA - brak na stoqu
        // SUI20
        add(l, "Nikkei225 - Japan", "^NKX", AssetType.INDEX, null, "JPN225");
        add(l, "NASDAQ100", "^NDX", AssetType.INDEX, null, "USA100");
        add(l, "SP500", "^SPX", AssetType.INDEX, null, "USA500");

        add(l, "Brent Oil Future", "SC.F", AssetType.COMMODITY, null, "COM.BRENT");
        add(l, "Gold Future", "GC.F", AssetType.COMMODITY, null, "COM.GOLD");
        add(l, "Silver Future", "SI.F", AssetType.COMMODITY, null, "COM.SILVER");
        add(l, "Brent Oil Future", "SC.F", AssetType.COMMODITY, null, "COM.BRENT");
        add(l, "Wheat Future", "W.F", AssetType.COMMODITY, null, "COM.WHEATUS.N2");
        add(l, "Sugar Future", "SB.F", AssetType.COMMODITY, null, "COM.SUGARUS.N2");
        add(l, "Soybean Future", "S.F", AssetType.COMMODITY, null, "COM.SOY.N2");
        add(l, "Platinum Future", "PL.F", AssetType.COMMODITY, null, "COM.PLAT.N2");
        add(l, "Palladium Future", "PA.F", AssetType.COMMODITY, null, "COM.PALLAD.M2");
        add(l, "Natural Gas Future", "NG.F", AssetType.COMMODITY, null, "COM.NG.M2");
        add(l, "Copper Future", "HG.F", AssetType.COMMODITY, null, "COM.HG.N2");
        add(l, "Cotton #2 Future", "CT.F", AssetType.COMMODITY, null, "COM.COTTON.N2");
        add(l, "Corn Future", "C.F", AssetType.COMMODITY, null, "COM.CORNUS.N2");
        add(l, "London Coffee Future", "LKC.F", AssetType.COMMODITY, null, "COM.COFFEE.N2");
        add(l, "London Cocoa Future", "LCC.F", AssetType.COMMODITY, null, "COM.COCOA.N2");

        return Collections.unmodifiableList(l);
    }

    private static void add(final List<Asset> l,
                            final String name,
                            final String stooq,
                            final AssetType t,
                            final AssetDomain d) {
        add(l, name, stooq, t, d, null);
    }

    private static void add(final List<Asset> l,
                            final String name,
                            final String stooq,
                            final AssetType t,
                            final AssetDomain d,
                            final String aliorName) {
        final AssetDomain dom = d == null ? AssetDomain.NONE : d;
        final Asset asset = new Asset(name, stooq, t, dom, aliorName);
        l.add(asset);
    }

}
