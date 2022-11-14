package com.zileanstdio.chatapp.Ui.start;

import androidx.lifecycle.ViewModel;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.auth.AuthActivity;
import com.zileanstdio.chatapp.Ui.main.MainActivity;

public class StartActivity extends BaseActivity {

    private int STATUS = 0;
    private boolean isLogin = false;

    @Override
    public ViewModel getViewModel() {
        return null;
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

        checkUser();

        // Thực hiện các chuyển động
        new Handler().postDelayed(() -> {
            animateImage.start();
            textName.setVisibility(View.VISIBLE);
            textName.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            new Handler().postDelayed(() -> {
                progressWait.setVisibility(View.VISIBLE);
                STATUS ++;
                transferActivity();
            }, 1200);
        }, 750);
    }

    @Override
    public void onClick(View view) {

    }

    public void checkUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if ((user == null) || (user.getPhoneNumber() == null)) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();
        }
        STATUS ++;
        transferActivity();
    }

    private void transferActivity() {
        if (STATUS == 2) {
            Intent intent;
            if (isLogin) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, AuthActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }
}