package com.moutamid.daiptv.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

@Root(name = "programme")
public class ProgrammeModel {
    @Element(name = "start")
    private String start;

    @Element(name = "stop")
    private String stop;

    @Element(name = "channel")
    private String channel;

    @Element(name = "title")
    private String title;

    @Element(name = "desc")
    private String description;

    public String getStart() {
        return start;
    }

    public String getStop() {
        return stop;
    }

    public String getChannel() {
        return channel;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
