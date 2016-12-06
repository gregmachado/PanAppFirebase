package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.activity.ProductListActivity;
import gregmachado.com.panappfirebase.domain.Bakery;
import gregmachado.com.panappfirebase.interfaces.ItemClickListener;
import gregmachado.com.panappfirebase.viewHolder.NewBakeryViewHolder;

/**
 * Created by gregmachado on 03/12/16.
 */
public class NewBakeryAdapter extends RecyclerView.Adapter<NewBakeryViewHolder> {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String TAG = NewBakeryAdapter.class.getSimpleName();
    private Context mContext;
    private ItemClickListener clickListener;
    private Double userLatitude, userLongitude, lastLatitude, lastLongitude;
    private String bakeryID, userID, userName;
    private Bundle params;
    private ArrayList<String> favorites;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();
    private boolean isFavoriteList;
    private final ArrayList<Bakery> bakeries;

    public NewBakeryAdapter(Context context, ArrayList<Bakery> bakeries,String userID, String userName,
                       ItemClickListener listener, ArrayList<String> favorites) {
        this.mContext = context;
        this.bakeries = bakeries;
        this.favorites = favorites;
        this.userID = userID;
        this.clickListener = listener;
        this.userName = userName;
    }

    @Override
    public NewBakeryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        final View v = layoutInflater.inflate(R.layout.card_bakery, viewGroup, false);
        return new NewBakeryViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(final NewBakeryViewHolder viewHolder, final int i) {
        viewHolder.tvBakeryName.setText(bakeries.get(i).getFantasyName());
        if(bakeries.get(i).getBakeryImage() == null){
            viewHolder.ivBakery.setImageResource(R.drawable.padaria);
        } else {
            StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
            StorageReference imageRef = mStorage.child(bakeries.get(i).getId());
            final long ONE_MEGABYTE = 1024 * 1024;
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    viewHolder.ivBakery.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        viewHolder.ivBakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "You clicked on " + bakeries.get(i).getId());
                bakeryID = bakeries.get(i).getId();
                String name = bakeries.get(i).getFantasyName();
                params = new Bundle();
                params.putString("bakeryID", bakeryID);
                params.putString("name", name);
                params.putString("userName", userName);
                Intent intentProductList = new Intent(mContext, ProductListActivity.class);
                intentProductList.putExtras(params);
                mContext.startActivity(intentProductList);
            }
        });
        viewHolder.tvDistance.setText(String.valueOf(bakeries.get(i).getDistance()));
        if(favorites!=null){
            if(favorites.contains(bakeries.get(i).getId())){
                viewHolder.ibtnFavoriteOn.setVisibility(View.VISIBLE);
                viewHolder.ibtnFavoriteOff.setClickable(false);
            } else {
                viewHolder.ibtnFavoriteOff.setVisibility(View.VISIBLE);
                viewHolder.ibtnFavoriteOn.setClickable(false);
            }
        } else {
            viewHolder.ibtnFavoriteOff.setVisibility(View.VISIBLE);
            viewHolder.ibtnFavoriteOn.setClickable(false);
        }
        viewHolder.ibtnFavoriteOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bakeryID = bakeries.get(i).getId();
                setFavorite(true);
                viewHolder.ibtnFavoriteOn.setVisibility(View.INVISIBLE);
                viewHolder.ibtnFavoriteOff.setVisibility(View.VISIBLE);
                viewHolder.ibtnFavoriteOn.setClickable(false);
            }
        });
        viewHolder.ibtnFavoriteOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bakeryID = bakeries.get(i).getId();
                setFavorite(false);
                viewHolder.ibtnFavoriteOff.setVisibility(View.INVISIBLE);
                viewHolder.ibtnFavoriteOn.setVisibility(View.VISIBLE);
                viewHolder.ibtnFavoriteOff.setClickable(false);
            }
        });
        viewHolder.progressBar.setVisibility(View.INVISIBLE);
        viewHolder.ivBakery.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return bakeries.size();
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
