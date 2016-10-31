package gregmachado.com.panappfirebase.listener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import gregmachado.com.panappfirebase.domain.Bakery;

/**
 * Created by gregmachado on 29/10/16.
 */

public class CustomChildEventListener implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Bakery b = dataSnapshot.getValue( Bakery.class );
        Log.i("log", "ADDED");
        Log.i("log", "Name: "+b.getFantasyName());
        Log.i("log", "Email: "+b.getEmail());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Bakery b = dataSnapshot.getValue( Bakery.class );
        Log.i("log", "CHANGED");
        Log.i("log", "Name: "+b.getFantasyName());
        Log.i("log", "Email: "+b.getEmail());
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Bakery b = dataSnapshot.getValue( Bakery.class );
        Log.i("log", "REMOVED");
        Log.i("log", "Name: "+b.getFantasyName());
        Log.i("log", "Email: "+b.getEmail());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}