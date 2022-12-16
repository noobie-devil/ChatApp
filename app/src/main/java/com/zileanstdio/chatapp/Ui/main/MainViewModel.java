package com.zileanstdio.chatapp.Ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.Data.repository.AuthRepository;
import com.zileanstdio.chatapp.Data.repository.DatabaseRepository;
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
    final String uid;
    final MutableLiveData<List<Contact>> listMutableLiveData = new MutableLiveData<>();

    @Inject
    public MainViewModel(DatabaseRepository databaseRepository, AuthRepository authRepository) {
        this.databaseRepository = databaseRepository;
        this.authRepository = authRepository;
        uid = authRepository.getCurrentFirebaseUser().getUid();
        loadUserInfo("1bee7ac8a7cddc6bbfedb997da4b4decb50542fb8a6169b6ba31865eedba2105");
        loadContacts(uid);
    }

    public MutableLiveData<List<Contact>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    private void loadContacts(String uid) {
        databaseRepository.getContacts(uid)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .subscribe(new Observer<Contact>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Contact contact) {
                        if(listMutableLiveData.getValue() == null) {
                            listMutableLiveData.setValue(new ArrayList<Contact>(){{ add(contact);}});
                        } else{
                            List<Contact> contacts = listMutableLiveData.getValue();
                            contacts.add(contact);
                            listMutableLiveData.setValue(contacts);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadUserInfo(String uid) {
        databaseRepository.getUserInfo(uid)
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

