package com.example.myapplication.activities.establishment.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Establishment implements Serializable {

    private String id;
    private String name;
    private int numberOfFloors;
    private int numberOfTiles;
    private List<String> structuralWeaknesses = new ArrayList<>();
    private String country;
    private List<String> preferredTileTypes = new ArrayList<>();
    private Map<String, String> preferredSuppliers = new HashMap<>();
    private String establishmentType;
    private String logoUri;
    private double latitude;
    private double longitude;

    public Establishment() {}

    public Establishment(String name, int numberOfFloors, int numberOfTiles, List<String> structuralWeaknesses, String country, List<String> preferredTileTypes, Map<String, String> preferredSuppliers, String establishmentType, String logoUri) {
        this.name = name;
        this.numberOfFloors = numberOfFloors;
        this.numberOfTiles = numberOfTiles;
        this.structuralWeaknesses = structuralWeaknesses != null ? structuralWeaknesses : new ArrayList<>();
        this.country = country;
        this.preferredTileTypes = preferredTileTypes != null ? preferredTileTypes : new ArrayList<>();
        this.preferredSuppliers = preferredSuppliers != null ? preferredSuppliers : new HashMap<>();
        this.establishmentType = establishmentType;
        this.logoUri = logoUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public void setNumberOfFloors(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    public int getNumberOfTiles() {
        return numberOfTiles;
    }

    public void setNumberOfTiles(int numberOfTiles) {
        this.numberOfTiles = numberOfTiles;
    }

    public List<String> getStructuralWeaknesses() {
        return structuralWeaknesses;
    }

    public void setStructuralWeaknesses(List<String> structuralWeaknesses) {
        this.structuralWeaknesses = structuralWeaknesses != null ? structuralWeaknesses : new ArrayList<>();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getPreferredTileTypes() {
        return preferredTileTypes;
    }

    public void setPreferredTileTypes(List<String> preferredTileTypes) {
        this.preferredTileTypes = preferredTileTypes != null ? preferredTileTypes : new ArrayList<>();
    }

    public Map<String, String> getPreferredSuppliers() {
        return preferredSuppliers;
    }

    public void setPreferredSuppliers(Map<String, String> preferredSuppliers) {
        this.preferredSuppliers = preferredSuppliers != null ? preferredSuppliers : new HashMap<>();
    }

    public String getEstablishmentType() {
        return establishmentType;
    }

    public void setEstablishmentType(String establishmentType) {
        this.establishmentType = establishmentType;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getInitials() {
        if (name != null && !name.isEmpty()) {
            String[] words = name.split("\\s+");
            StringBuilder initials = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    initials.append(word.charAt(0));
                }
            }
            return initials.toString().toUpperCase();
        }
        return "";
    }
}
