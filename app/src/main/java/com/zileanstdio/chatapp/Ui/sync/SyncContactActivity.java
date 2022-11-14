package com.zileanstdio.chatapp.Ui.sync;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SyncContactActivity extends BaseActivity {

    @Override
    public ViewModel getViewModel() {
        return null;
    }

    @Override
    public Integer getLayoutId() {
        return R.layout.activity_sync_contact;
    }

    @Override
    public Integer getViewRootId() {
        return R.id.clSyncContactActivity;
    }

    @Override
    public void replaceFragment(BaseFragment fragment) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAppBar();
        PERMISSIONS_NOT_GRANTED = new HashMap<Integer, String>() {{
            put(Constants.READ_CONTACTS_CODE, Manifest.permission.READ_CONTACTS);
            put(Constants.WRITE_CONTACTS_CODE, Manifest.permission.WRITE_CONTACTS);
        }};
        observablePermissionsData.setValue(false);

        final Observer<Boolean> observerPermissionsNotGranted = isValid -> {
            Log.d(TAG, "test");
            if(isValid) {
                run();
            }
        };

        observablePermissionsData.observe(this, observerPermissionsNotGranted);

        run();
    }

    @Override
    public void onClick(View v) {

    }

    void run() {
        if(handlePermissionsInitial(PERMISSIONS_NOT_GRANTED)) {
            for(LocalContactTesting contactTesting: getContactList()) {
                Log.d(TAG, contactTesting.toString());
            }
        }
    }

    @Override
    public void initAppBar() {
        super.initAppBar();
        setTitleToolbar("Danh bạ máy");
        setDisplayHomeAsUpEnabled(true);
    }

    private List<LocalContactTesting> getContactList() {
        Cursor cursor = null;
        List<LocalContactTesting> localContactTestings = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        try {
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception e) {
            Log.d(TAG + ":getContactList", e.getMessage());
        }
        if(cursor != null) {

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    LocalContactTesting contactTesting = new LocalContactTesting();

                    @SuppressLint("Range") String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    @SuppressLint("Range") String contactDisplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    contactTesting.setContactDisplayName(contactDisplayName);
                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                                , null
                                , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                                , new String[]{contactId}
                                , null);

                        while (phoneCursor.moveToNext()) {
                            @SuppressLint("Range") String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactTesting.setPhoneNumber(phoneNumber);
                        }
                        phoneCursor.close();
                    }

                    localContactTestings.add(contactTesting);
                }
                cursor.close();
            }
        }
        return localContactTestings;
    }


    public static class LocalContactTesting {
        private String contactDisplayName;
        private String phoneNumber;

        public LocalContactTesting() {

        }

        public void setContactDisplayName(String contactDisplayName) {
            this.contactDisplayName = contactDisplayName;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        public String toString() {
            return "LocalContactTesting{" +
                    "contactDisplayName='" + contactDisplayName + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    '}';
        }
    }
}