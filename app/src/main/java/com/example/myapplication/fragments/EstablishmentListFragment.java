package com.example.myapplication.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.activities.establishment.adapters.EstablishmentListAdapter;
import com.example.myapplication.activities.establishment.models.Establishment;
import com.example.myapplication.activities.establishment.repositories.EstablishmentRepository;
import com.example.myapplication.utils.FirestoreManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EstablishmentListFragment extends Fragment implements OnMapReadyCallback {

    private RecyclerView recyclerView;
    private EstablishmentListAdapter adapter;
    private FirestoreManager firestoreManager;
    private List<Establishment> establishmentList = new ArrayList<>();
    private LinearLayout detailPane;
    private TextView placeholderTextView;
    private ImageView paneLogoImageView, panePlanImageView;
    private TextView paneInitialsTextView, paneEstNameLeft;
    private TextView paneTextType, paneTextLocation, paneTextFloors, paneTextTiles;
    private TextView paneTextWeaknesses, paneTextTileTypes, paneTextSuppliers;
    private GoogleMap googleMap;
    private Establishment currentSelectedEst;
    private TextToSpeech tts;
    private static final String MALL_IMAGE = "https://images.ctfassets.net/z78475or6i3d/4ncEpBqUYr2OOpGsYVPv9B/ffd4235f9b0f702e904df616eccfe76d/Urban_Strip_Mall_Retail_to_Housing_Concept_Plan.jpg";
    private static final String OFFICE_IMAGE = "https://i.pinimg.com/736x/bd/d6/f5/bdd6f5247dbea0e5eedf33fe8cc491ee--office-layout-plan-office-floor-plan.jpg";
    private static final String RESTAURANT_IMAGE = "https://th.bing.com/th/id/R.2f7e50ef9157e082f4775217792b39b1?rik=79LiudyrK3VCkQ&pid=ImgRaw&r=0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_establishment_list, container, false);
        firestoreManager = FirestoreManager.getInstance();
        detailPane = view.findViewById(R.id.detailPane);
        placeholderTextView = view.findViewById(R.id.placeholderTextView);
        paneLogoImageView = view.findViewById(R.id.paneLogoImageView);
        panePlanImageView = view.findViewById(R.id.panePlanImageView);
        paneInitialsTextView = view.findViewById(R.id.paneInitialsTextView);
        paneEstNameLeft = view.findViewById(R.id.paneEstNameLeft);
        paneTextType = view.findViewById(R.id.paneTextType);
        paneTextLocation = view.findViewById(R.id.paneTextLocation);
        paneTextFloors = view.findViewById(R.id.paneTextFloors);
        paneTextTiles = view.findViewById(R.id.paneTextTiles);
        paneTextWeaknesses = view.findViewById(R.id.paneTextWeaknesses);
        paneTextTileTypes = view.findViewById(R.id.paneTextTileTypes);
        paneTextSuppliers = view.findViewById(R.id.paneTextSuppliers);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);
        Button btnSpeak = view.findViewById(R.id.btnSpeakDetailsPane);
        tts = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.US);
        });
        btnSpeak.setOnClickListener(v -> speakDetails());
        recyclerView = view.findViewById(R.id.establishmentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EstablishmentListAdapter(establishmentList, new EstablishmentListAdapter.OnEstablishmentActionListener() {
            @Override
            public void onEstablishmentView(Establishment establishment) {
                showEstablishmentDetails(establishment);
            }
            @Override
            public void onEstablishmentOptions(Establishment establishment, View anchor) {
                showOptionsPopup(establishment, anchor);
            }
        });
        recyclerView.setAdapter(adapter);
        EstablishmentRepository.getInstance().getEstablishmentList().observe(getViewLifecycleOwner(), establishments -> {
            this.establishmentList.clear();
            this.establishmentList.addAll(establishments);
            adapter.notifyDataSetChanged();
            if (establishments.isEmpty()) {
                showPlaceholder();
            } else {
                showEstablishmentDetails(establishments.get(0));
            }
        });
        return view;
    }

    private void speakDetails() {
        if (currentSelectedEst == null || tts == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Establishment details for ").append(currentSelectedEst.getName()).append(". ");
        sb.append("It is a ").append(currentSelectedEst.getEstablishmentType()).append(" in ").append(currentSelectedEst.getCountry()).append(". ");
        sb.append("Number of floors is ").append(currentSelectedEst.getNumberOfFloors()).append(". ");
        sb.append("Total tiles provided is ").append(currentSelectedEst.getNumberOfTiles()).append(". ");
        if (currentSelectedEst.getStructuralWeaknesses() != null && !currentSelectedEst.getStructuralWeaknesses().isEmpty()) {
            sb.append("Structural weaknesses include ").append(TextUtils.join(", ", currentSelectedEst.getStructuralWeaknesses())).append(". ");
        } else {
            sb.append("No structural weaknesses reported. ");
        }
        if (currentSelectedEst.getPreferredTileTypes() != null && !currentSelectedEst.getPreferredTileTypes().isEmpty()) {
            sb.append("Preferred tile types are ").append(TextUtils.join(", ", currentSelectedEst.getPreferredTileTypes())).append(". ");
        }
        Map<String, String> suppliers = currentSelectedEst.getPreferredSuppliers();
        if (suppliers != null && !suppliers.isEmpty()) {
            sb.append("Suppliers are ");
            for (Map.Entry<String, String> entry : suppliers.entrySet()) {
                sb.append(entry.getKey()).append(" provided by ").append(entry.getValue()).append(". ");
            }
        }
        tts.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        updateMapLocation();
    }

    private void updateMapLocation() {
        if (googleMap != null && currentSelectedEst != null) {
            googleMap.clear();
            LatLng loc = new LatLng(currentSelectedEst.getLatitude(), currentSelectedEst.getLongitude());
            if (loc.latitude != 0.0 || loc.longitude != 0.0) {
                googleMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .title(currentSelectedEst.getName())
                        .snippet(currentSelectedEst.getEstablishmentType()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f));
            }
        }
    }

    private void showPlaceholder() {
        placeholderTextView.setVisibility(View.VISIBLE);
        detailPane.setVisibility(View.GONE);
    }

    private void showEstablishmentDetails(Establishment est) {
        currentSelectedEst = est;
        placeholderTextView.setVisibility(View.GONE);
        detailPane.setVisibility(View.VISIBLE);
        paneEstNameLeft.setText(est.getName());
        paneInitialsTextView.setText(est.getInitials());
        if (est.getLogoUri() != null) {
            try {
                paneLogoImageView.setImageURI(Uri.parse(est.getLogoUri()));
                paneLogoImageView.setVisibility(View.VISIBLE);
                paneInitialsTextView.setVisibility(View.GONE);
            } catch (Exception e) {
                paneLogoImageView.setVisibility(View.GONE);
                paneInitialsTextView.setVisibility(View.VISIBLE);
            }
        } else {
            paneLogoImageView.setVisibility(View.GONE);
            paneInitialsTextView.setVisibility(View.VISIBLE);
        }
        String planUrl = est.getEstablishmentType().equalsIgnoreCase("Mall") ? MALL_IMAGE :
                (est.getEstablishmentType().equalsIgnoreCase("Office") || est.getEstablishmentType().equalsIgnoreCase("Office Building")) ? OFFICE_IMAGE : RESTAURANT_IMAGE;
        Glide.with(this).load(planUrl).into(panePlanImageView);
        paneTextType.setText("Type: " + est.getEstablishmentType());
        paneTextLocation.setText("Country: " + est.getCountry());
        paneTextFloors.setText("Floors: " + est.getNumberOfFloors());
        paneTextTiles.setText("Total Tiles: " + est.getNumberOfTiles());
        paneTextWeaknesses.setText(est.getStructuralWeaknesses().isEmpty() ? "None reported" : TextUtils.join(", ", est.getStructuralWeaknesses()));
        paneTextTileTypes.setText("Types: " + TextUtils.join(", ", est.getPreferredTileTypes()));
        StringBuilder suppliers = new StringBuilder("Preferred Suppliers:\n");
        est.getPreferredSuppliers().forEach((key, value) ->
                suppliers.append(" • ").append(key).append(": ").append(value).append("\n"));
        paneTextSuppliers.setText(suppliers.toString().trim());
        updateMapLocation();
    }

    private void showOptionsPopup(Establishment est, View anchor) {
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        popup.getMenu().add("Edit Info");
        popup.getMenu().add("Delete");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Edit Info")) {
                if (getActivity() instanceof OnEstablishmentEditListener) {
                    ((OnEstablishmentEditListener) getActivity()).onEditEstablishment(est);
                }
                return true;
            } else if (item.getTitle().equals("Delete")) {
                firestoreManager.deleteEstablishment(est);
                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public void onDestroy() { 
        super.onDestroy(); 
        if (tts != null) { tts.stop(); tts.shutdown(); }
    }

    public interface OnEstablishmentEditListener {
        void onEditEstablishment(Establishment establishment);
    }
}
