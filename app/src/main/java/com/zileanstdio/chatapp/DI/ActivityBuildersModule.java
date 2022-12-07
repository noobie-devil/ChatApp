package com.zileanstdio.chatapp.DI;

import com.zileanstdio.chatapp.DI.auth.AuthViewModelModule;
import com.zileanstdio.chatapp.DI.login.LoginViewModelModule;
import com.zileanstdio.chatapp.DI.main.MainFragmentsBuilderModule;
import com.zileanstdio.chatapp.DI.main.MainViewModelModule;
import com.zileanstdio.chatapp.DI.register.RegisterFragmentBuildersModule;
import com.zileanstdio.chatapp.DI.register.RegisterModule;
import com.zileanstdio.chatapp.DI.register.RegisterViewModelModule;
import com.zileanstdio.chatapp.DI.start.StartViewModelModule;
import com.zileanstdio.chatapp.DI.sync.SyncContactViewModelModule;
import com.zileanstdio.chatapp.Ui.main.MainActivity;
import com.zileanstdio.chatapp.Ui.auth.AuthActivity;
import com.zileanstdio.chatapp.Ui.login.LoginActivity;
import com.zileanstdio.chatapp.Ui.register.RegisterActivity;
import com.zileanstdio.chatapp.Ui.start.StartActivity;
import com.zileanstdio.chatapp.Ui.sync.SyncContactActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = {
            LoginViewModelModule.class
    })
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector(modules = {
            RegisterViewModelModule.class,
            RegisterFragmentBuildersModule.class,
            RegisterModule.class
    })
    abstract RegisterActivity contributeRegisterActivity();

    @ContributesAndroidInjector(modules = {
            AuthViewModelModule.class
    })
    abstract AuthActivity contributeAuthActivity();

    @ContributesAndroidInjector(modules = {
            MainFragmentsBuilderModule.class,
            MainViewModelModule.class
    })
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = {
            SyncContactViewModelModule.class
    })
    abstract SyncContactActivity contributeSyncContactActivity();

    @ContributesAndroidInjector(modules = {
            StartViewModelModule.class
    })
    abstract StartActivity contributeStartActivity();
}
