package com.zileanstdio.chatapp.Ui.register.selectGenderAndBirthDate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;

public class SelectGenderAndBirthDateView extends BaseFragment {


    public SelectGenderAndBirthDateView() {
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
        return inflater.inflate(R.layout.layout_select_gender_and_birth_date_view, container, false);
    }
}