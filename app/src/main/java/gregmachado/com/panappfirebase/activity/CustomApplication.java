package gregmachado.com.panappfirebase.activity;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by gregmachado on 23/10/16.
 */

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}