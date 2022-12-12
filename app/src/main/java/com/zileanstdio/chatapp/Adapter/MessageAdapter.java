package com.zileanstdio.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.zileanstdio.chatapp.Data.model.Message;
import com.zileanstdio.chatapp.Data.model.MessageWrapper;
import com.zileanstdio.chatapp.R;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    final Context context;
    final CompositeDisposable disposable = new CompositeDisposable();
    final AsyncListDiffer<MessageWrapper> messageAsyncListDiffer;

    final DiffUtil.ItemCallback<MessageWrapper> diffCallback = new DiffUtil.ItemCallback<MessageWrapper>() {
        @Override
        public boolean areItemsTheSame(@NonNull MessageWrapper oldItem, @NonNull MessageWrapper newItem) {
            return Objects.equals(oldItem.getDocumentId(), newItem.getDocumentId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageWrapper oldItem, @NonNull MessageWrapper newItem) {
            return oldItem.equals(newItem);
        }
    };

    public MessageAdapter(Context context) {
        this.context = context;
        this.messageAsyncListDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_sender_role, parent, false);
            return new SendMessageViewHolder(view);
        } else if(viewType == VIEW_TYPE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_receiver_role, parent, false);
            return new ReceiverMessageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageWrapper messageWrapper = getItem(position);
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SendMessageViewHolder) holder).bindData(position, messageWrapper.getMessage());
        } else if(getItemViewType(position) == VIEW_TYPE_RECEIVED) {
            ((ReceiverMessageViewHolder) holder).bindData(position, messageWrapper.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageAsyncListDiffer.getCurrentList().size();
    }

    public MessageWrapper getItem(int position) {
        return messageAsyncListDiffer.getCurrentList().get(position);
    }

    class SendMessageViewHolder extends RecyclerView.ViewHolder {
        final MaterialTextView txvMessage;
        final MaterialTextView txvDateSend;

        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txvMessage = itemView.findViewById(R.id.txv_message);
            txvDateSend = itemView.findViewById(R.id.txv_date_send);
        }

        public void bindData(int position, Message message) {

        }
    }

    class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {
        final MaterialTextView txvMessage;
        final MaterialTextView txvDateSend;
        final ShapeableImageView imvAvatar;

        public ReceiverMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txvMessage = itemView.findViewById(R.id.txv_message);
            txvDateSend = itemView.findViewById(R.id.txv_date_send);
            imvAvatar = itemView.findViewById(R.id.imv_avatar);
        }

        public void bindData(int position, Message message) {

        }
    }


}
