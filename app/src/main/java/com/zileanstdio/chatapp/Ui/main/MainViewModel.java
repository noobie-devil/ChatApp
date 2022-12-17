package com.zileanstdio.chatapp.Ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.Data.repository.AuthRepository;
import com.zileanstdio.chatapp.Data.repository.DatabaseRepository;
import com.zileanstdio.chatapp.Utils.CipherUtils;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends ViewModel {

    static final String TAG = "MainViewModel";
    final DatabaseRepository databaseRepository;
    final AuthRepository authRepository;
    final CompositeDisposable disposable = new CompositeDisposable();
    final MediatorLiveData<User> currentUserInfo = new MediatorLiveData<>();

    @Inject
    public MainViewModel(DatabaseRepository databaseRepository, AuthRepository authRepository) {
        this.databaseRepository = databaseRepository;
        this.authRepository = authRepository;
        String id = authRepository.getCurrentFirebaseUser().getEmail();
        if (id != null) {
            id = id.substring(0, id.indexOf('@'));
        }
        loadUserInfo(CipherUtils.Hash.sha256(id));
    }

    public void loadUserInfo(String id) {
        databaseRepository.getUserInfo(id)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull User user) {
                        currentUserInfo.setValue(user);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Debug.log("loadUserInfo:onError", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public LiveData<User> getUserInfo() {
        return currentUserInfo;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}