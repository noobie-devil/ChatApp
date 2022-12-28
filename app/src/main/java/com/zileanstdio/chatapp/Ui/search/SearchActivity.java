package com.zileanstdio.chatapp.Ui.search;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.google.android.material.textview.MaterialTextView;
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

@SuppressLint({"NotifyDataSetChanged", "ClickableViewAccessibility", "SetTextI18n"})
public class SearchActivity extends BaseActivity {

    private Integer status = 0;
    private String userName;
    private String phoneNumber;

    private RelativeLayout layoutResult;
    private MaterialTextView txvStatus;
    private ProgressBar progressBar;
    private SearchView searchView;
    private RecyclerView rcvSearchResult;

    private SearchAdapter searchAdapter;
    private final List<User> users = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initAppBar();
        setView();
        clearSearch();
        subscribeObserver();

        if (searchView != null) {
            Observable<Integer> searchInputObservable = RxSearchView.queryTextChanges(searchView)
                    .map(inputText -> specifyNumberPhone(String.valueOf(inputText)))
                    .distinctUntilChanged();

            ((SearchViewModel) viewModel).getDisposable().add(searchInputObservable.filter(item -> item != 0)
                    .debounce(800, TimeUnit.MILLISECONDS)
                    .subscribe(result -> {
                        users.clear();
                        new Handler(Looper.getMainLooper()).post(() -> searchAdapter.setUsers(users));
                        ((SearchViewModel) viewModel).search(String.valueOf(searchView.getQuery()));
                    }));

            searchView.setOnClickListener(v -> searchView.onActionViewExpanded());
        }

        layoutResult.setOnClickListener(v -> hideKeyboard());
        rcvSearchResult.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });
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

    public void setView() {
        layoutResult = findViewById(R.id.layout_result);
        txvStatus = findViewById(R.id.txv_status);
        progressBar = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.search_view);
        rcvSearchResult = findViewById(R.id.rcv_search_result);

        searchAdapter = new SearchAdapter();
        rcvSearchResult.setHasFixedSize(true);
        rcvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        rcvSearchResult.setItemAnimator(new DefaultItemAnimator());
        rcvSearchResult.setAdapter(searchAdapter);

        userName = getIntent().getStringExtra("userName");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
    }

    public void clearSearch() {
        rcvSearchResult.setVisibility(View.INVISIBLE);
        txvStatus.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        txvStatus.setText("Nhập từ khóa để tìm kiếm");
        status = 0;
    }

    public void startSearch() {
        rcvSearchResult.setVisibility(View.INVISIBLE);
        txvStatus.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        txvStatus.setText("Đang tìm kiếm...");
        status = (status == 1 ? 2 : 1);
    }

    public void searchWithData() {
        txvStatus.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        rcvSearchResult.setVisibility(View.VISIBLE);
    }

    public void searchWithoutData() {
        rcvSearchResult.setVisibility(View.INVISIBLE);
        txvStatus.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        txvStatus.setText("Không có kết quả trùng khớp");
    }

    public Integer specifyNumberPhone(String input) {
        if (!input.isEmpty()) {
            startSearch();
        } else {
            clearSearch();
        }
        return status;
    }

    public void subscribeObserver() {
        ((SearchViewModel) viewModel).getListUser().observe(this, user -> {
            if ((user.getUserName() == null) || (user.getPhoneNumber() == null) ) {
                searchWithoutData();
            } else {
                if (!user.getUserName().equals(userName) && !user.getPhoneNumber().equals(phoneNumber)) {
                    users.add(user);
                    searchAdapter.setUsers(users);
                    searchWithData();
                } else {
                    searchWithoutData();
                }
            }
        });
    }
}