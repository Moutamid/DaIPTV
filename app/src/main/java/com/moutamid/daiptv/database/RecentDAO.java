package com.moutamid.daiptv.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MoviesGroupModel;

import java.util.List;

@Dao
public interface RecentDAO {
    @Insert(onConflict = REPLACE)
    void insert(ChannelsModel channelsModel);

    @Query("SELECT * FROM recent_channels ORDER BY ID DESC")
    DataSource.Factory<Integer, ChannelsModel> getAll();
}
