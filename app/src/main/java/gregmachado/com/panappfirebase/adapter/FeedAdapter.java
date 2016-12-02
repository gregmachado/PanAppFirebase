package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.activity.RequestActivity;
import gregmachado.com.panappfirebase.domain.Feed;
import gregmachado.com.panappfirebase.viewHolder.FeedViewHolder;

/**
 * Created by gregmachado on 18/11/16.
 */
public class FeedAdapter extends FirebaseRecyclerAdapter<Feed, FeedViewHolder> {

    private static final String TAG = FeedAdapter.class.getSimpleName();
    private Context mContext;
    private boolean type;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference imageRef;

    public FeedAdapter(Query ref, Context context, boolean type) {
        super(Feed.class, R.layout.card_feed, FeedViewHolder.class, ref);
        this.mContext = context;
        this.type = type;
    }

    public void remove(int position) {
        notifyItemRemoved(position);
        this.getRef(position).removeValue();
    }

    @Override
    protected void populateViewHolder(final FeedViewHolder viewHolder, final Feed model, final int position) {

        StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
        if (type) {
            imageRef = mStorage.child(model.getUserID());
        } else {
            imageRef = mStorage.child(model.getBakeryID());
        }
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                viewHolder.ivSender.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        viewHolder.tvDate.setText(model.getDate());
        if (type) {
            viewHolder.tvName.setText(model.getUserName());
        } else {
            viewHolder.tvName.setText(model.getBakeryName());
        }
        viewHolder.tvMsg.setText(model.getMsg());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "You clicked on " + model.getFeedID());
                String feedID = model.getFeedID();
                int action = model.getAction();
                String id;
                if (type) {
                    id = model.getBakeryID();
                } else {
                    id = model.getUserID();
                }
                switch (action) {
                    case 1:
                        String bakeryID = model.getBakeryID();
                        Bundle params = new Bundle();
                        params.putString("id", bakeryID);
                        params.putBoolean("type", type);
                        Intent intentRequest = new Intent(mContext, RequestActivity.class);
                        intentRequest.putExtras(params);
                        mContext.startActivity(intentRequest);
                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    default:
                        break;
                }
            }
        });
    }
}
