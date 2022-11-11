package com.zileanstdio.chatapp.DataSource.remote;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zileanstdio.chatapp.Exceptions.PhoneNumberException;
import com.zileanstdio.chatapp.Exceptions.VerificationException;
import com.zileanstdio.chatapp.Utils.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

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
                    .document(phoneNumber)
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




}
