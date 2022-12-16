package com.zileanstdio.chatapp.Ui.main;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.main.connections.MainViewPagerAdapter;
import com.zileanstdio.chatapp.Utils.Debug;

public class MainActivity extends BaseActivity<MainViewModel> {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager2;

    @Override
    public MainViewModel getViewModel() {
        if(viewModel != null) {
            return viewModel;
        }
        viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(MainViewModel.class);
        return viewModel;
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
        setDisplayHomeAsUpEnabled(true);
        setDisplayShowHomeEnabled(false);
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

//        viewModel.getUserInfo().observe(this, user -> {
//            Debug.log("getUserInfo", user.toString());
//        });
        viewModel.getListMutableLiveData().observe(this, contacts -> {

        });
    }


    @Override
    public void onClick(View v) {

    }
}