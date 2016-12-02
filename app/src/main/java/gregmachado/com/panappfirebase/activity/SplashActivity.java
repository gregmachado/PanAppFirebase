package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.User;
import gregmachado.com.panappfirebase.util.LibraryClass;


/**
 * Created by gregmachado on 17/06/16.
 */
public class SplashActivity extends CommonActivity implements Runnable {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        if (checKConnection(getApplicationContext())) {
            go();
        } else {
            showToast("Verifique sua conexão com a internet!");
            Intent intent = new Intent(SplashActivity.this, NoConnectionActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void go() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(SplashActivity.this, SelectLoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String id = firebaseUser.getUid();
                    databaseReference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            String name = user.getName();
                            String email = user.getEmail();
                            String bakeryID = user.getBakeryID();
                            boolean firstOpen = user.isFirstOpen();
                            params = new Bundle();
                            params.putString("name", name);
                            params.putString("email", email);
                            params.putString("bakeryID", bakeryID);
                            params.putBoolean("firstOpen", firstOpen);
                            if (!user.isType()) {
                                Intent intentHomeUser = new Intent(SplashActivity.this, UserMainActivity.class);
                                intentHomeUser.putExtras(params);
                                startActivity(intentHomeUser);
                            } else {
                                Intent intentHomeAdmin = new Intent(SplashActivity.this, AdminMainActivity.class);
                                intentHomeAdmin.putExtras(params);
                                startActivity(intentHomeAdmin);
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
                }
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(this, 2000);
    }

    @Override
    protected void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void run() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        databaseReference = LibraryClass.getFirebase();
        databaseReference.getRef();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onPause");
    }
}