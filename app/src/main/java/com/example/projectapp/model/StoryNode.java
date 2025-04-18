package com.example.projectapp.model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoryNode implements Serializable {
    private String id;
    private final String title;
    private final String context;
    private final ChoiceType type;      // Combat, Exploration, Marchand, Repos
    private final String requiredItem;  // id de l’item requis (ou null)
    private final String newItem;       // id de l’item attribué (ou null)
    private final EndingPoint endingPoint; // Points pour fin
    private List<StoryNode> choices;
    private List<String> choiceIds;     // ids temporaires

    public enum ChoiceType {
        COMBAT,
        EXPLORATION,
        MERCHANT,
        REST,
        ENDING
    }

    public StoryNode(String id, String title, String context, ChoiceType type, String requiredItem, String newItem, EndingPoint endingPoint) {
        this.id = id;
        this.title = title;
        this.context = context;
        this.type = type;
        this.requiredItem = requiredItem;
        this.newItem = newItem;
        this.endingPoint = endingPoint;
        this.choices = new ArrayList<>();
        this.choiceIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getContext() {
        return context;
    }
    public ChoiceType getType() {
        return type;
    }
    public String getRequiredItem() {
        return requiredItem;
    }
    public String getNewItem() {
        return newItem;
    }
    public EndingPoint getEndingPoint() {
        return endingPoint;
    }
    public List<StoryNode> getChoices() {
        return choices;
    }
    public List<String> getChoiceIds() {
        return choiceIds;
    }

    public void addChoice(StoryNode node) {
        choices.add(node);
    }
    public void addChoiceId(String choiceId) {
        choiceIds.add(choiceId);
    }

    public void resolveChoices(Map<String, StoryNode> nodeMap) {
        for (String childId : choiceIds) {
            StoryNode child = nodeMap.get(childId);
            if (child != null) {
                choices.add(child);
            }
        }
        choiceIds.clear();
    }

    public static StoryNode fromJson(JSONObject obj) {
        String id = obj.optString("id");
        String title = obj.optString("title");
        String context = obj.optString("context");
        ChoiceType type;
        try {
            type = ChoiceType.valueOf(obj.optString("type","EXPLORATION").toUpperCase());
        } catch (Exception e) {
            type = ChoiceType.EXPLORATION;
        }

        String requiredItem = obj.optString("requiredItem", null);
        if ("null".equalsIgnoreCase(requiredItem)) {
            requiredItem = null;
        }

        String newItem = obj.optString("newItem", null);
        if ("null".equalsIgnoreCase(newItem)) {
            newItem = null;
        }

        int endingPointValue = obj.optInt("endingPoint", 0);
        EndingPoint.endingPointType endingPointType;
        try {
            endingPointType = EndingPoint.endingPointType.valueOf(obj.optString("endingPointType","MID_END").toUpperCase());
        } catch (Exception e) {
            endingPointType = EndingPoint.endingPointType.MID_END;
        }
        EndingPoint endingPointObj = new EndingPoint(endingPointValue, endingPointType);

        StoryNode node = new StoryNode(
                id, title, context, type, requiredItem, newItem, endingPointObj
        );

        JSONArray jsonChoices = obj.optJSONArray("choices");
        if (jsonChoices != null) {
            for (int i = 0; i < jsonChoices.length(); i++) {
                String childId = jsonChoices.optString(i);
                if (childId != null && !childId.isEmpty()) {
                    node.addChoiceId(childId);
                }
            }
        }
        return node;
    }
}
