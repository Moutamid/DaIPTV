package com.moutamid.daiptv.utilis;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.moutamid.daiptv.models.ChannelsModel;

import java.util.List;

public class CustomDataSource extends PositionalDataSource<ChannelsModel> {
    private List<ChannelsModel> data;

    public CustomDataSource(List<ChannelsModel> data) {
        this.data = data;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ChannelsModel> callback) {
        int totalCount = data.size();
        int startPosition = computeInitialLoadPosition(params, totalCount);
        int loadSize = computeInitialLoadSize(params, startPosition, totalCount);
        List<ChannelsModel> sublist = data.subList(startPosition, startPosition + loadSize);
        callback.onResult(sublist, startPosition, totalCount);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ChannelsModel> callback) {
        int start = params.startPosition;
        int end = start + params.loadSize;
        callback.onResult(data.subList(start, end));
    }
}

