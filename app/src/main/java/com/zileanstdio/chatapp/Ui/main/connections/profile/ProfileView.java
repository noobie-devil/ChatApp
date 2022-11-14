package com.zileanstdio.chatapp.Ui.main.connections.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;

public class ProfileView extends BaseFragment {

    public ProfileView() {
        // Required empty public constructor
    }

    @Override
    public ViewModel getViewModel() {
        return null;
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
}