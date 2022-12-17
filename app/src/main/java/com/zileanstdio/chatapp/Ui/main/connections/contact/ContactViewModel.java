package com.zileanstdio.chatapp.Ui.main.connections.contact;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.Data.model.ContactWrapInfo;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.Data.repository.DatabaseRepository;
import com.zileanstdio.chatapp.Ui.main.connections.chat.ChatViewModel;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ContactViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private final DatabaseRepository databaseRepository;
    private final CompositeDisposable disposable;
    final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, ContactWrapInfo>> contactWrapInfoLiveData = new MutableLiveData<>();
    private final HashMap<String, ContactWrapInfo> contactWrapInfoHashMap = new HashMap<>();
    private Navigator navigator;

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    @Inject
    public ContactViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
        this.disposable = new CompositeDisposable();

    }

    public CompositeDisposable getDisposable() {
        return disposable;
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public HashMap<String, ContactWrapInfo> getContactWrapInfoHashMap() {
        return contactWrapInfoHashMap;
    }

    public void getContactWrapInfo(String uid) {
        databaseRepository.getContactWrapInfo(uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
            .subscribe(new Observer<ContactWrapInfo>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    disposable.add(d);
                }

                @Override
                public void onNext(@NonNull ContactWrapInfo contactWrapInfo) {
                    Debug.log("getContactWrapInfo:ViewModel", "onNext: " + contactWrapInfo.toString());
                    contactWrapInfoHashMap.put(contactWrapInfo.getContact().getNumberPhone(), contactWrapInfo);
                    contactWrapInfoLiveData.setValue(contactWrapInfoHashMap);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Debug.log("getContactWrapInfo:ViewModel", "onError: " + e.getMessage());
                    contactWrapInfoLiveData.setValue(contactWrapInfoHashMap);
                }

                @Override
                public void onComplete() {

                }
            });
    }

    public MutableLiveData<HashMap<String, ContactWrapInfo>> getContactWrapInfoLiveData() {
        return contactWrapInfoLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposable != null) {
            disposable.dispose();
        }
    }

    public interface Navigator {
        public void navigateToMessage(ConversationWrapper conversationWrapper, Contact contact, User contactProfile);
    }
}
