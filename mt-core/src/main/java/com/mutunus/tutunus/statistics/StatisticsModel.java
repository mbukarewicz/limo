package com.mutunus.tutunus.statistics;

public interface StatisticsModel {

    String getDescription();

    int getRows();

    int getColumns();

    String getValue(int row, int column);
}
