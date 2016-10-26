package gregmachado.com.panappfirebase.activity;

/**
 * Created by gregmachado on 24/10/16.
 */

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;


abstract public class CommonActivity extends AppCompatActivity {

    protected AutoCompleteTextView email;
    protected EditText password;
    protected ProgressBar progressBar;
    protected ProgressDialog progressDialog;

    protected void showSnackbar(String message ){
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_SHORT)
                .show();
    }

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    abstract protected void initViews();

    abstract protected void initUser();

    public abstract void onConnectionFailed(ConnectionResult connectionResult);

    protected void openProgressDialog(String msg1, String msg2){
        progressDialog = ProgressDialog.show(this, msg1, msg2, true, false);
    }

    protected void closeProgressDialog(){
        progressDialog.dismiss();
    }
}