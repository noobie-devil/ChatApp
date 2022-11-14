package com.zileanstdio.chatapp.DI.main;

import androidx.lifecycle.ViewModel;

import com.zileanstdio.chatapp.DI.ViewModelKey;
import com.zileanstdio.chatapp.Ui.main.MainViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    public abstract ViewModel bindMainViewModel(MainViewModel viewModel);
}
