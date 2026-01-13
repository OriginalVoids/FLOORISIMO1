package com.example.myapplication;

import java.io.Serializable;
import java.util.List;

public class Client implements Serializable {
    private String name;
    private List<String> structuralWeaknesses;
    private String preferredDesign;
    private int numberOfFloors;
    private int numberFlooringsProvided;

    public Client(String name, List<String> structuralWeaknesses, String preferredDesign, int numberOfFloors, int numberFlooringsProvided) {
        this.name = name;
        this.structuralWeaknesses = structuralWeaknesses;
        this.preferredDesign = preferredDesign;
        this.numberOfFloors = numberOfFloors;
        this.numberFlooringsProvided = numberFlooringsProvided;
    }

    public String getName() { return name; }
    public List<String> getStructuralWeaknesses() { return structuralWeaknesses; }
    public String getPreferredDesign() { return preferredDesign; }
    public int getNumberOfFloors() { return numberOfFloors; }
    public int getNumberFlooringsProvided() { return numberFlooringsProvided; }
}
