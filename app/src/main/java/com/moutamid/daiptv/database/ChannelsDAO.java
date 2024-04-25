package com.moutamid.daiptv.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MoviesGroupModel;

import java.util.List;

@Dao
public interface ChannelsDAO {
    @Insert(onConflict = REPLACE)
    void insert(ChannelsModel channelsModel);

    @Query("UPDATE channels SET channelImg = :link, isPosterUpdated = 1 WHERE ID = :id")
    void update(int id, String link);

    @Query("SELECT * FROM channels WHERE type IN (:types) AND type NOT IN (:excludedTypes) ORDER BY channelName ASC")
    List<ChannelsModel> getAllFiltered(List<String> types, List<String> excludedTypes);

    @Query("SELECT * FROM channels WHERE type =:type ORDER BY channelName ASC")
    DataSource.Factory<Integer, ChannelsModel> getAllItems(String type);

    @Query("SELECT * FROM channels WHERE channelGroup = :channelGroup AND type= :type ORDER BY channelName ASC")
    DataSource.Factory<Integer, ChannelsModel> getAllByGroup(String channelGroup, String type);

    @Query("SELECT * FROM channels WHERE channelGroup = :channelGroup ORDER BY RANDOM() LIMIT 1")
    ChannelsModel getRand(String channelGroup);

    @Query("SELECT * FROM channels WHERE LOWER(channelName) LIKE '%' || LOWER(:query) || '%' AND channelGroup = :channelGroup LIMIT 1")
    ChannelsModel getSearchChannel(String query, String channelGroup);

    @Query("SELECT * FROM channels WHERE LOWER(TRIM(channelName)) = LOWER(TRIM(:query))")
    ChannelsModel getSearchChannel(String query);

    @Query("SELECT * FROM channels WHERE LOWER(channelName) LIKE '%' || LOWER(:name) || '%' ORDER BY channelName ASC")
    List<ChannelsModel> getSeasons(String name);

}
