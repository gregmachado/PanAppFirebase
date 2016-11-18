package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 16/10/16.
 */
public class RequestActivity extends CommonActivity {

    private static final String TAG = RequestActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String id;
    private boolean typeUser;
    private PendentRequestFragment pendentRequestFragment;
    private DeliveredRequestFragment deliveredRequestFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            id = params.getString("id");
            typeUser = params.getBoolean("type");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    private void setupViewPager(ViewPager viewPager) {
        pendentRequestFragment = new PendentRequestFragment();
        deliveredRequestFragment = new DeliveredRequestFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(pendentRequestFragment, "PENDENTES");
        adapter.addFragment(deliveredRequestFragment, "ENTREGUES");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_request);
        setSupportActionBar(toolbar);
        setTitle("Pedidos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        sendParamsToFragment(id, typeUser, pendentRequestFragment);
        sendParamsToFragment(id, typeUser, deliveredRequestFragment);
        tabLayout = (TabLayout) findViewById(R.id.tabs_request);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void sendParamsToFragment(String id, boolean typeUser, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putBoolean("type", typeUser);
        fragment.setArguments(bundle);
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
