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
import java.util.concurrent.ExecutionException;

import cieloecommerce.sdk.Merchant;
import cieloecommerce.sdk.ecommerce.CieloEcommerce;
import cieloecommerce.sdk.ecommerce.Customer;
import cieloecommerce.sdk.ecommerce.Environment;
import cieloecommerce.sdk.ecommerce.Payment;
import cieloecommerce.sdk.ecommerce.Sale;
import cieloecommerce.sdk.ecommerce.request.CieloError;
import cieloecommerce.sdk.ecommerce.request.CieloRequestException;
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
    private String bakeryId, userId, productID, userName, bakeryName, code;
    private ArrayList<Product> itemsCart;
    private WithdrawFragment withdrawFragment;
    private DeliveryFragment deliveryFragment;
    private int items;
    private boolean isDelivery;
    private Request request;
    private double total;

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
        // Configure seu merchant
        Merchant merchant = new Merchant("8f785e77-1098-4eaf-9483-3e1160d8ab03", "TWYIEDUHYTOWKSDDGPZGJQVSFYDLXGXTIDEDRJGZ");
        // Crie uma instância de Sale informando o ID do pagamento
        Sale sale = new Sale("ID do pagamento");
        // Crie uma instância de Customer informando o nome do cliente
        Customer customer = sale.customer("Comprador Teste");
        // Crie uma instância de Payment informando o valor do pagamento
        Payment payment = sale.payment(15700);
        // Crie  uma instância de Credit Card utilizando os dados de teste
        // esses dados estão disponíveis no manual de integração
        payment.creditCard("123", "Visa").setExpirationDate("12/2018")
                .setCardNumber("0000000000000001")
                .setHolder("Fulano de Tal");

        // Crie o pagamento na Cielo
        try {
            // Configure o SDK com seu merchant e o ambiente apropriado para criar a venda
            sale = new CieloEcommerce(merchant, Environment.SANDBOX).createSale(sale);
            // Com a venda criada na Cielo, já temos o ID do pagamento, TID e demais
            // dados retornados pela Cielo
            String paymentId = sale.getPayment().getPaymentId();
            Log.v(TAG, paymentId);
            // Com o ID do pagamento, podemos fazer sua captura, se ela não tiver sido capturada ainda
            //sale = new CieloEcommerce(merchant, Environment.SANDBOX).captureSale(paymentId, 15700, 0);
            // E também podemos fazer seu cancelamento, se for o caso
            //sale = new CieloEcommerce(merchant, Environment.SANDBOX).cancelSale(paymentId, 15700);
        } catch (ExecutionException | InterruptedException e) {
            // Como se trata de uma AsyncTask, será preciso tratar ExecutionException e InterruptedException
            e.printStackTrace();
        } catch (CieloRequestException e) {
            // Em caso de erros de integração, podemos tratar o erro aqui.
            // os códigos de erro estão todos disponíveis no manual de integração.
            CieloError error = e.getError();
            Log.v("Cielo", error.getCode().toString());
            Log.v("Cielo", error.getMessage());
            if (error.getCode() != 404) {
                Log.e("Cielo", null, e);
            }
        }
    }

    /*
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
    */

    private void callFinishRequest() {
        if(isDelivery){
            request = deliveryFragment.initRequest();
        } else {
            request = withdrawFragment.initRequest();
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        String requestID = mDatabaseReference.push().getKey();
        request.setRequestID(requestID);
        code = generateRandomCode();
        request.setRequestCode(code);
        request.setTotal(total);
        mDatabaseReference.child("requests").child(userId).child(requestID).setValue(request);
        mDatabaseReference.child("requests").child(bakeryId).child(requestID).setValue(request);
        newFeed();
        newHistoric();
        sendNotification();
        showToast("Pedido realizado com sucesso!");
        //Intent intentHome = new Intent(ScheduleActivity.this, UserMainActivity.class);
        //startActivity(intentHome);
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
        for (Product productAux : itemsCart) {
            productID = productAux.getId();
            items = productAux.getUnit();
            int itemsSale = productAux.getItensSale();
            mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).child("itensSale").
                    setValue(itemsSale);
            /*mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).
                    addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int itemsSale = dataSnapshot.child("itensSale").getValue(Integer.class);
                    items = itemsSale + items;
                    mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).child("itensSale").
                            setValue(items);
                    Log.w(TAG, "itemsSale: " + itemsSale);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                }
            });
            Log.w(TAG, "saiu");*/
        }
    }

    private void sendNotification() {

    }
}
