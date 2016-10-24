package gregmachado.com.panappfirebase.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.firebase.client.Firebase;

/**
 * Created by gregmachado on 23/10/16.
 */
public class LibraryClass {

    private static Firebase firebase;
    public static String PREF = "gregmachado.com.panappfirebase.PREF";

    private LibraryClass(){}

    public static Firebase getFirebase(){
        if( firebase == null ){
            firebase = new Firebase("https://panappfirebase.firebaseio.com");
        }
        return( firebase );
    }

    static public String getSP(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String token = sp.getString(key, "");
        return( token );
    }

    static public void saveSP(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }
}
