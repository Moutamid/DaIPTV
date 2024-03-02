package com.moutamid.daiptv.models;

public class EPGModel {
    public String start, stop, channel, title;

    public EPGModel(String start, String stop, String channel, String title) {
        this.start = start;
        this.stop = stop;
        this.channel = channel;
        this.title = title;
    }
}
