package com.zileanstdio.chatapp.Base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.android.material.appbar.MaterialToolbar;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.ViewModel.ViewModelProviderFactory;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public abstract class BaseActivity<V extends ViewModel> extends DaggerAppCompatActivity implements View.OnClickListener {

    protected final String TAG = this.getClass().getSimpleName();

    private Dialog loadingDialog;

    protected V viewModel;

    public abstract V getViewModel();

    public abstract @LayoutRes Integer getLayoutId();

    public abstract @IdRes Integer getViewRootId();

    public MaterialToolbar toolbar = null;

    @Inject
    protected ViewModelProviderFactory providerFactory;

    public ViewModelProviderFactory getProviderFactory() {
        return providerFactory;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if(getViewRootId() != null) {
            ViewGroup viewGroup = findViewById(getViewRootId());
            viewGroup.setOnTouchListener((v, event) -> {
                hideKeyboard();
                return false;
            });
        }
//        BaseApplication.getInstance().setActivityContext(this);
        createLoadingDialog();
        getViewModel();
    }

    public void initAppBar() {
        if(getViewRootId() != null) {
            ViewGroup viewGroup = findViewById(getViewRootId());
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View appBar = inflater.inflate(R.layout.layout_appbar, null);
            appBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            toolbar = appBar.findViewById(R.id.toolbar);
            setSupportActionBar(this.toolbar);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
            viewGroup.addView(appBar, 0);
        }
    }

    public void setTitleToolbar(String title) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void setNavigationIcon(@DrawableRes int resId) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(resId);
        }
    }

    public void setDisplayShowHomeEnabled(boolean showHomeEnabled) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(showHomeEnabled);
        }
    }
    public void setDisplayHomeAsUpEnabled(boolean homeAsUpEnabled) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpEnabled);
        }
    }


    private void createLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.layout_dialog_loading);
        loadingDialog.setCanceledOnTouchOutside(false);
        Window window = loadingDialog.getWindow();
        if(window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
    }

    public void showLoadingDialog(String content) {
        showLoadingDialog(content, true);
    }

    public void showLoadingDialog() {
        showLoadingDialog(null, true);
    }

    public void showLoadingDialog(boolean cancelable) {
        showLoadingDialog(null, false);
    }

    public void showLoadingDialog(String content, boolean cancelable) {
        try {
            hideKeyboard();
            if(loadingDialog != null && loadingDialog.isShowing()) {
                closeLoadingDialog();
            }
            createLoadingDialog();
            TextView loadingContent = loadingDialog.findViewById(R.id.txvContent);
            loadingContent.setText(getResources().getString(R.string.loading_text));
            if(content != null) {
                loadingContent.setText(content);
            }
            loadingDialog.setCancelable(cancelable);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }



    public void closeLoadingDialog() {
        if(loadingDialog != null) {
            try {
                loadingDialog.dismiss();
                loadingDialog = null;
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    public void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, 0);
            }
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
