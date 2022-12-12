package com.zileanstdio.chatapp.Ui.message;

import android.os.Bundle;
import android.view.View;

import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;

public class MessageActivity extends BaseActivity<MessageViewModel> {

    @Override
    public MessageViewModel getViewModel() {
        return null;
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
        super.initAppBar();
        setTitleToolbar("Message");
        setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAppBar();
    }

    @Override
    public void onClick(View v) {

    }
}