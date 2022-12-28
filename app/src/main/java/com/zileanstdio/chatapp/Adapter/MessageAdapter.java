package com.zileanstdio.chatapp.Adapter;

import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.zileanstdio.chatapp.Data.model.Message;
import com.zileanstdio.chatapp.Data.model.MessageWrapper;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.message.MessageViewModel;
import com.zileanstdio.chatapp.Utils.Common;
import com.zileanstdio.chatapp.Utils.Constants;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    final Context context;
    final MessageViewModel viewModel;
    final CompositeDisposable disposable = new CompositeDisposable();
    final AsyncListDiffer<MessageWrapper> messageAsyncListDiffer;

    final DiffUtil.ItemCallback<MessageWrapper> diffCallback = new DiffUtil.ItemCallback<MessageWrapper>() {
        @Override
        public boolean areItemsTheSame(@NonNull MessageWrapper oldItem, @NonNull MessageWrapper newItem) {
            return Objects.equals(oldItem.getDocumentId(), newItem.getDocumentId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageWrapper oldItem, @NonNull MessageWrapper newItem) {
            return Objects.equals(oldItem.getDocumentId(), newItem.getDocumentId()) && oldItem.getMessage().equals(newItem.getMessage());
        }
    };

    public MessageAdapter(Context context, MessageViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
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
    public int getItemViewType(int position) {
        MessageWrapper messageWrapper = getItem(position);
        if(messageWrapper.getMessage().getType().equals(Constants.KEY_TYPE_TEXT)) {
            if(messageWrapper.getMessage().getSender().equals(viewModel.getUidLiveData().getValue())) {
                return VIEW_TYPE_SENT;
            } else {
                return VIEW_TYPE_RECEIVED;
            }
        }
        return super.getItemViewType(position);

    }

    public void submitList(List<MessageWrapper> messageWrapperList) {
        messageAsyncListDiffer.submitList(messageWrapperList);
        notifyItemInserted(messageWrapperList.size() - 1);
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
        final MaterialCardView cvContainerMessage;

        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txvMessage = itemView.findViewById(R.id.txv_message);
            txvDateSend = itemView.findViewById(R.id.txv_date_send);
            cvContainerMessage = itemView.findViewById(R.id.cv_container_message);
            txvDateSend.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void bindData(int position, Message message) {
            txvMessage.setText(message.getMessage());
            txvDateSend.setText(Common.getReadableTime(message.getSendAt()));
            cvContainerMessage.setOnClickListener(v -> {
                showHideDateSendAnim();
            });
        }

        private void showHideDateSendAnim() {
            txvDateSend.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ValueAnimator animator = null;
            if(txvDateSend.getHeight() == 0) {
                animator = ValueAnimator.ofInt(0, txvDateSend.getMeasuredHeight());
            } else {
                animator = ValueAnimator.ofInt(txvDateSend.getMeasuredHeight(), 0);
            }
            animator.addUpdateListener(animation -> {
                txvDateSend.getLayoutParams().height = (int) animation.getAnimatedValue();
                txvDateSend.requestLayout();
            });
            animator.setDuration(300);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        }
    }

    class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {
        final MaterialTextView txvMessage;
        final MaterialTextView txvDateSend;
        final ShapeableImageView imvAvatar;
        final MaterialCardView cvContainerMessage;

        public ReceiverMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txvMessage = itemView.findViewById(R.id.txv_message);
            txvDateSend = itemView.findViewById(R.id.txv_date_send);
            imvAvatar = itemView.findViewById(R.id.imv_avatar);
            cvContainerMessage = itemView.findViewById(R.id.cv_container_message);
        }

        public void bindData(int position, Message message) {
            txvMessage.setText(message.getMessage());
            txvDateSend.setText(Common.getReadableTime(message.getSendAt()));
            cvContainerMessage.setOnClickListener(v -> {
                txvDateSend.setVisibility(txvDateSend.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            });
        }
    }


}
