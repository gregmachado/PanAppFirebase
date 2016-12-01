package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

import gregmachado.com.panappfirebase.R;


/**
 * Created by gregmachado on 17/06/16.
 */
public class SelectLoginActivity extends CommonActivity {

    private static final String TAG = SelectLoginActivity.class.getSimpleName();
    private static final int RC_GOOGLE_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_login);
    }



    @Override
    protected void initViews() {

    }

    public void selectSignInEmail(View view) {
        Intent intentLoginEmail = new Intent(SelectLoginActivity.this, LoginEmailActivity.class);
        startActivity(intentLoginEmail);
    }

    public void selectSignInGoogle(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    public void selectSignInFaceBook(View view) {
    }


    public void callRegisterBakery(View view) {
        Intent intentRegisterBakery = new Intent(SelectLoginActivity.this, RegisterBakeryActivity.class);
        startActivity(intentRegisterBakery);
    }
}
