package gregmachado.com.panappfirebase.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Feed;
import gregmachado.com.panappfirebase.domain.Historic;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.util.DateUtil;

/**
 * Created by gregmachado on 02/11/16.
 */
public class ScheduleActivity extends CommonActivity {

    private static final String TAG = ScheduleActivity.class.getSimpleName();
    private String bakeryId, userId, productID, userName, bakeryName, code, returnCode;
    private ArrayList<Product> itemsCart;
    private WithdrawFragment withdrawFragment;
    private DeliveryFragment deliveryFragment;
    private int items;
    private boolean isDelivery;
    private Request request;
    private double total;
    private ProductCartActivity productCartActivity;
    private ProductListActivity productListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            userName = params.getString("userName");
            bakeryName = params.getString("bakeryName");
            bakeryId = params.getString("bakeryID");
            total = params.getDouble("total");
            itemsCart = (ArrayList<Product>)
                    getIntent().getSerializableExtra("items");
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
        userId = userFirebase.getUid();
        initViews();
    }

    private void setupViewPager(ViewPager viewPager) {
        withdrawFragment = new WithdrawFragment();
        deliveryFragment = new DeliveryFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(withdrawFragment, "RETIRAR NA PADARIA");
        adapter.addFragment(deliveryFragment, "RECEBER EM CASA");
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        sendParamsToFragment(bakeryId, userId, itemsCart, withdrawFragment);
        sendParamsToFragment(bakeryId, userId, itemsCart, deliveryFragment);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_schedule);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void sendParamsToFragment(String bakeryId, String userId, ArrayList<Product> products, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("bakeryID", bakeryId);
        bundle.putString("userID", userId);
        bundle.putString("userName", userName);
        bundle.putString("bakeryName", bakeryName);
        bundle.putParcelableArrayList("products", products);
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sair?");
        builder.setMessage("Se você voltar agora perderá seus itens. Deseja realmente sair?");
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                for (Product productAux : itemsCart) {
                    productID = productAux.getId();
                    items = productAux.getUnit();
                    int itemsSale = productAux.getItensSale();
                    mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).child("itensSale").
                            setValue(itemsSale);
                }
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

    public void callPayment(boolean b){
        isDelivery = b;
        code = generateRandomCode();
        Intent intentPayment = new Intent(ScheduleActivity.this,PaymentActivity.class);
        Bundle params = new Bundle();
        params.putString("idPayment", code);
        params.putString("customer", userName);
        int amount = (int) (total * 100);
        Log.w(TAG, "amount: " + amount);
        params.putInt("amount", amount);
        intentPayment.putExtras(params);
        startActivityForResult(intentPayment, 2);
    }

    private void callFinishRequest() {
        if(isDelivery){
            request = deliveryFragment.initRequest();
        } else {
            request = withdrawFragment.initRequest();
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        String requestID = mDatabaseReference.push().getKey();
        request.setRequestID(requestID);
        request.setRequestCode(code);
        request.setTotal(total);
        request.setSituation(1);
        mDatabaseReference.child("requests").child(userId).child(requestID).setValue(request);
        mDatabaseReference.child("requests").child(bakeryId).child(requestID).setValue(request);
        newFeed();
        newHistoric();
        sendNotification();
        showToast("Pedido realizado com sucesso!");
        Intent intentHome = new Intent(ScheduleActivity.this, UserMainActivity.class);
        params.putString("id", userId);
        params.putString("name", userName);
        params.putBoolean("type", false);
        intentHome.putExtras(params);
        startActivity(intentHome);
        finish();
    }

    private void newFeed() {
        String feedID = mDatabaseReference.push().getKey();
        String time = DateUtil.getTime();
        String date = DateUtil.getTodayDate();
        String msgUser = "Recebemos o seu pedido!";
        Feed feedUser = new Feed(feedID, bakeryId, userId, date, userName, bakeryName, msgUser, 1, null);
        //save user feed
        mDatabaseReference.child("users").child(userId).child("feed").child(feedID).setValue(feedUser);
        String msgBakery = "Você possui um novo pedido!";
        Feed feedBakery = new Feed(feedID, bakeryId, userId, date, userName, bakeryName, msgBakery, 1, null);
        //save bakery feed
        mDatabaseReference.child("bakeries").child(bakeryId).child("feed").child(feedID).setValue(feedBakery);
    }

    private void newHistoric() {
        String historicID = mDatabaseReference.push().getKey();
        String date = DateUtil.getTodayDate();
        //user historic
        String hUser = "Pedido " + code + " realizado no estabelecimento " + bakeryName + " foi enviado!";
        Historic historicUser = new Historic(historicID, date, userName, bakeryName, hUser);
        mDatabaseReference.child("users").child(userId).child("historic").child(historicID).setValue(historicUser);
        //bakery historic
        String hBakery = "Pedido " + code + " realizado por " + userName + " foi recebido!";
        Historic historicBakery = new Historic(historicID, date, userName, bakeryName, hBakery);
        mDatabaseReference.child("bakeries").child(bakeryId).child("historic").child(historicID).setValue(historicBakery);
    }

    private String generateRandomCode() {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVYWXZ0123456789";
        Random random = new Random();
        String code = "";
        int index = -1;
        for (int i = 0; i < 2; i++) {
            index = random.nextInt(chars.length());
            code += chars.substring(index, index + 1);
        }
        String code2 = "";
        int index2 = -1;
        for (int j = 0; j < 3; j++) {
            index2 = random.nextInt(chars.length());
            code2 += chars.substring(index2, index2 + 1);
        }
        code = code + "-" + code2;
        return code;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void sendNotification() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2)
        {
            if (resultCode == 2){
                returnCode = data.getStringExtra("returnCode");
                switch (returnCode){
                    case "4":
                        callFinishRequest();
                        break;
                    case "2":
                        showToast("Paganento não autorizado!");
                        break;
                    case "99":
                        showToast("Paganento não autorizado! Tempo esgotado!");
                        break;
                    case "77":
                        showToast("Paganento não autorizado! Cartão cancelado!");
                        break;
                    case "70":
                        showToast("Paganento não autorizado! Problemas com o cartão!");
                        break;
                    case "78":
                        showToast("Paganento não autorizado! Cartão bloqueado!");
                        break;
                    case "57":
                        showToast("Paganento não autorizado! Cartão expirado!");
                        break;
                }
            } else if (resultCode == 1){
                showToast("Pagamento cancelado!");
            }
        }
    }
}
