package com.example.jrmy.dmhours;

import java.util.List;

/**
 * Created by Jérémy on 12/01/2015.
 */
public class Station {
    private String id;
    private String city;
    private String station;
    private String something;
    private List<Timing> timings;

    public Station(String id, String city, String station, String something, List<Timing> timings) {
        this.id = id;
        this.city = city;
        this.station = station;
        this.something = something;
        this.timings = timings;
    }

    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getStation() {
        return station;
    }

    public String getSomething() {
        return something;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public void setSomething(String something) {
        this.something = something;
    }

    public List<Timing> getTimings() {
        return timings;
    }

    @Override
    public String toString() {
        return station + " : " + city;
    }
}
