package com.mutunus.tutunus.statistics.visualisation;

import com.mutunus.tutunus.statistics.StatisticsModel;

import java.util.Arrays;


public class ConsoleVisualiser implements StatisticsVisualiser {

    @Override
    public void print(final StatisticsModel stats) {
        final int rows = stats.getRows();
        final int cols = stats.getColumns();

        final String[][] t = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                t[i][j] = stats.getValue(i, j);
            }
        }

        log(stats.getDescription());
        final String result = printTable(t);
        log(result);

    }

    private void log(final String s) {
        System.out.println(s);
    }

    private static String printTable(final String[][] table) {
        final int[] maxWidth = new int[table[0].length];
        Arrays.fill(maxWidth, 0);
        for (int row = 0; row < table.length; row++) {
            for (int col = 0; col < table[row].length; col++) {
                if (table[row][col] == null) {
                    throw new NullPointerException(String.format("Value at row %d column %d is null", row, col));
                }
                maxWidth[col] = Math.max(maxWidth[col], table[row][col].length());
            }
        }

        final StringBuilder sb = new StringBuilder();
        for (int row = 0; row < table.length; row++) {
            for (int col = 0; col < table[row].length; col++) {
                final String value = table[row][col];
                sb.append(addSpaces(value, maxWidth[col]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String addSpaces(final String value, final int spacesInFront) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spacesInFront - value.length() + 1; i++) {
            sb.append(" ");
        }
        sb.append(value);
        return sb.toString();
    }

}
