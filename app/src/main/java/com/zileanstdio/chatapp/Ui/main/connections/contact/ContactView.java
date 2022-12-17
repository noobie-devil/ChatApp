package com.zileanstdio.chatapp.Ui.main.connections.contact;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.zileanstdio.chatapp.Adapter.ContactAdapter;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.main.MainActivity;
import com.zileanstdio.chatapp.Ui.message.MessageActivity;
import com.zileanstdio.chatapp.Ui.sync.SyncContactActivity;
import com.zileanstdio.chatapp.Utils.CipherUtils;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ContactView extends BaseFragment<ContactViewModel> implements ContactViewModel.Navigator {
    MaterialCardView cardViewLocalContact;
    private RecyclerView rcvContact;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmer;

    private ContactAdapter contactAdapter;
    private final BehaviorSubject<Boolean> shimmerListener = BehaviorSubject.createDefault(true);

    public ContactView() {
        // Required empty public constructor
    }


    @Override
    public ContactViewModel getViewModel() {
        if(viewModel != null) {
            return viewModel;
        }
        viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(ContactViewModel.class);
        viewModel.setNavigator(this);
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
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        shimmer = view.findViewById(R.id.shimmer_view_contact);
        rcvContact = view.findViewById(R.id.rcv_contact);
        rcvContact.setLayoutManager(new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false));
        rcvContact.setItemAnimator(new DefaultItemAnimator());
        contactAdapter = new ContactAdapter(baseActivity, viewModel);
        rcvContact.setAdapter(contactAdapter);
        cardViewLocalContact = view.findViewById(R.id.cardViewLocalContact);
        cardViewLocalContact.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SyncContactActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(SyncContactActivity.ARG_CURRENT_USER, viewModel.getCurrentUser().getValue());
            intent.putExtras(bundle);
            startActivity(intent);
        });

        shimmerListener.subscribe(new io.reactivex.rxjava3.core.Observer<Boolean>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                viewModel.getDisposable().add(d);
            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {
                if(aBoolean != null && aBoolean) {
                    showShimmer();
                } else {
                    hideShimmer();
                }
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> viewModel.getContactWrapInfo(CipherUtils.Hash.sha256(user.getPhoneNumber())));

        ((MainActivity) baseActivity).getViewModel().getUserInfo().observe(baseActivity, user -> {
            Debug.log("getUserInfo", user.toString());
            viewModel.getCurrentUser().setValue(user);
        });

        viewModel.getContactWrapInfoLiveData().observe(getViewLifecycleOwner(), contactWrapInfo -> {
            Debug.log("getContactWrapInfoLiveData", String.valueOf(contactWrapInfo.size()));
            if(contactWrapInfo.size() == 0) {
                shimmerListener.onNext(false);

            } else {
                contactAdapter.stringHashMap.clear();
                contactAdapter.submitList(new ArrayList<>(contactWrapInfo.values()));
                shimmerListener.onNext(false);

            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(viewModel.getCurrentUser().getValue() != null) {
                shimmerListener.onNext(true);
                viewModel.getContactWrapInfo(CipherUtils.Hash.sha256(viewModel.getCurrentUser().getValue().getPhoneNumber()));
            }
        });
    }

    public void showShimmer() {
        rcvContact.setVisibility(View.GONE);
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();
    }
    public void hideShimmer() {
        rcvContact.setVisibility(View.VISIBLE);
        shimmer.setVisibility(View.GONE);
        shimmer.stopShimmer();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void navigateToMessage(ConversationWrapper conversationWrapper, Contact contact, User contactProfile) {
        Intent intent = new Intent(baseActivity, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MessageActivity.ARG_CONVERSATION_WRAPPER, conversationWrapper);
        bundle.putSerializable(MessageActivity.ARG_CURRENT_UID, CipherUtils.Hash.sha256(viewModel.getCurrentUser().getValue().getPhoneNumber()));
        bundle.putSerializable(MessageActivity.ARG_CONTACT, contact);
        bundle.putSerializable(MessageActivity.ARG_CURRENT_USER, viewModel.getCurrentUser().getValue());
        bundle.putSerializable(MessageActivity.ARG_CONTACT_PROFILE, contactProfile);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}