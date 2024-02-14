package com.moutamid.daiptv.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "seriesGroups", indices = @Index(value = "seriesGroup", unique = true))
public class SeriesGroupModel {
    @PrimaryKey(autoGenerate = true)
    int ID = 0;
    @ColumnInfo(name = "seriesGroup")
    public String seriesGroup;

    public SeriesGroupModel(String seriesGroup) {
        this.seriesGroup = seriesGroup;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getChannelGroup() {
        return seriesGroup;
    }

    public void setChannelGroup(String seriesGroup) {
        this.seriesGroup = seriesGroup;
    }
}
