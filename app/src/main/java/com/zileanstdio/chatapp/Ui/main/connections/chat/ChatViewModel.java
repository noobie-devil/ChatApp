package com.zileanstdio.chatapp.Ui.main.connections.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QuerySnapshot;
import com.zileanstdio.chatapp.Data.model.Conversation;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.Data.repository.DatabaseRepository;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChatViewModel extends ViewModel {

    private final String TAG = this.getClass().getSimpleName();
    private final DatabaseRepository databaseRepository;
    private final CompositeDisposable disposable;
    private final MediatorLiveData<List<ConversationWrapper>> conversationsLiveData;
    private final HashMap<String, ConversationWrapper> conversationList = new HashMap<>();
    final MutableLiveData<User> currentUser = new MutableLiveData<>();


    @Inject
    public ChatViewModel(DatabaseRepository databaseRepository) {
        Debug.log("constructor:ChatViewModel", "working");
        this.databaseRepository = databaseRepository;
        this.disposable = new CompositeDisposable();
        this.conversationsLiveData = new MediatorLiveData<>();
    }


    public void loadRecentConversations(List<String> conversationsId) {
        databaseRepository.getRecentConversations(conversationsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribe(new Observer<QuerySnapshot>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots != null) {
                            for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if(documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    Conversation conversation = documentChange.getDocument().toObject(Conversation.class);
                                    String conversationId = documentChange.getDocument().getId();
                                    conversationList.put(conversationId, new ConversationWrapper(conversationId, conversation));
                                }
                            }
                        }
                        new ArrayList<>(conversationList.values())
                                .sort((obj1, obj2) -> obj2.getConversation().getLastUpdated()
                                        .compareTo(obj1.getConversation().getLastUpdated()));
                        conversationsLiveData.setValue(new ArrayList<>(conversationList.values()));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposable != null) {
            disposable.dispose();
        }
    }

    public Single<User> getUserFromUid(String uid) {
        return databaseRepository.getInfoFromUid(uid);
    }
//    public LiveData<User> getUserFromUid(String uid) {
//        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
//        databaseRepository.getInfoFromUid(uid)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new SingleObserver<User>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        disposable.add(d);
//                    }
//
//                    @Override
//                    public void onSuccess(@NonNull User user) {
//                        Debug.log("getUserFromUid:onSuccess", user.toString());
//                        mutableLiveData.setValue(user);
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Debug.log("getUserFromUid:onError", e.getMessage());
//                        mutableLiveData.setValue(null);
//                    }
//                });
//        return mutableLiveData;
//    }

    public LiveData<List<ConversationWrapper>> getConversationsLiveData() {
        return conversationsLiveData;
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }
}
