package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Adress;
import gregmachado.com.panappfirebase.domain.Product;

/**
 * Created by gregmachado on 02/11/16.
 */
public class ScheduleActivity extends CommonActivity{

    private static final String TAG = ScheduleActivity.class.getSimpleName();
    private String bakeryId, userId;
    private List<Adress> _list;
    private ArrayList<Product> items;
    final ArrayList<HashMap<String, Object>> m_data = new ArrayList<HashMap<String, Object>>();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FirebaseAuth firebaseAuth;
    private WithdrawtFragment withdrawtFragment;
    private DeliveryFragment deliveryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initViews();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
        userId = userFirebase.getUid();
    }

    private void setupViewPager(ViewPager viewPager) {
        withdrawtFragment = new WithdrawtFragment();
        deliveryFragment = new DeliveryFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(withdrawtFragment, "RECEBER EM CASA");
        adapter.addFragment(deliveryFragment, "RETIRAR NA PADARIA");
        viewPager.setAdapter(adapter);
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

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_schedule);
        setSupportActionBar(toolbar);
        setTitle("Agendamento");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        sendParamsToFragment(bakeryId, userId, items, withdrawtFragment);
        sendParamsToFragment(bakeryId, userId, items, deliveryFragment);
        tabLayout = (TabLayout) findViewById(R.id.tabs_schedule);
        tabLayout.setupWithViewPager(viewPager);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
            items = (ArrayList<Product>)
                    getIntent().getSerializableExtra("items");
        }
    }

    public void sendParamsToFragment(String bakeryId, String userId, ArrayList<Product> products, Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putString("bakeryID", bakeryId);
        bundle.putString("userID", userId);
        bundle.putParcelableArrayList("products", products);
        fragment.setArguments(bundle);
    }
}
