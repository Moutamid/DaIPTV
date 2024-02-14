package com.moutamid.daiptv.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "moviesGroups", indices = @Index(value = "movieGroup", unique = true))
public class MoviesGroupModel {
    @PrimaryKey(autoGenerate = true)
    int ID = 0;
    @ColumnInfo(name = "movieGroup")
    public String movieGroup;

    public MoviesGroupModel(String movieGroup) {
        this.movieGroup = movieGroup;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getChannelGroup() {
        return movieGroup;
    }

    public void setChannelGroup(String movieGroup) {
        this.movieGroup = movieGroup;
    }
}
