
package com.example.myapplication.repositories;

import com.example.myapplication.models.Establishment;

import java.util.ArrayList;
import java.util.List;

public class EstablishmentRepository {

    private static EstablishmentRepository instance;
    private final List<Establishment> establishmentList = new ArrayList<>();

    private EstablishmentRepository() {}

    public static synchronized EstablishmentRepository getInstance() {
        if (instance == null) {
            instance = new EstablishmentRepository();
        }
        return instance;
    }

    public List<Establishment> getEstablishmentList() {
        return establishmentList;
    }

    public void setEstablishmentList(List<Establishment> establishments) {
        this.establishmentList.clear();
        this.establishmentList.addAll(establishments);
    }

    public void addEstablishmentLocal(Establishment establishment) {
        // Remove existing establishment with the same ID to avoid duplicates
        establishmentList.removeIf(e -> e.getId().equals(establishment.getId()));
        establishmentList.add(establishment);
    }

    public void removeEstablishmentLocal(Establishment establishment) {
        establishmentList.removeIf(e -> e.getId().equals(establishment.getId()));
    }
}
