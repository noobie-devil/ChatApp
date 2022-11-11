package com.zileanstdio.chatapp.Data.repository;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.zileanstdio.chatapp.DataSource.remote.FirebaseAuthSource;

import io.reactivex.rxjava3.core.Completable;

public class AuthRepository {
    FirebaseAuthSource firebaseAuthSource;

    public AuthRepository(FirebaseAuthSource firebaseAuthSource) {
        this.firebaseAuthSource = firebaseAuthSource;
    }

    public Completable checkExistedPhoneNumber(String phoneNumber) {
        return this.firebaseAuthSource.checkExistedPhoneNumber(phoneNumber);
    }

    public void phoneNumberVerification(String phoneNumber, FragmentActivity fragmentActivity, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        this.firebaseAuthSource.phoneNumberVerification(phoneNumber, fragmentActivity, callbacks);
    }

    public Completable signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        return this.firebaseAuthSource.signInWithPhoneAuthCredential(phoneAuthCredential);
    }
}
