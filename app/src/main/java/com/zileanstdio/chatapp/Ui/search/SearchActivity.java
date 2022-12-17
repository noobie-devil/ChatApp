package com.zileanstdio.chatapp.Ui.search;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.jakewharton.rxbinding4.widget.RxSearchView;
import com.zileanstdio.chatapp.Adapter.SearchAdapter;
import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@SuppressLint("NotifyDataSetChanged")
public class SearchActivity extends BaseActivity {

    private boolean isTrue = false;
    private String userName;
    private String phoneNumber;

    private SearchView searchView;
    private SearchAdapter searchAdapter;
    private final List<User> users = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initAppBar();

        searchAdapter = new SearchAdapter();
        searchView = findViewById(R.id.search_view);
        RecyclerView rcvSearchResult = findViewById(R.id.rcv_search_result);

        rcvSearchResult.setHasFixedSize(true);
        rcvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        rcvSearchResult.setItemAnimator(new DefaultItemAnimator());
        rcvSearchResult.setAdapter(searchAdapter);

        userName = getIntent().getStringExtra("userName");
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        subscribeObserver();

        if (searchView != null) {
            Observable<Boolean> searchInputInputObservable = RxSearchView.queryTextChanges(searchView)
                    .debounce(752, TimeUnit.MILLISECONDS)
                    .filter(item -> item.length() > 0)
                    .map(inputText -> specifyNumberPhone())
                    .distinctUntilChanged();
            disposable.add(searchInputInputObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(isNumberPhone -> {
                users.clear();
                searchAdapter.setUsers(users);
                ((SearchViewModel) viewModel).search(String.valueOf(searchView.getQuery()));
                isTrue = !isNumberPhone;
            }));
        }
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
    public void initAppBar() {
        super.initAppBar();
        setTitleToolbar("Tìm kiếm");
        setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public ViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(SearchViewModel.class);
        }
        return viewModel;
    }

    @Override
    public Integer getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public Integer getViewRootId() {
        return R.id.clSearchActivity;
    }

    @Override
    public void replaceFragment(BaseFragment fragment) {

    }

    @Override
    public void onClick(View view) {

    }

    public boolean specifyNumberPhone() {
        return isTrue;
    }

    public void subscribeObserver() {
        ((SearchViewModel) viewModel).getListUser().observe(this, user -> {
            if (!user.getUserName().equals(userName) && !user.getPhoneNumber().equals(phoneNumber)) {
                users.add(user);
                searchAdapter.setUsers(users);
            }
        });
    }
}