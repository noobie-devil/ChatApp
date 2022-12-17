package com.zileanstdio.chatapp.Ui.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.Data.repository.DatabaseRepository;
import com.zileanstdio.chatapp.Utils.CipherUtils;

import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchViewModel extends ViewModel {

    public static final String TAG = "SearchViewModel";
    private final DatabaseRepository databaseRepository;
    private final MediatorLiveData<User> listUser = new MediatorLiveData<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public SearchViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void search(String keyword) {
        if (Pattern.matches("^[0-9]{9}[0-9]+$", keyword)) {
            databaseRepository.searchUserFromPhoneNumber(CipherUtils.Hash.sha256(keyword))
                    .subscribeOn(Schedulers.io())
                    .toObservable()
                    .subscribe(new Observer<User>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            disposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull User user) {
                            listUser.setValue(user);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d("AAA", e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            databaseRepository.searchUserFromUserName(keyword)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
                    .subscribe(new Observer<User>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            disposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull User user) {
                            listUser.setValue(user);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d("AAA", e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    public LiveData<User> getListUser() {
        return listUser;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}