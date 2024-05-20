package com.moutamid.daiptv.database;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.DataSource;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.models.ChannelsFilmsModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.CustomArrayListLiveData;

import java.util.ArrayList;
import java.util.List;

public class ChannelRepository {
    private final ChannelsDAO itemDao;
    private final SeriesDAO seriesDAO;
    private final FilmsDAO filmsDAO;
    private final RecentDAO recentDao;

    public ChannelRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        itemDao = db.channelsDAO();
        recentDao = db.recentDAO();
        seriesDAO = db.seriesDAO();
        filmsDAO = db.filmsDAO();
    }

    public DataSource.Factory<Integer, ChannelsModel> getAllItems(String type) {
        return itemDao.getAllItems(type);
    }

    public DataSource.Factory<Integer, ChannelsModel> getItemsByGroup(String group, String type) {
        return itemDao.getAllByGroup(group, type);
    }

    public DataSource.Factory<Integer, ChannelsSeriesModel> getSeriesByGroup(String group, String type) {
        return seriesDAO.getAllByGroup(group, type);
    }
    public List<ChannelsSeriesModel> getAllSeries(String group, String type) {
        return seriesDAO.getAllSeries(group, type);
    }
    public List<ChannelsFilmsModel> getAllFilms(String group, String type) {
        return filmsDAO.getAllFilms(group, type);
    }
    public DataSource.Factory<Integer, ChannelsFilmsModel> getFilmByGroup(String group, String type) {
        return filmsDAO.getAllByGroup(group, type);
    }

    public DataSource.Factory<Integer, ChannelsModel> getRecentItems() {
        return recentDao.getAll();
    }

}
