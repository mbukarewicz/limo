package verdelhan.ta4j;


import com.mutunus.tutunus.dao.readers.StooqCsvParser;
import com.mutunus.tutunus.structures.MTException;
import com.mutunus.tutunus.structures.QuotationsImpl;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TimeSeriesStooqCsvLoader {
    private final File storagePath;
    public static final String CSV_DAILY_POSTFIX = "_d.csv";
    public static final String FOLDER_DAILY = "daily";
    private final StooqCsvParser parser;

    public TimeSeriesStooqCsvLoader(String storagePath) {
        this.storagePath = new File(storagePath, FOLDER_DAILY);
        parser = new StooqCsvParser();

    }

    public TimeSeries loadData(String asset) {
        final File sourceFile = new File(storagePath, asset + CSV_DAILY_POSTFIX);
        if (!sourceFile.exists()) {
            String msg = "No quotations for [" + asset + "] found in [" + sourceFile.getAbsolutePath() + "]";
            throw new RuntimeException(msg);
        }
        try {
            final String csv = FileUtils.readFileToString(sourceFile);
            List<Tick> ticks = parser.extractTicks(csv);
            TimeSeries timeSeries = new TimeSeries(asset, ticks);
            return timeSeries;
        } catch (final IOException e) {
            throw new MTException(e);
        }
    }

}

