package com.zileanstdio.chatapp.Ui.message;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.zileanstdio.chatapp.Adapter.MessageAdapter;
import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.Data.model.Contact;
import com.zileanstdio.chatapp.Data.model.Conversation;
import com.zileanstdio.chatapp.Data.model.ConversationWrapper;
import com.zileanstdio.chatapp.Data.model.Message;
import com.zileanstdio.chatapp.Data.model.User;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Utils.CipherUtils;
import com.zileanstdio.chatapp.Utils.Debug;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MessageActivity extends BaseActivity<MessageViewModel> {
    public static final String ARG_CURRENT_UID = "current_uid";
    public static final String ARG_CONVERSATION_WRAPPER = "conversation_wrapper";
    public static final String ARG_CONTACT = "contact";
    public static final String ARG_CURRENT_USER = "current_user";
    public static final String ARG_CONTACT_PROFILE = "contact_profile";
    private RecyclerView rcvMessage;
    private MessageAdapter messageAdapter;
    private String currentUid;
    private ConversationWrapper currentConversationWrapper = null;
    private User currentUser = null;
    private Contact contact = null;
    private User contactProfile = null;
    private TextInputEditText edtMessage;
    private MaterialButton btnSendMessage;
    private ShapeableImageView imvAvatarContact;
    private ShapeableImageView viewStatus;
    private MaterialTextView txvContactName;
    private MaterialTextView txvOnlineStatus;
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public MessageViewModel getViewModel() {
        if(viewModel != null) {
            return viewModel;
        }
        viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(MessageViewModel.class);
        return viewModel;
    }

    @Override
    public Integer getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    public Integer getViewRootId() {
        return R.id.clMessageActivity;
    }

    @Override
    public void replaceFragment(BaseFragment fragment) {

    }

    @Override
    public void initAppBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
//        setTitleToolbar("Message");
        setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAppBar();
        rcvMessage = findViewById(R.id.rcv_message);
        edtMessage = findViewById(R.id.edt_message);
        btnSendMessage = findViewById(R.id.btn_send_message);
        imvAvatarContact = findViewById(R.id.imv_avatar);
        viewStatus = findViewById(R.id.view_status);
        txvContactName = findViewById(R.id.txv_contact_name);
        txvOnlineStatus = findViewById(R.id.txv_online_status);
        messageAdapter = new MessageAdapter(this, viewModel);
        rcvMessage.setAdapter(messageAdapter);

        observeMessages();
        if(getIntent() != null && getIntent().hasExtra(ARG_CONVERSATION_WRAPPER) && getIntent().hasExtra(ARG_CURRENT_UID)) {
            currentUid = getIntent().getStringExtra(ARG_CURRENT_UID);
            currentConversationWrapper = (ConversationWrapper) getIntent().getSerializableExtra(ARG_CONVERSATION_WRAPPER);
            contact = (Contact) getIntent().getSerializableExtra(ARG_CONTACT);
            contactProfile = (User) getIntent().getSerializableExtra(ARG_CONTACT_PROFILE);
            currentUser = (User) getIntent().getSerializableExtra(ARG_CURRENT_USER);
            viewModel.getContactProfileLiveData().observe(this, user -> initial(user));
            if(contact != null && contact.getContactName() != null) {
                Debug.log("contact", contact.toString());
                viewModel.getContactLiveData().setValue(contact);
            }
            if(currentUid != null) {
                viewModel.getUidLiveData().setValue(currentUid);
            }
            if(contactProfile != null && contactProfile.getUserName() != null) {
                Debug.log("contactProfile", contactProfile.toString());
                // Use for testing
                viewModel.getLatestInfoContact(CipherUtils.Hash.sha256(contactProfile.getPhoneNumber()))
                    .observe(this, user -> {
                        viewModel.getContactProfileLiveData().setValue(contactProfile);
                    });
                // Use for release
//                viewModel.getLatestInfoContact(CipherUtils.Hash.sha256(contactProfile.getPhoneNumber()))
//                        .observe(this, user -> {
//                            viewModel.getContactProfileLiveData().setValue(contactProfile);
//                        });

            }
            if(currentConversationWrapper != null) {
                Debug.log("currentConversationWrapper", currentConversationWrapper.toString());
                viewModel.getMessageList(currentConversationWrapper.getDocumentId());
            }
        }


        disposable.add(RxView.clicks(btnSendMessage)
                .debounce(200, TimeUnit.MILLISECONDS)
                .subscribe(unit -> {
                    if(edtMessage.getText() != null && edtMessage.getText().toString().trim().length() > 0) {
                        if(currentConversationWrapper != null) {
                            currentConversationWrapper.getConversation().setTypeMessage(Conversation.Type.TEXT.label);
                            currentConversationWrapper.getConversation().setLastSender(currentUid);
                            currentConversationWrapper.getConversation().setLastMessage(edtMessage.getText().toString().trim());
                            currentConversationWrapper.getConversation().setLastUpdated(new Date());
                        } else {
                            Conversation conversation = Conversation.TEXT;
                            conversation.setLastSender(currentUid);
                            conversation.setLastUpdated(new Date());
                            conversation.setCreatedAt(new Date());
                            conversation.setUserJoined(new ArrayList<String>(){{
                                add(currentUid);
                                add(CipherUtils.Hash.sha256(contactProfile.getPhoneNumber()));
                            }});
                            conversation.setLastMessage(edtMessage.getText().toString().trim());
                            currentConversationWrapper = new ConversationWrapper(null, conversation);
                        }
                        Message message = new Message(currentUid,
                                        Conversation.Type.TEXT.label,
                                        edtMessage.getText().toString().trim(),
                                        new Date());
                        viewModel.sendMessage(currentConversationWrapper, message);
                        edtMessage.getText().clear();
                    }
                    else{

                    }
                })
        );


//        if(viewModel.getConversationLiveData().getValue() != null) {
//            currentConversationWrapper = viewModel.getConversationLiveData().getValue();
//            HashMap<String, String> hashMap = currentConversationWrapper.getConversation()
//                    .getUserJoined()
//                    .stream()
//                    .filter(s -> !s.equals(currentUid))
//                    .collect(Collectors.toMap(o -> o, null, (o, o2) -> o2, HashMap::new));
//            viewModel.getMessageList(currentConversationWrapper.getDocumentId());
//        }
    }

    private void observeMessages() {
        viewModel.getMessagesLiveData().observe(this, messageWrappers -> {
            Debug.log("observeMessages:onChanged", messageWrappers.toString());
            Debug.log("observeMessages:onChanged", String.valueOf(messageWrappers.size()));
            messageAdapter.submitList(messageWrappers);
            rcvMessage.smoothScrollToPosition(messageWrappers.size() - 1);
        });
    }

    private void initial(User user) {
        Debug.log("initial");
        MessageActivity.this.runOnUiThread(() -> {
            if(user != null) {
                txvContactName.setText(user.getUserName());
                if(user.getAvatarImageUrl() != null && !user.getAvatarImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(user.getAvatarImageUrl())
                            .error(R.drawable.ic_default_user)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imvAvatarContact);
                } else {
                    imvAvatarContact.setImageResource(R.drawable.ic_default_user);
                }
                toolbar.findViewById(R.id.view_status).setVisibility(user.isOnlineStatus() ? View.VISIBLE : View.GONE);
                toolbar.findViewById(R.id.view_status).requestLayout();
//                viewStatus.setVisibility(user.isOnlineStatus() ? View.VISIBLE : View.GONE);

            }
            if(viewModel.getContactLiveData().getValue() != null && viewModel.getContactLiveData().getValue().getContactName() != null) {
                txvContactName.setText(viewModel.getContactLiveData().getValue().getContactName());
            }
        });



    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}