package com.moutamid.daiptv.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.database.ChannelRepository;
import com.moutamid.daiptv.datasourse.CustomDataSourceFilms;
import com.moutamid.daiptv.models.ChannelsFilmsModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.CustomArrayListLiveData;
import com.moutamid.daiptv.datasourse.CustomDataSource;
import com.moutamid.daiptv.datasourse.CustomDataSourceSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ChannelViewModel extends AndroidViewModel {
    public static final int PAGE_SIZE = 18;
    private final ChannelRepository repository;

    public ChannelViewModel(@NonNull Application application) {
        super(application);
        repository = new ChannelRepository(application);
    }

    public LiveData<PagedList<ChannelsSeriesModel>> getSeries(String group, String type) {
        return new LivePagedListBuilder<>(repository.getSeriesByGroup(group, type),
                new PagedList.Config.Builder()
                        .setPageSize(PAGE_SIZE)
                        .setEnablePlaceholders(true)
                        .build())
                .build();
    }
    public LiveData<PagedList<ChannelsFilmsModel>> getFilms(String group, String type) {
        return new LivePagedListBuilder<>(repository.getFilmByGroup(group, type),
                new PagedList.Config.Builder()
                        .setPageSize(PAGE_SIZE)
                        .setEnablePlaceholders(true)
                        .build())
                .build();
    }

    public LiveData<PagedList<ChannelsModel>> getAll(String type) {
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


    public LiveData<PagedList<ChannelsModel>> getRecentChannels() {
        return new LivePagedListBuilder<>(repository.getRecentItems(),
                new PagedList.Config.Builder()
                        .setPageSize(PAGE_SIZE)
                        .setEnablePlaceholders(true)
                        .build())
                .build();
    }

    private static final String TAG = "ChannelViewModel";

    public LiveData<PagedList<ChannelsFilmsModel>> getTopFilms() {
        ArrayList<ChannelsFilmsModel> fvrt = Stash.getArrayList(Constants.TOP_FILMS, ChannelsFilmsModel.class);
        Log.d(TAG, "getTopFilms: " + fvrt.size());
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(PAGE_SIZE)
                .build();

        PagedList<ChannelsFilmsModel> pagedList = new PagedList.Builder<>(new CustomDataSourceFilms(fvrt), config)
                .setNotifyExecutor(Executors.newSingleThreadExecutor())
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();

        return new LivePagedListBuilder<>(new DataSource.Factory<Integer, ChannelsFilmsModel>() {
            @Override
            public DataSource<Integer, ChannelsFilmsModel> create() {
                return new CustomDataSourceFilms(fvrt);
            }
        }, config).build();
    }

    public LiveData<PagedList<ChannelsSeriesModel>> getTopSeries() {
        ArrayList<ChannelsSeriesModel> fvrt = Stash.getArrayList(Constants.TOP_SERIES, ChannelsSeriesModel.class);
        Log.d(TAG, "getTopFilms: " + fvrt.size());
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(PAGE_SIZE)
                .build();

        PagedList<ChannelsSeriesModel> pagedList = new PagedList.Builder<>(new CustomDataSourceSeries(fvrt), config)
                .setNotifyExecutor(Executors.newSingleThreadExecutor())
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();

        return new LivePagedListBuilder<>(new DataSource.Factory<Integer, ChannelsSeriesModel>() {
            @Override
            public DataSource<Integer, ChannelsSeriesModel> create() {
                return new CustomDataSourceSeries(fvrt);
            }
        }, config).build();
    }

    public LiveData<PagedList<ChannelsModel>> getFavoriteChannels(String type) {
        UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
        ArrayList<ChannelsModel> fvrt = Stash.getArrayList(userModel.id, ChannelsModel.class);
        List<ChannelsModel> data = new ArrayList<>();
        CustomArrayListLiveData<ChannelsModel> recentChannelsLiveData = new CustomArrayListLiveData<>();
        for (ChannelsModel model : fvrt) {
            if (type.equals(model.getType()))
                data.add(model);
        }
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(PAGE_SIZE)
                .build();

        PagedList<ChannelsModel> pagedList = new PagedList.Builder<>(new CustomDataSource(data), config)
                .setNotifyExecutor(Executors.newSingleThreadExecutor())
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();

        return new LivePagedListBuilder<>(new DataSource.Factory<Integer, ChannelsModel>() {
            @Override
            public DataSource<Integer, ChannelsModel> create() {
                return new CustomDataSource(data);
            }
        }, config).build();
    }
}
