package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
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
import gregmachado.com.panappfirebase.adapter.HistoricAdapter;
import gregmachado.com.panappfirebase.util.DividerItemDecoration;

/**
 * Created by gregmachado on 24/11/16.
 */
public class HistoricActivity extends CommonActivity{
    private static final String TAG = HistoricActivity.class.getSimpleName();
    private String id, reference;
    private boolean typeUser;
    private TextView tvNoHistoric;
    private ImageView ivHistoric;
    private RecyclerView rvHistoric;
    private HistoricAdapter adapter;
    //private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            id = params.getString("id");
            typeUser = params.getBoolean("type");
        }
        if (typeUser){
            reference = "bakeries";
        } else {
            reference = "users";
        }
        initViews();
        rvHistoric.setHasFixedSize(true);
        rvHistoric.setLayoutManager(new LinearLayoutManager(HistoricActivity.this));
        rvHistoric.addItemDecoration(new DividerItemDecoration(HistoricActivity.this));
        loadHistoric();
    }

    private void loadHistoric() {
        openProgressBar();
        mDatabaseReference.child(reference).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("historic")) {
                    closeProgressBar();
                    if (tvNoHistoric.getVisibility() == View.VISIBLE) {
                        tvNoHistoric.setVisibility(View.GONE);
                    }
                    if (ivHistoric.getVisibility() == View.VISIBLE) {
                        ivHistoric.setVisibility(View.GONE);
                    }
                    //cardView.setVisibility(View.VISIBLE);
                    adapter = new HistoricAdapter(mDatabaseReference.child(reference).child(id).child("historic").getRef(),
                            HistoricActivity.this);
                    rvHistoric.setAdapter(adapter);
                } else {
                    closeProgressBar();
                    tvNoHistoric.setVisibility(View.VISIBLE);
                    ivHistoric.setVisibility(View.VISIBLE);
                    //cardView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_historic);
        setSupportActionBar(toolbar);
        setTitle("Histórico");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvNoHistoric = (TextView) findViewById(R.id.tv_no_historic);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        ivHistoric = (ImageView) findViewById(R.id.ic_historic);
        //cardView = (CardView) findViewById(R.id.card_historic);
        rvHistoric = (RecyclerView) findViewById(R.id.rv_historic);
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
}
