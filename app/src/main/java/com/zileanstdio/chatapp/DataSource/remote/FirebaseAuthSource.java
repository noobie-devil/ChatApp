package com.zileanstdio.chatapp.DataSource.remote;

import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.Exceptions.PhoneNumberException;
import com.zileanstdio.chatapp.Exceptions.VerificationException;
import com.zileanstdio.chatapp.Utils.CipherUtils;
import com.zileanstdio.chatapp.Utils.Constants;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FirebaseAuthSource {
    private static final String TAG = "FirebaseAuthSource";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Inject
    public FirebaseAuthSource(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return firebaseAuth.getCurrentUser();
    }

    public Completable checkExistedPhoneNumber(final String phoneNumber) {
        return Completable.create(emitter ->
                firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(CipherUtils.Hash.sha256(phoneNumber))
                    .get()
                    .addOnSuccessListener(command -> {
                        if(command.exists()) {
                            emitter.onError(
                                new PhoneNumberException(PhoneNumberException.ErrorType.HAS_ALREADY_EXISTED,
                                "Số điện thoại này đã tồn tại"));
                        } else {
                            emitter.onComplete();
                        }
                     })
                    .addOnFailureListener(emitter::onError));
    }

    public Completable signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        return Completable.create(emitter -> {
            firebaseAuth.signInWithCredential(phoneAuthCredential)
                    .addOnSuccessListener(command -> {
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "The verification code entered was invalid");
                        emitter.onError(new VerificationException(VerificationException.ErrorType.INVALID_CODE,
                                "The verification code entered was invalid"));
                    });
        });
    }

    public void phoneNumberVerification(String phoneNumber, FragmentActivity fragmentActivity, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(fragmentActivity)
                        .setCallbacks(callbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public Observable<FirebaseUser> createWithEmailPasswordAuthCredential(String email, String password) {
        return Observable.create(emitter -> {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(command -> {
                                    FirebaseUser firebaseUser = command.getUser();
                                    emitter.onNext(firebaseUser);
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG + ":createWithEmailPasswordAuthCredential", e.getMessage());
                                    emitter.onError(e);
                                });

                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG + ":createWithEmailPasswordAuthCredential", e.getMessage());
                        emitter.onError(e);
                    });
        });
    }


    public Completable updateRegisterInfo(User user) {
        return Completable.create(emitter -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            user.setCreatedAt(new Date());
            if(firebaseUser != null) {
                firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                        .document(CipherUtils.Hash.sha256(user.getPhoneNumber()))
                        .set(user)
                        .addOnSuccessListener(command -> emitter.onComplete())
                        .addOnFailureListener(e -> {
                            Log.d(TAG + ":updateRegisterInfo", new NullPointerException().getMessage());
                            emitter.onError(new NullPointerException());
                        });
            } else {
                Log.d(TAG + ":updateRegisterInfo", new NullPointerException().getMessage());
                emitter.onError(new NullPointerException());
            }
        });
    }

    public Completable login(String phoneNumber, String password) {
        return Completable.create(emitter -> {
            firebaseAuth.signInWithEmailAndPassword(String.format("%s@gmail.com", phoneNumber), password)
                    .addOnSuccessListener(command -> {
                        emitter.onComplete();
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }


}
