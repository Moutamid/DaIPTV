package com.moutamid.daiptv.datasourse;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;

import java.util.List;

public class CustomDataSourceSeries extends PositionalDataSource<ChannelsSeriesModel> {
    private List<ChannelsSeriesModel> data;

    public CustomDataSourceSeries(List<ChannelsSeriesModel> data) {
        this.data = data;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ChannelsSeriesModel> callback) {
        int totalCount = data.size();
        int startPosition = computeInitialLoadPosition(params, totalCount);
        int loadSize = computeInitialLoadSize(params, startPosition, totalCount);
        List<ChannelsSeriesModel> sublist = data.subList(startPosition, startPosition + loadSize);
        callback.onResult(sublist, startPosition, totalCount);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ChannelsSeriesModel> callback) {
        int start = params.startPosition;
        int end = start + params.loadSize;
        callback.onResult(data.subList(start, end));
    }
}

