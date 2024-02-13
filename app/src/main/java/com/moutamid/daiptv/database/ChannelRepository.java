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

    public DataSource.Factory<Integer, ChannelsModel> getAllItems() {
        return itemDao.getAllItems();
    }

    public DataSource.Factory<Integer, ChannelsModel> getItemsByGroup(String group) {
        return itemDao.getAllByGroup(group);
    }

}
