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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroAddress;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroAreaCode;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroBrazilianStates;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroBuyer;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroCheckout;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroFactory;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroItem;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroPayment;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroPhone;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroShipping;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroShippingType;
import gregmachado.com.panappfirebase.util.AppUtil;

/**
 * Created by gregmachado on 02/11/16.
 */
public class ScheduleActivity extends CommonActivity {

    private static final String TAG = ScheduleActivity.class.getSimpleName();
    private String bakeryId, userId, productID;
    private ArrayList<Product> itemsCart;
    private WithdrawFragment withdrawFragment;
    private DeliveryFragment deliveryFragment;
    private int items;
    private boolean isDelivery;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
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
        final PagSeguroFactory pagseguro = PagSeguroFactory.instance();
        List<PagSeguroItem> shoppingCart = new ArrayList<>();
        for (Iterator<Product> iterator = itemsCart.iterator(); iterator.hasNext(); ) {
            Product product = iterator.next();
            shoppingCart.add(pagseguro.item(product.getId(), product.getProductName(), BigDecimal.valueOf(product.getProductPrice()),
                    product.getUnit(), 300));
        }
        PagSeguroPhone buyerPhone = pagseguro.phone(PagSeguroAreaCode.DDD81, "998187427");
        PagSeguroBuyer buyer = pagseguro.buyer("Ricardo Ferreira", "14/02/1978", "15061112000", "test@email.com.br", buyerPhone);
        PagSeguroAddress buyerAddress = pagseguro.address("Av. Angelo Guisso", "850", "Apt507A", "Esplanada", "95095497",
                "Caxias do Sul", PagSeguroBrazilianStates.RIOGRANDEDOSUL);
        PagSeguroShipping buyerShippingOption = pagseguro.shipping(PagSeguroShippingType.NOT_DEFINED, buyerAddress);
        PagSeguroCheckout checkout = pagseguro.checkout("Ref0001", shoppingCart, buyer, buyerShippingOption);
        // starting payment process
        new PagSeguroPayment(ScheduleActivity.this, itemsCart, bakeryId).pay(checkout.buildCheckoutXml());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            // se foi uma tentativa de pagamento
            if (requestCode == PagSeguroPayment.PAG_SEGURO_REQUEST_CODE) {
                // exibir confirmação de cancelamento
                final String msg = getString(R.string.transaction_cancelled);
                AppUtil.showConfirmDialog(this, msg, null);
            }
        } else if (resultCode == RESULT_OK) {
            // se foi uma tentativa de pagamento
            if (requestCode == PagSeguroPayment.PAG_SEGURO_REQUEST_CODE) {
                // exibir confirmação de sucesso
                final String msg = getString(R.string.transaction_succeded);
                AppUtil.showConfirmDialog(this, msg, null);
            }
        } else if (resultCode == PagSeguroPayment.PAG_SEGURO_REQUEST_CODE) {
            switch (data.getIntExtra(PagSeguroPayment.PAG_SEGURO_EXTRA, 0)) {
                case PagSeguroPayment.PAG_SEGURO_REQUEST_SUCCESS_CODE: {
                    final String msg = getString(R.string.transaction_succeded);
                    AppUtil.showConfirmDialog(this, msg, null);
                    callFinishRequest();
                    break;
                }
                case PagSeguroPayment.PAG_SEGURO_REQUEST_FAILURE_CODE: {
                    final String msg = getString(R.string.transaction_error);
                    AppUtil.showConfirmDialog(this, msg, null);
                    break;
                }
                case PagSeguroPayment.PAG_SEGURO_REQUEST_CANCELLED_CODE: {
                    final String msg = getString(R.string.transaction_cancelled);
                    AppUtil.showConfirmDialog(this, msg, null);
                    break;
                }
            }
        }
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
        mDatabaseReference.child("users").child(userId).child("requests").child(requestID).setValue(request);
        Toast.makeText(ScheduleActivity.this, "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show();
        Intent intentHome = new Intent(ScheduleActivity.this, UserMainActivity.class);
        startActivity(intentHome);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Product productAux : itemsCart) {
            productID = productAux.getId();
            items = productAux.getUnit();
            mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int itemsSale = Integer.parseInt(dataSnapshot.child("itensSale").getValue().toString());
                    items = itemsSale + items;
                    mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).child("itensSale").
                            setValue(items);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                }
            });
        }
    }
}
