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
import gregmachado.com.panappfirebase.activity.RequestDetailActivity;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.viewHolder.RequestViewHolder;

/**
 * Created by gregmachado on 15/11/16.
 */
public class RequestAdapter extends FirebaseRecyclerAdapter<Request, RequestViewHolder> {

    private static final String TAG = RequestAdapter.class.getSimpleName();
    private Context mContext;
    private String id, userName, bakeryName;
    private boolean type, open;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();

    public RequestAdapter(Query ref, Context context, String id, boolean type) {
        super(Request.class, R.layout.card_request, RequestViewHolder.class, ref);
        this.mContext = context;
        this.id = id;
        this.type = type;
    }

    @Override
    protected void populateViewHolder(final RequestViewHolder viewHolder, final Request model, final int position) {
        viewHolder.tvRequestCode.setText(model.getRequestCode());
        viewHolder.tvMethod.setText(model.getMethod());
        viewHolder.tvHour.setText(model.getScheduleHour());
        if(type){
            viewHolder.tvBakeryName.setText(model.getUserName());
        } else {
            viewHolder.tvBakeryName.setText(model.getBakeryName());
        }
        open = model.isOpen();
        if (open){
            viewHolder.icNewRequest.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.icNewRequest.setVisibility(View.VISIBLE);
        }
        viewHolder.tvDate.setText(model.getScheduleDate());
        viewHolder.tvUnits.setText(String.valueOf(model.getProductList().size()));
        viewHolder.tvStatus.setText(model.getStatus());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "You clicked on " + model.getRequestID());
                String requestID = model.getRequestID();
                if(!open){
                    viewHolder.icNewRequest.setVisibility(View.INVISIBLE);
                    mDatabaseReference.child("requests").child(id).child(requestID)
                            .child("open").setValue(true);
                }
                String bakeryID = model.getBakeryID();
                Bundle params = new Bundle();
                bakeryName = model.getBakeryName();
                userName = model.getUserName();
                String code = model.getRequestCode();
                params.putString("code", code);
                params.putString("bakeryID", bakeryID);
                params.putString("requestID", requestID);
                params.putString("userName", userName);
                params.putString("bakeyName", bakeryName);
                params.putBoolean("type", type);
                Intent intentProductList = new Intent(mContext, RequestDetailActivity.class);
                intentProductList.putExtras(params);
                mContext.startActivity(intentProductList);
            }
        });
    }
}
