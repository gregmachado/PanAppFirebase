package gregmachado.com.panappfirebase.activity;

/**
 * Created by gregmachado on 24/10/16.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


abstract public class CommonActivity extends AppCompatActivity {

    protected ProgressBar progressBar;
    protected ProgressDialog progressDialog;
    protected Bundle params = new Bundle();
    protected FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected DatabaseReference mDatabaseReference = database.getReference();

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

    protected void openProgressDialog(String msg1, String msg2){
        progressDialog = ProgressDialog.show(this, msg1, msg2, true, false);
    }

    protected void closeProgressDialog(){
        progressDialog.dismiss();
    }
}