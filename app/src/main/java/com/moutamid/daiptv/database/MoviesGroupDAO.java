package com.moutamid.daiptv.database;
import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.moutamid.daiptv.models.ChannelsGroupModel;
import com.moutamid.daiptv.models.MoviesGroupModel;

import java.util.List;

@Dao
public interface MoviesGroupDAO {
    @Insert(onConflict = REPLACE)
    void insert(MoviesGroupModel moviesGroups);

    @Query("SELECT * FROM moviesGroups ORDER BY ID DESC")
    List<MoviesGroupModel> getAll();

}
