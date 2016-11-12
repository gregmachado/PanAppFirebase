package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.activity.ProductListActivity;
import gregmachado.com.panappfirebase.domain.Bakery;
import gregmachado.com.panappfirebase.util.GeoLocation;
import gregmachado.com.panappfirebase.viewHolder.BakeryViewHolder;

/**
 * Created by gregmachado on 11/11/16.
 */
public class BakeryAdapter extends FirebaseRecyclerAdapter<Bakery, BakeryViewHolder> {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String TAG = BakeryAdapter.class.getSimpleName();
    private Context mContext;
    private Double userLatitude, userLongitude;
    private String bakeryID, userID;
    private Bundle params;
    private ArrayList<String> favorites;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();
    private boolean isFavoriteList;

    public BakeryAdapter(Query ref, Context context, Double latitude, Double longitude, ArrayList<String> favorites,
                         String userID, boolean isFavoriteList){
        super(Bakery.class, R.layout.card_bakery, BakeryViewHolder.class, ref);
        this.mContext = context;
        this.userLatitude = latitude;
        this.userLongitude = longitude;
        this.favorites = favorites;
        this.userID = userID;
        this.isFavoriteList = isFavoriteList;
    }

    @Override
    protected void populateViewHolder(final BakeryViewHolder viewHolder, final Bakery model, final int position) {
        viewHolder.tvBakeryName.setText(model.getFantasyName());
        Log.w(TAG, "latitude: " + model.getAdress().getLatitude());
        Log.w(TAG, "longitude: " + model.getAdress().getLongitude());
        Log.w(TAG, "user latitude: " + userLatitude);
        Log.w(TAG, "user longitude: " + userLongitude);
        double distance = GeoLocation.distanceCalculate(userLatitude, userLongitude,
                model.getAdress().getLatitude(), model.getAdress().getLongitude());
        viewHolder.tvDistance.setText(String.valueOf(distance));
        if(favorites!=null){
            if(favorites.contains(model.getId())){
                viewHolder.ibtnFavoriteOn.setVisibility(View.VISIBLE);
                viewHolder.ibtnFavoriteOff.setClickable(false);
            } else {
                viewHolder.ibtnFavoriteOff.setVisibility(View.VISIBLE);
                viewHolder.ibtnFavoriteOn.setClickable(false);
                if(isFavoriteList){
                    getRef(position).removeValue();
                }
            }
        } else {
            viewHolder.ibtnFavoriteOff.setVisibility(View.VISIBLE);
            viewHolder.ibtnFavoriteOn.setClickable(false);
        }
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "You clicked on " + model.getId());
                bakeryID = model.getId();
                String name = model.getFantasyName();
                params = new Bundle();
                params.putString("bakeryID", bakeryID);
                params.putString("name", name);
                Intent intentProductList = new Intent(mContext, ProductListActivity.class);
                intentProductList.putExtras(params);
                mContext.startActivity(intentProductList);
            }
        });
        viewHolder.ibtnFavoriteOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bakeryID = model.getId();
                setFavorite(true);
                viewHolder.ibtnFavoriteOn.setVisibility(View.INVISIBLE);
                viewHolder.ibtnFavoriteOff.setVisibility(View.VISIBLE);
                viewHolder.ibtnFavoriteOn.setClickable(false);
            }
        });
        viewHolder.ibtnFavoriteOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bakeryID = model.getId();
                setFavorite(false);
                viewHolder.ibtnFavoriteOff.setVisibility(View.INVISIBLE);
                viewHolder.ibtnFavoriteOn.setVisibility(View.VISIBLE);
                viewHolder.ibtnFavoriteOff.setClickable(false);
            }
        });
    }

    private void setFavorite(final boolean isFavorite){
        mDatabaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("favorites")){
                    favorites = (ArrayList<String>) dataSnapshot.child("favorites").getValue();
                } else {
                    favorites = new ArrayList<String>();
                }
                if (isFavorite){
                    favorites.remove(bakeryID);
                } else {
                    favorites.add(bakeryID);
                }
                mDatabaseReference.child("users").child(userID).child("favorites").setValue(favorites);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }
}
