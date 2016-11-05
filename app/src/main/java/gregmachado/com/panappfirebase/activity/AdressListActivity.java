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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Adress;
import gregmachado.com.panappfirebase.viewHolder.AdressViewHolder;

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
    private Bundle params;
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
                    FirebaseRecyclerAdapter<Adress, AdressViewHolder> adapter = new FirebaseRecyclerAdapter<Adress, AdressViewHolder>(
                            Adress.class,
                            R.layout.card_my_adress,
                            AdressViewHolder.class,
                            //referencing the node where we want the database to store the data from our Object
                            mDatabaseReference.child("users").child(userId).child("adress").getRef()
                    ) {
                        @Override
                        protected void populateViewHolder(final AdressViewHolder viewHolder, final Adress model, final int position) {

                            viewHolder.tvStreet.setText(model.getStreet());
                            viewHolder.tvComplement.setText(model.getComplement());
                            viewHolder.tvDistrict.setText(model.getDistrict());
                            viewHolder.tvCity.setText(model.getCity());
                            viewHolder.tvNumber.setText(model.getNumber());
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                        }
                    };
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