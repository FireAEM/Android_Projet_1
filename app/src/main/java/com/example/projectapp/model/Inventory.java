package com.example.projectapp.model;

import com.example.projectapp.model.item.Item;
import com.example.projectapp.model.item.Armor;
import com.example.projectapp.model.item.Weapon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventory implements Serializable {
    private List<Item> items = new ArrayList<>();
    private Weapon equippedWeapon;
    private Armor  equippedArmor;
    public static final int MAX_ITEMS = 10;

    public boolean addItem(Item item) {
        if (items.size() < MAX_ITEMS) {
            return items.add(item);
        }
        return false;
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public void setEquippedArmor(Armor equippedArmor) {
        this.equippedArmor = equippedArmor;
    }

    /**
     * Vérifie la présence de l’item (inventaire + équipé)
     */
    public boolean hasItemById(String id) {
        if (equippedWeapon != null && equippedWeapon.getId().equals(id)) return true;
        if (equippedArmor  != null && equippedArmor.getId().equals(id))  return true;
        for (Item it : items) {
            if (it.getId().equals(id)) return true;
        }
        return false;
    }
}
