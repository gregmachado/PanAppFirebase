package gregmachado.com.panappfirebase.listener;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import gregmachado.com.panappfirebase.domain.Bakery;

/**
 * Created by gregmachado on 29/10/16.
 */

public class CustomValueEventListener implements ValueEventListener {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        for( DataSnapshot d : dataSnapshot.getChildren() ){
            Bakery b = d.getValue( Bakery.class );

            Log.i("log", "Name: "+b.getFantasyName());
            Log.i("log", "Email: "+b.getEmail());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
