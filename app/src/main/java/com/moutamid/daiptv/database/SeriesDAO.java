package com.moutamid.daiptv.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;

import java.util.List;

@Dao
public interface SeriesDAO {

    @Insert(onConflict = REPLACE)
    void insert(ChannelsSeriesModel channelsModel);

    @Query("UPDATE series SET channelImg = :link, isPosterUpdated = 1 WHERE ID = :id")
    void update(int id, String link);

    @Query("SELECT * FROM series ORDER BY channelName ASC")
    List<ChannelsSeriesModel> getAll();

    @Query("SELECT * FROM series WHERE channelGroup = :channelGroup AND type= :type ORDER BY channelName ASC")
    DataSource.Factory<Integer, ChannelsSeriesModel> getAllByGroup(String channelGroup, String type);

}
