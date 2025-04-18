package com.example.projectapp.model;

import org.json.JSONObject;

public class Enemy {
    private final String id;
    private final String name;
    private int lifePoints;
    private final int damage;
    private final int armor;

    public Enemy(String id, String name, int lifePoints, int damage, int armor) {
        this.id = id;
        this.name = name;
        this.lifePoints = lifePoints;
        this.damage = damage;
        this.armor = armor;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getLifePoints() {
        return lifePoints;
    }
    public int getDamage() {
        return damage;
    }
    public int getArmor() {
        return armor;
    }

    public void takeDamage(int dmg) {
        lifePoints -= dmg;
    }

    // Méthode de construction depuis un JSONObject
    public static Enemy fromJson(JSONObject obj) {
        String id = obj.optString("id");
        String name = obj.optString("name");
        int lifePoints = obj.optInt("lifePoints");
        int damage = obj.optInt("damage");
        int armor = obj.optInt("armor");
        return new Enemy(id, name, lifePoints, damage, armor);
    }
}