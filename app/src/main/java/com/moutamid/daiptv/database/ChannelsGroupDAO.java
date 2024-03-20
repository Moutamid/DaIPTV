package com.moutamid.daiptv.database;
import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.moutamid.daiptv.models.ChannelsGroupModel;
import com.moutamid.daiptv.models.ChannelsModel;

import java.util.List;

@Dao
public interface ChannelsGroupDAO {
    @Insert(onConflict = REPLACE)
    void insert(ChannelsGroupModel groupModel);

    @Query("SELECT * FROM channelsGroups ORDER BY channelGroup ASC")
    List<ChannelsGroupModel> getAll();
}
