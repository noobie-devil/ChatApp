package com.zileanstdio.chatapp.DI;

import com.zileanstdio.chatapp.DI.auth.AuthViewModelModule;
import com.zileanstdio.chatapp.DI.login.LoginViewModelModule;
import com.zileanstdio.chatapp.DI.register.RegisterFragmentBuildersModule;
import com.zileanstdio.chatapp.DI.register.RegisterModule;
import com.zileanstdio.chatapp.DI.register.RegisterViewModelModule;
import com.zileanstdio.chatapp.MainActivity;
import com.zileanstdio.chatapp.Ui.auth.AuthActivity;
import com.zileanstdio.chatapp.Ui.login.LoginActivity;
import com.zileanstdio.chatapp.Ui.register.RegisterActivity;

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

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();
}
