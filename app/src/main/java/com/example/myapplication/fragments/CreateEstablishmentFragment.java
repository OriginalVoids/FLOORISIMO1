package com.example.myapplication.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.activities.establishment.EstablishmentViewModel;
import com.example.myapplication.activities.establishment.models.Establishment;
import com.example.myapplication.activities.establishment.repositories.EstablishmentRepository;
import com.example.myapplication.utils.FirestoreManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateEstablishmentFragment extends Fragment {

    private TextInputEditText editName, editFloors, editTiles, editCountry, editWeaknesses;
    private TextInputEditText editLatitude, editLongitude;
    private TextInputEditText editSensingSupplier, editRoutingSupplier, editPressurizedSupplier, editEnergySupplier;
    private CheckBox checkSensing, checkRouting, checkPressurized, checkEnergy;
    private RadioGroup radioGroupType;
    private ImageView imgLogoPreview;
    private Uri selectedLogoUri;
    private Establishment existingEstablishment;
    private TextView textFormTitle;
    private EstablishmentViewModel viewModel;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean fine = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarse = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if ((fine != null && fine) || (coarse != null && coarse)) {
                    captureCurrentLocation();
                } else {
                    Toast.makeText(getContext(), "Location access denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private LinearLayout selectionContainer, editPickerContainer;
    private View formScrollView;
    private Spinner spinnerEstablishments;
    private List<Establishment> availableEstablishments = new ArrayList<>();

    private final ActivityResultLauncher<String> logoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedLogoUri = uri;
                    imgLogoPreview.setImageURI(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_establishment, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EstablishmentViewModel.class);
        selectionContainer = view.findViewById(R.id.selectionContainer);
        editPickerContainer = view.findViewById(R.id.editPickerContainer);
        formScrollView = view.findViewById(R.id.formScrollView);
        spinnerEstablishments = view.findViewById(R.id.spinnerEstablishments);
        textFormTitle = view.findViewById(R.id.textFormTitle);
        imgLogoPreview = view.findViewById(R.id.imgLogoPreview);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        Button btnModeCreate = view.findViewById(R.id.btnModeCreate);
        Button btnModeEdit = view.findViewById(R.id.btnModeEdit);
        Button btnConfirmSelection = view.findViewById(R.id.btnConfirmSelection);
        Button btnUploadLogo = view.findViewById(R.id.btnUploadLogo);
        Button btnCaptureLocation = view.findViewById(R.id.btnCaptureLocation);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        editName = view.findViewById(R.id.editName);
        editFloors = view.findViewById(R.id.editFloors);
        editTiles = view.findViewById(R.id.editTiles);
        editCountry = view.findViewById(R.id.editCountry);
        editWeaknesses = view.findViewById(R.id.editWeaknesses);
        editLatitude = view.findViewById(R.id.editLatitude);
        editLongitude = view.findViewById(R.id.editLongitude);
        radioGroupType = view.findViewById(R.id.radioGroupType);
        checkSensing = view.findViewById(R.id.checkSensing);
        checkRouting = view.findViewById(R.id.checkRouting);
        checkPressurized = view.findViewById(R.id.checkPressurized);
        checkEnergy = view.findViewById(R.id.checkEnergy);
        editSensingSupplier = view.findViewById(R.id.editSensingSupplier);
        editRoutingSupplier = view.findViewById(R.id.editRoutingSupplier);
        editPressurizedSupplier = view.findViewById(R.id.editPressurizedSupplier);
        editEnergySupplier = view.findViewById(R.id.editEnergySupplier);
        TextInputLayout inputSensing = view.findViewById(R.id.inputSensingSupplier);
        TextInputLayout inputRouting = view.findViewById(R.id.inputRoutingSupplier);
        TextInputLayout inputPressurized = view.findViewById(R.id.inputPressurizedSupplier);
        TextInputLayout inputEnergy = view.findViewById(R.id.inputEnergySupplier);
        btnUploadLogo.setOnClickListener(v -> logoPickerLauncher.launch("image/*"));
        checkSensing.setOnCheckedChangeListener((v, checked) -> inputSensing.setVisibility(checked ? View.VISIBLE : View.GONE));
        checkRouting.setOnCheckedChangeListener((v, checked) -> inputRouting.setVisibility(checked ? View.VISIBLE : View.GONE));
        checkPressurized.setOnCheckedChangeListener((v, checked) -> inputPressurized.setVisibility(checked ? View.VISIBLE : View.GONE));
        checkEnergy.setOnCheckedChangeListener((v, checked) -> inputEnergy.setVisibility(checked ? View.VISIBLE : View.GONE));
        btnModeCreate.setOnClickListener(v -> {
            resetForm();
            showForm(null);
        });
        btnModeEdit.setOnClickListener(v -> setupEditPicker());
        btnConfirmSelection.setOnClickListener(v -> {
            int pos = spinnerEstablishments.getSelectedItemPosition();
            if (pos != Spinner.INVALID_POSITION && !availableEstablishments.isEmpty()) {
                showForm(availableEstablishments.get(pos));
            }
        });
        btnCaptureLocation.setOnClickListener(v -> checkLocationPermissions());
        btnSave.setOnClickListener(v -> saveEstablishment());
        btnCancel.setOnClickListener(v -> {
            viewModel.selectEstablishment(null);
            resetForm();
        });
        viewModel.getSelectedEstablishment().observe(getViewLifecycleOwner(), establishment -> {
            if (establishment != null) {
                showForm(establishment);
            } else {
                resetForm();
            }
        });
        return view;
    }

    private void setupEditPicker() {
        EstablishmentRepository.getInstance().getEstablishmentList().observe(getViewLifecycleOwner(), establishments -> {
            availableEstablishments = establishments;
            List<String> names = new ArrayList<>();
            for (Establishment e : establishments) {
                names.add(e.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEstablishments.setAdapter(adapter);
            editPickerContainer.setVisibility(View.VISIBLE);
        });
    }

    private void showForm(@Nullable Establishment establishment) {
        selectionContainer.setVisibility(View.GONE);
        formScrollView.setVisibility(View.VISIBLE);
        existingEstablishment = establishment;
        if (establishment != null) {
            populateFields(establishment);
            textFormTitle.setText("Edit Establishment");
        } else {
            textFormTitle.setText("New Establishment Survey");
        }
    }

    private void resetForm() {
        existingEstablishment = null;
        editName.setText("");
        editFloors.setText("");
        editTiles.setText("");
        editCountry.setText("");
        editLatitude.setText("");
        editLongitude.setText("");
        editWeaknesses.setText("");
        radioGroupType.clearCheck();
        checkSensing.setChecked(false);
        checkRouting.setChecked(false);
        checkPressurized.setChecked(false);
        checkEnergy.setChecked(false);
        editSensingSupplier.setText("");
        editRoutingSupplier.setText("");
        editPressurizedSupplier.setText("");
        editEnergySupplier.setText("");
        imgLogoPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        selectedLogoUri = null;
        selectionContainer.setVisibility(View.VISIBLE);
        formScrollView.setVisibility(View.GONE);
        editPickerContainer.setVisibility(View.GONE);
    }

    private void populateFields(Establishment est) {
        editName.setText(est.getName());
        editFloors.setText(String.valueOf(est.getNumberOfFloors()));
        editTiles.setText(String.valueOf(est.getNumberOfTiles()));
        editCountry.setText(est.getCountry());
        editLatitude.setText(String.valueOf(est.getLatitude()));
        editLongitude.setText(String.valueOf(est.getLongitude()));
        if (est.getStructuralWeaknesses() != null) {
            editWeaknesses.setText(String.join(", ", est.getStructuralWeaknesses()));
        }
        if ("Mall".equals(est.getEstablishmentType())) radioGroupType.check(R.id.radioMall);
        else if ("Restaurant".equals(est.getEstablishmentType())) radioGroupType.check(R.id.radioRestaurant);
        else radioGroupType.check(R.id.radioOffice);
        checkSensing.setChecked(false);
        checkRouting.setChecked(false);
        checkPressurized.setChecked(false);
        checkEnergy.setChecked(false);
        if (est.getPreferredTileTypes() != null) {
            for (String type : est.getPreferredTileTypes()) {
                if ("Sensing".equals(type)) {
                    checkSensing.setChecked(true);
                    editSensingSupplier.setText(est.getPreferredSuppliers().get("Sensing"));
                } else if ("Routing".equals(type)) {
                    checkRouting.setChecked(true);
                    editRoutingSupplier.setText(est.getPreferredSuppliers().get("Routing"));
                } else if ("Pressurized".equals(type)) {
                    checkPressurized.setChecked(true);
                    editPressurizedSupplier.setText(est.getPreferredSuppliers().get("Pressurized"));
                } else if ("Energy Producing".equals(type)) {
                    checkEnergy.setChecked(true);
                    editEnergySupplier.setText(est.getPreferredSuppliers().get("Energy Producing"));
                }
            }
        }
        if (est.getLogoUri() != null) {
            selectedLogoUri = Uri.parse(est.getLogoUri());
            imgLogoPreview.setImageURI(selectedLogoUri);
        }
    }

    private void saveEstablishment() {
        String name = Objects.requireNonNull(editName.getText()).toString().trim();
        if (name.isEmpty()) {
            editName.setError("Required");
            return;
        }
        int floors = 0;
        try { floors = Integer.parseInt(Objects.requireNonNull(editFloors.getText()).toString()); } catch (Exception ignored) {}
        int tiles = 0;
        try { tiles = Integer.parseInt(Objects.requireNonNull(editTiles.getText()).toString()); } catch (Exception ignored) {}
        double lat = 0;
        try { lat = Double.parseDouble(Objects.requireNonNull(editLatitude.getText()).toString()); } catch (Exception ignored) {}
        double lng = 0;
        try { lng = Double.parseDouble(Objects.requireNonNull(editLongitude.getText()).toString()); } catch (Exception ignored) {}
        String country = Objects.requireNonNull(editCountry.getText()).toString();
        List<String> weaknesses = new ArrayList<>();
        String wText = Objects.requireNonNull(editWeaknesses.getText()).toString();
        if (!wText.isEmpty()) {
            weaknesses = new ArrayList<>(Arrays.asList(wText.split("\\s*,\\s*")));
        }
        String type = "Office";
        int selectedType = radioGroupType.getCheckedRadioButtonId();
        if (selectedType == R.id.radioMall) type = "Mall";
        else if (selectedType == R.id.radioRestaurant) type = "Restaurant";
        List<String> tileTypes = new ArrayList<>();
        Map<String, String> suppliers = new HashMap<>();
        if (checkSensing.isChecked()) {
            tileTypes.add("Sensing");
            suppliers.put("Sensing", Objects.requireNonNull(editSensingSupplier.getText()).toString());
        }
        if (checkRouting.isChecked()) {
            tileTypes.add("Routing");
            suppliers.put("Routing", Objects.requireNonNull(editRoutingSupplier.getText()).toString());
        }
        if (checkPressurized.isChecked()) {
            tileTypes.add("Pressurized");
            suppliers.put("Pressurized", Objects.requireNonNull(editPressurizedSupplier.getText()).toString());
        }
        if (checkEnergy.isChecked()) {
            tileTypes.add("Energy Producing");
            suppliers.put("Energy Producing", Objects.requireNonNull(editEnergySupplier.getText()).toString());
        }
        String logoUriString = selectedLogoUri != null ? selectedLogoUri.toString() : null;
        Establishment newEst = new Establishment(name, floors, tiles, weaknesses, country, tileTypes, suppliers, type, logoUriString);
        newEst.setLatitude(lat);
        newEst.setLongitude(lng);
        if (existingEstablishment != null) {
            newEst.setId(existingEstablishment.getId());
        }
        FirestoreManager.getInstance().addOrUpdateEstablishment(newEst);
        Toast.makeText(getContext(), "Establishment saved", Toast.LENGTH_SHORT).show();
        if (getActivity() instanceof OnEstablishmentSavedListener) {
            ((OnEstablishmentSavedListener) getActivity()).onEstablishmentSaved();
        }
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            captureCurrentLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void captureCurrentLocation() {
        Toast.makeText(getContext(), "Capturing location...", Toast.LENGTH_SHORT).show();
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        editLatitude.setText(String.valueOf(location.getLatitude()));
                        editLongitude.setText(String.valueOf(location.getLongitude()));
                        Toast.makeText(getContext(), "Location captured", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "GPS failed to get fix", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (SecurityException ignored) {}
    }

    public interface OnEstablishmentSavedListener {
        void onEstablishmentSaved();
    }
}
