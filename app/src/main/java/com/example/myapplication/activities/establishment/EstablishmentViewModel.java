package com.example.myapplication.activities.establishment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.activities.establishment.models.Establishment;

public class EstablishmentViewModel extends ViewModel {
    private final MutableLiveData<Establishment> selectedEstablishment = new MutableLiveData<>();

    public void selectEstablishment(Establishment establishment) {
        selectedEstablishment.setValue(establishment);
    }

    public LiveData<Establishment> getSelectedEstablishment() {
        return selectedEstablishment;
    }
}
