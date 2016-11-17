package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.ItemRequestAdapter;
import gregmachado.com.panappfirebase.domain.Request;

/**
 * Created by gregmachado on 16/11/16.
 */
public class RequestDetailActivity extends CommonActivity {
    private static final String TAG = RequestDetailActivity.class.getSimpleName();
    private String id, name, requestID;
    private CardView cardView;
    private TextView tvCode, tvName, tvTotal, tvStatus;
    private RecyclerView rvItems;
    private ItemRequestAdapter adapter;
    private Button btnChangeStatus;
    private boolean type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            id = params.getString("bakeryID");
            name = params.getString("name");
            requestID = params.getString("requestID");
            type = params.getBoolean("type");
        }
        initViews();
        rvItems.setHasFixedSize(true);
        rvItems.setItemAnimator(new DefaultItemAnimator());
        rvItems.setLayoutManager(new LinearLayoutManager(RequestDetailActivity.this));
        loadItems();
    }

    private void loadItems() {
        openProgressBar();
        mDatabaseReference.child("requests").child(id).child(requestID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeProgressBar();
                Request request = dataSnapshot.getValue(Request.class);
                tvStatus.setText(request.getStatus());
                tvCode.setText(request.getRequestCode());
                tvName.setText(name);
                tvTotal.setText(String.valueOf(request.getTotal()));
                cardView.setVisibility(View.VISIBLE);
                adapter = new ItemRequestAdapter(mDatabaseReference.child("requests").child(id).child(requestID).
                        child("productList").getRef(),
                        RequestDetailActivity.this
                ) {
                };
                rvItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product);
        setSupportActionBar(toolbar);
        setTitle("Detalhes do Pedido");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        cardView = (CardView) findViewById(R.id.card_detail_request);
        tvCode = (TextView) findViewById(R.id.tv_request_code);
        tvName = (TextView) findViewById(R.id.tv_request_bakery_name);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        cardView.setVisibility(View.INVISIBLE);
        btnChangeStatus = (Button) findViewById(R.id.btn_change_status);
        if(!type){
            btnChangeStatus.setVisibility(View.INVISIBLE);
        }
    }

    public void changeStatus(View view) {

    }
}
