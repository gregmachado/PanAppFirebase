package gregmachado.com.panappfirebase.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.FeedAdapter;
import gregmachado.com.panappfirebase.domain.Bakery;
import gregmachado.com.panappfirebase.util.ImagePicker;
import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 29/10/16.
 */

public class AdminMainActivity extends CommonActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = AdminMainActivity.class.getSimpleName();
    private String bakeryID;
    private String adminName, adminEmail;
    private TextView tvAdminName, tvAdminEmail;
    private ImageView ivBakery;
    private Bakery bakery;
    private static final int PICK_IMAGE_ID = 234;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String id;
    private RecyclerView rvFeed;
    private TextView tvNoFeed;
    private FeedAdapter adapter;
    private ImageView icFeed;
    private boolean firstOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_base);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            adminName = params.getString("name");
            bakeryID = params.getString("bakeryID");
            adminEmail = params.getString("email");
            firstOpen = params.getBoolean("firstOpen");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        id = firebaseUser.getUid();
        if (firstOpen){
            params.putString("bakeryID", bakeryID);
            params.putString("userID", id);
            params.putBoolean("isRegister", true);
            Intent intentFormEditBakery = new Intent(AdminMainActivity.this, FormEditBakeryActivity.class);
            intentFormEditBakery.putExtras(params);
            startActivity(intentFormEditBakery);
        }
        initViews();
        rvFeed.setItemAnimator(new DefaultItemAnimator());
        rvFeed.setLayoutManager(new LinearLayoutManager(AdminMainActivity.this));
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(AdminMainActivity.this, SelectLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        databaseReference = LibraryClass.getFirebase();
        databaseReference.getRef();
        String token = FirebaseInstanceId.getInstance().getToken();
        mDatabaseReference.child("users").child(id).child("token").setValue(token);
        loadFeed();
    }

    private void loadFeed() {
        openProgressBar();
        mDatabaseReference.child("bakeries").child(bakeryID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("feed")) {
                    closeProgressBar();
                    if (tvNoFeed.getVisibility() == View.VISIBLE) {
                        tvNoFeed.setVisibility(View.GONE);
                    }
                    if (icFeed.getVisibility() == View.VISIBLE) {
                        icFeed.setVisibility(View.GONE);
                    }
                    adapter = new FeedAdapter(mDatabaseReference.child("bakeries").child(bakeryID).child("feed").getRef(),
                            AdminMainActivity.this, true
                    ) {
                    };
                    rvFeed.setAdapter(adapter);
                } else {
                    closeProgressBar();
                    tvNoFeed.setVisibility(View.VISIBLE);
                    icFeed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        params.putString("id", bakeryID);
        params.putBoolean("type", true);
        params.putString("bakeryName", adminName);

        if (id == R.id.nav_products) {
            Intent intentProductAdmin = new Intent(AdminMainActivity.this, ProductAdminActivity.class);
            intentProductAdmin.putExtras(params);
            startActivity(intentProductAdmin);
        } else if (id == R.id.nav_follow_orders_admin) {
            Intent intentRequest = new Intent(AdminMainActivity.this, RequestActivity.class);
            intentRequest.putExtras(params);
            startActivity(intentRequest);
        } else if (id == R.id.nav_history_admin) {
            Intent intentHistoric = new Intent(AdminMainActivity.this, HistoricActivity.class);
            intentHistoric.putExtras(params);
            startActivity(intentHistoric);
        } else if (id == R.id.nav_my_bakery) {
            Intent intentMyBakery = new Intent(AdminMainActivity.this, MyBakeryActivity.class);
            intentMyBakery.putExtras(params);
            startActivity(intentMyBakery);
        } else if (id == R.id.nav_talk_whit_us_admin) {

        } else if (id == R.id.nav_offers) {
            Intent intentOffers = new Intent(AdminMainActivity.this, OfferActivity.class);
            intentOffers.putExtras(params);
            startActivity(intentOffers);
        } else if (id == R.id.nav_exit_admin) {
            onBackPressed();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Home");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        tvAdminName = (TextView) header.findViewById(R.id.tv_user_name);
        tvAdminEmail = (TextView) header.findViewById(R.id.tv_user_email);
        ivBakery = (ImageView) header.findViewById(R.id.iv_user);
        tvAdminName.setText(adminName);
        tvAdminEmail.setText(adminEmail);
        ivBakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(AdminMainActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
        tvNoFeed = (TextView) findViewById(R.id.tv_no_feed);
        icFeed = (ImageView) findViewById(R.id.ic_feed);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        rvFeed = (RecyclerView) findViewById(R.id.rv_feed);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sair?");
        builder.setMessage("Deseja realmente sair?");
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

