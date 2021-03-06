package gregmachado.com.panappfirebase.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.FeedAdapter;
import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 29/10/16.
 */

public class UserMainActivity extends CommonActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "UserMainActivity";
    private TextView tvUserName, tvUserEmail;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private RecyclerView rvFeed;
    private TextView tvNoFeed;
    private FeedAdapter adapter;
    private ImageView icFeed, ivUser;
    private String bakeryName, userName, userID, userEmail;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private boolean firstOpen;
    private int distance;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_base);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            userName = params.getString("name");
            userEmail = params.getString("email");
            firstOpen = params.getBoolean("firstOpen");
            distance = params.getInt("distanceRef");
            image = params.getString("image");
        }
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(UserMainActivity.this, SelectLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        databaseReference = LibraryClass.getFirebase();
        databaseReference.getRef();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        userID = firebaseUser.getUid();
        if (userEmail == null){
            userEmail = firebaseUser.getEmail();
            Log.i(TAG, "email: " + userEmail);
        }
        mDatabaseReference.child("users").child(userID).child("image").setValue(image);
        initViews();
        rvFeed.setItemAnimator(new DefaultItemAnimator());
        rvFeed.setLayoutManager(new LinearLayoutManager(UserMainActivity.this));
        firebaseAuth.addAuthStateListener(authStateListener);
        String token = FirebaseInstanceId.getInstance().getToken();
        mDatabaseReference.child("users").child(userID).child("token").setValue(token);
        loadFeed();
            //checkFirstOpen();
    }

    private void checkFirstOpen() {
        if (firstOpen){
            params.putString("userID", userID);
            params.putBoolean("update", false);
            params.putBoolean("isRegister", true);
            Intent intentFormEditBakery = new Intent(UserMainActivity.this, FormAdressActivity.class);
            intentFormEditBakery.putExtras(params);
            startActivityForResult(intentFormEditBakery, 5);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkFirstOpen();
    }

    private void loadFeed() {
        openProgressBar();
        mDatabaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("image").exists()){
                    StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
                    StorageReference imageRef = mStorage.child(userID);
                    final long ONE_MEGABYTE = 1024 * 1024;
                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ivUser.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
                if (dataSnapshot.hasChild("feed")) {
                    closeProgressBar();
                    if (tvNoFeed.getVisibility() == View.VISIBLE) {
                        tvNoFeed.setVisibility(View.GONE);
                    }
                    if (icFeed.getVisibility() == View.VISIBLE) {
                        icFeed.setVisibility(View.GONE);
                    }
                    adapter = new FeedAdapter(mDatabaseReference.child("users").child(userID).child("feed").getRef(),
                            UserMainActivity.this, false);
                    rvFeed.setAdapter(adapter);
                    setUpItemTouchHelper();
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

    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                FeedAdapter adapter = (FeedAdapter) rvFeed.getAdapter();
                adapter.remove(swipedPosition);
                if (adapter.getItemCount() == 0){
                    tvNoFeed.setVisibility(View.VISIBLE);
                    icFeed.setVisibility(View.VISIBLE);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvFeed);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        if (checKConnection(getApplicationContext())) {
            int id = item.getItemId();
            params.putString("id", userID);
            params.putBoolean("type", false);
            params.putString("name", userName);

            if (id == R.id.nav_bakerys) {
                params.putInt("distanceRef", distance);
                Intent intentBakeryList = new Intent(UserMainActivity.this, BakeryListActivity.class);
                intentBakeryList.putExtras(params);
                startActivity(intentBakeryList);
            } else if (id == R.id.nav_favorites) {
                params.putInt("distanceRef", distance);
                Intent intentFavoriteBakeryList = new Intent(UserMainActivity.this, FavoriteListActivity.class);
                intentFavoriteBakeryList.putExtras(params);
                startActivity(intentFavoriteBakeryList);
            } else if (id == R.id.nav_configuration) {
                Intent intSettings = new Intent(UserMainActivity.this, SettingsActivity.class);
                intSettings.putExtras(params);
                startActivity(intSettings);
            } else if (id == R.id.nav_follow_orders) {
                Intent intentRequest = new Intent(UserMainActivity.this, RequestActivity.class);
                intentRequest.putExtras(params);
                startActivity(intentRequest);
            } else if (id == R.id.nav_history) {
                Intent intentHistoric = new Intent(UserMainActivity.this, HistoricActivity.class);
                intentHistoric.putExtras(params);
                startActivity(intentHistoric);
            } else if (id == R.id.nav_my_adrees) {
                Intent intentAdressList = new Intent(UserMainActivity.this, AdressListActivity.class);
                intentAdressList.putExtras(params);
                startActivity(intentAdressList);
            } else if (id == R.id.nav_offers) {
                Intent intentOffers = new Intent(UserMainActivity.this, OfferActivity.class);
                intentOffers.putExtras(params);
                startActivity(intentOffers);
            } else if (id == R.id.nav_talk_whit_us) {
                Intent intentTalkWithUs = new Intent(UserMainActivity.this, TalkWithUsActivity.class);
                intentTalkWithUs.putExtras(params);
                startActivity(intentTalkWithUs);
            } else if (id == R.id.nav_exit) {
                onBackPressed();
            }
        } else {
            showToast("Verifique sua conexão com a internet!");
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        tvUserName = (TextView) header.findViewById(R.id.tv_user_name);
        tvUserEmail = (TextView) header.findViewById(R.id.tv_user_email);
        tvUserName.setText(userName);
        tvUserEmail.setText(userEmail);
        tvNoFeed = (TextView) findViewById(R.id.tv_no_feed);
        icFeed = (ImageView) findViewById(R.id.ic_feed);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        rvFeed = (RecyclerView) findViewById(R.id.rv_feed);
        ivUser = (ImageView) header.findViewById(R.id.iv_user);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 5)
        {
            if (resultCode == 5){
                firstOpen = data.getBooleanExtra("firstOpen", false);
                Log.i(TAG, String.valueOf(firstOpen));
                loadFeed();
            }
        }
    }
}