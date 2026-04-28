package com.example.myapplication.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.activities.establishment.EstablishmentViewModel;
import com.example.myapplication.activities.establishment.models.Establishment;
import com.example.myapplication.activities.establishment.repositories.EstablishmentRepository;
import com.example.myapplication.utils.FirestoreManager;
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
    private TextInputEditText editSensingSupplier, editRoutingSupplier, editPressurizedSupplier, editEnergySupplier;
    private CheckBox checkSensing, checkRouting, checkPressurized, checkEnergy;
    private RadioGroup radioGroupType;
    private ImageView imgLogoPreview;
    private Uri selectedLogoUri;
    private Establishment existingEstablishment;
    private TextView textFormTitle;
    private EstablishmentViewModel viewModel;

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

    public static CreateEstablishmentFragment newInstance(Establishment establishment) {
        CreateEstablishmentFragment fragment = new CreateEstablishmentFragment();
        Bundle args = new Bundle();
        args.putSerializable("establishment", establishment);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_establishment, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EstablishmentViewModel.class);

        // UI References
        selectionContainer = view.findViewById(R.id.selectionContainer);
        editPickerContainer = view.findViewById(R.id.editPickerContainer);
        formScrollView = view.findViewById(R.id.formScrollView);
        spinnerEstablishments = view.findViewById(R.id.spinnerEstablishments);
        textFormTitle = view.findViewById(R.id.textFormTitle);
        imgLogoPreview = view.findViewById(R.id.imgLogoPreview);

        // Buttons
        Button btnModeCreate = view.findViewById(R.id.btnModeCreate);
        Button btnModeEdit = view.findViewById(R.id.btnModeEdit);
        Button btnConfirmSelection = view.findViewById(R.id.btnConfirmSelection);
        Button btnUploadLogo = view.findViewById(R.id.btnUploadLogo);
        Button btnSave = view.findViewById(R.id.btnSave);

        // Form Fields
        editName = view.findViewById(R.id.editName);
        editFloors = view.findViewById(R.id.editFloors);
        editTiles = view.findViewById(R.id.editTiles);
        editCountry = view.findViewById(R.id.editCountry);
        editWeaknesses = view.findViewById(R.id.editWeaknesses);
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

        // Listeners
        btnUploadLogo.setOnClickListener(v -> logoPickerLauncher.launch("image/*"));
        checkSensing.setOnCheckedChangeListener((v, checked) -> inputSensing.setVisibility(checked ? View.VISIBLE : View.GONE));
        checkRouting.setOnCheckedChangeListener((v, checked) -> inputRouting.setVisibility(checked ? View.VISIBLE : View.GONE));
        checkPressurized.setOnCheckedChangeListener((v, checked) -> inputPressurized.setVisibility(checked ? View.VISIBLE : View.GONE));
        checkEnergy.setOnCheckedChangeListener((v, checked) -> inputEnergy.setVisibility(checked ? View.VISIBLE : View.GONE));

        btnModeCreate.setOnClickListener(v -> showForm(null));
        btnModeEdit.setOnClickListener(v -> setupEditPicker());
        btnConfirmSelection.setOnClickListener(v -> {
            int pos = spinnerEstablishments.getSelectedItemPosition();
            if (pos != Spinner.INVALID_POSITION && !availableEstablishments.isEmpty()) {
                showForm(availableEstablishments.get(pos));
            }
        });

        btnSave.setOnClickListener(v -> saveEstablishment());

        // Observe ViewModel for establishment to edit
        viewModel.getSelectedEstablishment().observe(getViewLifecycleOwner(), establishment -> {
            if (establishment != null) {
                showForm(establishment);
            }
        });

        // Check if opened with specific establishment (direct edit from list)
        if (getArguments() != null) {
            existingEstablishment = (Establishment) getArguments().getSerializable("establishment");
            if (existingEstablishment != null) {
                showForm(existingEstablishment);
            }
        }

        return view;
    }

    private void setupEditPicker() {
        EstablishmentRepository.getInstance().getEstablishmentList().observe(getViewLifecycleOwner(), establishments -> {
            availableEstablishments = establishments;
            List<String> names = new ArrayList<>();
            for (Establishment e : establishments) {
                names.add(e.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
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

    private void populateFields(Establishment est) {
        editName.setText(est.getName());
        editFloors.setText(String.valueOf(est.getNumberOfFloors()));
        editTiles.setText(String.valueOf(est.getNumberOfTiles()));
        editCountry.setText(est.getCountry());
        if (est.getStructuralWeaknesses() != null) {
            editWeaknesses.setText(String.join(", ", est.getStructuralWeaknesses()));
        }

        if ("Mall".equals(est.getEstablishmentType())) radioGroupType.check(R.id.radioMall);
        else if ("Restaurant".equals(est.getEstablishmentType())) radioGroupType.check(R.id.radioRestaurant);
        else radioGroupType.check(R.id.radioOffice);

        // Reset checks
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
            suppliers.put("Sensing", editSensingSupplier.getText().toString());
        }
        if (checkRouting.isChecked()) {
            tileTypes.add("Routing");
            suppliers.put("Routing", editRoutingSupplier.getText().toString());
        }
        if (checkPressurized.isChecked()) {
            tileTypes.add("Pressurized");
            suppliers.put("Pressurized", editPressurizedSupplier.getText().toString());
        }
        if (checkEnergy.isChecked()) {
            tileTypes.add("Energy Producing");
            suppliers.put("Energy Producing", editEnergySupplier.getText().toString());
        }

        String logoUriString = selectedLogoUri != null ? selectedLogoUri.toString() : null;

        Establishment newEst = new Establishment(name, floors, tiles, weaknesses, country, tileTypes, suppliers, type, logoUriString);
        if (existingEstablishment != null) {
            newEst.setId(existingEstablishment.getId());
        }

        FirestoreManager.getInstance().addOrUpdateEstablishment(newEst);
        Toast.makeText(getContext(), "Establishment saved", Toast.LENGTH_SHORT).show();
        
        if (getActivity() instanceof OnEstablishmentSavedListener) {
            ((OnEstablishmentSavedListener) getActivity()).onEstablishmentSaved();
        }
    }

    public interface OnEstablishmentSavedListener {
        void onEstablishmentSaved();
    }
}
