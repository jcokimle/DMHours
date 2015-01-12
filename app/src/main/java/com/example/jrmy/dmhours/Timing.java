package com.example.jrmy.dmhours;

/**
 * Created by Jérémy on 12/01/2015.
 */
public class Timing {
    private String c;
    private String h;
    private String sens;

    public Timing(String c, String h, String sens) {
        this.c = c;
        this.h = h;
        this.sens = sens;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getSens() {
        return sens;
    }

    public void setSens(String sens) {
        this.sens = sens;
    }

    @Override
    public String toString() {
        return h;
    }
}
