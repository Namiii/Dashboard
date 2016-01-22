package controllers;

import android.app.Application;

import Utils.Constants;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by nami on 1/22/16.
 */
public class AppController extends Application {

    public AppController() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name(Constants.DB_NAME)
                .schemaVersion(Constants.DB_SCHEMA_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
