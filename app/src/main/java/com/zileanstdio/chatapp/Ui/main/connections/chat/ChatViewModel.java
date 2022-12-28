package com.zileanstdio.chatapp.Ui.main.connections.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QuerySnapshot;
import com.zileanstdio.chatapp.Data.model.Contact;
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
    private final List<ConversationWrapper> conversationWrapperList = new ArrayList<>();
    private final HashMap<String, ConversationWrapper> conversationList = new HashMap<>();
    final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private Navigator navigator;

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    @Inject
    public ChatViewModel(DatabaseRepository databaseRepository) {
        Debug.log("constructor:ChatViewModel", "working");
        this.databaseRepository = databaseRepository;
        this.disposable = new CompositeDisposable();
        this.conversationsLiveData = new MediatorLiveData<>();
    }


//    public void loadRecentConversations(List<String> conversationsId) {
//        databaseRepository.getRecentConversations(conversationsId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .toObservable()
//                .subscribe(new Observer<QuerySnapshot>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        disposable.add(d);
//                    }
//
//                    @Override
//                    public void onNext(@NonNull QuerySnapshot queryDocumentSnapshots) {
//                        if(queryDocumentSnapshots != null) {
//                            for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
//                                if(documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
//                                    Conversation conversation = documentChange.getDocument().toObject(Conversation.class);
//                                    String conversationId = documentChange.getDocument().getId();
//                                    conversationList.put(conversationId, new ConversationWrapper(conversationId, conversation));
//                                }
//                            }
//                        }
//                        new ArrayList<>(conversationList.values())
//                                .sort((obj1, obj2) -> obj2.getConversation().getLastUpdated()
//                                        .compareTo(obj1.getConversation().getLastUpdated()));
//                        conversationsLiveData.setValue(new ArrayList<>(conversationList.values()));
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Debug.log("loadRecentConversations:onError", e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Debug.log("loadRecentConversations", "onComplete");
//                    }
//                });
//    }


    public void listenRecentConversation(List<String> conversationsId) {
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
                        Debug.log("loadRecentConversations:onNext");
                        if(queryDocumentSnapshots != null) {
                            for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if(documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    Conversation conversation = documentChange.getDocument().toObject(Conversation.class);
                                    String conversationId = documentChange.getDocument().getId();
                                    Debug.log("loadRecentConversation:onNext", conversation.toString());
                                    conversationList.put(conversationId, new ConversationWrapper(conversationId, conversation));
                                }
                            }
                            conversationWrapperList.clear();
                            conversationWrapperList.addAll(0, new ArrayList<>(conversationList.values()));
                            conversationWrapperList.sort((obj1, obj2) -> obj2.getConversation().getLastUpdated()
                                    .compareTo(obj1.getConversation().getLastUpdated()));
                            conversationsLiveData.postValue(conversationWrapperList);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Debug.log("listenRecentConversations:onError", e.getMessage());
                        if(conversationWrapperList.size() == 0) {
                            conversationsLiveData.postValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onComplete() {
                        Debug.log("listenRecentConversations", "onComplete");
                    }
                });
    }

    public LiveData<List<ConversationWrapper>> loadRecentConversations(List<String> conversationsId) {
        MutableLiveData<List<ConversationWrapper>> listMutableLiveData = new MutableLiveData<>();
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
                        Debug.log("loadRecentConversations:onNext");
                        if(queryDocumentSnapshots != null) {
                            for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if(documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    Conversation conversation = documentChange.getDocument().toObject(Conversation.class);
                                    String conversationId = documentChange.getDocument().getId();
                                    conversationList.put(conversationId, new ConversationWrapper(conversationId, conversation));
                                }
                            }
                            conversationWrapperList.clear();
                            conversationWrapperList.addAll(0, new ArrayList<>(conversationList.values()));
                            conversationWrapperList.sort((obj1, obj2) -> obj2.getConversation().getLastUpdated()
                                    .compareTo(obj1.getConversation().getLastUpdated()));
                            listMutableLiveData.postValue(conversationWrapperList);
                        }
//                        new ArrayList<>(conversationList.values())
//                                .sort((obj1, obj2) -> obj2.getConversation().getLastUpdated()
//                                        .compareTo(obj1.getConversation().getLastUpdated()));
//                        conversationsLiveData.setValue(new ArrayList<>(conversationList.values()));
//                        listMutableLiveData.setValue(new ArrayList<>(conversationList.values()));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Debug.log("loadRecentConversations:onError", e.getMessage());
                        if(conversationWrapperList.size() == 0) {
                            listMutableLiveData.postValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onComplete() {
                        Debug.log("loadRecentConversations", "onComplete");
                    }
                });
        return listMutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposable != null) {
            disposable.dispose();
        }
    }

//    public Single<User> getUserFromUid(String uid) {
//        return databaseRepository.getInfoFromUid(uid);
//    }

    public LiveData<User> getUserFromUid(String uid) {
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        databaseRepository.getInfoFromUid(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull User user) {
                        Debug.log("getUserFromUid:onSuccess", user.toString());
                        mutableLiveData.setValue(user);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Debug.log("getUserFromUid:onError", e.getMessage());
                        mutableLiveData.setValue(new User());
                    }
                });
        return mutableLiveData;
    }

    public LiveData<Contact> getContact(String uid, String documentId) {
        MutableLiveData<Contact> mutableLiveData = new MutableLiveData<>();
        databaseRepository.getContact(uid, documentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Contact>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Contact contact) {
                        Debug.log("getContact:onSuccess", contact.toString());
                        mutableLiveData.setValue(contact);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Debug.log("getContact:onError", e.getMessage());
                        mutableLiveData.setValue(new Contact());
                    }
                });
        return mutableLiveData;
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

    public CompositeDisposable getDisposable() {
        return disposable;
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public interface Navigator {
        public void navigateToMessage(ConversationWrapper conversationWrapper, Contact contact, User contactProfile);
    }

}
