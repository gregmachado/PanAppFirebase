package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
    private String id, name;
    private boolean type;

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
        viewHolder.tvDate.setText(model.getScheduleDate());
        viewHolder.tvUnits.setText(String.valueOf(model.getProductList().size()));
        viewHolder.tvStatus.setText(model.getStatus());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "You clicked on " + model.getRequestID());
                String bakeryID = model.getBakeryID();
                String requestID = model.getRequestID();
                Bundle params = new Bundle();
                params.putString("bakeryID", bakeryID);
                params.putString("requestID", requestID);
                if(type){
                    name = model.getUserName();
                } else {
                    name = model.getBakeryName();
                }
                params.putString("name", name);
                params.putBoolean("type", type);
                Intent intentProductList = new Intent(mContext, RequestDetailActivity.class);
                intentProductList.putExtras(params);
                mContext.startActivity(intentProductList);
            }
        });
    }
}
