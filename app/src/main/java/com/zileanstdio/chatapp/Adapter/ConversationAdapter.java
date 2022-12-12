package com.zileanstdio.chatapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.Data.model.Conversation;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.main.connections.chat.ChatViewModel;
import com.zileanstdio.chatapp.Utils.CipherUtils;
import com.zileanstdio.chatapp.Utils.Common;
import com.zileanstdio.chatapp.Utils.Constants;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    final Context context;
    final ChatViewModel viewModel;
    final CompositeDisposable disposable = new CompositeDisposable();

    private final AsyncListDiffer<ConversationWrapper> asyncListDiffer;

    public ConversationAdapter(Context context, ChatViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
        DiffUtil.ItemCallback<ConversationWrapper> diffCallback = new DiffUtil.ItemCallback<ConversationWrapper>() {

            @Override
            public boolean areItemsTheSame(@NonNull ConversationWrapper oldItem, @NonNull ConversationWrapper newItem) {
                return Objects.equals(oldItem.getDocumentId(), newItem.getDocumentId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ConversationWrapper oldItem, @NonNull ConversationWrapper newItem) {
                return oldItem.equals(newItem);
            }
        };
        asyncListDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recent_conversation_item, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        final int index = position;
        Conversation conversation = getItem(position).getConversation();
        viewModel.getCurrentUser().observe((LifecycleOwner) holder.itemView.getContext(), user -> {
            for(String uid : conversation.getUserJoined()) {
                if(!uid.equals(CipherUtils.Hash.sha256(user.getPhoneNumber()))) {
                    viewModel.getUserFromUid(uid)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<User>() {
                                @Override
                                public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                    disposable.add(d);
                                }

                                @Override
                                public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull User user) {
                                    Debug.log(":onBindViewHolder:getUserFromUid:onSuccess", user.toString());
                                    holder.bindData(index, conversation, null, user);
                                }

                                @Override
                                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                    Debug.log(":onBindViewHolder:getUserFromUid:onError", e.getMessage());
                                    holder.bindData(index, conversation, null, null);
                                }
                            });
                }
            }
        });

    }

    public void submitList(List<ConversationWrapper> conversationWrappers) {
        asyncListDiffer.submitList(conversationWrappers);
    }

    public ConversationWrapper getItem(int position) {
        return asyncListDiffer.getCurrentList().get(position);
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }


    public class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ShapeableImageView imvAvatar;
        final MaterialTextView txvLastMessage;
        final MaterialTextView txvName;
        final MaterialTextView txvDateSend;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            imvAvatar = itemView.findViewById(R.id.imv_avatar);
            txvName = itemView.findViewById(R.id.txv_name);
            txvLastMessage = itemView.findViewById(R.id.tv_last_message);
            txvDateSend = itemView.findViewById(R.id.tv_date_send);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bindData(int position, Conversation conversation, Contact contact, User contactProfile) {
            if(contact != null) {
                this.txvName.setText(contact.getContactName());
            } else {
                this.txvName.setText(contactProfile.getUserName());
            }

            if(conversation.getTypeMessage().equals(Constants.KEY_TYPE_RECORD)) {
                this.txvLastMessage.setText(context.getResources().getString(R.string.recent_message_type_record));
            } else {
                this.txvLastMessage.setText(conversation.getLastMessage());
            }
            this.txvDateSend.setText(Common.getReadableTime(conversation.getLastUpdated()));
            if(contactProfile.getAvatarImageUrl() == null) {
                imvAvatar.setImageResource(R.drawable.ic_default_user);
            } else {
                Glide.with(context)
                    .load(contactProfile.getAvatarImageUrl())
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

        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        disposable.clear();
    }
}
