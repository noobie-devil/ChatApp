package com.zileanstdio.chatapp.Ui.start;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.auth.AuthActivity;
import com.zileanstdio.chatapp.Ui.main.MainActivity;
import com.zileanstdio.chatapp.Utils.Debug;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class StartActivity extends BaseActivity {

    private boolean isLogin = false;
    private int STATUS = 0;
    private String message = "";
    BehaviorSubject<Boolean> animateLoading = BehaviorSubject.createDefault(false);
    BehaviorSubject<Boolean> checkLoginState = BehaviorSubject.createDefault(false);

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public ViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(StartViewModel.class);
        }
        return viewModel;
    }

    @Override
    public Integer getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    public Integer getViewRootId() {
        return R.id.clStartActivity;
    }

    @Override
    public void replaceFragment(BaseFragment fragment) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ImageView imageIcon = findViewById(R.id.image_icon);
        ProgressBar progressWait = findViewById(R.id.progress_wait);
        TextView textName = findViewById(R.id.text_name);

        // Tạo chuyển động cho ImageView
        ObjectAnimator scaleImageX = ObjectAnimator.ofFloat(imageIcon, "scaleX", 0.85f);
        scaleImageX.setDuration(1000);
        ObjectAnimator scaleImageY = ObjectAnimator.ofFloat(imageIcon, "scaleY", 0.85f);
        scaleImageY.setDuration(1000);
        ObjectAnimator translationImageY = ObjectAnimator.ofFloat(imageIcon, "translationY", -200);
        translationImageY.setDuration(1000);
        AnimatorSet animateImage = new AnimatorSet();
        animateImage.play(scaleImageX).with(scaleImageY).with(translationImageY);

        disposable.add(BehaviorSubject.combineLatest(
                animateLoading,
                checkLoginState,
                (state1, state2) -> state1 && state2
        ).subscribe(aBoolean -> {
            if(aBoolean != null && aBoolean) {
                transferActivity();
            }
        }));

        subscribeObservers();


//        // Kiểm tra User
//        ((StartViewModel) viewModel).checkLoginUser();

//        // Thực hiện các chuyển động
        new Handler().postDelayed(() -> {
            animateImage.start();
            textName.setVisibility(View.VISIBLE);
            textName.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            new Handler().postDelayed(() -> {
                progressWait.setVisibility(View.VISIBLE);
//                STATUS ++;
                animateLoading.onNext(true);
//                transferActivity();
            }, 1200);
        }, 750);



    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public void onClick(View view) {

    }

    public void subscribeObservers() {
        // Lắng nghe kết quả trả về từ quá trình kiểm tra User
        ((StartViewModel) viewModel).observeCheckLoginUser().observe(this, stateResource -> {
            if (stateResource != null) {
                switch (stateResource.status) {
                    case SUCCESS:
                        isLogin = true;
//                        STATUS ++;
                        message = "Chào mừng trở lại";
                        checkLoginState.onNext(true);
                        Debug.log("loginState", "success");
                        transferActivity();
                        break;
                    case ERROR:
                        isLogin = false;
//                        STATUS ++;
                        message = stateResource.message;
                        checkLoginState.onNext(true);
                        Debug.log("loginState", "error");
                        transferActivity();
                        break;
                }
            }
        });
    }

    private void transferActivity() {
        // Đợi hoàn thành 'chuyển động' + 'kiểm tra User' -> chuyển Activity khi cả 2 hoàn thành
//        if (STATUS == 2) {
            Intent intent;
            if (isLogin) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, AuthActivity.class);
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
//        }
    }
}