package com.mutunus.tutunus.dao.readers;

import com.google.common.collect.Maps;
import com.mutunus.tutunus.Main;
import com.mutunus.tutunus.structures.DataProvider;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import verdelhan.ta4j.Tick;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class BulkStooqDownloader {

    public static final String CSV_DAILY_POSTFIX = "_d.csv";
    public static final String FOLDER_DAILY = "daily";

    public static void main(final String[] args) throws Exception {
//        System.setProperty("http.proxyHost", "proxy.mib-is.org");
//        System.setProperty("http.proxyPort", "8080");
//        System.setProperty("https.proxyHost", "proxy.mib-is.org");
//        System.setProperty("https.proxyPort", "8080");

        String[] assets = new String[]{
                Main.SP500,
                Main.FKGH,
                Main.FW20,
                Main.USD_PLN
        };
        final File outDir = new File(Main.FOLDER_TIMESERIES, BulkStooqDownloader.FOLDER_DAILY);
        outDir.mkdirs();

        final DataProvider dataProvider = new DataProvider.DataProviderBuilder()
//                .addMainRepo
                .addLocalRepository(new File(Main.FOLDER_TIMESERIES))
//                .addSupplementaryRepo
                //instant
                .addRemoteRepository(new StooqWebReader.StooqHistoricalReader())
//                .addRemoteRepository(new StooqWebReader.StooqHistoricalReader(10))
                .addRemoteRepository(new StooqWebReader.StooqRealTimeReader())
//                .addRemoteRepository("")
                .build();

        int i = 1;
        for (final String asset : assets) {
            final NavigableMap<LocalDate, Tick> ticks1 = dataProvider.getTicks(asset);
            for (Map.Entry<LocalDate, Tick> e : ticks1.entrySet()) {
//                System.out.println(e);
            }

            String outCsv = toCsv(ticks1);

            final File outFile = new File(outDir, asset + BulkStooqDownloader.CSV_DAILY_POSTFIX);
            FileUtils.write(outFile, outCsv);
            final String msg = String.format("%d\\%d) Written %d bytes to %s", i, assets.length, outCsv.length(), outFile);
            System.out.println(msg);
            i++;
        }

    }

    private static String toCsv(NavigableMap<LocalDate, Tick> tickMap) {
        String outCsv = "Data,Otwarcie,Najwyzszy,Najnizszy,Zamkniecie,Wolumen\r\n";
        outCsv += tickMap.values().stream()
                .map(tick -> tick.toCsvLine())
                .collect(Collectors.joining("\r\n"));
        return outCsv;
    }

}
