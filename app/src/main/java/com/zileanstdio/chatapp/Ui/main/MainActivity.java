package com.zileanstdio.chatapp.Ui.main;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.main.connections.MainViewPagerAdapter;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends BaseActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager2;
    private SearchView searchView;
    private MaterialCardView searchViewActionLayout;

    @Override
    public ViewModel getViewModel() {
        return null;
    }

    @Override
    public Integer getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public Integer getViewRootId() {
        return R.id.clMainActivity;
    }

    @Override
    public void replaceFragment(BaseFragment fragment) {

    }

    @Override
    public void initAppBar() {
        super.initAppBar();
        setTitleToolbar("Tìm kiếm");
        setNavigationIcon(R.drawable.ic_magnifying_glass_light);
//        toolbar.setBackgroundColor(com.google.android.material.R.attr.colorSurface);
//        toolbar.setNavigationIconTint();
        setDisplayHomeAsUpEnabled(true);
        setDisplayShowHomeEnabled(false);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_app_bar, menu);
//        for(int i = 0; i < menu.size(); i++) {
//            MenuItem item = menu.getItem(i);
//            if(item.getItemId() == R.id.search_action) {
//                if(item.getActionView() != null) {
//                    searchView = item.getActionView().findViewById(R.id.search_view);
//                    searchViewActionLayout = item.getActionView().findViewById(R.id.cv_search_action);
//                    item.getActionView().setOnClickListener(v -> {
//                        expandSearchViewHorizontal(searchViewActionLayout, 500);
//                        Snackbar.make(v, "Test", Snackbar.LENGTH_SHORT).show();
//                    });
//                }
//            }
//        }
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    private void expandSearchViewHorizontal(View view, int duration) {
//        int prevWidth = view.getWidth();
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevWidth, (int) getResources().getDimension(com.intuit.sdp.R.dimen._240sdp));
//        valueAnimator.setDuration(duration);
//        valueAnimator.setInterpolator(new DecelerateInterpolator());
//        valueAnimator.addUpdateListener(animation -> {
//            view.getLayoutParams().width = (int) animation.getAnimatedValue();
//            searchView.setVisibility(View.VISIBLE);
//            view.requestLayout();
//        });
//        valueAnimator.start();
//    }

    public int convertDpToPixel(float dp){
        return (int) (dp * (getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_action:
                Toast.makeText(this, "hahahhahha", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAppBar();
        this.bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        this.viewPager2 = findViewById(R.id.activity_main_view_pager);
        MainViewPagerAdapter viewPagerAdapter = new MainViewPagerAdapter(this);
        this.viewPager2.setAdapter(viewPagerAdapter);
        this.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.navigiation_messages).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.navigiation_contacts).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.navigiation_profile).setChecked(true);
                        break;
                }
            }
        });

        this.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.navigiation_messages) {
                viewPager2.setCurrentItem(0);
            } else if(id == R.id.navigiation_contacts) {
                viewPager2.setCurrentItem(1);
            } else if(id == R.id.navigiation_profile) {
                viewPager2.setCurrentItem(2);
            }
            return true;
        });
    }


    @Override
    public void onClick(View v) {

    }
}