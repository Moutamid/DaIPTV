package com.moutamid.daiptv.models;

import java.util.ArrayList;

public class ParentItemModel {

    public String name;
    public ArrayList<Integer> items;

    public ParentItemModel(String name, ArrayList<Integer> items) {
        this.name = name;
        this.items = items;
    }
}
