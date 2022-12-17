package com.zileanstdio.chatapp.DataSource.remote;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.Utils.Constants;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class FirestoreDBSource {
    private final FirebaseFirestore firebaseFirestore;

    @Inject
    public FirestoreDBSource(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    public Flowable<QuerySnapshot> getRecentConversations(final List<String> conversationsId) {
        return Flowable.create(emitter -> {
            final ListenerRegistration registration = firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATION)
                    .whereIn(FieldPath.documentId(), conversationsId)
                    .addSnapshotListener((value, error) -> {
                        if(error != null) {
                            emitter.onError(error);
                        }
                        if(value != null) {
                            emitter.onNext(value);
                        }
                    });
            emitter.setCancellable(registration::remove);
        }, BackpressureStrategy.BUFFER);
    }

    public Flowable<User> getUserInfo(final String uid) {
        return Flowable.create(emitter -> {
            DocumentReference reference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(uid);
            final ListenerRegistration registration = reference.addSnapshotListener((value, error) -> {
                if(error != null) {
                    emitter.onError(error);
                }
                if(value != null) {
                    User user = value.toObject(User.class);
                    emitter.onNext(user);
                }
            });
            emitter.setCancellable(registration::remove);
        }, BackpressureStrategy.BUFFER);
    }

    public Single<User> getInfoFromUid(final String uid) {
        return Single.create(emitter -> {
            DocumentReference reference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(uid);
            final ListenerRegistration registration = reference.addSnapshotListener((value, error) -> {
                if(error != null) {
                    emitter.onError(error);
                }
                if(value != null) {
                    User user = value.toObject(User.class);
                    emitter.onSuccess(user);
                }
            });
        });
    }

    public Flowable<QuerySnapshot> getMessageList(final String documentId) {
        return Flowable.create(emitter -> {
            CollectionReference reference = firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATION)
                    .document(documentId)
                    .collection(Constants.KEY_COLLECTION_MESSAGE);
            final ListenerRegistration registration = reference.addSnapshotListener((value, error) -> {
                if(error != null) {
                    emitter.onError(error);
                }
                if(value != null) {
                    emitter.onNext(value);
                }
            });

            emitter.setCancellable(registration::remove);
        }, BackpressureStrategy.BUFFER);
    }

    public Flowable<User> searchUserFromUserName(final String keyword) {
        return Flowable.create(emitter -> {
            final ListenerRegistration registration = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo("userName", keyword)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                        }
                        if (value != null) {
                            for (DocumentChange documentChange : value.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    User user = documentChange.getDocument().toObject(User.class);
                                    emitter.onNext(user);
                                }
                            }
                        }
                    });
            emitter.setCancellable(registration::remove);
        }, BackpressureStrategy.BUFFER);
    }

    public Flowable<User> searchUserFromPhoneNumber(final String phoneNumberHashed) {
        return Flowable.create(emitter -> {
            final ListenerRegistration registration = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(FieldPath.documentId(), phoneNumberHashed)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                        }
                        if (value != null) {
                            for(DocumentChange documentChange : value.getDocumentChanges()) {
                                if(documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    User user = documentChange.getDocument().toObject(User.class);
                                    emitter.onNext(user);
                                }
                            }
                        }
                    });
            emitter.setCancellable(registration::remove);
        }, BackpressureStrategy.BUFFER);
    }
}