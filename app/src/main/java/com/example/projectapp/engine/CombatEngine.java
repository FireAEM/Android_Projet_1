package com.example.projectapp.engine;

import android.content.Context;
import android.util.Log;
import com.example.projectapp.model.Enemy;
import com.example.projectapp.model.StoryNode;

import java.util.List;
import java.util.Random;

public class CombatEngine {

    private static CombatEngine instance;
    private Context context;
    private GameEngine gameEngine;
    private StoryEngine storyEngine;
    private GameDataManager gameDataManager;
    private Enemy currentEnemy;

    private CombatEngine(Context context) {
        this.context = context;
        this.gameEngine = GameEngine.getInstance(context);
        this.storyEngine = StoryEngine.getInstance(context);
        this.gameDataManager = GameDataManager.getInstance(context);
    }

    public static synchronized CombatEngine getInstance(Context context) {
        if (instance == null) {
            instance = new CombatEngine(context);
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    public String getEnemyInfo() {
        Log.d("GameEngine", "Current Enemy : " + this.currentEnemy);
        if (this.currentEnemy != null) {
            return this.currentEnemy.getName() +
                    " - PV: " + this.currentEnemy.getLifePoints() +
                    " Dégâts: " + this.currentEnemy.getDamage() +
                    " Armure: " + this.currentEnemy.getArmor();
        }
        return "";
    }

    /**
     * Retourne un texte indiquant l'action de l'ennemi (pour le contexte combat).
     */
    public String getCombatContext() {
        return (this.currentEnemy != null) ? "Le " + this.currentEnemy.getName() + " attaque!" : "";
    }

    public Enemy getCurrentEnemy() {
        return currentEnemy;
    }

    public void spawnCurrentEnemy() {
        List<Enemy> enemyList = GameDataManager.getInstance(context).getEnemyItems();
        if (enemyList != null && !enemyList.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(enemyList.size());
            this.currentEnemy = enemyList.get(index);
        }
    }

    public boolean playerAttack() {
        if (currentEnemy != null) {
            int dmg = gameDataManager.getPlayer().getInventory().getEquippedWeapon().getDamage();
            currentEnemy.takeDamage(dmg);
            Log.d("CombatEngine", "Le joueur inflige " + dmg + " dégât(s) à " + currentEnemy.getName());
            if (currentEnemy.getLifePoints() > 0) {
                int damageTaken = Math.max(currentEnemy.getDamage() -
                        gameDataManager.getPlayer().getInventory().getEquippedArmor().getProtection(), 0);
                gameDataManager.getPlayer().setLifePoints(gameDataManager.getPlayer().getLifePoints() - damageTaken);
                Log.d("CombatEngine", currentEnemy.getName() + " contre-attaque : " + damageTaken + " dégât(s) subis");
            }
            gameEngine.saveGame();
            return currentEnemy.getLifePoints() <= 0;
        }
        return false;
    }

    public void playerBlock() {
        if (currentEnemy != null) {
            int damageTaken = Math.max(currentEnemy.getDamage() -
                    gameDataManager.getPlayer().getInventory().getEquippedArmor().getProtection() - 2, 0);
            gameDataManager.getPlayer().setLifePoints(gameDataManager.getPlayer().getLifePoints() - damageTaken);
            Log.d("CombatEngine", "Blocage : " + damageTaken + " dégât(s) subis");
            gameEngine.saveGame();
        }
    }

    /**
     * Vérifie si le joueur est mort et, le cas échéant, définit le nœud Game Over.
     */
    public boolean checkGameOver() {
        if (gameDataManager.getPlayer().getLifePoints() <= 0) {
            StoryNode gameOverNode = GameDataManager.getInstance(context).getGameOverNode();
            if (gameOverNode != null) {
                storyEngine.setCurrentNode(gameOverNode);
                Log.d("CombatEngine", "Game Over défini : " + gameOverNode.getTitle());
                return true;
            }
        }
        return false;
    }

    public boolean checkVictory() {
        return currentEnemy.getLifePoints() <= 0;
    }
}
