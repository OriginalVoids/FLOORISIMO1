package com.example.myapplication.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.myapplication.utils.CheckupReceiver;
import java.util.Calendar;

public class PlaceholderFragment extends Fragment {

    private TimePicker timePicker;
    private TextView textStatus, textCountdown;
    private CountDownTimer countDownTimer;
    private long targetTimeMillis = 0;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    scheduleCheckup();
                } else {
                    Toast.makeText(getContext(), "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
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
        return view;
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
            String status = String.format("Check-up scheduled for %02d:%02d daily", hour, minute);
            textStatus.setText(status);
            Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
        }
    }

    private void startCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        long currentTime = System.currentTimeMillis();
        long diff = targetTimeMillis - currentTime;
        if (diff <= 0) {
            textCountdown.setText("Alert pending...");
            return;
        }
        countDownTimer = new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                textCountdown.setText(String.format("Next alert in: %02d:%02d:%02d", hours, minutes, seconds));
            }
            @Override
            public void onFinish() {
                textCountdown.setText("Time for check-up!");
                targetTimeMillis += 24 * 60 * 60 * 1000;
                startCountdown();
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
