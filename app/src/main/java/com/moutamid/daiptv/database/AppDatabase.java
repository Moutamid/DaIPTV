package com.moutamid.daiptv.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.moutamid.daiptv.models.ChannelsGroupModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MoviesGroupModel;
import com.moutamid.daiptv.models.SeriesGroupModel;

@Database(entities = {ChannelsModel.class, ChannelsGroupModel.class, SeriesGroupModel.class, MoviesGroupModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase database;
    private static String Channels_DATABASE = "Channels_DATABASE";

    public synchronized static AppDatabase getInstance(Context context){
        if (database == null){
            database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, Channels_DATABASE)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract ChannelsDAO channelsDAO();
    public abstract ChannelsGroupDAO channelsGroupDAO();
    public abstract MoviesGroupDAO moviesGroupDAO();
    public abstract SeriesGroupDAO seriesGroupDAO();
}
