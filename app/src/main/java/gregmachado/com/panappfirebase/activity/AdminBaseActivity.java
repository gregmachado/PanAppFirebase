package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Bakery;
import gregmachado.com.panappfirebase.util.ImagePicker;
import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 29/10/16.
 */

public class AdminBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected FrameLayout frameLayout;
    private String bakeryID, adminId;
    private Bundle params;
    private String adminName, adminEmail;
    private TextView tvAdminName, tvAdminEmail;
    private ImageView ivBakery;
    private static String TAG = AdminBaseActivity.class.getSimpleName();
    private Bakery bakery;
    private static final int PICK_IMAGE_ID = 234;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Home");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //new GetBakeryAsyncTask(this).execute();

        Intent it = getIntent();
        params = it.getExtras();

        if (params != null) {
            adminName = params.getString("name");
            bakeryID = params.getString("bakeryID");
            adminEmail = params.getString("email");
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(AdminBaseActivity.this, SelectLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        databaseReference = LibraryClass.getFirebase();
        databaseReference.getRef();

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(AdminBaseActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        params.putString("bakeryID", bakeryID);

        if (id == R.id.nav_products) {
            Intent intentProductAdmin = new Intent(AdminBaseActivity.this, ProductAdminActivity.class);
            intentProductAdmin.putExtras(params);
            startActivity(intentProductAdmin);
        } else if (id == R.id.nav_follow_orders_admin) {

        } else if (id == R.id.nav_history_admin) {

        } else if (id == R.id.nav_home_admin) {

        } else if (id == R.id.nav_my_bakery) {
            /*Intent intentMyBakery = new Intent(AdminBaseActivity.this, MyBakeryActivity.class);
            intentMyBakery.putExtras(params);
            startActivity(intentMyBakery);*/
        } else if (id == R.id.nav_talk_whit_us_admin) {

        } else if (id == R.id.nav_exit_admin) {
            FirebaseAuth.getInstance().signOut();
            finish();
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
}

