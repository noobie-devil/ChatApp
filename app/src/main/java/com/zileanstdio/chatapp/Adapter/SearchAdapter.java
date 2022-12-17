package com.zileanstdio.chatapp.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.R;

import java.util.List;

@SuppressLint("SetTextI18n")
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    List<User> users;

    public SearchAdapter() {

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUsers(List<User> users){
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_result_item, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        User user = users.get(position);

        if ((user.getAvatarImageUrl() != null) && !user.getAvatarImageUrl().isEmpty()) {
            Picasso.get().load(user.getAvatarImageUrl()).into(holder.imvAvatar);
        } else {
            holder.imvAvatar.setImageResource(R.drawable.ic_default_user);
        }
        if ((user.getUserName() != null) && !user.getUserName().isEmpty()) {
            holder.txvName.setText(user.getUserName());
        } else {
            holder.txvName.setText("Người dùng Zimess");
        }
        holder.txvPhoneNumber.setText(user.getPhoneNumber());
    }


    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        }
        return 0;
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        public ShapeableImageView imvAvatar;
        public MaterialTextView txvName;
        public MaterialTextView txvPhoneNumber;
        public MaterialButton btnAddFriend;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imvAvatar = itemView.findViewById(R.id.imv_avatar);
            txvName = itemView.findViewById(R.id.txv_name);
            txvPhoneNumber = itemView.findViewById(R.id.txv_phone);
            btnAddFriend = itemView.findViewById(R.id.btn_add_friend);
        }
    }
}