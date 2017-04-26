package com.mutunus.tutunus.dao.readers;


import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import verdelhan.ta4j.Tick;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class StooqWebReader {
    private static final String STOOQ = "http://stooq.pl";
    private static final String STOOQ_DAILY = STOOQ + "/q/?s=";
    private static final String PLACEHOLDER_START_DATE = "19800101";
    private static final String STOOQ_HISTORICAL = STOOQ + "/q/d/l/?i=d&c=0&d1=" + PLACEHOLDER_START_DATE + "&d2=20991231&s=";
    private final ContentProvider<Document> dailyDataContentProvider;
    private final ContentProvider<Document> historicalDataContentProvider;

    @FunctionalInterface
    public interface TimeSeriesReader {
        List<Tick> getContent(String asset, WebReaderLoadParams params);
    }

    public static class WebReaderLoadParams {
        private LocalDate loadBeginDate = LocalDate.of(1980, 1, 1);

        public void setLoadBeginDate(LocalDate oldestTickDate) {
            this.loadBeginDate = oldestTickDate;
        }

        public LocalDate getLoadBeginDate() {
            return loadBeginDate;
        }
    }

    public static class StooqHistoricalReader implements TimeSeriesReader {

        @Override
        public List<Tick> getContent(String asset, WebReaderLoadParams params) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            final LocalDate beginDate = params.getLoadBeginDate();
            String beginDateString = beginDate.format(formatter);

            String finalUrl = STOOQ_HISTORICAL.replace(PLACEHOLDER_START_DATE, beginDateString);

            final Document urlContent = getUrlContent(finalUrl, asset);
            final String csv = urlContent.text();

            final List<Tick> ticks = StooqCsvParser.extractTicks(csv);
            return ticks;
        }
    }

    public static class StooqRealTimeReader implements TimeSeriesReader {
        @Override
        public List<Tick> getContent(String asset, WebReaderLoadParams params) {
            final Document doc = getUrlContent(STOOQ_DAILY, asset);

            final LocalDate date = extractDate(doc);
            String close = getTextValue(doc, "Kurs");
            //The class [\x00-\x7F] is all ascii characters, so [^\x00-\x7F] is all non-ascii characters
            close = close.split("[^\\x00-\\x7F]")[0];
            String open = getTextValue(doc, "Otwarcie");

            String maxMin = getTextValue(doc, "Max/min");
            final String[] highLow = maxMin.split(" ");
            String high = highLow[0];
            String low = highLow[1];

            DateTime joda = new DateTime(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth(), 0, 0);
            Tick tick = new Tick(joda, open, high, low, close, "1");

            System.out.println(tick.toCsvLine());

            final List<Tick> result = Lists.newArrayList(tick);
            return result;
        }

        private LocalDate extractDate(Document doc) {
            String date = getTextValue(doc, "Data");
            date = date.split(" ")[0];
            return LocalDate.parse(date);
        }

        private String getTextValue(Document doc, String searchParam) {
            final Element el = doc
                    .select("td:has(span)")
                    .select("[id=f13]")
                    .select("td:contains(" + searchParam + ")").first();
            final String text = el.text();
            final String cleanedUp = text.replace(searchParam, "").trim();

            return cleanedUp;
        }
    }

    @FunctionalInterface
    private interface ContentProvider<T> {
        T getContent(String asset);
    }

    public static class StooqWebReader2Builder {
        ContentProvider<Document> dailyDataContentProvider;
        ContentProvider<Document> historicalDataContentProvider;

        public StooqWebReader2Builder() {
            this.dailyDataContentProvider = (asset) -> getUrlContent(STOOQ_DAILY, asset);
            this.historicalDataContentProvider = (asset) -> getUrlContent(STOOQ_HISTORICAL, asset);
        }

        public StooqWebReader build() {
            return new StooqWebReader(dailyDataContentProvider, historicalDataContentProvider);
        }

        public StooqWebReader2Builder webDailyDataSource() {
            this.dailyDataContentProvider = (asset) -> getUrlContent(STOOQ_DAILY, asset);
            return this;
        }

        public StooqWebReader2Builder fileDailyDataSource(File dataFolder) {
            this.dailyDataContentProvider = (asset) -> getFileContent(dataFolder);
            return this;
        }

        public StooqWebReader2Builder webHistoricalDataSource(int daysToFetchData) {
            if (daysToFetchData <= 0) {
                throw new IllegalArgumentException("daysToFetchData must be > 0");
            }

            LocalDateTime beginDate = LocalDateTime.now().minusDays(daysToFetchData);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String beginDateString = beginDate.format(formatter);

            final String finalUrl = STOOQ_HISTORICAL.replace(PLACEHOLDER_START_DATE, beginDateString);

            this.historicalDataContentProvider = (asset) -> getUrlContent(finalUrl, asset);
            return this;
        }

        public StooqWebReader2Builder fileHistoricalDataSource(File dataFolder) {
            this.historicalDataContentProvider = (asset) -> getFileContent(dataFolder);
            return this;
        }
    }

    private StooqWebReader(
            ContentProvider<Document> dailyDataContentProvider,
            ContentProvider<Document> historicalDataContentProvider) {
        this.dailyDataContentProvider = dailyDataContentProvider;
        this.historicalDataContentProvider = historicalDataContentProvider;
    }

    private static StooqWebReader getReader() {
        return new StooqWebReader(
                (asset) -> getUrlContent(STOOQ_DAILY, asset),
                (asset) -> getUrlContent(STOOQ_HISTORICAL, asset));
    }

//    private static StooqWebReader2 getReader(File content) {
//        return new StooqWebReader2((asset) -> getFileContent(content), (asset) -> getFileContent(content));
//    }

    private static Document getFileContent(File dataFolder) {
        try {
            return Jsoup.parse(dataFolder, StandardCharsets.UTF_8.name(), STOOQ);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileContent2(File folder) {
        try {
            return "TODO";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Document getUrlContent(String stooqUrl, String asset) {
        try {
            final String url = stooqUrl + asset;
            System.out.println("URL: " + url);
            return Jsoup.connect(url).timeout(0).maxBodySize(0).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Tick getDaily(String assetName) {
        final Document doc = dailyDataContentProvider.getContent(assetName);
        final DateTime date = extractDate(doc);
        String close = getTextValue(doc, "Kurs");
        //The class [\x00-\x7F] is all ascii characters, so [^\x00-\x7F] is all non-ascii characters
        close = close.split("[^\\x00-\\x7F]")[0];
        String open = getTextValue(doc, "Otwarcie");

        String maxMin = getTextValue(doc, "Max/min");
        final String[] highLow = maxMin.split(" ");
        String high = highLow[0];
        String low = highLow[1];

        Tick tick = new Tick(date, open, high, low, close, "1");
        return tick;
    }

    public String getHistorical(String assetName) {
        final Document doc = historicalDataContentProvider.getContent(assetName);

        final String text = doc.text();
        return text;
    }

    private DateTime extractDate(Document doc) {
        String date = getTextValue(doc, "Data");
        date = date.split(" ")[0];
        return DateTime.parse(date);
    }

    private String getTextValue(Document doc, String searchParam) {
        final Element el = doc
                .select("td:has(span)")
                .select("[id=f13]")
                .select("td:contains(" + searchParam + ")").first();
        final String text = el.text();
        final String cleanedUp = text.replace(searchParam, "").trim();

        return cleanedUp;
    }


    public static void main(String[] args) throws IOException {
        System.setProperty("http.proxyHost", "proxy.mib-is.org");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "proxy.mib-is.org");
        System.setProperty("https.proxyPort", "8080");

        final StooqWebReader stooq = new StooqWebReader.StooqWebReader2Builder()
                .webDailyDataSource()
                .webHistoricalDataSource(5)
                .build();

//        Document doc = Jsoup.connect("http://stooq.pl/q/?s=fkgh").get();
//        File input = new File("C:\\t\\workspace\\idea\\x16-01\\limo\\src\\main\\resources\\stooq.html");
//        Document doc = Jsoup.parse(input, "UTF-8", "http://stooq.pl/");

//        final StooqWebReader2 stooq = StooqWebReader2.getReader(input);
//        final StooqWebReader stooq = StooqWebReader.getReader();

        final String stooqHistorical = stooq.getHistorical("^spx");
        final String[] split = stooqHistorical.split("\\s+");
        for (String s : split) {
            System.out.println(s);
        }

        final Tick tick = stooq.getDaily("^spx");
        System.out.println(tick.getEndTime() + " " + tick.getOpenPrice() + " " + tick.getMaxPrice() + " " + tick.getLowPrice() + " " + tick.getClosePrice());

//        System.out.println(stooqHistorical);
    }

}
