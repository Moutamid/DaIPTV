package com.moutamid.daiptv.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "channelsGroups", indices = @Index(value = "channelGroup", unique = true))
public class ChannelsGroupModel {
    @PrimaryKey(autoGenerate = true)
    int ID = 0;
    @ColumnInfo(name = "channelGroup")
    public String channelGroup;

    public ChannelsGroupModel(String channelGroup) {
        this.channelGroup = channelGroup;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getChannelGroup() {
        return channelGroup;
    }

    public void setChannelGroup(String channelGroup) {
        this.channelGroup = channelGroup;
    }
}
