package com.moutamid.daiptv.database;
import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.moutamid.daiptv.models.MoviesGroupModel;
import com.moutamid.daiptv.models.SeriesGroupModel;

import java.util.List;

@Dao
public interface SeriesGroupDAO {
    @Insert(onConflict = REPLACE)
    void insert(SeriesGroupModel seriesGroups);

    @Query("SELECT * FROM seriesGroups ORDER BY ID DESC")
    List<SeriesGroupModel> getAll();
}
