package com.example.projectapp.model;

import java.io.Serializable;

public class Player implements Serializable {
    private int lifePoints = 20;
    private int money = 10;
    private Inventory inventory;

    public Player() {
        inventory = new Inventory();
    }

    public int getLifePoints() {
        return lifePoints;
    }
    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }

    public void addLifePoints(int lifePoints) {
        setLifePoints(Math.min(this.lifePoints + lifePoints, 20));
    }

    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
