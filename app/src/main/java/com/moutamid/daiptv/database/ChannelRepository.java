package com.moutamid.daiptv.database;

import android.content.Context;

import androidx.paging.DataSource;

import com.moutamid.daiptv.models.ChannelsModel;

public class ChannelRepository {
    private final ChannelsDAO itemDao;

    public ChannelRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        itemDao = db.channelsDAO();
    }

    public DataSource.Factory<Integer, ChannelsModel> getAllItems(String type) {
        return itemDao.getAllItems(type);
    }

    public DataSource.Factory<Integer, ChannelsModel> getItemsByGroup(String group, String type) {
        return itemDao.getAllByGroup(group, type);
    }

}
