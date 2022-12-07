package com.zileanstdio.chatapp.Ui.start;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zileanstdio.chatapp.Data.repository.AuthRepository;
import com.zileanstdio.chatapp.Utils.StateResource;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class StartViewModel extends ViewModel {

    public static final String TAG = "StartViewModel";
    private final AuthRepository authRepository;
    private final MediatorLiveData<StateResource> onCheckLoginUser = new MediatorLiveData<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public StartViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void checkLoginUser() {

    }

    public LiveData<StateResource> observeCheckLoginUser() {
        return onCheckLoginUser;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}