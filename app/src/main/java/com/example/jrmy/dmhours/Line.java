package com.example.jrmy.dmhours;

/**
 * Created by Jérémy on 11/01/2015.
 */
public class Line {
    private String id;
    private String label;

    public Line(String id, String label) {

        this.id = id;
        this.label = label;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {

        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
