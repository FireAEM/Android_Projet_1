package com.example.projectapp.engine;

import android.content.Context;
import android.util.Log;

import com.example.projectapp.model.Player;
import com.example.projectapp.model.item.Armor;
import com.example.projectapp.model.item.Item;
import com.example.projectapp.model.item.Weapon;
import com.example.projectapp.model.StoryNode;
import com.example.projectapp.model.Enemy;
import com.example.projectapp.model.EndingPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GameDataManager {

    private static GameDataManager instance;

    private List<Armor>  armorItems;
    private List<Weapon> weaponItems;
    private List<Item> genericItems;
    private List<Enemy>  enemyItems;
    private List<StoryNode> storyNodes;
    public static Player player;
    private StoryNode rootStoryNode;

    private GameDataManager(Context context) {
        armorItems  = new ArrayList<>();
        weaponItems = new ArrayList<>();
        genericItems  = new ArrayList<>();
        enemyItems  = new ArrayList<>();
        storyNodes  = new ArrayList<>();
        loadData(context);
    }

    public static synchronized GameDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new GameDataManager(context);
        }
        return instance;
    }

    private String readJsonFromAsset(Context context, String fileName) {
        try (InputStream is = context.getAssets().open(fileName)) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void loadData(Context context) {
        loadArmorItems(context);
        loadWeaponItems(context);
        loadGenericItems(context);
        loadEnemyItems(context);
        loadStoryNodes(context);
    }

    private void loadArmorItems(Context context) {
        try {
            String jsonStr = readJsonFromAsset(context, "armors.json");
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Armor armor = Armor.fromJson(obj);
                armorItems.add(armor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWeaponItems(Context context) {
        try {
            String jsonStr = readJsonFromAsset(context, "weapons.json");
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Weapon weapon = Weapon.fromJson(obj);
                weaponItems.add(weapon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGenericItems(Context context) {
        try {
            String jsonStr = readJsonFromAsset(context, "items.json");
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Item item = Item.fromJson(obj);
                genericItems.add(item);
            }
            Log.d("GDM", "Generic items chargés: " + genericItems.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEnemyItems(Context context) {
        try {
            String jsonStr = readJsonFromAsset(context, "enemies.json");
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Enemy enemy = Enemy.fromJson(obj);
                enemyItems.add(enemy);
            }
            Log.d("GameDataManager", "Chargement des ennemis terminé. Nombre d'ennemis : " + enemyItems.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStoryNodes(Context context) {
        try {
            String jsonStr = readJsonFromAsset(context, "storyNodes.json");
            JSONArray jsonArray = new JSONArray(jsonStr);
            Map<String, StoryNode> nodeMap = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                StoryNode node = StoryNode.fromJson(obj);
                nodeMap.put(node.getId(), node);
                storyNodes.add(node);
            }

            // Déterminer le nœud racine (par exemple, celui dont l'id vaut "root")
            rootStoryNode = nodeMap.get("root");
            if (rootStoryNode == null && !storyNodes.isEmpty()) {
                rootStoryNode = storyNodes.get(0);
            }

            // Résoudre les références des nœuds enfants pour chaque StoryNode.
            for (StoryNode node : storyNodes) {
                node.resolveChoices(nodeMap);
            }

            // Ajout du log pour confirmer le chargement
            Log.d("StoryLoader", "Chargement des nœuds d'histoire terminé. Nombre de nœuds : " + storyNodes.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // getters...
    public List<Armor> getArmorItems() {
        return armorItems;
    }
    public List<Weapon> getWeaponItems() {
        return weaponItems;
    }
    public List<Enemy> getEnemyItems() {
        return enemyItems;
    }
    public List<StoryNode> getStoryNodes() {
        return storyNodes;
    }
    public StoryNode getRootStoryNode() {
        return rootStoryNode;
    }
    public int getPlayerLife() {
        return player.getLifePoints();
    }
    public int getPlayerMoney() {
        return player.getMoney();
    }
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player p) {
        player = p;
    }
    public static void resetPlayer() {
        player = null;
    }
    public static synchronized void resetInstance() {
        instance = null;
        player = null;
    }

    /**
     * Cherche l’item (weapon ou armor) et l’ajoute à l’inventaire du joueur
     */
    public boolean grantItemToPlayer(String id) {
        for (Weapon w : weaponItems) {
            if (w.getId().equals(id)) {
                return player.getInventory().addItem(w);
            }
        }
        for (Armor a : armorItems) {
            if (a.getId().equals(id)) {
                return player.getInventory().addItem(a);
            }
        }
        for (Item it : genericItems) {
            if (it.getId().equals(id)) {
                return player.getInventory().addItem(it);
            }
        }
        return false;
    }

    public StoryNode getGameOverNode() {
        for (StoryNode node : storyNodes) {
            if ("game_over".equalsIgnoreCase(node.getId())) {
                return node;
            }
        }
        // Si aucun nœud n'est défini dans le JSON, on crée un nœud par défaut.
        return new StoryNode(
                "game_over",
                "Game Over",
                "Vous êtes mort. Fin de partie.",
                StoryNode.ChoiceType.ENDING,
                null,
                null,
                new EndingPoint(0, EndingPoint.endingPointType.BAD_END)
        );
    }
    public StoryNode getEndingNode(String id) {
        for (StoryNode node : storyNodes) {
            if (id.equalsIgnoreCase(node.getId())) {
                return node;
            }
        }
        return new StoryNode(
                "end_default",
                "Game Over",
                "Fin de partie.",
                StoryNode.ChoiceType.ENDING,
                null,
                null,
                new EndingPoint(0, EndingPoint.endingPointType.BAD_END)
        );
    }
}
