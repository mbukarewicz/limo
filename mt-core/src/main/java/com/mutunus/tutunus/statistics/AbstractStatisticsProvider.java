package com.mutunus.tutunus.statistics;

import com.mutunus.tutunus.structures.TradingRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractStatisticsProvider implements StatisticsProcessor {

    @Override
    public StatisticsModel process(TradingRegister tradingRegister) {
        ArrayList<TradingRegister> tradingRegisters = new ArrayList<>();
        tradingRegisters.add(tradingRegister);
        StatisticsModel[] models = process(tradingRegisters);
        return models[0];
    }

    public static class SimpleStatisticsModel implements StatisticsModel {

        protected final Map<String, Integer> columnIds = new HashMap<String, Integer>();
        protected final List<String[]> data;
        private final String modelName;

        public SimpleStatisticsModel(final String modelName, final String[] headers) {
            this(modelName, headers, 0);
        }

        public SimpleStatisticsModel(final String modelName, final String[] headers, final int datamodelSizeWithoutHeaderRow) {
            this.modelName = modelName;
            for (int i = 0; i < headers.length; i++) {
                columnIds.put(headers[i], i);
            }

            final int rows = datamodelSizeWithoutHeaderRow + 1;
            data = new ArrayList<String[]>(rows);

            data.add(headers);
            for (int i = 1; i < rows; i++) {
                data.add(new String[headers.length]);
            }

        }

        public void set(final int row, final String columnName, final String value) {
            final int rowWithHeader = row + 1;

            if (rowWithHeader == data.size()) {
                data.add(new String[columnIds.size()]);
            } // TODO: dodac spr indeksow

            final int columnId = columnIds.get(columnName);
            data.get(rowWithHeader)[columnId] = value;
        }

        @Override
        public String getDescription() {
            return modelName;
        }

        @Override
        public int getRows() {
            return data.size();
        }

        @Override
        public int getColumns() {
            return columnIds.size();
        }

        @Override
        public String getValue(final int row, final int column) {
            return data.get(row)[column];
        }
    }

}
