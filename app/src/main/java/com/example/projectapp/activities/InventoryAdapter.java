package com.example.projectapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.projectapp.R;
import com.example.projectapp.engine.GameDataManager;
import com.example.projectapp.engine.GameEngine;
import com.example.projectapp.model.item.Armor;
import com.example.projectapp.model.item.Item;
import com.example.projectapp.model.item.Weapon;

import java.util.List;

public class InventoryAdapter extends ArrayAdapter<Item> {
    private final Context ctx;
    private final List<Item> items;
    private final GameDataManager dataManager;
    private final GameEngine gameEngine;

    public InventoryAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.ctx = context;
        this.items = items;
        this.dataManager = GameDataManager.getInstance(context);
        this.gameEngine = GameEngine.getInstance(context);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx)
                    .inflate(R.layout.item_inventory_row, parent, false);
        }

        Item item = items.get(pos);
        TextView tv = convertView.findViewById(R.id.textItemInfo);
        Button btnEquip = convertView.findViewById(R.id.buttonEquip);
        Button btnDelete = convertView.findViewById(R.id.buttonDelete);

        // Construire la ligne d'infos
        String desc = "Nom: " + item.getName() +
                " | Prix: " + item.getPrice();
        if (item instanceof Armor) {
            Armor a = (Armor) item;
            desc += " | Prot: " + a.getProtection() +
                    " | Nv: " + a.getLevel();
        } else if (item instanceof Weapon) {
            Weapon w = (Weapon) item;
            desc += " | Dégâts: " + w.getDamage() +
                    " | Nv: " + w.getLevel() +
                    " | Type: " + w.getType();
        }
        tv.setText(desc);

        // État du bouton Équiper : masquer si l'objet n'est ni Armor ni Weapon
        boolean isEquipable = (item instanceof Armor) || (item instanceof Weapon);
        btnEquip.setVisibility(isEquipable ? View.VISIBLE : View.GONE);

        // Désactiver Équiper si déjà équipé
        boolean isEquipped = (item instanceof Armor &&
                item.equals(dataManager.getPlayer().getInventory().getEquippedArmor()))
                || (item instanceof Weapon &&
                item.equals(dataManager.getPlayer().getInventory().getEquippedWeapon()));
        btnEquip.setEnabled(isEquipable && !isEquipped);

        // Listener Équiper
        btnEquip.setOnClickListener(v -> {
            if (item instanceof Armor) {
                Armor previous = dataManager.getPlayer()
                        .getInventory().getEquippedArmor();
                dataManager.getPlayer().getInventory().setEquippedArmor((Armor) item);
                if (previous != null) items.add(previous);
            } else if (item instanceof Weapon) {
                Weapon previous = dataManager.getPlayer()
                        .getInventory().getEquippedWeapon();
                dataManager.getPlayer().getInventory().setEquippedWeapon((Weapon) item);
                if (previous != null) items.add(previous);
            }
            items.remove(item);
            gameEngine.saveGame();
            notifyDataSetChanged();
            ((InventoryActivity)ctx).updateEquippedDisplay();
        });

        // Listener Supprimer (toujours présent)
        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(ctx)
                .setTitle("Supprimer l'objet")
                .setMessage("Êtes-vous sûr de vouloir supprimer " + item.getName() + " ?")
                .setPositiveButton("Oui", (d, w) -> {
                    items.remove(item);
                    dataManager.getPlayer().getInventory().removeItem(item);
                    gameEngine.saveGame();
                    notifyDataSetChanged();
                })
                .setNegativeButton("Non", null)
                .show()
        );

        return convertView;
    }
}
