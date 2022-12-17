package com.zileanstdio.chatapp.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.Data.model.ContactWrapInfo;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.main.connections.contact.ContactViewModel;
import com.zileanstdio.chatapp.Utils.Common;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private final ContactViewModel viewModel;
    public HashMap<String, Contact> stringHashMap = new HashMap<>();


    private final AsyncListDiffer<ContactWrapInfo> infoAsyncListDiffer;

    public ContactAdapter(Context context, ContactViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;

        DiffUtil.ItemCallback<ContactWrapInfo> diffCallback = new DiffUtil.ItemCallback<ContactWrapInfo>() {

            @Override
            public boolean areItemsTheSame(@NonNull ContactWrapInfo oldItem, @NonNull ContactWrapInfo newItem) {
                return Objects.equals(oldItem.getUser().getPhoneNumber(), newItem.getUser().getPhoneNumber());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ContactWrapInfo oldItem, @NonNull ContactWrapInfo newItem) {
                return oldItem.equals(newItem);
            }
        };
        infoAsyncListDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_contact_item_by_alphabet, parent, false);
        return new ContactViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position) {
        final ContactWrapInfo contactWrapInfo = getItem(position);
        holder.bindData(position, contactWrapInfo);
    }

    public void submitList(List<ContactWrapInfo> contactWrapInfoList) {
        infoAsyncListDiffer.submitList(contactWrapInfoList);
    }

    public ContactWrapInfo getItem(int position) {
        return infoAsyncListDiffer.getCurrentList().get(position);
    }

    @Override
    public int getItemCount() {
        return infoAsyncListDiffer.getCurrentList().size();
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cvContactItem;
        private final ConstraintLayout ctlItemContact;
        private final MaterialTextView txvUserName;
        private final MaterialTextView txvContactName;
        private final MaterialButton btnCall;
        private final MaterialButton btnVideoCall;
        private final MaterialTextView txvAlphabetHeader;
        private final ShapeableImageView imvAvatar;
        private final MaterialButton btnAddFriend;
        private final MaterialTextView txvIsFriend;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            cvContactItem = itemView.findViewById(R.id.cv_contact_item);
            ctlItemContact=itemView.findViewById(R.id.ctl_item_contact);
            imvAvatar = itemView.findViewById(R.id.imv_avatar);
            txvUserName = itemView.findViewById(R.id.tv_user_name);
            txvContactName = itemView.findViewById(R.id.tv_contact_name);
            txvAlphabetHeader = itemView.findViewById(R.id.tv_alphabet);
            btnCall = itemView.findViewById(R.id.btn_call);
            btnVideoCall = itemView.findViewById(R.id.btn_video_call);
            btnAddFriend = itemView.findViewById(R.id.btn_add_friend);
            txvIsFriend = itemView.findViewById(R.id.txv_in_friend_relationship);
            txvIsFriend.setVisibility(View.GONE);
            btnAddFriend.setVisibility(View.GONE);
            btnVideoCall.setVisibility(View.VISIBLE);
            btnCall.setVisibility(View.VISIBLE);
        }

        public void bindData(int position, ContactWrapInfo contactWrapInfo) {
            String alphabet = String.valueOf(Common.removeAccent(String.valueOf(contactWrapInfo.getContact().getContactName())).charAt(0)).toUpperCase(Locale.ROOT);
            if(stringHashMap.containsKey(alphabet) ) {
                if(stringHashMap.get(alphabet).getNumberPhone().equals(contactWrapInfo.getUser().getPhoneNumber())) {
                    txvAlphabetHeader.setText(alphabet);
                    txvAlphabetHeader.setVisibility(View.VISIBLE);
                    Debug.log( "contains alphabet " + alphabet + " with: " + contactWrapInfo.getContact().getContactName());
                }
                else {
                    txvAlphabetHeader.setText("");
                    txvAlphabetHeader.setVisibility(View.GONE);
                }

            } else {
                stringHashMap.put(alphabet, contactWrapInfo.getContact());
                txvAlphabetHeader.setVisibility(View.VISIBLE);
                Debug.log("put alphabet " + alphabet + " with: " + contactWrapInfo.getContact().getContactName());
                txvAlphabetHeader.setText(alphabet);
            }
            txvContactName.setText(contactWrapInfo.getContact().getContactName());
            txvUserName.setText(contactWrapInfo.getUser().getUserName());

            if(contactWrapInfo.getUser().getAvatarImageUrl() == null) {
                imvAvatar.setImageResource(R.drawable.ic_default_user);
            } else {
                Glide.with(context)
                        .load(contactWrapInfo.getUser().getAvatarImageUrl())
                        .error(R.drawable.ic_default_user)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(imvAvatar);
            }

            cvContactItem.setOnClickListener(v -> viewModel.getNavigator().navigateToMessage(null, contactWrapInfo.getContact(), contactWrapInfo.getUser()));
        }
    }
}