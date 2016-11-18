package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.ItemRequestAdapter;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.util.DividerItemDecoration;

/**
 * Created by gregmachado on 16/11/16.
 */
public class RequestDetailActivity extends CommonActivity {
    private static final String TAG = RequestDetailActivity.class.getSimpleName();
    private String id, name, requestID;
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
        rvItems.addItemDecoration(new DividerItemDecoration(this));
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

                List<Product> productList = new ArrayList<Product>();
                for (DataSnapshot productSnapshot : dataSnapshot.child("productList").getChildren()) {
                    Product product = new Product();
                    product.setUnit(productSnapshot.child("unit").getValue(Integer.class));
                    product.setProductName(productSnapshot.child("productName").getValue(String.class));
                    product.setProductPrice(productSnapshot.child("productPrice").getValue(double.class));
                    productList.add(product);
                }
                adapter = new ItemRequestAdapter(productList, RequestDetailActivity.this);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_request_detail);
        setSupportActionBar(toolbar);
        setTitle("Detalhes do Pedido");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        tvCode = (TextView) findViewById(R.id.tv_request_code);
        tvName = (TextView) findViewById(R.id.tv_request_bakery_name);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        rvItems = (RecyclerView) findViewById(R.id.rv_items);
        btnChangeStatus = (Button) findViewById(R.id.btn_change_status);
        if(!type){
            btnChangeStatus.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeStatus(View view) {

    }
}
