package com.zileanstdio.chatapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.zileanstdio.chatapp.DI.DaggerAppComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class BaseApplication extends DaggerApplication {
    public String getTag() {
        return this.getClass().getSimpleName();
    }

    @SuppressLint("StaticFieldLeak")
    private static BaseApplication instance = null;

    private static synchronized void setInstance(BaseApplication instance) {
        BaseApplication.instance = instance;
    }

    public static BaseApplication getInstance() { return instance; }

    Context activityContext;

    private static SharedPreferences sharedPreferences = null;

    private static synchronized void setSharedPreferences(SharedPreferences sharedPreferences) {
        BaseApplication.sharedPreferences = sharedPreferences;
    }

    public Context getBaseApplicationContext() {
        return instance;
    }

    public void setActivityContext(Context context) {
        if(this.activityContext != context) {
            this.activityContext = context;
        }
    }

    public Context getActivityContext() {
        return activityContext;
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
    }


    
}
