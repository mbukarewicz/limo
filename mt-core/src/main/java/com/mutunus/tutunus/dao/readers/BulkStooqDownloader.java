package com.mutunus.tutunus.dao.readers;

import com.mutunus.tutunus.Main;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


public class BulkStooqDownloader {

    public static final String CSV_DAILY_POSTFIX = "_d.csv";
    public static final String FOLDER_DAILY = "daily";

    public static void main(final String[] args) throws Exception {
        final StooqWebReader reader = new StooqWebReader();



        String[] assets = new String[]{Main.SP500, Main.FKGH, Main.FW20, Main.USD_PLN,Main.SP500};
        final File outDir = new File(Main.STOOQ_FOLDER, BulkStooqDownloader.FOLDER_DAILY);
        outDir.mkdirs();

        URL url = new URL("https://stooq.pl/q/d/l/?s=^spx&i=d");
        final File file = new File("e:\\out.txt");
        FileUtils.copyURLToFile(url, file);
        URL website = new URL("https://stooq.pl/q/d/l/?s=^spx&i=d");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("e:\\out2.txt");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();

        int i = 1;
        for (final String asset : assets) {
            final String csv = reader.getQuotationsForAsset(asset);
            final File outFile = new File(outDir, asset + BulkStooqDownloader.CSV_DAILY_POSTFIX);
            FileUtils.write(outFile, csv);
            final String msg = String.format("%d\\%d) Written %d bytes to %s", i, assets.length, csv.length(), outFile);
            System.out.println(msg);
            i++;
        }

    }

}
