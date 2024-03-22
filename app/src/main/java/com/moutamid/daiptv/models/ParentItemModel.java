package com.moutamid.daiptv.models;

import java.util.ArrayList;

public class ParentItemModel {

    public String name;
    public boolean isRoom;
    ArrayList<MovieModel> list;

    public ParentItemModel(String name, boolean isRoom) {
        this.name = name;
        this.isRoom = isRoom;
    }

    public ParentItemModel(String name, boolean isRoom, ArrayList<MovieModel> list) {
        this.name = name;
        this.isRoom = isRoom;
        this.list = list;
    }
}
