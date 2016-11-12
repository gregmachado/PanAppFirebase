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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 29/10/16.
 */

public class UserMainActivity extends CommonActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    protected FrameLayout frameLayout;
    private String userName, userEmail;
    private TextView tvUserName, tvUserEmail;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_base);
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

        Intent it = getIntent();
        params = it.getExtras();

        if (params != null) {
            userName = params.getString("name");
            userEmail = params.getString("email");
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
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        databaseReference = LibraryClass.getFirebase();
        databaseReference.getRef();
        frameLayout = (FrameLayout)findViewById(R.id.content_frame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_base, menu);
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

        if (id == R.id.nav_bakerys) {
            Intent intentBakeryList = new Intent(UserMainActivity.this, BakeryListActivity.class);
            intentBakeryList.putExtras(params);
            startActivity(intentBakeryList);
        } else if (id == R.id.nav_favorites) {
            Intent intentFavoriteBakeryList = new Intent(UserMainActivity.this, FavoriteListActivity.class);
            intentFavoriteBakeryList.putExtras(params);
            startActivity(intentFavoriteBakeryList);
        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_follow_orders) {
            Intent intentRequest = new Intent(UserMainActivity.this, RequestActivity.class);
            //params.putBoolean("isAdmin", false);
            intentRequest.putExtras(params);
            startActivity(intentRequest);
        } else if (id == R.id.nav_history) {
            Intent intentRequest = new Intent(UserMainActivity.this, ScheduleActivity.class);
            intentRequest.putExtras(params);
            startActivity(intentRequest);
        } else if (id == R.id.nav_home) {
            Intent intentHome = new Intent(UserMainActivity.this, UserMainActivity.class);
            intentHome.putExtras(params);
            startActivity(intentHome);
        } else if (id == R.id.nav_my_adrees) {
            Intent intentAdressList = new Intent(UserMainActivity.this, AdressListActivity.class);
            intentAdressList.putExtras(params);
            startActivity(intentAdressList);
        } else if (id == R.id.nav_offers) {

        } else if (id == R.id.nav_talk_whit_us) {

        } else if (id == R.id.nav_exit) {
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        tvUserName = (TextView) header.findViewById(R.id.tv_user_name);
        tvUserEmail = (TextView) header.findViewById(R.id.tv_user_email);
        tvUserName.setText(userName);
        tvUserEmail.setText(userEmail);
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