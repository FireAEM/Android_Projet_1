package com.example.projectapp.engine;

import android.content.Context;
import android.widget.Toast;

import com.example.projectapp.model.item.Armor;
import com.example.projectapp.model.item.Item;
import com.example.projectapp.model.item.Weapon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurchaseEngine {

    private static PurchaseEngine instance;
    private Context context;
    private GameEngine gameEngine;
    private GameDataManager gameDataManager;
    private static final int DEFAULT_SALE_COUNT = 3;

    private PurchaseEngine(Context context) {
        this.context = context;
        this.gameEngine = GameEngine.getInstance(context);
        this.gameDataManager = GameDataManager.getInstance(context);
    }

    public static synchronized PurchaseEngine getInstance(Context context) {
        if (instance == null) {
            instance = new PurchaseEngine(context);
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    public List<Item> getItemsForSale() {
        List<Item> all = new ArrayList<>();
        all.addAll(gameDataManager.getArmorItems());
        all.addAll(gameDataManager.getWeaponItems());
        Collections.shuffle(all);
        // on retourne au plus DEFAULT_SALE_COUNT éléments
        return all.subList(0, Math.min(DEFAULT_SALE_COUNT, all.size()));
    }

    /**
     * Tente d'acheter l'item donné.
     * Affiche un Toast si le joueur n'a pas assez d'or.
     */
    public boolean attemptPurchase(Item item) {
        if (gameDataManager.getPlayer().getMoney() >= item.getPrice()) {
            gameDataManager.getPlayer().setMoney(
                    gameDataManager.getPlayer().getMoney() - item.getPrice()
            );
            gameDataManager.getPlayer().getInventory().addItem(item);
            gameEngine.saveGame();
            return true;
        } else {
            Toast.makeText(
                    context,
                    "Pas assez d'or pour acheter " + item.getName(),
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
    }

    /**
     * Permet de continuer l'histoire après une phase d'achat.
     * Ici, on fait simplement revenir à GameActivity.
     */
    public void continueAfterPurchase() {
        // Si tu veux implémenter une logique de transition automatique, fais-le ici.
        // Par exemple :
        // gameEngine.getStoryEngine().setCurrentNode(nextNode);
        // Dans cette implémentation de base, la simple fermeture de PurchaseActivity suffit.
    }
}
