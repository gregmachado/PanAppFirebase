package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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

import java.text.DecimalFormat;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.OfferAdapter;

/**
 * Created by gregmachado on 19/11/16.
 */
public class OfferActivity extends CommonActivity {

    private static final String TAG = OfferActivity.class.getSimpleName();
    private RecyclerView rvOffer;
    private String bakeryID, id, userName, bakeryName;
    private TextView tvNoOffers;
    private ImageView icOffer;
    private Handler handler;
    private TextView tvUnits;
    private OfferAdapter adapter;
    private boolean type;
    private DecimalFormat precision = new DecimalFormat("#0.000");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryID = params.getString("bakeryID");
            userName = params.getString("userName");
            bakeryName = params.getString("bakeryName");
            type = params.getBoolean("type");
        }
        initViews();
        if (rvOffer != null) {
            rvOffer.setHasFixedSize(true);
        }
        rvOffer.setItemAnimator(new DefaultItemAnimator());
        rvOffer.setLayoutManager(new GridLayoutManager(OfferActivity.this, 1));
        loadOffers();
    }

    private void loadOffers() {
        openProgressBar();
        mDatabaseReference.child("offers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    closeProgressBar();
                    if (tvNoOffers.getVisibility() == View.VISIBLE) {
                        tvNoOffers.setVisibility(View.GONE);
                    }
                    if (icOffer.getVisibility() == View.VISIBLE) {
                        icOffer.setVisibility(View.GONE);
                    }
                    adapter = new OfferAdapter(mDatabaseReference.child("offers").getRef(),
                            OfferActivity.this, userName, type, bakeryName
                    ) {};
                    rvOffer.setAdapter(adapter);
                } else {
                    closeProgressBar();
                    tvNoOffers.setVisibility(View.VISIBLE);
                    icOffer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_offer);
        setSupportActionBar(toolbar);
        setTitle("Ofertas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvNoOffers = (TextView) findViewById(R.id.tv_no_offer);
        icOffer = (ImageView) findViewById(R.id.ic_offer);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        rvOffer = (RecyclerView) findViewById(R.id.rv_offer);
        FloatingActionButton btnAddOffer = (FloatingActionButton) findViewById(R.id.btn_add_offer);
        if (type){
            btnAddOffer.setVisibility(View.VISIBLE);
        }
    }

    public void newOffer(View view) {
        Bundle params = new Bundle();
        params.putString("bakeryID", bakeryID);
        params.putString("bakeryName", bakeryName);
        params.putBoolean("update", false);
        Intent intentFormProduct = new Intent(OfferActivity.this, FormOfferActivity.class);
        intentFormProduct.putExtras(params);
        startActivity(intentFormProduct);
    }
}
