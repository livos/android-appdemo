package com.livos.appdemo.root;

import com.livos.appdemo.network.ApiModuleForInfo;
import com.livos.appdemo.network.ApiModuleForName;
import com.livos.appdemo.topmovies.TopMoviesActivity;
import com.livos.appdemo.topmovies.TopMoviesModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, ApiModuleForName.class, ApiModuleForInfo.class,TopMoviesModule.class})
public interface ApplicationComponent {

    void inject(TopMoviesActivity target);

}