package gregmachado.com.panappfirebase.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.NewBakeryAdapter;
import gregmachado.com.panappfirebase.domain.Bakery;
import gregmachado.com.panappfirebase.interfaces.ItemClickListener;
import gregmachado.com.panappfirebase.util.GeoLocation;

/**
 * Created by gregmachado on 12/11/16.
 */
public class FavoriteListActivity extends CommonActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ItemClickListener {

    private static final String TAG = FavoriteListActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 555;
    private RecyclerView rvBakery;
    private String userID, userName;
    private TextView tvNoBakeries;
    private GoogleApiClient googleApiClient;
    private Location l;
    private Context context;
    private Double userLatitude, userLongitude, lastLatitude, lastLongitude;
    private FirebaseAuth firebaseAuth;
    private ImageView icFavorite;
    private NewBakeryAdapter adapter;
    private ArrayList<String> favorites;
    private int distReference;
    private double distance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_favorite_bakery_list);
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        callConnection();
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            userName = params.getString("name");
            distReference = params.getInt("distanceRef");
        }
        initViews();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        userID = firebaseUser.getUid();
        userName = firebaseUser.getDisplayName();
        loadBakery();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadBakery() {
        openProgressBar();
        mDatabaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("favorites")) {
                    favorites = (ArrayList<String>) dataSnapshot.child("favorites").getValue();
                    distReference = dataSnapshot.child("distanceForSearchBakery").getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
        final ArrayList<Bakery> bakeryList = new ArrayList<>();
        if (userLatitude == null) {
            mDatabaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lastLatitude = dataSnapshot.child("lastLatitude").getValue(Double.class);
                    lastLongitude = dataSnapshot.child("lastLongitude").getValue(Double.class);
                    Log.i(TAG, "lastLatitude: " + lastLatitude);
                    Log.i(TAG, "lastLongitude: " + lastLongitude);
                    userLatitude = lastLatitude;
                    userLongitude = lastLongitude;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                }
            });
        }
        mDatabaseReference.child("bakeries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeProgressBar();
                if (dataSnapshot.getChildrenCount() > 0) {
                    if (favorites != null){
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Bakery bakery = new Bakery();
                            bakery = data.getValue(Bakery.class);
                            Log.i(TAG, bakery.getFantasyName());
                            //Log.i(TAG, String.valueOf(userLatitude + userLongitude));
                            distance = GeoLocation.distanceCalculate(userLatitude, userLongitude,
                                    bakery.getAdress().getLatitude(), bakery.getAdress().getLongitude());
                            bakery.setDistance(distance);

                            if (distance <= distReference && favorites.contains(bakery.getId())) {
                                bakeryList.add(bakery);
                            }
                        }
                        Comparator crescente = new ComparatorBakery();
                        //Comparator decrescente = Collections.reverseOrder(crescente);
                        Collections.sort(bakeryList, crescente);
                        adapter = new NewBakeryAdapter(FavoriteListActivity.this, bakeryList, userID, userName,
                                FavoriteListActivity.this, favorites);
                        Log.i(TAG, String.valueOf(bakeryList.size()));
                        Log.i(TAG, "dist: " + distance + "/ " + distReference);
                        rvBakery.setAdapter(adapter);
                    } else {
                        tvNoBakeries.setVisibility(View.VISIBLE);
                        icFavorite.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvNoBakeries.setVisibility(View.VISIBLE);
                    icFavorite.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    private synchronized void callConnection() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            }
        } else {
            l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        if (l != null) {
            Log.i("Log", "onConnected(" + bundle + ")");
            Log.i("Log", "latitude: " + l.getLatitude());
            Log.i("Log", "longitude: " + l.getLongitude());
            userLatitude = l.getLatitude();
            userLongitude = l.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Log", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Log", "onConnectionFailed(" + connectionResult + ")");
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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(context, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissão aceita!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permissão negada!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bakery);
        setSupportActionBar(toolbar);
        setTitle("Padarias Favoritas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvNoBakeries = (TextView) findViewById(R.id.tv_no_favorites);
        rvBakery = (RecyclerView) findViewById(R.id.rv_favorite);
        icFavorite = (ImageView) findViewById(R.id.ic_favorite);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        if (rvBakery != null) {
            //to enable optimization of recyclerview
            rvBakery.setHasFixedSize(true);
        }
        rvBakery.setItemAnimator(new DefaultItemAnimator());
        //registerForContextMenu(rvBakery);
        rvBakery.setLayoutManager(new LinearLayoutManager(FavoriteListActivity.this));
    }

    @Override
    public void onClick(View view, int position) {

    }

    class ComparatorBakery implements Comparator<Bakery> {
        public int compare(Bakery p1, Bakery p2) {
            return p1.getDistance() < p2.getDistance() ? -1 : (p1.getDistance() > p2.getDistance() ? +1 : 0);
        }
    }
}
