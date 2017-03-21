package com.livos.appdemo.root;

import com.livos.appdemo.MainActivity;
import com.livos.appdemo.network.ApiModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, ApiModule.class})
public interface ApplicationComponent {

    void inject(MainActivity target);

}