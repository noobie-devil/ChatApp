package com.zileanstdio.chatapp.Ui.sync;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.google.android.material.textview.MaterialTextView;
import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Utils.Common;
import com.zileanstdio.chatapp.Utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SyncContactActivity extends BaseActivity<SyncContactViewModel> {
    //private FragmentTransaction fragmentTransaction;

    private RecyclerView rcvSyncContact;


    SyncContactAdapter contactAdapter = new SyncContactAdapter();
    private List<Contact> contacts;
    private SearchView svContact;
    private MaterialTextView txvNoResult;




    @Override
    public SyncContactViewModel getViewModel() {
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
        svContact=findViewById(R.id.sv_contact);
        txvNoResult=findViewById(R.id.tv_no_result_contact);
        initAppBar();

        svContact.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

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
        contacts=getContactList();
        initView();
        //svContact.clearFocus();
    }

    private void initView() {

        //
        rcvSyncContact = findViewById(R.id.rcv_add_contact);
        rcvSyncContact.setHasFixedSize(true);
        rcvSyncContact.setLayoutManager(new LinearLayoutManager(this));
        rcvSyncContact.setItemAnimator(new DefaultItemAnimator());

        Collections.sort(contacts, Comparator.comparing(contact -> Common.removeAccent(contact.getContactName()).toLowerCase()));
        contactAdapter.setContacts(contacts);
        rcvSyncContact.setAdapter(contactAdapter);

    }

    @Override
    public void onClick(View v) {

    }



    void run() {
        if(handlePermissionsInitial(PERMISSIONS_NOT_GRANTED)) {
            for(Contact contact: getContactList()) {
                Log.d(TAG, contact.toString());
            }
        }
    }

    @Override
    public void initAppBar() {
        super.initAppBar();
        setTitleToolbar("Danh bạ máy");
        setDisplayHomeAsUpEnabled(true);
    }


    private List<Contact> getContactList() {
        Cursor cursor = null;
        List<Contact> localContact = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        try {
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception e) {
            Log.d(TAG + ":getContactList", e.getMessage());
        }
        if(cursor != null) {

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Contact contact = new Contact();

                    @SuppressLint("Range") String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    @SuppressLint("Range") String contactDisplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    contact.setContactName(contactDisplayName);
                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                                , null
                                , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                                , new String[]{contactId}
                                , null);

                        while (phoneCursor.moveToNext()) {
                            @SuppressLint("Range") String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contact.setNumberPhone(phoneNumber);
                        }
                        phoneCursor.close();
                    }

                    localContact.add(contact);
                }
                cursor.close();
            }
        }
        return localContact;
    }



    private void filterList(String newText){
        List<Contact> filterList = new ArrayList<>();
        for(Contact contact : contacts){
            if(Common.removeAccent(contact.getContactName()).toLowerCase().contains(newText.toLowerCase())){
                filterList.add(contact);
            }
        }
        if(filterList.isEmpty()){
            rcvSyncContact.setVisibility(View.GONE);
            txvNoResult.setVisibility(View.VISIBLE);
        }
        else{
            contactAdapter.setFilteredList(filterList);;
            rcvSyncContact.setVisibility(View.VISIBLE);
            txvNoResult.setVisibility(View.GONE);
        }

    }
}