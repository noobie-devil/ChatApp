package com.zileanstdio.chatapp.Ui.main.connections.chat;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.zileanstdio.chatapp.Adapter.ConversationAdapter;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.Data.model.Conversation;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.main.MainActivity;
import com.zileanstdio.chatapp.Ui.message.MessageActivity;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.List;


public class ChatView extends BaseFragment<ChatViewModel> implements ChatViewModel.Navigator {
    private ShimmerFrameLayout shimmer;
    private RecyclerView rcvConversation;
    private ConversationAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ChatView() {
        // Required empty public constructor
    }


    @Override
    public ChatViewModel getViewModel() {
        if(viewModel != null) {
            return viewModel;
        }
        viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(ChatViewModel.class);
        viewModel.setNavigator(this);
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
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rcvConversation = view.findViewById(R.id.rcv_conversation);
        adapter = new ConversationAdapter(baseActivity, viewModel);
        rcvConversation.setLayoutManager(new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false));
        rcvConversation.setAdapter(adapter);

//        observeLatestConversation();


        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if ((user.getConversationList() != null) && user.getConversationList().size() > 0) {
                getRecentConversation(user.getConversationList());
            }
        });

        ((MainActivity) baseActivity).getViewModel().getUserInfo().observe(baseActivity, user -> {
            Debug.log("getUserInfo", user.toString());
            viewModel.getCurrentUser().setValue(user);

            if ((user.getConversationList() != null) && (user.getConversationList().size() > 0))
                viewModel.loadRecentConversations(user.getConversationList());
            else
                hideShimmer();

        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(viewModel.getCurrentUser().getValue() != null) {
                getRecentConversation(viewModel.getCurrentUser().getValue().getConversationList());
            }
        });


    }

    private void getRecentConversation(List<String> conversationList) {
        showShimmer();
        viewModel.loadRecentConversations(conversationList).observe(getViewLifecycleOwner(), conversationWrappers -> {
            Debug.log("getRecentConversation:onChanged", conversationWrappers.toString());
            adapter.submitList(conversationWrappers);
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
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void navigateToMessage(ConversationWrapper conversationWrapper, Contact contact, User contactProfile) {
        Intent intent = new Intent(baseActivity, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MessageActivity.ARG_CONVERSATION_WRAPPER, conversationWrapper);
        bundle.putSerializable(MessageActivity.ARG_CURRENT_UID, "1bee7ac8a7cddc6bbfedb997da4b4decb50542fb8a6169b6ba31865eedba2105");
        bundle.putSerializable(MessageActivity.ARG_CONTACT, contact);
        bundle.putSerializable(MessageActivity.ARG_CURRENT_USER, viewModel.getCurrentUser().getValue());
        bundle.putSerializable(MessageActivity.ARG_CONTACT_PROFILE, contactProfile);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}