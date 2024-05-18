package com.moutamid.daiptv.utilis;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import com.moutamid.daiptv.models.ChannelsModel;

import java.util.ArrayList;
import java.util.List;

public class SeriesGrouper {
    private static final String TAG = "SeriesGrouper";
    public static ArrayList<String> groupSeries(ArrayList<ChannelsModel> seriesList) {
        ArrayList<String> groupedSeries = new ArrayList<>();
        String currentSeries = "";
        int count = 0;

        for (ChannelsModel series : seriesList) {
            String seriesName = series.getChannelName().split(" ")[0];
            if (!seriesName.equals(currentSeries)) {
                currentSeries = seriesName;
                Log.d(TAG, "groupSeries: " + currentSeries);
                groupedSeries.add(currentSeries);
                count++;
            }
//            if (count == 3) {
//                return groupedSeries;
//            }
        }
        return groupedSeries;
    }

//    public static PagedList<String> groupSeriesPaged(PagedList<String> seriesPagedList) {
//        List<String> seriesList = new ArrayList<>();
//        String currentSeries = null;
//        int count = 0;
//
//        for (String series : seriesPagedList) {
//            String seriesName = series.split(" ")[0];
//            if (!seriesName.equals(currentSeries)) {  // New series encountered
//                currentSeries = seriesName;
//                seriesList.add(currentSeries);
//                count++;
//            }
//            if (count == 3) {  // Reached desired number of series
//                break;
//            }
//        }
//
//        // Create a PagedListBuilder with a custom DataSourceFactory
//        PagedList.Config config = seriesPagedList.getConfig();
//        return new PagedList.Builder<>(new SeriesDataSourceFactory(seriesList), config).build();
//    }
//
//    private static class SeriesDataSourceFactory implements DataSource.Factory<String> {
//
//        private List<String> seriesList;
//
//        public SeriesDataSourceFactory(List<String> seriesList) {
//            this.seriesList = seriesList;
//        }
//
//        @Override
//        public DataSource<String> create() {
//            return new ItemKeyedDataSource<String>(seriesList);
//        }
//    }
//
//    private class ItemKeyedDataSource<T> extends PageKeyedDataSource<String> {
//
//        private List<String> seriesList;
//
//        public ItemKeyedDataSource(List<String> seriesList) {
//            this.seriesList = seriesList;
//        }
//
//        @Override
//        public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<String> callback) {
//            int size = Math.min(params.requestedLoadSize, seriesList.size());
//            callback.onResponse(seriesList.subList(0, size), null, null);
//        }
//
//        @Override
//        public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String> callback) {
//            // No further loading required as we only want the first 3 items
//            callback.onInvalidData();
//        }
//
//        @Override
//        public String getKey(String item) {
//            return item;
//        }
//    }
}

