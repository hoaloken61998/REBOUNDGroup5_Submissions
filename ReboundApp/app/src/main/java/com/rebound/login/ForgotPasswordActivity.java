package com.rebound.login;
import com.rebound.utils.GmailSender;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.rebound.models.Customer.Customer;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.rebound.models.Customer.ListCustomer;
import com.rebound.R;
import com.rebound.utils.SharedPrefManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    ImageView imgBackForgotPassword;
    TextView txtBottomForgotPasswordLogin;
    Button btnSendCode;
    EditText edtForgotPasswordEmail;
    ListCustomer listCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);



        // ✅ Load từ SharedPreferences
        listCustomer = SharedPrefManager.getCustomerList(this);
        if (listCustomer == null) {
            listCustomer = new ListCustomer(); // tạo danh sách mẫu
            SharedPrefManager.saveCustomerList(this, listCustomer); // lưu lại
        }

        addViews();
        addEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        imgBackForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        txtBottomForgotPasswordLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });

    }

    private void addViews() {
        imgBackForgotPassword = findViewById(R.id.imgBackForgotPassword);
        txtBottomForgotPasswordLogin = findViewById(R.id.txtBottomForgotPasswordLogin);
        btnSendCode = findViewById(R.id.btnSendCode);
        edtForgotPasswordEmail = findViewById(R.id.edtForgotPasswordEmail);
    }

    private void openLoginActivity() {
        Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String generateOTP() {
        int otp = (int)(Math.random() * 9000) + 1000; // random 6 số
        return String.valueOf(otp);
    }

    public void do_send_code(View view) {
        String email = edtForgotPasswordEmail.getText().toString().trim();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show();
            return;
        }

        boolean emailExists = false;
        for (Customer customer : listCustomer.getCustomers()) {
            if (customer.getEmail().equalsIgnoreCase(email)) {
                emailExists = true;
                break;
            }
        }

        if (!emailExists) {
            Toast.makeText(this, getString(R.string.email_does_not_exist), Toast.LENGTH_SHORT).show();
            return;
        }

        String otp = generateOTP();

        new Thread(() -> {
            GmailSender.sendEmail(email, getString(R.string.otp_email_subject), getString(R.string.otp_email_body, otp));
        }).start();

        Intent intent = new Intent(ForgotPasswordActivity.this, OTPVerificationActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("otp", otp);
        intent.putExtra("listCustomer", listCustomer);
        startActivity(intent);

        Toast.makeText(this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
    }
}