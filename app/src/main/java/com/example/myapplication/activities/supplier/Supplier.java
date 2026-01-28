package com.example.myapplication.activities.supplier;

import java.io.Serializable;

public class Supplier implements Serializable {
    private String companyName;
    private String personalName;
    private int totalFlooringsOrdered;
    private double pricePerUnit;
    private String country;

    public Supplier(String companyName, String personalName, int totalFlooringsOrdered, double pricePerUnit, String country) {
        this.companyName = companyName;
        this.personalName = personalName;
        this.totalFlooringsOrdered = totalFlooringsOrdered;
        this.pricePerUnit = pricePerUnit;
        this.country = country;
    }

    public String getCompanyName() { return companyName; }
    public String getPersonalName() { return personalName; }
    public int getTotalFlooringsOrdered() { return totalFlooringsOrdered; }
    public void setTotalFlooringsOrdered(int totalFlooringsOrdered) { this.totalFlooringsOrdered = totalFlooringsOrdered; }
    public double getPricePerUnit() { return pricePerUnit; }
    public String getCountry() { return country; }
}
