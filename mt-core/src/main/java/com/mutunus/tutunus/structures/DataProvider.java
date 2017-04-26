package com.mutunus.tutunus.structures;//datasource

import com.google.common.collect.Maps;
import com.mutunus.tutunus.dao.readers.StooqWebReader;
import verdelhan.ta4j.Tick;
import verdelhan.ta4j.TimeSeries;
import verdelhan.ta4j.TimeSeriesStooqCsvLoader;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

//https://en.wikipedia.org/wiki/Chain-of-responsibility_pattern
//        DataProviderBuilder
//                addLocalRepository("path")
//                addRemoteRepository(StooqWebReader, minTicksToLoad=10)
//                addRemoteRepository(StooqWebReaderRealTime)
public class DataProvider {

    private final List<TicksLoader> tickLoaders = new ArrayList<>();

    private static abstract class TicksLoader {
        boolean allowOverride = true;

        abstract void fetchTicks(String asset, NavigableMap<LocalDate, Tick> ticks);

        protected void merge(NavigableMap<LocalDate, Tick> targetTicks, NavigableMap<LocalDate, Tick> newTicks) {
            if (allowOverride || targetTicks.isEmpty()) {
                targetTicks.putAll(newTicks);
            }

            for (Map.Entry<LocalDate, Tick> e : newTicks.entrySet()) {
                if (!targetTicks.containsKey(e.getKey())) {
                    targetTicks.put(e.getKey(), e.getValue());
                }
            }

        }
    }

    private static class LocalTicksLoader extends TicksLoader {
        private final File resourcePath;

        //TODO NameResolver
        LocalTicksLoader(File path) {
            resourcePath = path;
        }

        @Override
        void fetchTicks(String asset, NavigableMap<LocalDate, Tick> ticks) {
            final TimeSeries timeSeries = new TimeSeriesStooqCsvLoader(resourcePath.toString()).loadData(asset);
            final List<Tick> newTicks = timeSeries.getTicks();

//            final List<Tick> ticksList = StooqCsvParser.extractTicks(csv);
            final NavigableMap<LocalDate, Tick> newTicksMap = new TreeMap(Maps.uniqueIndex(newTicks, Tick::getEndDate));
            merge(ticks, newTicksMap);
        }
    }
    private static class RemoteTicksLoader extends TicksLoader {

        private final StooqWebReader.TimeSeriesReader dataRetriever;

        //TODO NameResolver
        RemoteTicksLoader(StooqWebReader.TimeSeriesReader dataRetriever) {
            this.dataRetriever = dataRetriever;
        }

        @Override
        void fetchTicks(String asset, NavigableMap<LocalDate, Tick> ticks) {
            final StooqWebReader.WebReaderLoadParams searchParams = new StooqWebReader.WebReaderLoadParams();

            if (!ticks.isEmpty()) {
                final LocalDate newestTickDate = ticks.lastKey();
                searchParams.setLoadBeginDate(newestTickDate);
            }

            final List<Tick> newTicks = dataRetriever.getContent(asset, searchParams);
            final NavigableMap<LocalDate, Tick> newTicksMap = new TreeMap(Maps.uniqueIndex(newTicks, Tick::getEndDate));
            merge(ticks, newTicksMap);
            return;
        }
    }

    public static class DataProviderBuilder {
        private File localRepoPath;
        private final List<RemoteTicksLoader> remoteFetchers = new ArrayList();

        public DataProvider build() {
            final DataProvider dataProvider = new DataProvider();
            dataProvider.tickLoaders.add(new LocalTicksLoader(localRepoPath));
            dataProvider.tickLoaders.addAll(remoteFetchers);

            return dataProvider;
        }
        public DataProviderBuilder addLocalRepository(File localRepositoryPath) {
            localRepoPath = localRepositoryPath;
            return this;
        }

        public DataProviderBuilder addRemoteRepository(StooqWebReader.TimeSeriesReader reader) {
            final RemoteTicksLoader remoteTicksLoader = new RemoteTicksLoader(reader);

            remoteFetchers.add(remoteTicksLoader);

            return this;
        }
    }

    public NavigableMap<LocalDate, Tick> getTicks(String asset) {
        final NavigableMap<LocalDate, Tick> tickMap = new TreeMap<>();

        tickLoaders.forEach((l) -> l.fetchTicks(asset, tickMap));

        return tickMap;
    }

}