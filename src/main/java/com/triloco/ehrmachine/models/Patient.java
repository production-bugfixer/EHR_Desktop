package com.triloco.ehrmachine.models;

public class Patient {
    private String name;
    private String lastVisit;

    public Patient(String name, String lastVisit) {
        this.name = name;
        this.lastVisit = lastVisit;
    }

    public String getName() { return name; }
    public String getLastVisit() { return lastVisit; }
}
