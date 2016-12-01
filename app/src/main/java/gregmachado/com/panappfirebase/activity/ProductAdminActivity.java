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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.ProductAdapterAdmin;

/**
 * Created by gregmachado on 30/10/16.
 */
public class ProductAdminActivity extends CommonActivity {

    private static final String TAG = ProductAdminActivity.class.getSimpleName();
    private RecyclerView rvProductAdmin;
    private String bakeryId;
    private TextView tvNoProducts;
    private ImageView icProduct;
    private ProductAdapterAdmin adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_admin);
        initViews();
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
        }
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product);
        setSupportActionBar(toolbar);
        setTitle("Meus Produtos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvNoProducts = (TextView) findViewById(R.id.tv_no_products_admin);
        icProduct = (ImageView) findViewById(R.id.ic_product);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        rvProductAdmin = (RecyclerView) findViewById(R.id.rv_product_admin);
        if (rvProductAdmin != null) {
            //to enable optimization of recyclerview
            rvProductAdmin.setHasFixedSize(true);
        }
        rvProductAdmin.setItemAnimator(new DefaultItemAnimator());
        //registerForContextMenu(rvProductAdmin);
        rvProductAdmin.setLayoutManager(new LinearLayoutManager(ProductAdminActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "ID: " + bakeryId);
        openProgressBar();
        mDatabaseReference.child("bakeries").child(bakeryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeProgressBar();
                if (dataSnapshot.hasChild("products")) {
                    if (tvNoProducts.getVisibility() == View.VISIBLE) {
                        tvNoProducts.setVisibility(View.GONE);
                    }
                    if (icProduct.getVisibility() == View.VISIBLE) {
                        icProduct.setVisibility(View.GONE);
                    }
                    adapter = new ProductAdapterAdmin(mDatabaseReference.child("bakeries").child(bakeryId).child("products").getRef(),
                            ProductAdminActivity.this, ProductAdminActivity.this, bakeryId
                    ) {};
                    rvProductAdmin.setAdapter(adapter);
                } else {
                    tvNoProducts.setVisibility(View.VISIBLE);
                    icProduct.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
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

    public void newProduct(View view) {
        params.putString("bakeryID", bakeryId);
        params.putBoolean("update", false);
        Intent intentFormProduct = new Intent(ProductAdminActivity.this, FormProductActivity.class);
        intentFormProduct.putExtras(params);
        startActivity(intentFormProduct);
    }
}