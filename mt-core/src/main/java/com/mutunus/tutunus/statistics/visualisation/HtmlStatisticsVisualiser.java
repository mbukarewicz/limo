package com.mutunus.tutunus.statistics.visualisation;

import com.mutunus.tutunus.statistics.StatisticsModel;
import com.mutunus.tutunus.statistics.visualisation.charts.ChartFeederModel;
import com.mutunus.tutunus.statistics.visualisation.charts.TimeSeriesVisualizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HtmlStatisticsVisualiser implements StatisticsVisualiser.StatisticsVisualiserExt {

    public static final String PLACEHOLDER_TRADES_HISTORY = "$TRADES_HISTORY$";
    public static final String PLACEHOLDER_STRATEGY_DESC = "$STRATEGY_DESC$";
    public static final String PLACEHOLDER_CHARTS = "$CHARTS$";
    private static final String HTML_TEMPLATE = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<style>\n" +
            "table, th, td {\n" +
            "    border: 1px solid black;\n" +
            "}\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>" +
            PLACEHOLDER_STRATEGY_DESC +
            "<br><br>" +
            PLACEHOLDER_CHARTS +
            "<br><br>" +
            PLACEHOLDER_TRADES_HISTORY +
            "</body>\n" +
            "</html>";
    private final File outFolder;
    private final StatisticsModel tradeHistoryModelData;
    private final List<ChartFeederModel> chartModels;


    public HtmlStatisticsVisualiser(
            final String outFolder,
            StatisticsModel tradeHistoryModelData,
            List<ChartFeederModel> chartModels) {
        this.outFolder = new File(outFolder);
        this.tradeHistoryModelData = tradeHistoryModelData;
        this.chartModels = chartModels;
    }

    @Override
    public void print(final StatisticsModel stats) throws IOException {
        throw new RuntimeException("do not call");
    }

    private String makeTable(final StatisticsModel stats) {
        final int cols = stats.getColumns();
        final int rows = stats.getRows();

        StringBuffer sb = new StringBuffer("<table style=\"width:100%\">");
        sb.append("<tr>");
        for (int colId = 0; colId < cols; colId++) {
            String value = stats.getValue(0, colId);
            sb.append("<th>").append(value).append("</th>");
        }
        sb.append("</tr>");

        for (int rowId = rows - 1; rowId > 0; rowId--) {
            StringBuffer sbRow = new StringBuffer();
            String bgColor = "#00FF00";
            if (!isPositive(stats, rowId)) {
                bgColor = "#FF5500";
            }
            sbRow.append("<tr bgcolor=\"" + bgColor + "\">");
            for (int colId = 0; colId < cols; colId++) {
                String value = stats.getValue(rowId, colId);
                sbRow.append("<td>").append(value).append("</td>");
            }
            sbRow.append("</tr>");
            sb.append(sbRow).append("\n");
        }
        sb.append("</table");

        return sb.toString();
    }

    private boolean isPositive(StatisticsModel stats, int row) {
        String value = stats.getValue(row, 8);
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        if (value.trim().startsWith("-")) {
            return false;
        }
        return true;
    }

    @Override
    public void print() {
        try {
            final String description = tradeHistoryModelData.getDescription().replaceAll("\\s", "_");// whitespace
            outFolder.mkdirs();
            final File outFile = new File(outFolder, description + ".html");
            Path path = outFile.toPath();

            String tradeHistoryTable = makeTable(tradeHistoryModelData);
            String output = HTML_TEMPLATE.replace(PLACEHOLDER_STRATEGY_DESC, tradeHistoryModelData.getDescription());
            output = output.replace(PLACEHOLDER_TRADES_HISTORY, tradeHistoryTable);

            String chartsHtml = "";
            for (ChartFeederModel chartModel : chartModels) {
                String chartImage = TimeSeriesVisualizer.get(chartModel);
                chartsHtml += chartImage;
                chartsHtml += "<br><br>";
            }

            output = output.replace(PLACEHOLDER_CHARTS, chartsHtml);

            System.out.println(
                    String.format("Writing '%d' bytes to '%s' file.", output.length(), outFile.getAbsolutePath()));

            Files.write(path, output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
