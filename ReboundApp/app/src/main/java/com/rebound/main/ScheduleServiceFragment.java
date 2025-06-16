package com.rebound.main;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import com.rebound.models.Customer.Customer;
import com.rebound.utils.SharedPrefManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.data.BranchData;
import com.rebound.connectors.BranchConnector;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

import java.util.Calendar;


public class ScheduleServiceFragment extends Fragment {

    private final long[] selectedDateMillis = {0};
    private String selectedService = "";

    public ScheduleServiceFragment() {
    }


    //Chỉnh màu nút
    private void setSelectedButton(MaterialButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F2F0D4")));
        button.setTextColor(Color.WHITE);
        button.setStrokeWidth(0);
    }


    private void setUnselectedButton(MaterialButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        button.setTextColor(Color.parseColor("#22000000"));
        button.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#22000000")));
        button.setStrokeWidth(1);
    }

    private void setSelectedServiceButton(Button button) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        button.setTextColor(Color.WHITE);
    }

    private void setUnselectedServiceButton(Button button) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        button.setTextColor(Color.BLACK);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_service, container, false);


        TextView txtScheduleSelectedTime = view.findViewById(R.id.txtScheduleSelectedTime);
        MaterialButton btnScheduleBook = view.findViewById(R.id.btnScheduleBook);
        ImageView imgBell = view.findViewById(R.id.imgBell);


        txtScheduleSelectedTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);


            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
                        String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                        txtScheduleSelectedTime.setText(time);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });


        btnScheduleBook.setOnClickListener(v -> {
            Customer currentCustomer = SharedPrefManager.getCurrentCustomer(requireContext());
            if (currentCustomer == null) {
                Toast.makeText(requireContext(), "Please log in to book an appointment!", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedTime = txtScheduleSelectedTime.getText().toString();

            if (selectedDateMillis[0] == 0) {
                Toast.makeText(requireContext(), "Please select a date!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTime.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a time!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedService.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a service!", Toast.LENGTH_SHORT).show();
                return;
            }

            Date date = new Date(selectedDateMillis[0]);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String selectedDate = dateFormat.format(date);

            ReservationDialog dialog = ReservationDialog.newInstance(selectedDate, selectedTime, selectedService);
            dialog.show(requireActivity().getSupportFragmentManager(), "ReservationDialog");

            // Hiệu ứng nút
            btnScheduleBook.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BEB488")));
            btnScheduleBook.setTextColor(Color.WHITE);
            btnScheduleBook.setStrokeWidth(0);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                btnScheduleBook.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                btnScheduleBook.setTextColor(Color.BLACK);
                btnScheduleBook.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#BEB488")));
                btnScheduleBook.setStrokeWidth(1);
            }, 300);
        });


        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            selectedDateMillis[0] = calendar.getTimeInMillis();
        });



        //Nhan vao brachHCM va Brach Hanoi
        MaterialButton btnHanoi = view.findViewById(R.id.btnScheduleHanoi);
        MaterialButton btnHCM = view.findViewById(R.id.btnScheduleHCM);


        // Các TextView hiển thị chi nhánh
        TextView txtBranch1 = view.findViewById(R.id.txtScheduleBranch1);
        TextView txtAddress1 = view.findViewById(R.id.txtScheduleAddress1);
        TextView txtTime1 = view.findViewById(R.id.txtScheduleTime1);
        ImageView imgBranch1 = view.findViewById(R.id.imgBranch1);


        TextView txtBranch2 = view.findViewById(R.id.txtScheduleBranch2);
        TextView txtAddress2 = view.findViewById(R.id.txtScheduleAddress2);
        TextView txtTime2 = view.findViewById(R.id.txtScheduleTime2);
        ImageView imgBranch2 = view.findViewById(R.id.imgBranch2);


        // Khi bấm nút HANOI
        btnHanoi.setOnClickListener(v -> {
            List<BranchConnector> branches = BranchData.getHanoiBranches();
            if (branches.size() >= 2) {
                txtBranch1.setText(branches.get(0).getName());
                txtAddress1.setText(branches.get(0).getAddress());
                txtTime1.setText(branches.get(0).getHours());
                imgBranch1.setImageResource(branches.get(0).getImageResId());


                txtBranch2.setText(branches.get(1).getName());
                txtAddress2.setText(branches.get(1).getAddress());
                txtTime2.setText(branches.get(1).getHours());
                imgBranch2.setImageResource(branches.get(1).getImageResId());
            }
            // Highlight nút HANOI
            setSelectedButton(btnHanoi);
            setUnselectedButton(btnHCM);
        });


        // Khi bấm nút HCM
        btnHCM.setOnClickListener(v -> {
            List<BranchConnector> branches = BranchData.getHCMBranches();
            if (branches.size() >= 2) {
                txtBranch1.setText(branches.get(0).getName());
                txtAddress1.setText(branches.get(0).getAddress());
                txtTime1.setText(branches.get(0).getHours());
                imgBranch1.setImageResource(branches.get(0).getImageResId());


                txtBranch2.setText(branches.get(1).getName());
                txtAddress2.setText(branches.get(1).getAddress());
                txtTime2.setText(branches.get(1).getHours());
                imgBranch2.setImageResource(branches.get(1).getImageResId());
            }
            setSelectedButton(btnHCM);
            setUnselectedButton(btnHanoi);
        });

        Button btn1 = view.findViewById(R.id.btnScheduleSelected1);
        Button btn2 = view.findViewById(R.id.btnScheduleSelected2);
        Button btn3 = view.findViewById(R.id.btnScheduleSelected3);
        Button btn4 = view.findViewById(R.id.btnScheduleSelected4);

        View.OnClickListener serviceClickListener = v -> {
            Button clicked = (Button) v;
            selectedService = clicked.getText().toString();

            setUnselectedServiceButton(btn1);
            setUnselectedServiceButton(btn2);
            setUnselectedServiceButton(btn3);
            setUnselectedServiceButton(btn4);
            setSelectedServiceButton(clicked);
        };

        btn1.setOnClickListener(serviceClickListener);
        btn2.setOnClickListener(serviceClickListener);
        btn3.setOnClickListener(serviceClickListener);
        btn4.setOnClickListener(serviceClickListener);


        //Gán sự kiện khi nhấn vào thông báo
        imgBell.setOnClickListener(v -> {
            Customer currentCustomer = SharedPrefManager.getCurrentCustomer(requireContext());
            Intent intent;
            if (currentCustomer != null) {
                // Đã đăng nhập
                intent = new Intent(requireContext(), NotificationActivity.class);
            } else {
                // Chưa đăng nhập
                intent = new Intent(requireContext(), NoNotificationActivity.class);
            }
            startActivity(intent);
        });

        return view;
    }
}
