package com.omni.moviewdb.utils;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;



public class AppController  extends Application{


    @Override
    public void onCreate() {
        super.onCreate();


        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
