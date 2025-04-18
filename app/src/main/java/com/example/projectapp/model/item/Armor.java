package com.example.projectapp.model.item;

import org.json.JSONObject;

public class Armor extends Item {
    private final int protection;

    // Ici, pour une seule catégorie d’armure à 3 niveaux
    private final int level;

    public Armor(String id, String name, int price, int protection, int level) {
        super(id, name, price);
        this.protection = protection;
        this.level = level;
    }

    public int getProtection() {
        return protection;
    }

    public int getLevel() {
        return level;
    }

    // Factory method pour construire une Armor depuis un JSONObject
    public static Armor fromJson(JSONObject obj) {
        String id = obj.optString("id");
        String name = obj.optString("name");
        int price = obj.optInt("price");
        int protection = obj.optInt("protection");
        int level = obj.optInt("level");
        return new Armor(id, name, price, protection, level);
    }
}