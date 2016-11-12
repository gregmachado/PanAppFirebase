package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
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
    private double distance;
    private String bakeryID;
    private Bundle params;

    public BakeryAdapter(Query ref, Context context, Double latitude, Double longitude){
        super(Bakery.class, R.layout.card_bakery, BakeryViewHolder.class, ref);
        this.mContext = context;
        this.userLatitude = latitude;
        this.userLongitude = longitude;
    }

    @Override
    protected void populateViewHolder(BakeryViewHolder viewHolder, final Bakery model, final int position) {
        viewHolder.tvBakeryName.setText(model.getFantasyName());
        distance = GeoLocation.distanceCalculate(userLatitude, userLongitude,
                model.getAdress().getLatitude(), model.getAdress().getLongitude());
        viewHolder.tvDistance.setText(String.valueOf(distance));
        if(model.isFavorite()){
            viewHolder.ibtnFavoriteOn.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ibtnFavoriteOff.setVisibility(View.VISIBLE);
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
    }
}
