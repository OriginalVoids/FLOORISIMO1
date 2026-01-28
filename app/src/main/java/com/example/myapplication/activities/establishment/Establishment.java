package com.example.myapplication.activities.establishment;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Establishment implements Serializable {
    private String name;
    private int numberOfFloors;
    private int numberOfTiles;
    private List<String> structuralWeaknesses;
    private String country;
    private List<String> preferredTileTypes; 
    private Map<String, String> preferredSuppliers; 
    private String establishmentType; 
    private String logoUri; // URI of the uploaded logo

    public Establishment(String name, int numberOfFloors, int numberOfTiles, List<String> structuralWeaknesses, 
                         String country, List<String> preferredTileTypes, Map<String, String> preferredSuppliers, 
                         String establishmentType, String logoUri) {
        this.name = name;
        this.numberOfFloors = numberOfFloors;
        this.numberOfTiles = numberOfTiles;
        this.structuralWeaknesses = structuralWeaknesses;
        this.country = country;
        this.preferredTileTypes = preferredTileTypes;
        this.preferredSuppliers = preferredSuppliers;
        this.establishmentType = establishmentType;
        this.logoUri = logoUri;
    }

    public String getName() { return name; }
    public int getNumberOfFloors() { return numberOfFloors; }
    public int getNumberOfTiles() { return numberOfTiles; }
    public List<String> getStructuralWeaknesses() { return structuralWeaknesses; }
    public String getCountry() { return country; }
    public List<String> getPreferredTileTypes() { return preferredTileTypes; }
    public Map<String, String> getPreferredSuppliers() { return preferredSuppliers; }
    public String getEstablishmentType() { return establishmentType; }
    public String getLogoUri() { return logoUri; }

    public String getInitials() {
        if (name == null || name.isEmpty()) return "??";
        String[] parts = name.split("\\s+");
        if (parts.length == 1) return name.substring(0, Math.min(name.length(), 2)).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}
