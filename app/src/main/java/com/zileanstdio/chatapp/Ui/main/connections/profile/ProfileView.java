package com.zileanstdio.chatapp.Ui.main.connections.profile;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.auth.AuthActivity;
import com.zileanstdio.chatapp.Ui.change.ChangePasswordActivity;
import com.zileanstdio.chatapp.Ui.main.MainActivity;

@SuppressLint("SetTextI18n")
public class ProfileView extends BaseFragment {

    private String userName, numberPhone;

    private ImageView imvAvatar;
    private MaterialButton btnUsername, btnPassword, btnLogout;
    private MaterialTextView txvName, txvPhone, txvBirthdate, txvGender;

    public ProfileView() {
        // Required empty public constructor
    }

    @Override
    public ViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(ProfileViewModel.class);
        }
        return viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initAppBar() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.layout_profile_view, container, false);
        return super.onCreateView(inflater, viewGroup, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        subscribeObserversUpdateUserName();
        subscribeObserversLogout();


        ((MainActivity) baseActivity).getViewModel().getUserInfo().observe(getViewLifecycleOwner(), user -> {
            if ((user.getAvatarImageUrl() != null) && !user.getAvatarImageUrl().isEmpty()) {
                Picasso.get().load(user.getAvatarImageUrl()).into(imvAvatar);
            } else {
                imvAvatar.setImageResource(R.drawable.ic_default_user);
            }
            if ((user.getUserName() != null) && !user.getUserName().isEmpty()) {
                btnUsername.setText("Người dùng: " + user.getUserName());
            } else {
                btnUsername.setText("Người dùng: (không có)");
            }
            txvName.setText("Họ và tên: " + user.getFullName());
            txvPhone.setText("Số điện thoại: " + user.getPhoneNumber());
            txvBirthdate.setText("Ngày sinh: " + user.getBirthDate());
            if (user.getGender().equals("Male")) {
                txvGender.setText("Giới tính: Nam");
            } else {
                txvGender.setText("Giới tính: Nữ");
            }

            userName = user.getUserName();
            numberPhone = user.getPhoneNumber();
        });

        btnUsername.setOnClickListener(v -> {
            View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.layout_text_username, null, false);
            TextInputLayout userNameInputLayout = viewDialog.findViewById(R.id.text_input_user_name);
            EditText userNameEditText = userNameInputLayout.getEditText();

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(baseActivity);
            builder.setTitle("Đổi tên");
            builder.setIcon(R.drawable.icon_start);
            builder.setView(viewDialog);
            builder.setPositiveButton("Lưu", null);
            builder.setNegativeButton("Hủy", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(position -> {
                if ((userNameEditText != null) && !userNameEditText.getText().toString().isEmpty()) {
                    if (!userNameEditText.getText().toString().equals(userName)) {
                        ((ProfileViewModel) viewModel).updateUserName(userNameEditText.getText().toString(), numberPhone);
                        dialog.cancel();
                    } else {
                        userNameInputLayout.setError("Tên mới phải khác tên hiện tại '" + userName + "'");
                        userNameInputLayout.setErrorIconDrawable(null);
                    }
                } else {
                    userNameInputLayout.setError("Tên người dùng không thể trống");
                    userNameInputLayout.setErrorIconDrawable(null);
                }
            });
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(negative -> dialog.cancel());
        });
        btnPassword.setOnClickListener(v -> {
            Intent startChangePasswordActivity = new Intent(getContext(), ChangePasswordActivity.class);
            startChangePasswordActivity.putExtra("email", String.format("%s@gmail.com", numberPhone));
            startActivity(startChangePasswordActivity);
        });
        btnLogout.setOnClickListener(v -> ((ProfileViewModel) viewModel).logout());
    }

    private void initView(View view) {
        imvAvatar = view.findViewById(R.id.imv_avatar);
        btnUsername = view.findViewById(R.id.btn_user_name);
        btnPassword = view.findViewById(R.id.btn_password);
        txvName = view.findViewById(R.id.txv_name);
        txvPhone = view.findViewById(R.id.txv_phone);
        txvBirthdate = view.findViewById(R.id.txv_birthdate);
        txvGender = view.findViewById(R.id.txv_gender);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    public void subscribeObserversUpdateUserName() {
        ((ProfileViewModel) viewModel).observeUpdateUserName().observe(getViewLifecycleOwner(), stateResource -> {
            if (stateResource != null) {
                switch (stateResource.status) {
                    case LOADING:
                        baseActivity.showLoadingDialog();
                        break;
                    case SUCCESS:
                        baseActivity.closeLoadingDialog();
                        Toast.makeText(getContext(), "Đổi tên thành công", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        baseActivity.closeLoadingDialog();
                        Toast.makeText(getContext(), "Đã có lỗi xảy ra!\nVui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public void subscribeObserversLogout() {
        ((ProfileViewModel) viewModel).observeLogout().observe(getViewLifecycleOwner(), stateResource -> {
            if (stateResource != null) {
                switch (stateResource.status) {
                    case LOADING:
                        baseActivity.showLoadingDialog();
                        break;
                    case SUCCESS:
                        Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                        Intent startAuthActivity = new Intent(getContext(), AuthActivity.class);
                        startAuthActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(startAuthActivity);
                        break;
                    case ERROR:
                        baseActivity.closeLoadingDialog();
                        Toast.makeText(getContext(), stateResource.message, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}