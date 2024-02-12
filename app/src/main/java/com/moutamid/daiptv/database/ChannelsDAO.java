package com.moutamid.daiptv.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.moutamid.daiptv.models.ChannelsModel;

import java.util.List;

@Dao
public interface ChannelsDAO {
    @Insert(onConflict = REPLACE)
    void insert(ChannelsModel channelsModel);

    @Query("SELECT * FROM channels ORDER BY ID DESC")
    List<ChannelsModel> getAll();

    @Query("SELECT * FROM channels WHERE channelGroup = :channelGroup ORDER BY ID DESC")
    List<ChannelsModel> getAllByGroup(String channelGroup);

    @Delete
    void Delete(ChannelsModel note);
}
