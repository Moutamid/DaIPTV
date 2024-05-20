package com.moutamid.daiptv.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.moutamid.daiptv.models.ChannelsFilmsModel;

import java.util.List;

@Dao
public interface FilmsDAO {

    @Insert(onConflict = REPLACE)
    void insert(ChannelsFilmsModel channelsModel);

    @Query("UPDATE films SET channelImg = :link, isPosterUpdated = 1 WHERE ID = :id")
    void update(int id, String link);

    @Query("SELECT * FROM films ORDER BY channelName ASC")
    List<ChannelsFilmsModel> getAll();

    @Query("SELECT * FROM films WHERE channelGroup = :channelGroup AND type= :type ORDER BY channelName ASC")
    List<ChannelsFilmsModel> getAllFilms(String channelGroup, String type);

    @Query("SELECT * FROM films WHERE channelGroup = :channelGroup AND type= :type ORDER BY channelName ASC")
    DataSource.Factory<Integer, ChannelsFilmsModel> getAllByGroup(String channelGroup, String type);

    @Query("SELECT * FROM films WHERE LOWER(channelName) LIKE '%' || LOWER(:query) || '%' LIMIT 20")
    ChannelsFilmsModel getSearchChannel(String query);
}