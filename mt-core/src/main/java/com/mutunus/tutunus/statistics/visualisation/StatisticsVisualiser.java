package com.mutunus.tutunus.statistics.visualisation;

import com.mutunus.tutunus.statistics.StatisticsModel;


public interface StatisticsVisualiser {

    interface StatisticsVisualiserExt extends StatisticsVisualiser {

        void print();
    }

    void print(final StatisticsModel stats) throws Exception;


}