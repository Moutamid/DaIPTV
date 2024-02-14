package com.moutamid.daiptv.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.moutamid.daiptv.database.ChannelRepository;
import com.moutamid.daiptv.models.ChannelsModel;

public class ChannelViewModel extends AndroidViewModel {
    public static final int PAGE_SIZE = 18;
    private final ChannelRepository repository;

    public ChannelViewModel(@NonNull Application application) {
        super(application);
        repository = new ChannelRepository(application);
    }

    public LiveData<PagedList<ChannelsModel>> getAll(String type){
        return new LivePagedListBuilder<>(repository.getAllItems(type),
                new PagedList.Config.Builder()
                        .setPageSize(PAGE_SIZE)
                        .setEnablePlaceholders(true)
                        .build())
                .build();
    }


    public LiveData<PagedList<ChannelsModel>> getItemsByGroup(String group, String type) {
        return new LivePagedListBuilder<>(repository.getItemsByGroup(group, type),
                new PagedList.Config.Builder()
                        .setPageSize(PAGE_SIZE)
                        .setEnablePlaceholders(true)
                        .build())
                .build();
    }
}
