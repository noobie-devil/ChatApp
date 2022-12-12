package com.zileanstdio.chatapp.Ui.message;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QuerySnapshot;
import com.zileanstdio.chatapp.Data.model.Conversation;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.Data.model.Message;
import com.zileanstdio.chatapp.Data.model.MessageWrapper;
import com.zileanstdio.chatapp.Data.repository.DatabaseRepository;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MessageViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    final DatabaseRepository databaseRepository;
    final CompositeDisposable disposable;
    final List<MessageWrapper> messageWrapperList;
    final MediatorLiveData<List<MessageWrapper>> messagesLiveData;

    @Inject
    public MessageViewModel(DatabaseRepository repository) {
        Debug.log("constructor:MessageViewModel", "working");
        this.databaseRepository = repository;
        this.disposable = new CompositeDisposable();
        this.messagesLiveData = new MediatorLiveData<>();
        this.messageWrapperList = new ArrayList<>();
    }

    public void getMessageList(String documentId) {
        databaseRepository.getMessageList(documentId)
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
                                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                                    Message message = documentChange.getDocument().toObject(Message.class);
                                    String messageId = documentChange.getDocument().getId();
                                    messageWrapperList.add(new MessageWrapper(messageId, message));
                                }
                            }
                            messagesLiveData.setValue(messageWrapperList);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposable != null) {
            disposable.dispose();
        }
    }
}
