package com.zileanstdio.chatapp.Ui.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;


import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAppBar();
    }

    @Override
    public void initAppBar() {
        super.initAppBar();
        setTitleToolbar("Đăng nhập");
        setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public ViewModel getViewModel() {
        if(viewModel != null) {
            return viewModel;
        }
        viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(LoginViewModel.class);
        return viewModel;    }

    @Override
    public Integer getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public Integer getViewRootId() {
        return R.id.clLoginActivity;
    }

    @Override
    public void replaceFragment(BaseFragment fragment) {

    }

    @Override
    public void onClick(View v) {

    }
}