package com.rebound.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.models.Customer.Customer;
import com.rebound.utils.SharedPrefManager;

public class EditProfileActivity extends AppCompatActivity {
    ImageView imgBackEditProfile;
    MaterialButton btnUpdate;

    EditText edtUsername, edtFullName, edtEmail, edtPhone, edtPassword;
    TextView txtAvatarNote;
    Spinner spGender;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgAvatar, imgEdit;
    private Uri selectedImageUri;

    private Customer currentCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        addViews();
        loadUserData();
        addEvents();
    }

    private void addViews() {
        btnUpdate = findViewById(R.id.btnUpdate);
        imgBackEditProfile = findViewById(R.id.imgBackEditProfile);
        edtUsername = findViewById(R.id.edtEditProfileUsername);
        edtUsername.setFocusable(false);
        edtUsername.setClickable(false);
        edtFullName = findViewById(R.id.edtEditProfileFullName);
        edtEmail = findViewById(R.id.edtEditProfileEmail);
        edtPhone = findViewById(R.id.edtEditProfilePhone);
        edtPassword = findViewById(R.id.edtEditProfilePassword);
        spGender = findViewById(R.id.spGenderEditProfile);
        txtAvatarNote = findViewById(R.id.txtAvatarNote);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgEdit = findViewById(R.id.imgEdit);
    }

    private void addEvents() {
        imgBackEditProfile.setOnClickListener(v -> finish());

        imgEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnUpdate.setOnClickListener(v -> {
            if (currentCustomer != null) {
                currentCustomer.setUsername(edtUsername.getText().toString().trim());
                currentCustomer.setFullName(edtFullName.getText().toString().trim());
                currentCustomer.setEmail(edtEmail.getText().toString().trim());
                currentCustomer.setPhone(edtPhone.getText().toString().trim());
                currentCustomer.setPassword(edtPassword.getText().toString().trim());
                currentCustomer.setGender(spGender.getSelectedItem().toString());

                // Lấy customer hiện tại từ SharedPref để đảm bảo avatar luôn đồng bộ
                Customer savedCustomer = SharedPrefManager.getCurrentCustomer(this);


                // Nếu có chọn ảnh mới thì dùng ảnh mới, không thì giữ ảnh cũ từ savedCustomer
                if (selectedImageUri != null) {
                    currentCustomer.setAvatarUrl(selectedImageUri.toString());
                } else {
                    if (savedCustomer != null && savedCustomer.getAvatarUrl() != null) {
                        currentCustomer.setAvatarUrl(savedCustomer.getAvatarUrl());
                    } else {
                        // Không có ảnh cũ, thì để null hoặc placeholder
                        currentCustomer.setAvatarUrl(null);
                    }
                }
                SharedPrefManager.updateCustomer(this, currentCustomer);
                SharedPrefManager.setCurrentCustomer(this, currentCustomer);

                Toast.makeText(this, "Update profile successfully", Toast.LENGTH_SHORT).show();
                new android.os.Handler().postDelayed(this::finish, 1200);
            } else {
                Toast.makeText(this, "Cannot find user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        currentCustomer = SharedPrefManager.getCurrentCustomer(this);

        if (currentCustomer != null) {
            edtUsername.setText(currentCustomer.getUsername());
            edtFullName.setText(currentCustomer.getFullName());
            edtEmail.setText(currentCustomer.getEmail());
            edtPhone.setText(currentCustomer.getPhone());
            edtPassword.setText(currentCustomer.getPassword());
            txtAvatarNote.setText(currentCustomer.getFullName());

            String[] genderOptions = getResources().getStringArray(R.array.gender_options);
            int selectedIndex = 0;
            for (int i = 0; i < genderOptions.length; i++) {
                if (genderOptions[i].equalsIgnoreCase(currentCustomer.getGender())) {
                    selectedIndex = i;
                    break;
                }
            }
            spGender.setSelection(selectedIndex);

            if (currentCustomer.getAvatarUrl() != null && !currentCustomer.getAvatarUrl().isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(currentCustomer.getAvatarUrl()))
                        .circleCrop()
                        .placeholder(R.mipmap.ic_avatar_sample)
                        .into(imgAvatar);
            }
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Cấp quyền truy cập URI vĩnh viễn
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                try {
                    getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

            Glide.with(this)
                    .load(selectedImageUri)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
                    .placeholder(R.mipmap.ic_avatar_sample)
                    .into(imgAvatar);
        }
    }

}
