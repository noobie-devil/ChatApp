package com.zileanstdio.chatapp.Data.repository;

import com.google.firebase.firestore.QuerySnapshot;
import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.Data.model.Message;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.DataSource.remote.FirestoreDBSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class DatabaseRepository {

    FirestoreDBSource firestoreDBSource;

    @Inject
    public DatabaseRepository(FirestoreDBSource firestoreDBSource) {
        this.firestoreDBSource = firestoreDBSource;
    }

    public Flowable<QuerySnapshot> getRecentConversations(List<String> conversationsId) {
        return firestoreDBSource.getRecentConversations(conversationsId);
    }

    public Flowable<User> getUserInfo(String uid) {
        return firestoreDBSource.getUserInfo(uid);
    }

    public Single<User> getInfoFromUid(String uid) {
        return firestoreDBSource.getInfoFromUid(uid);
    }

    public Single<Contact> getContact(String uid, String documentId) {
        return firestoreDBSource.getContact(uid, documentId);
    }

    public Flowable<QuerySnapshot> getMessageList(final String uid) {
        return firestoreDBSource.getMessageList(uid);
    }

    public Completable sendMessage(ConversationWrapper conversationWrapper, Message message) {
        return firestoreDBSource.sendMessage(conversationWrapper, message);
    }

    public Flowable<Contact> getContacts(String uid) {
        return firestoreDBSource.getContacts(uid);
    }
}
