package gregmachado.com.panappfirebase.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import gregmachado.com.panappfirebase.R;


/**
 * Created by gregmachado on 17/06/16.
 */
public class SplashActivity extends Activity implements Runnable {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(this, 3000);
    }

    public void run(){
        startActivity(new Intent(this, SelectLoginActivity.class));
        finish();
    }
}