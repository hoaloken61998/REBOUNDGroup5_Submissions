package com.rebound.login;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;
import com.rebound.utils.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.rebound.R;
import com.rebound.models.Customer.Customer;
import com.rebound.login.WishlistActivity;


public class ProfileFragment extends Fragment {

    private LinearLayout optionHelpCenter;
    private LinearLayout optionYourProfile;
    private LinearLayout optionLogOut;
    private LinearLayout optionWishList;
    private ImageView imgBackProfile;
    private ImageView imgAvatar;
    private TextView txtProfileUserName;

    private Customer currentCustomer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        addViews(view);
        addEvents();
        loadUserInfo();

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        return view;
    }

    private void addViews(View view) {
        optionHelpCenter = view.findViewById(R.id.optionHelpCenter);
        optionYourProfile = view.findViewById(R.id.optionYourProfile);
        optionLogOut = view.findViewById(R.id.optionLogOut);
        optionWishList = view.findViewById(R.id.optionWishList);
        imgBackProfile = view.findViewById(R.id.imgBackProfile);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        txtProfileUserName = view.findViewById(R.id.txtProfileUserName);
    }

    private void addEvents() {
        optionHelpCenter.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HelpCenterActivity.class));
        });

        optionWishList.setOnClickListener(v -> {
            currentCustomer = SharedPrefManager.getCurrentCustomer(requireContext());
            if (currentCustomer == null) {
                // Chưa đăng nhập
                Toast.makeText(getContext(), "Please log in to view your wishlist", Toast.LENGTH_SHORT).show();
                return;
            }
            // Đã đăng nhập
            startActivity(new Intent(getActivity(), WishlistActivity.class));
        });

        optionYourProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            // Có thể truyền thêm username nếu cần
            if (currentCustomer != null) {
                intent.putExtra("username", currentCustomer.getUsername());
            }
            startActivity(intent);
        });

        optionLogOut.setOnClickListener(v -> {
            // Xóa thông tin đăng nhập
            SharedPreferences.Editor editor = requireActivity()
                    .getSharedPreferences("user_session", Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        imgBackProfile.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void loadUserInfo() {
        currentCustomer = SharedPrefManager.getCurrentCustomer(requireContext());

        if (currentCustomer != null) {
            txtProfileUserName.setText(currentCustomer.getFullName());

            String avatarUrl = currentCustomer.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this)
                        .load(avatarUrl)
                        .placeholder(R.mipmap.ic_avatar_sample)
                        .into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.mipmap.ic_avatar_sample);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo(); // Load lại dữ liệu khi quay về fragment
    }
}
