package com.moutamid.daiptv.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "channels")
public class ChannelsModel {

    @PrimaryKey(autoGenerate = true)
    int ID = 0;

    @ColumnInfo(name = "channelName")
    public String channelName;

    @ColumnInfo(name = "channelUrl")
    public String channelUrl;

    @ColumnInfo(name = "channelImg")
    public String channelImg;

    @ColumnInfo(name = "channelGroup")
    public String channelGroup;

    @ColumnInfo(name = "channelDrmKey")
    public String channelDrmKey;

    @ColumnInfo(name = "channelDrmType")
    public String channelDrmType;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public String getChannelImg() {
        return channelImg;
    }

    public void setChannelImg(String channelImg) {
        this.channelImg = channelImg;
    }

    public String getChannelGroup() {
        return channelGroup;
    }

    public void setChannelGroup(String channelGroup) {
        this.channelGroup = channelGroup;
    }

    public String getChannelDrmKey() {
        return channelDrmKey;
    }

    public void setChannelDrmKey(String channelDrmKey) {
        this.channelDrmKey = channelDrmKey;
    }

    public String getChannelDrmType() {
        return channelDrmType;
    }

    public void setChannelDrmType(String channelDrmType) {
        this.channelDrmType = channelDrmType;
    }
}
