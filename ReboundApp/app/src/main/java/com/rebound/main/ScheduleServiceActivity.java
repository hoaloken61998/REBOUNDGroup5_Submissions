//package com.rebound.main;
//
//import android.app.TimePickerDialog;
//import android.os.Bundle;
//import android.widget.TextView;
//import android.widget.TimePicker;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.rebound.R;
//
//import java.util.Calendar;
//
//public class ScheduleServiceActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_schedule_service);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Thêm phần chọn giờ
//        TextView txtScheduleSelectedTime = findViewById(R.id.txtScheduleSelectedTime);
//        txtScheduleSelectedTime.setOnClickListener(v -> {
//            Calendar calendar = Calendar.getInstance();
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);
//
//            TimePickerDialog timePickerDialog = new TimePickerDialog(
//                    ScheduleServiceActivity.this,
//                    (TimePicker view, int selectedHour, int selectedMinute) -> {
//                        String time = String.format("%02d:%02d", selectedHour, selectedMinute);
//                        txtScheduleSelectedTime.setText(time);
//                    },
//                    hour, minute, true
//            );
//
//            timePickerDialog.show();
//        });
//    }
//}
