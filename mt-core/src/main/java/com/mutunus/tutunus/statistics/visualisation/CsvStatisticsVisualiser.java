//package com.mutunus.tutunus.statistics.visualisation;
//
//import com.mutunus.tutunus.statistics.StatisticsModel;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//
//public class CsvStatisticsVisualiser implements StatisticsVisualiser {
//
//    private static final char SEPARATOR = ',';
//    private final File outFolder;
//
//    public CsvStatisticsVisualiser(final String outFolder) {
//        this.outFolder = new File(outFolder);
//    }
//
//    @Override
//    public void print(final StatisticsModel stats) throws IOException {
//        final String description = stats.getDescription().replaceAll("\\s", "_");// whitespace
//        outFolder.mkdirs();
//        final File outFile = new File(outFolder, description + ".csv");
//
//        try (CSVWriter writer = new CSVWriter(
//                new FileWriter(outFile), SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER)) {
//
//            writeData(stats, writer);
//            System.out
//                    .println(String.format("Written '%d' lines to '%s' file.", stats.getRows(), outFile.getAbsolutePath()));
//        }
//
//    }
//
//    private void writeData(final StatisticsModel stats, final CSVWriter writer) {
//        final int cols = stats.getColumns();
//        final int rows = stats.getRows();
//
//        final String[] t = new String[cols];
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                t[j] = stats.getValue(i, j);
//            }
//            writer.writeNext(t);
//        }
//    }
//
//}
