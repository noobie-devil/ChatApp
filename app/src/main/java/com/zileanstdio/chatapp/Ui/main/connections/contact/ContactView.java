package com.zileanstdio.chatapp.Ui.main.connections.contact;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.main.connections.chat.ChatViewModel;
import com.zileanstdio.chatapp.Ui.sync.SyncContactActivity;

public class ContactView extends BaseFragment<ContactViewModel> {
    MaterialCardView cardViewLocalContact;

    public ContactView() {
        // Required empty public constructor
    }


    @Override
    public ContactViewModel getViewModel() {
        if(viewModel != null) {
            return viewModel;
        }
        viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(ContactViewModel.class);
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
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.layout_contact_view, container, false);
        return super.onCreateView(inflater, viewGroup, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardViewLocalContact = view.findViewById(R.id.cardViewLocalContact);
        cardViewLocalContact.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SyncContactActivity.class);
            startActivity(intent);
        });
    }
}