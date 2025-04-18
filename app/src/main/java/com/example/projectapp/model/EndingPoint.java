package com.example.projectapp.model;

import java.io.Serializable;

public class EndingPoint implements Serializable {
    private final int points;
    private final endingPointType type;
    public enum endingPointType {
        GOOD_END,
        MID_END,
        BAD_END
    }

    public EndingPoint(int points, endingPointType type) {
        this.points = points;
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public endingPointType getType() {
        return type;
    }
}
