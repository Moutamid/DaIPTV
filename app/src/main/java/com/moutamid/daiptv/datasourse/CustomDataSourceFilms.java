package com.moutamid.daiptv.datasourse;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.moutamid.daiptv.models.ChannelsFilmsModel;
import com.moutamid.daiptv.models.ChannelsFilmsModel;

import java.util.List;

public class CustomDataSourceFilms extends PositionalDataSource<ChannelsFilmsModel> {
    private List<ChannelsFilmsModel> data;

    public CustomDataSourceFilms(List<ChannelsFilmsModel> data) {
        this.data = data;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ChannelsFilmsModel> callback) {
        int totalCount = data.size();
        int startPosition = computeInitialLoadPosition(params, totalCount);
        int loadSize = computeInitialLoadSize(params, startPosition, totalCount);
        List<ChannelsFilmsModel> sublist = data.subList(startPosition, startPosition + loadSize);
        callback.onResult(sublist, startPosition, totalCount);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ChannelsFilmsModel> callback) {
        int start = params.startPosition;
        int end = start + params.loadSize;
        callback.onResult(data.subList(start, end));
    }
}

