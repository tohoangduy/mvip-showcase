package com.mq.myvtg.base.recyclesection.model;

import java.util.ArrayList;
import java.util.List;

public class Header {
    public int id = 0;
    public List<Item> items = null;

    public Header(int id) {
        this.id = id;
    }

    public void addItem(Item item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }
}
