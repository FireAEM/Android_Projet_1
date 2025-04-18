package com.example.projectapp.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectapp.R;
import com.example.projectapp.engine.GameEngine;
import com.example.projectapp.engine.GameDataManager;
import com.example.projectapp.model.item.Armor;
import com.example.projectapp.model.item.Item;
import com.example.projectapp.model.item.Weapon;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private TextView textViewArmorInfo;
    private TextView textViewWeaponInfo;
    private ListView listViewItems;
    private Button buttonReturn;
    private InventoryAdapter adapter;
    private GameEngine gameEngine;
    private GameDataManager gameDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Bind views
        textViewArmorInfo   = findViewById(R.id.textViewArmorInfo);
        textViewWeaponInfo  = findViewById(R.id.textViewWeaponInfo);
        listViewItems       = findViewById(R.id.listViewItems);
        buttonReturn        = findViewById(R.id.buttonReturn);

        // Récupérer l'instance de GameEngine
        gameEngine = GameEngine.getInstance(this);
        gameDataManager = GameDataManager.getInstance(this);

        // Afficher les infos de l'équipement
        displayEquippedItems();

        // initialiser l’adapter
        adapter = new InventoryAdapter(this,
                gameDataManager.getPlayer().getInventory().getItems());
        listViewItems.setAdapter(adapter);

        // Afficher la liste des autres items
        // populateOtherItems();

        buttonReturn.setOnClickListener(v -> finish());
    }

    public void updateEquippedDisplay() {
        displayEquippedItems();
    }

    private void displayEquippedItems() {
        Armor armor = gameDataManager.getPlayer().getInventory().getEquippedArmor();
        if (armor != null) {
            String info = "Nom: " + armor.getName() +
                    "\nProtection: " + armor.getProtection() +
                    "\nNiveau: " + armor.getLevel();
            textViewArmorInfo.setText(info);
        } else {
            textViewArmorInfo.setText("Aucune armure équipée");
        }

        Weapon weapon = gameDataManager.getPlayer().getInventory().getEquippedWeapon();
        if (weapon != null) {
            String info = "Nom: " + weapon.getName() +
                    "\nDégâts: " + weapon.getDamage() +
                    "\nNiveau: " + weapon.getLevel() +
                    "\nType: " + weapon.getType();
            textViewWeaponInfo.setText(info);
        } else {
            textViewWeaponInfo.setText("Aucune arme équipée");
        }
    }

    /*private void populateOtherItems() {
        List<Item> allItems = gameDataManager.getPlayer().getInventory().getItems();
        List<String> descriptions = new ArrayList<>();

        for (Item item : allItems) {
            // On évite d'afficher l'arme/armure déjà équipées si elles figurent dans la liste
            boolean isEquipped = (item instanceof Armor &&
                    ((Armor) item).equals(gameDataManager.getPlayer().getInventory().getEquippedArmor()))
                    || (item instanceof Weapon &&
                    ((Weapon) item).equals(gameDataManager.getPlayer().getInventory().getEquippedWeapon()));
            if (isEquipped) continue;

            String line = "Nom: " + item.getName()
                    + " | Prix: " + item.getPrice();
            if (item instanceof Armor) {
                Armor a = (Armor) item;
                line += " | Protection: " + a.getProtection()
                        + " | Niveau: " + a.getLevel();
            } else if (item instanceof Weapon) {
                Weapon w = (Weapon) item;
                line += " | Dégâts: " + w.getDamage()
                        + " | Niveau: " + w.getLevel()
                        + " | Type: " + w.getType();
            }
            descriptions.add(line);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                descriptions
        );
        listViewItems.setAdapter(adapter);
    }*/
}
