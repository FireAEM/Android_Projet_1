package com.example.projectapp.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.projectapp.model.Enemy;
import com.example.projectapp.model.Player;
import com.example.projectapp.model.item.Armor;
import com.example.projectapp.model.item.Item;
import com.example.projectapp.model.item.Weapon;
import com.example.projectapp.model.item.Weapon.WeaponType;
import com.example.projectapp.model.StoryNode;
import com.google.gson.Gson;

import java.util.List;
import java.util.Random;

public class GameEngine {

    private static GameEngine instance;
    public static final int RESULT_CONTINUE = 0;
    public static final int RESULT_COMBAT = 1;
    public static final int RESULT_MERCHANT = 2;

    private Context context;
    private Player player;
    private Enemy currentEnemy;
    private StoryEngine storyEngine;
    private GameDataManager gameDataManager;

    // Clé pour le SharedPreferences
    private static final String PREFS_NAME = "game_save";
    private static final String KEY_PLAYER = "player";
    private static final String KEY_CURRENT_NODE_ID = "currentNodeId";

    private GameEngine(Context context) {
        this.context = context.getApplicationContext();
        this.gameDataManager = GameDataManager.getInstance(context);

        loadGame();

        if (gameDataManager.getPlayer() == null) {
            Player p = new Player();
            p.getInventory().setEquippedWeapon(
                    new Weapon("1", "Épée de niveau 1", 0, 1, WeaponType.SWORD, 1)
            );
            p.getInventory().setEquippedArmor(
                    new Armor("1", "Armure de niveau 1", 0, 1, 1)
            );
            gameDataManager.setPlayer(p);
        }

        this.storyEngine = StoryEngine.getInstance(context);
        Log.d("GameEngine", "StoryEngine initialisé : " + this.storyEngine);
    }

    public static synchronized GameEngine getInstance(Context context) {
        if (instance == null) {
            instance = new GameEngine(context);
        }
        return instance;
    }

    /**
     * Permet de réinitialiser l'instance, utile pour une "Nouvelle Partie".
     */
    public static synchronized void resetInstance() {
        instance = null;
    }


    // MÉTHODES D'AFFICHAGE POUR L'INTERFACE

    public String getCurrentLocation() {
        return storyEngine.getCurrentLocation();
    }

    public String getCurrentContext() {
        return storyEngine.getCurrentContext();
    }

    public String[] getCurrentChoices() {
        StoryNode currentNode = storyEngine.getCurrentNode();
        List<StoryNode> nodes = currentNode.getChoices();
        String[] choices = new String[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            choices[i] = nodes.get(i).getTitle();
        }
        return choices;
    }

    public StoryNode getCurrentNode() {
        return storyEngine.getCurrentNode();
    }

    public Enemy getCurrentEnemy() {
        return currentEnemy;
    }

    public StoryEngine getStoryEngine() {
        return storyEngine;
    }

    /**
     * Traite le choix narratif effectué par le joueur.
     */
    public int processChoice(String choiceTitle) {
        return storyEngine.processChoice(choiceTitle, context);
    }

    // GESTION DU COMBAT : déléguée au CombatEngine

    // GESTION DES ACHATS : déléguée au PurchaseEngine

    // MÉTHODES DE SAUVEGARDE / CHARGEMENT

    public void saveGame() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String playerJson = gson.toJson(player);
        editor.putString(KEY_PLAYER, playerJson);
        StoryNode currentNode = storyEngine.getCurrentNode();
        if (currentNode != null) {
            editor.putString(KEY_CURRENT_NODE_ID, currentNode.getId());
        }
        editor.apply();
    }

    public void loadGame() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String playerJson = prefs.getString(KEY_PLAYER, null);
        if (playerJson != null) {
            Player saved = gson.fromJson(playerJson, Player.class);
            GameDataManager.getInstance(context).setPlayer(saved);
        }

        String currentNodeId = prefs.getString(KEY_CURRENT_NODE_ID, null);
        Log.d("GameEngine", "Current Node Id : " + currentNodeId);

        if (currentNodeId != null) {
            List<StoryNode> allNodes = GameDataManager.getInstance(context).getStoryNodes();
            StoryEngine engine = StoryEngine.getInstance(context);

            for (StoryNode node : allNodes) {
                if (node.getId().equals(currentNodeId)) {
                    engine.setCurrentNode(node);
                    break;
                }
            }
        }
    }
}
