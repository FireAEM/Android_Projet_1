package com.example.projectapp.engine;

import android.content.Context;
import android.widget.Toast;
import com.example.projectapp.model.EndingPoint;
import com.example.projectapp.model.StoryNode;
import com.example.projectapp.model.StoryNode.ChoiceType;
import java.util.ArrayList;
import java.util.List;

public class StoryEngine {

    private static StoryEngine instance;
    private StoryNode rootNode;
    private StoryNode currentNode;
    private GameDataManager gameDataManager;

    public static final int RESULT_CONTINUE = 0;
    public static final int RESULT_COMBAT   = 1;
    public static final int RESULT_MERCHANT = 2;

    private int goodEndPoints = 0;
    private int midEndPoints  = 0;
    private int badEndPoints  = 0;


    public StoryEngine(Context context) {
        gameDataManager = GameDataManager.getInstance(context);
        List<StoryNode> allNodes = gameDataManager.getStoryNodes();
        if (allNodes == null || allNodes.isEmpty()) {
            throw new RuntimeException("Erreur de chargement de l'arbre narratif !");
        }
        rootNode = gameDataManager.getRootStoryNode();
        // via setCurrentNode pour déclencher newItem du root
        setCurrentNode(rootNode);
    }

    public static synchronized StoryEngine getInstance(Context context) {
        if (instance == null) {
            instance = new StoryEngine(context);
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    public String getCurrentLocation() {
        return currentNode.getTitle();
    }

    public String getCurrentContext() {
        return currentNode.getContext();
    }

    public StoryNode getCurrentNode() {
        return currentNode;
    }

    /**
     * Dès qu'on change de nœud, on ajoute l'item s'il y en a un (newItem).
     */
    public void setCurrentNode(StoryNode node) {
        currentNode = node;
        // Ne donner l'item que si le joueur est déjà créé
        if (gameDataManager.getPlayer() != null) {
            String newId = node.getNewItem();
            if (newId != null) {
                boolean added = gameDataManager.grantItemToPlayer(newId);
            }
        }
    }

    public void addEndingPoints(EndingPoint ep) {
        switch (ep.getType()) {
            case GOOD_END:
                goodEndPoints += ep.getPoints();
                break;
            case MID_END:
                midEndPoints += ep.getPoints();
                break;
            case BAD_END:
                badEndPoints += ep.getPoints();
                break;
        }
    }

    public String determineEnding() {
        int bestScore = Math.max(goodEndPoints, Math.max(midEndPoints, badEndPoints));
        if (goodEndPoints == bestScore) {
            return "ending_good";
        } else if (midEndPoints == bestScore) {
            return "ending_mid";
        } else {
            return "ending_bad";
        }
    }

    public void addEndingChoice() {
        if (currentNode.getType() == ChoiceType.ENDING) {
            return;
        }
        if (currentNode.getChoices().isEmpty()) {
            String endingId = determineEnding();
            StoryNode endingNode = gameDataManager.getEndingNode(endingId);
            currentNode.addChoice(endingNode);
        }
    }

    public List<StoryNode> getAvailableChoices() {
        List<StoryNode> avail = new ArrayList<>();
        for (StoryNode c : currentNode.getChoices()) {
            String req = c.getRequiredItem();
            if (req == null
                    || gameDataManager.getPlayer().getInventory().hasItemById(req)) {
                avail.add(c);
            }
        }
        return avail;
    }

    public int processChoice(String choiceTitle, Context context) {
        for (StoryNode target : currentNode.getChoices()) {
            if (target.getTitle().equalsIgnoreCase(choiceTitle)) {
                String req = target.getRequiredItem();
                if (req != null
                        && !gameDataManager.getPlayer().getInventory().hasItemById(req)) {
                    Toast.makeText(
                            context,
                            "Il vous faut " + req + " pour choisir ceci",
                            Toast.LENGTH_SHORT
                    ).show();
                    return RESULT_CONTINUE;
                }
                addEndingPoints(target.getEndingPoint());
                setCurrentNode(target);
                if (target.getType() == ChoiceType.REST) {
                    gameDataManager.getPlayer().addLifePoints(5);
                }
                switch (target.getType()) {
                    case COMBAT: return RESULT_COMBAT;
                    case MERCHANT: return RESULT_MERCHANT;
                    default: return RESULT_CONTINUE;
                }
            }
        }
        // Toast.makeText(context, "Choix invalide", Toast.LENGTH_SHORT).show();
        return RESULT_CONTINUE;
    }
}
