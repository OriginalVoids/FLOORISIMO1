package com.example.myapplication.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import com.example.myapplication.activities.establishment.models.Establishment;
import com.example.myapplication.activities.establishment.repositories.EstablishmentRepository;
import com.example.myapplication.utils.CheckupReceiver;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class TileDataFragment extends Fragment {

    private TimePicker timePicker;
    private TextView textStatus, textCountdown;
    private CountDownTimer countDownTimer;
    private long targetTimeMillis = 0;

    private Spinner spinnerDataEstablishments;
    private ScrollView dataScrollView;
    private TextView textSensingData, textPressurizedData, textEnergyData;
    private GridLayout routingGrid;
    private List<Establishment> availableEstablishments = new ArrayList<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) scheduleCheckup();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tile_data, container, false);

        Button btnToggleScheduler = view.findViewById(R.id.btnToggleScheduler);
        LinearLayout schedulerContainer = view.findViewById(R.id.schedulerContainer);
        btnToggleScheduler.setOnClickListener(v -> {
            boolean isVisible = schedulerContainer.getVisibility() == View.VISIBLE;
            schedulerContainer.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        });

        timePicker = view.findViewById(R.id.timePicker);
        Button btnSchedule = view.findViewById(R.id.btnSchedule);
        textStatus = view.findViewById(R.id.textStatus);
        textCountdown = view.findViewById(R.id.textCountdown);

        btnSchedule.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                } else {
                    scheduleCheckup();
                }
            } else {
                scheduleCheckup();
            }
        });

        spinnerDataEstablishments = view.findViewById(R.id.spinnerDataEstablishments);
        dataScrollView = view.findViewById(R.id.dataScrollView);
        textSensingData = view.findViewById(R.id.textSensingData);
        textPressurizedData = view.findViewById(R.id.textPressurizedData);
        textEnergyData = view.findViewById(R.id.textEnergyData);
        routingGrid = view.findViewById(R.id.routingGrid);

        setupEstablishmentPicker();

        return view;
    }

    private void setupEstablishmentPicker() {
        EstablishmentRepository.getInstance().getEstablishmentList().observe(getViewLifecycleOwner(), establishments -> {
            availableEstablishments = establishments;
            List<String> names = new ArrayList<>();
            names.add("Select an Establishment...");
            for (Establishment e : establishments) names.add(e.getName());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDataEstablishments.setAdapter(adapter);

            spinnerDataEstablishments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        showTileData(availableEstablishments.get(position - 1));
                    } else {
                        dataScrollView.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });
    }

    private void showTileData(Establishment est) {
        dataScrollView.setVisibility(View.VISIBLE);
        Random r = new Random();

        textSensingData.setText("Sensing: " + (r.nextInt(50) + 10) + " steps/min");
        textPressurizedData.setText("Pressurized: " + (r.nextInt(100) + 200) + " N avg. force");
        textEnergyData.setText("Energy Producing: " + (r.nextInt(30) + 5) + " W/min");

        routingGrid.removeAllViews();
        int totalTiles = 25;
        for (int i = 0; i < totalTiles; i++) {
            View tile = new View(requireContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(4, 4, 4, 4);
            tile.setLayoutParams(params);

            int frequency = r.nextInt(100);
            if (frequency > 80) tile.setBackgroundColor(Color.parseColor("#E74C3C"));
            else if (frequency > 50) tile.setBackgroundColor(Color.parseColor("#F1C40F"));
            else tile.setBackgroundColor(Color.parseColor("#BDC3C7"));

            routingGrid.addView(tile);
        }
    }

    private void scheduleCheckup() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        targetTimeMillis = calendar.getTimeInMillis();
        startCountdown();
        Intent intent = new Intent(getContext(), CheckupReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Intent intentExact = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intentExact);
                    return;
                }
            }
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            textStatus.setText("Check-up scheduled");
        }
    }

    private void startCountdown() {
        if (countDownTimer != null) countDownTimer.cancel();
        long diff = targetTimeMillis - System.currentTimeMillis();
        if (diff <= 0) {
            textCountdown.setText("Alert pending...");
            return;
        }
        countDownTimer = new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long h = (millisUntilFinished / 3600000) % 24;
                long m = (millisUntilFinished / 60000) % 60;
                long s = (millisUntilFinished / 1000) % 60;
                textCountdown.setText(String.format("Next alert in: %02d:%02d:%02d", h, m, s));
            }
            @Override
            public void onFinish() {
                textCountdown.setText("Time for check-up!");
                targetTimeMillis += 86400000;
                startCountdown();
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
