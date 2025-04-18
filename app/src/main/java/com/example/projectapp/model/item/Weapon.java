package com.example.projectapp.model.item;

import org.json.JSONObject;

public class Weapon extends Item {
    private final int damage;

    public enum WeaponType { SWORD, MACE, AXE }

    private final WeaponType type;
    private final int level; // 1, 2 ou 3

    public Weapon(String id, String name, int price, int damage, WeaponType type, int level) {
        super(id, name, price);
        this.damage = damage;
        this.type = type;
        this.level = level;
    }

    public int getDamage() {
        return damage;
    }

    public WeaponType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }


    // Méthode de construction depuis un JSONObject
    public static Weapon fromJson(JSONObject obj) {
        String id = obj.optString("id");
        String name = obj.optString("name");
        int price = obj.optInt("price");
        int damage = obj.optInt("damage");
        int level = obj.optInt("level");
        String typeStr = obj.optString("type", "SWORD");
        WeaponType type;
        try {
            type = WeaponType.valueOf(typeStr.toUpperCase());
        } catch (Exception e) {
            type = WeaponType.SWORD; // Valeur par défaut
        }
        return new Weapon(id, name, price, damage, type, level);
    }
}