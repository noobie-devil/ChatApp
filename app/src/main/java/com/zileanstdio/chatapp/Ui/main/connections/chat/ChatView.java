package com.zileanstdio.chatapp.Ui.main.connections.chat;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.zileanstdio.chatapp.Adapter.ConversationAdapter;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.main.MainActivity;
import com.zileanstdio.chatapp.Utils.Debug;


public class ChatView extends BaseFragment<ChatViewModel> {
    ShimmerFrameLayout shimmer;
    RecyclerView rcvConversation;
    ConversationAdapter adapter;

    public ChatView() {
        // Required empty public constructor
    }


    @Override
    public ChatViewModel getViewModel() {
        if(viewModel != null) {
            return viewModel;
        }
        viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(ChatViewModel.class);
        return viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initAppBar() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.layout_chat_view, container, false);
        return super.onCreateView(inflater, viewGroup, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shimmer = view.findViewById(R.id.shimmer_view_recent_conversation);
        rcvConversation = view.findViewById(R.id.rcv_conversation);
        adapter = new ConversationAdapter(baseActivity, viewModel);
        rcvConversation.setLayoutManager(new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false));
        rcvConversation.setAdapter(adapter);
        showShimmer();

        observeLatestConversation();

        ((MainActivity) baseActivity).getViewModel().getUserInfo().observe(baseActivity, user -> {
            Debug.log("getUserInfo", user.toString());
            viewModel.getCurrentUser().setValue(user);
            if(user.getConversationList().size() > 0)
                viewModel.loadRecentConversations(user.getConversationList());
            else
                hideShimmer();
        });


    }

    private void observeLatestConversation() {
        viewModel.getConversationsLiveData().observe(baseActivity, conversationWrappers -> {
            Debug.log("observeLatestConversation:onChanged", String.valueOf(conversationWrappers.size()));
            hideShimmer();
//            for(ConversationWrapper wrapper : conversationWrappers) {
//                Debug.log(wrapper.toString());
//            }
            adapter.submitList(conversationWrappers);
//            hideShimmer();

        });
    }

    public void showShimmer() {
        rcvConversation.setVisibility(View.GONE);
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();
    }
    public void hideShimmer() {
        rcvConversation.setVisibility(View.VISIBLE);
        shimmer.setVisibility(View.GONE);
        shimmer.stopShimmer();
    }
}