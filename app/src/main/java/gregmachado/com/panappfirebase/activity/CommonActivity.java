package gregmachado.com.panappfirebase.activity;

/**
 * Created by gregmachado on 24/10/16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


abstract public class CommonActivity extends AppCompatActivity {
    protected String TAGFATHER = "CommonActivity";
    protected ProgressBar progressBar;
    protected ProgressDialog progressDialog;
    protected Bundle params = new Bundle();
    protected FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected DatabaseReference mDatabaseReference = database.getReference();
    protected boolean connection;

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

    protected boolean checKConnection(Context context) {
        Handler handler = new Handler();
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            String LogSync = null;
            String LogToUserTitle = null;
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                LogSync += "\nConectado a Internet 3G ";
                LogToUserTitle += "Conectado a Internet 3G ";
                handler.sendEmptyMessage(0);
                Log.d(TAGFATHER,"Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
                return true;
            } else if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
                LogSync += "\nConectado a Internet WIFI ";
                LogToUserTitle += "Conectado a Internet WIFI ";
                handler.sendEmptyMessage(0);
                Log.d(TAGFATHER,"Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
                return true;
            } else {
                LogSync += "\nNão possui conexão com a internet ";
                LogToUserTitle += "Não possui conexão com a internet ";
                handler.sendEmptyMessage(0);
                Log.e(TAGFATHER,"Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
                Log.e(TAGFATHER,"Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
                return false;
            }
        } catch (Exception e) {
            Log.e(TAGFATHER,e.getMessage());
            return false;
        }
    }
}