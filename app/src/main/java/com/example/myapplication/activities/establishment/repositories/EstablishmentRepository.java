package com.example.myapplication.activities.establishment.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.activities.establishment.models.Establishment;

import java.util.ArrayList;
import java.util.List;

public class EstablishmentRepository {
    private static EstablishmentRepository instance;
    private final MutableLiveData<List<Establishment>> establishmentList = new MutableLiveData<>(new ArrayList<>());

    private EstablishmentRepository() {}

    public static synchronized EstablishmentRepository getInstance() {
        if (instance == null) {
            instance = new EstablishmentRepository();
        }
        return instance;
    }

    public LiveData<List<Establishment>> getEstablishmentList() {
        return establishmentList;
    }

    public void setEstablishmentList(List<Establishment> establishments) {
        this.establishmentList.setValue(establishments);
    }

    public void addEstablishmentLocal(Establishment establishment) {
        List<Establishment> currentList = establishmentList.getValue();
        if (currentList != null) {
            currentList.add(establishment);
            establishmentList.setValue(currentList);
        }
    }

    public void removeEstablishmentLocal(Establishment establishment) {
        List<Establishment> currentList = establishmentList.getValue();
        if (currentList != null) {
            currentList.remove(establishment);
            establishmentList.setValue(currentList);
        }
    }
}
