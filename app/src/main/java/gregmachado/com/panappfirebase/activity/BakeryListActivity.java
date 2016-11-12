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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.BakeryAdapter;
import gregmachado.com.panappfirebase.domain.Bakery;

/**
 * Created by gregmachado on 29/10/16.
 */

public class BakeryListActivity extends CommonActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = BakeryListActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 555;
    private RecyclerView rvBakery;
    private String bakeryID;
    private TextView tvNoBakeries;
    private List<Bakery> list;
    private Bakery bakery;
    private GoogleApiClient googleApiClient;
    private Location l;
    private Context context;
    private Double userLatitude, userLongitude;
    private double distance;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();
    private FirebaseAuth firebaseAuth;
    private ImageView icBakery;
    private BakeryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_bakery_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bakery);
        setSupportActionBar(toolbar);
        setTitle("Padarias");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initViews();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String id = firebaseUser.getUid();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        openProgressBar();
        callConnection();
        mDatabaseReference.child("bakeries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeProgressBar();
                if (dataSnapshot.getChildrenCount() > 0) {
                    if (tvNoBakeries.getVisibility() == View.VISIBLE) {
                        tvNoBakeries.setVisibility(View.GONE);
                    }
                    if (icBakery.getVisibility() == View.VISIBLE) {
                        icBakery.setVisibility(View.GONE);
                    }
                    adapter = new BakeryAdapter(mDatabaseReference.child("bakeries").getRef(),
                            BakeryListActivity.this, userLatitude, userLongitude
                    ) {
                    };
                    rvBakery.setAdapter(adapter);
                } else {
                    tvNoBakeries.setVisibility(View.VISIBLE);
                    icBakery.setVisibility(View.VISIBLE);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initViews() {
        tvNoBakeries = (TextView) findViewById(R.id.tv_no_bakeries);
        rvBakery = (RecyclerView) findViewById(R.id.rv_bakery);
        icBakery = (ImageView) findViewById(R.id.ic_bakery);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        if (rvBakery != null) {
            //to enable optimization of recyclerview
            rvBakery.setHasFixedSize(true);
        }
        rvBakery.setItemAnimator(new DefaultItemAnimator());
        //registerForContextMenu(rvBakery);
        rvBakery.setLayoutManager(new LinearLayoutManager(BakeryListActivity.this));
    }
}
