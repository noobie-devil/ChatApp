package com.zileanstdio.chatapp.Ui.sync;


import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SyncContactAdapter extends RecyclerView.Adapter<SyncContactAdapter.ViewHolder>

{
    public void setContacts(List<Contact> contacts) {
        this.contacts = (ArrayList<Contact>) contacts;

    }

    private List<Contact> contacts;

    HashMap<String, Contact> stringHashMap = new HashMap<>();


    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(List<Contact> filterList){
        this.contacts = filterList;
        notifyDataSetChanged();
    }

    public SyncContactAdapter() {

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_contact_item_by_alphabet, parent, false);
        return new ViewHolder(listItem);
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Contact myContacts = contacts.get(position);


            String alphabet = String.valueOf(Common.removeAccent(String.valueOf(myContacts.getContactName())).charAt(0)).toUpperCase(Locale.ROOT);
            if(stringHashMap.containsKey(alphabet) ) {
                if(stringHashMap.get(alphabet).equals(myContacts)) {
                    holder.txvAlphabetHeader.setText(alphabet);
                    holder.txvAlphabetHeader.setVisibility(View.VISIBLE);
                    Log.d("DEBUG", "contains alphabet " + alphabet + " with: " + myContacts.getContactName());
                }
                else {
                    holder.txvAlphabetHeader.setText("");
                    holder.txvAlphabetHeader.setVisibility(View.GONE);
                }

            } else {
                stringHashMap.put(alphabet, myContacts);
                holder.txvAlphabetHeader.setVisibility(View.VISIBLE);
                Log.d("DEBUG", "put alphabet " + alphabet + " with: " + myContacts.getContactName());
                holder.txvAlphabetHeader.setText(alphabet);
            }
            holder.txvContactName.setText(myContacts.getContactName());
            holder.txvUserName.setText(myContacts.getContactName());
    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout ctlItemContact;
        public MaterialTextView txvUserName;
        public MaterialTextView txvContactName;
        public MaterialButton btnCall;
        public MaterialButton btnVideoCall;
        public MaterialTextView txvAlphabetHeader;



        public ViewHolder(View itemView) {

            super(itemView);
            ctlItemContact=itemView.findViewById(R.id.ctl_item_contact);

            txvUserName = itemView.findViewById(R.id.tv_user_name);
            txvContactName = itemView.findViewById(R.id.tv_contact_name);
            txvAlphabetHeader = itemView.findViewById(R.id.tv_alphabet);
            btnCall = itemView.findViewById(R.id.btn_call);
            btnVideoCall = itemView.findViewById(R.id.btn_video_call);
        }
    }




}
