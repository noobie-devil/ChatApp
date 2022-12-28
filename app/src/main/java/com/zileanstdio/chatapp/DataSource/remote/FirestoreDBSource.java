package com.zileanstdio.chatapp.DataSource.remote;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.Data.model.ContactWrapInfo;
import com.zileanstdio.chatapp.Data.model.Conversation;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.Data.model.FriendRequest;
import com.zileanstdio.chatapp.Data.model.Message;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.Utils.CipherUtils;
import com.zileanstdio.chatapp.Utils.Constants;
import com.zileanstdio.chatapp.Utils.Debug;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Completable;
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

    public Single<Contact> getContact(final String uid, final String documentId) {
        return Single.create(emitter -> {
            DocumentReference reference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(uid)
                    .collection(Constants.KEY_COLLECTION_CONTACTS)
                    .document(documentId);
            final ListenerRegistration registration = reference.addSnapshotListener((value, error) -> {
                if(error != null) {
                    emitter.onError(error);
                }
                if(value != null) {
                    Contact contact = value.toObject(Contact.class);
                    emitter.onSuccess(contact);
                }
            });
        });
    }

    public Completable sendMessage(final ConversationWrapper conversation, final Message message) {
        return Completable.create(emitter -> {
            WriteBatch requestBatch = firebaseFirestore.batch();
            message.setSendAt(new Date());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            DocumentReference conversationReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATION)
                    .document(conversation.getDocumentId() != null ? conversation.getDocumentId() : String.valueOf(timestamp.getTime()));
            DocumentReference messageReference = conversationReference.collection(Constants.KEY_COLLECTION_MESSAGE)
                    .document(String.valueOf(timestamp.getTime()));
//            DocumentReference senderReference =firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
//                            .document(conversation.getConversation().getUserJoined().get(0))
//                    .update();
            requestBatch.set(conversationReference, conversation.getConversation());
            requestBatch.set(messageReference, message);
            requestBatch.commit()
                    .addOnSuccessListener(command -> {
                        emitter.onComplete();
                    })
                    .addOnFailureListener(emitter::onError);
        });
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
                            if (value.getDocumentChanges().size() == 0) {
                                emitter.onNext(new User());
                            } else {
                                for (DocumentChange documentChange : value.getDocumentChanges()) {
                                    if (documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                        User user = documentChange.getDocument().toObject(User.class);
                                        emitter.onNext(user);
                                    }
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
                            if (value.getDocumentChanges().size() == 0) {
                                emitter.onNext(new User());
                            } else {
                                for(DocumentChange documentChange : value.getDocumentChanges()) {
                                    if(documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                        User user = documentChange.getDocument().toObject(User.class);
                                        emitter.onNext(user);
                                    }
                                }
                            }
                        }
                    });
            emitter.setCancellable(registration::remove);
        }, BackpressureStrategy.BUFFER);
    }

    public Single<Contact> checkExistContact(final String uid, final String contactUid) {
        return Single.create(emitter -> {
            firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(uid)
                    .collection(Constants.KEY_COLLECTION_CONTACTS)
                    .document(contactUid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()) {
                            Debug.log("checkExistContact", documentSnapshot.toObject(Contact.class).toString());
                            emitter.onSuccess(documentSnapshot.toObject(Contact.class));
                        } else {
                            emitter.onSuccess(new Contact());
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
    public Flowable<ContactWrapInfo> getContactWrapInfo(final String uid) {
        return Flowable.create(emitter -> {
            final ListenerRegistration registration = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(uid)
                    .collection(Constants.KEY_COLLECTION_CONTACTS)
                    .whereEqualTo("relationship", 1)
                    .addSnapshotListener((value, error) -> {
                        if(error != null) {
                            emitter.onError(error);
                        }
                        if(value != null) {

                            for(DocumentChange documentChange : value.getDocumentChanges()) {
                                if(documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    Contact contact = documentChange.getDocument().toObject(Contact.class);
                                    firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(CipherUtils.Hash.sha256(contact.getNumberPhone()))
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if(documentSnapshot.exists()) {
                                                User user = documentSnapshot.toObject(User.class);
                                                emitter.onNext(new ContactWrapInfo(contact, user));
                                            } else {
                                                emitter.onNext(new ContactWrapInfo(contact, new User()));
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Debug.log("getContact", e.getMessage());
                                            emitter.onNext(new ContactWrapInfo(contact, new User()));
                                        });
                                }
                            }
                        }
                    });
            emitter.setCancellable(registration::remove);

        }, BackpressureStrategy.BUFFER);
    }

    public Flowable<ContactWrapInfo> syncLocalContact(final HashMap<String, String> localContact, final String uid) {
        return Flowable.create(emitter -> {
            HashMap<String, String> phoneHashed = localContact.keySet().stream().collect(HashMap::new, (hashMap, s) -> hashMap.put(CipherUtils.Hash.sha256(s), s), (hashMap, s) -> {});
            final ListenerRegistration registration = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .whereIn(FieldPath.documentId(), new ArrayList<>(phoneHashed.keySet()))
                    .addSnapshotListener((value, error) -> {
                            if(error != null) {
                                emitter.onError(error);
                            }
                            if(value != null) {
                                for(DocumentChange documentChange : value.getDocumentChanges()) {
                                    if(documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                        String key = documentChange.getDocument().getId();
                                        User user = documentChange.getDocument().toObject(User.class);
                                        Contact contact = new Contact();
                                        contact.setNumberPhone(phoneHashed.get(key));
                                        contact.setContactName(localContact.get(phoneHashed.get(key)));
                                        contact.setRelationship(-2);
                                        contact.setModifiedAt(new Date());

                                        ContactWrapInfo info = new ContactWrapInfo(contact, user);
                                        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(uid)
                                            .collection(Constants.KEY_COLLECTION_CONTACTS)
                                            .document(key)
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if(documentSnapshot.exists()) {
                                                    Debug.log("checkExistContact", documentSnapshot.toObject(Contact.class).toString());
                                                    info.setContact(documentSnapshot.toObject(Contact.class));
                                                    emitter.onNext(info);
                                                } else {
                                                    Debug.log("checkExistContact", "Not exists");
                                                    emitter.onNext(info);
                                                }
                                            })
                                            .addOnFailureListener((e) -> {
                                                Debug.log("checkExistContact", e.getMessage());
                                                emitter.onNext(info);
                                            });
                                    }
                                }
                            }
                        });
            emitter.setCancellable(registration::remove);
        }, BackpressureStrategy.BUFFER);
    }



    public Flowable<Contact> getContacts(final String uid) {
        return Flowable.create(emitter -> {
            final ListenerRegistration registration = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(uid)
                    .collection(Constants.KEY_COLLECTION_CONTACTS)
                    .whereEqualTo("relationship", 1)
                    .addSnapshotListener((value, error) -> {
                        if(error != null) {
                            emitter.onError(error);
                        }
                        if(value != null) {
                            for(DocumentChange documentChange : value.getDocumentChanges()) {
                                if(documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    Contact contact = documentChange.getDocument().toObject(Contact.class);
                                    emitter.onNext(contact);
                                }
                            }
                        }
            });
            emitter.setCancellable(registration::remove);

        }, BackpressureStrategy.BUFFER);
    }

    public Completable sendFriendRequest(final ContactWrapInfo contactWrapInfo, final String sender) {
        return Completable.create(emitter -> {
            WriteBatch requestBatch = firebaseFirestore.batch();
            DocumentReference senderReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(CipherUtils.Hash.sha256(sender))
                    .collection(Constants.KEY_COLLECTION_CONTACTS)
                    .document(CipherUtils.Hash.sha256(contactWrapInfo.getUser().getPhoneNumber()));
            DocumentReference receiverReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(CipherUtils.Hash.sha256(contactWrapInfo.getUser().getPhoneNumber()))
                    .collection(Constants.KEY_COLLECTION_CONTACTS)
                    .document(CipherUtils.Hash.sha256(sender));
            Date date = new Date();
            Contact contactFromSender = new Contact(contactWrapInfo.getUser().getPhoneNumber(), contactWrapInfo.getContact().getContactName(), -1, date);
            Contact contactFromReceiver = new Contact(sender, null, 0, date);

            requestBatch.set(senderReference, contactFromSender);
            requestBatch.set(receiverReference, contactFromReceiver);
            requestBatch.commit()
                    .addOnSuccessListener(command -> {
                        emitter.onComplete();
                    })
                    .addOnFailureListener(emitter::onError);


        });
    }

    public Completable sendCallMessage(final ConversationWrapper conversation, final Message message) {
        return Completable.create(emitter -> {
            WriteBatch requestBatch = firebaseFirestore.batch();
            message.setSendAt(new Date());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            DocumentReference conversationReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATION)
                    .document(conversation.getDocumentId() != null ? conversation.getDocumentId() : String.valueOf(timestamp.getTime()));
            DocumentReference messageReference = conversationReference.collection(Constants.KEY_COLLECTION_MESSAGE)
                    .document(String.valueOf(timestamp.getTime()));
            requestBatch.set(conversationReference, conversation.getConversation());
            requestBatch.set(messageReference, message);
            requestBatch.commit()
                    .addOnSuccessListener(command -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}