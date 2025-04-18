package com.example.projectapp.model.item;

import java.io.Serializable;
import org.json.JSONObject;

public class Item implements Serializable {
    protected String id;
    protected String name;
    protected int price;

    public Item(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }

    // Méthode de construction depuis un JSONObject
    public static Item fromJson(JSONObject obj) {
        String id = obj.optString("id");
        String name = obj.optString("name");
        int price = obj.optInt("price");
        return new Item(id, name, price);
    }
}