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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.AdressAdapter;
import gregmachado.com.panappfirebase.domain.Adress;

/**
 * Created by gregmachado on 09/10/16.
 */
public class AdressListActivity extends CommonActivity {

    private static final String TAG = AdressListActivity.class.getSimpleName();
    private RecyclerView rvAdress;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();;
    private TextView tvNoAdress;
    private List<Adress> list, _list;
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_adress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_my_adress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Meus Endereços");
        Intent it = getIntent();
        initViews();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
        if (userFirebase != null) {
            userId = userFirebase.getUid();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdress();
    }

    private void loadAdress() {
        openProgressBar();
        mDatabaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeProgressBar();
                if (dataSnapshot.hasChild("adress")) {
                    AdressAdapter adapter = new AdressAdapter(mDatabaseReference.child("users").child(userId).child("adress").getRef(),
                            AdressListActivity.this
                    ) {};
                    rvAdress.setAdapter(adapter);
                } else {
                    tvNoAdress.setVisibility(View.VISIBLE);
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
        tvNoAdress = (TextView) findViewById(R.id.tv_no_adress);
        rvAdress = (RecyclerView) findViewById(R.id.rv_adress_list);
        rvAdress.setItemAnimator(new DefaultItemAnimator());
        rvAdress.setHasFixedSize(true);
        rvAdress.setLayoutManager(new LinearLayoutManager(AdressListActivity.this));
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        registerForContextMenu(rvAdress);
    }

    public void openFormAdress(View view) {
        params = new Bundle();
        params.putString("userID", userId);
        params.putBoolean("update", false);
        Intent i = new Intent(AdressListActivity.this, FormAdressActivity.class);
        i.putExtras(params);
        startActivity(i);
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