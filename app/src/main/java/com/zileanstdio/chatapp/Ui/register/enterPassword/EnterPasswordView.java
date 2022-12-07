package com.zileanstdio.chatapp.Ui.register.enterPassword;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.register.RegisterActivity;
import com.zileanstdio.chatapp.Ui.register.RegisterViewModel;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class EnterPasswordView extends BaseFragment {

    private TextInputLayout textInputPassword;
    private User user;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public EnterPasswordView() {
        // Required empty public constructor
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public ViewModel getViewModel() {
        if(viewModel != null) {
            return viewModel;
        }
        viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(EnterPasswordViewModel.class);
        return viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscribeObservers();

    }

    @Override
    protected void initAppBar() {
        baseActivity.setTitleToolbar(getResources().getString(R.string.password_text));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_enter_password_view, container, false);
        return super.onCreateView(inflater, view, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textInputPassword = view.findViewById(R.id.enter_password_view_text_input_password);
        setListeners();

    }

    private void setListeners() {
        ((RegisterViewModel) baseActivity.getViewModel()).getRegisterInfo()
                .observe(getViewLifecycleOwner(), this::setUser);

        EditText passwordEditText = textInputPassword.getEditText();
        if(passwordEditText != null) {
            disposable.add(RxTextView.textChanges(passwordEditText)
                    .map(inputText -> validatePassword(inputText.toString().trim()))
                    .distinctUntilChanged()
                    .subscribe(isValid -> ((RegisterActivity) baseActivity).getNextActionBtn().setEnabled(isValid)));

            disposable.add(RxView.clicks(((RegisterActivity) baseActivity).getNextActionBtn())
                    .subscribe(unit -> ((EnterPasswordViewModel) viewModel).createWithEmailPasswordAuthCredential(user.getPhoneNumber(), passwordEditText.getText().toString())));
        }


    }

    private boolean validatePassword(String password) {
        if(password.trim().length() == 0) {
            try {
                Objects.requireNonNull(textInputPassword.getEditText()).getText().clear();
                textInputPassword.getEditText().setSelection(0);

            } catch (NullPointerException e) {
                Debug.log(getTag(), e.getMessage());
            }
            return false;
        } else if(password.length() < 8 || password.length() > 64) {
            textInputPassword.setError("Mật khẩu phải chứa từ 8 - 64 ký tự *");
            textInputPassword.setErrorIconDrawable(null);
            return false;
        }
        textInputPassword.setError(null);
        return true;
    }

    private void subscribeObservers() {
        ((EnterPasswordViewModel) viewModel).observeCreateWithEmailPassword()
                .observe(this, stateResource -> {
                    if(stateResource != null) {
                        switch (stateResource.status) {
                            case LOADING:
                                baseActivity.showLoadingDialog();
                                break;
                            case SUCCESS:
                                ((EnterPasswordViewModel) viewModel).updateRegisterInfo(user);                                break;
                            case ERROR:
                                baseActivity.closeLoadingDialog();
                                showSnackBar(stateResource.message, Snackbar.LENGTH_LONG);
                                break;
                        }
                    }
                });

        ((EnterPasswordViewModel) viewModel).observeUpdateRegisterInfo()
                .observe(this, stateResource -> {
                    if(stateResource != null) {
                        switch (stateResource.status) {
                            case LOADING:
                                baseActivity.showLoadingDialog();
                                break;
                            case SUCCESS:
                                baseActivity.closeLoadingDialog();
                                
                                showSnackBar("Registered successfully", Snackbar.LENGTH_LONG);
                                break;
                            case ERROR:
                                baseActivity.closeLoadingDialog();
                                showSnackBar(stateResource.message, Snackbar.LENGTH_LONG);
                                break;
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}