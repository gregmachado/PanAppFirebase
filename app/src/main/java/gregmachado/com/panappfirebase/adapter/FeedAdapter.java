package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Feed;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.viewHolder.FeedViewHolder;

/**
 * Created by gregmachado on 18/11/16.
 */
public class FeedAdapter extends FirebaseRecyclerAdapter<Feed, FeedViewHolder> {

    private static final String TAG = FeedAdapter.class.getSimpleName();
    private Context mContext;
    private boolean type;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();

    public FeedAdapter(Query ref, Context context, boolean type) {
        super(Feed.class, R.layout.card_feed, FeedViewHolder.class, ref);
        this.mContext = context;
        this.type = type;
    }

    @Override
    protected void populateViewHolder(final FeedViewHolder viewHolder, final Feed model, final int position) {
        viewHolder.tvHour.setText(model.getHour());
        viewHolder.tvDate.setText(model.getDate());
        if(type){
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
                boolean open = model.isOpen();
                String id;
                if (type){
                    id = model.getBakeryID();
                } else {
                    id = model.getUserID();
                }
                if(!open){
                    viewHolder.ivNewFeed.setVisibility(View.INVISIBLE);
                    mDatabaseReference.child(id).child("feed").child(feedID)
                            .child("open").setValue(true);
                }
                switch (action){
                    case 1:
                        String bakeryID = model.getBakeryID();
                        Bundle params = new Bundle();
                        params.putString("id", bakeryID);
                        params.putBoolean("type", type);
                        Intent intentRequest = new Intent(mContext, Request.class);
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
