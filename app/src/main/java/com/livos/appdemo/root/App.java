package com.livos.appdemo.root;

import android.app.Application;

import com.livos.appdemo.network.ApiModuleForInfo;
import com.livos.appdemo.network.ApiModuleForName;
import com.livos.appdemo.topmovies.TopMoviesModule;


public class App extends Application {

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .apiModuleForName(new ApiModuleForName())   // Module for movie name
                .topMoviesModule(new TopMoviesModule())     // Feature module
                .apiModuleForInfo(new ApiModuleForInfo())   // Module for movie country
                .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
