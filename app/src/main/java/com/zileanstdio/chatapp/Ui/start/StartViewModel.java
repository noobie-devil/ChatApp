package com.zileanstdio.chatapp.Ui.start;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.zileanstdio.chatapp.Data.repository.AuthRepository;
import com.zileanstdio.chatapp.Utils.StateResource;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StartViewModel extends ViewModel {

    public static final String TAG = "StartViewModel";
    private final AuthRepository authRepository;
    private final MediatorLiveData<StateResource> onCheckLoginUser = new MediatorLiveData<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public StartViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        checkLoginUser();
    }

    public void checkLoginUser() {
        authRepository.checkLoginUser().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onCheckLoginUser.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onCheckLoginUser.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        String message = "";
                        if (e.getMessage() != null) {
                            message = e.getMessage();
                        }
                        onCheckLoginUser.setValue(StateResource.error(message));
                    }
                });
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