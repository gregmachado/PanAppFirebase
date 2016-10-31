package gregmachado.com.panappfirebase.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.CartAdapter;
import gregmachado.com.panappfirebase.controller.CartController;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.helper.CartDB;
import gregmachado.com.panappfirebase.interfaces.ItemClickListener;
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
 * Created by gregmachado on 02/10/16.
 */
public class ProductCartActivity extends AppCompatActivity implements ItemClickListener {

    private static final String TAG = ProductCartActivity.class.getSimpleName();
    private RecyclerView rvProduct;
    private CartAdapter cartAdapter;
    private List<Product> list;
    private Product product;
    private String name;
    private TextView tvItens, tvPrice;
    private List<Product> _list;
    private CartDB database;
    private Double priceTotal = 0.00, price;
    private Button btnFinishPurchase;
    private Bundle params;
    private String userId, bakeryId;
    private String[][] itens;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getLayoutInflater().inflate(R.layout.activity_product_admin, frameLayout);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);

        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
        }

        setTitle("Meu carrinho");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvItens = (TextView) findViewById(R.id.tv_units);
        tvPrice = (TextView) findViewById(R.id.tv_total_price_cart);
        rvProduct = (RecyclerView) findViewById(R.id.product_cart_list);
        rvProduct.setItemAnimator(new DefaultItemAnimator());
        registerForContextMenu(rvProduct);
        loadCartList();
        btnFinishPurchase = (Button) findViewById(R.id.btn_finish_purchase);
        btnFinishPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // at this point you should check if the user has internet connection
                // before stating the pagseguro checkout process.(it will need internet connection)

                // simulating an user buying
                final PagSeguroFactory pagseguro = PagSeguroFactory.instance();
                itens = getItens(_list);
                List<PagSeguroItem> shoppingCart = new ArrayList<>();
                for (Iterator<Product> iterator = _list.iterator(); iterator.hasNext(); ) {
                    Product product = iterator.next();
                    shoppingCart.add(pagseguro.item(String.valueOf(product.getId()), product.getProductName(), BigDecimal.valueOf(product.getProductPrice()), product.getUnit(), 300));
                }
                    PagSeguroPhone buyerPhone = pagseguro.phone(PagSeguroAreaCode.DDD81, "998187427");
                    PagSeguroBuyer buyer = pagseguro.buyer("Ricardo Ferreira", "14/02/1978", "15061112000", "test@email.com.br", buyerPhone);
                    PagSeguroAddress buyerAddress = pagseguro.address("Av. Boa Viagem", "51", "Apt201", "Boa Viagem", "51030330", "Recife", PagSeguroBrazilianStates.PERNAMBUCO);
                    PagSeguroShipping buyerShippingOption = pagseguro.shipping(PagSeguroShippingType.NOT_DEFINED, buyerAddress);
                    PagSeguroCheckout checkout = pagseguro.checkout("Ref0001", shoppingCart, buyer, buyerShippingOption);
                // starting payment process
                CartController controller = new CartController(getBaseContext());
                controller.deleteAll();
                new PagSeguroPayment(ProductCartActivity.this).pay(checkout.buildCheckoutXml());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view, int position) {
        final Product product = list.get(position);
        Toast.makeText(this, "Produto: " + product.getProductName(), Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void loadCartList() {
        ProgressDialog pd;
        pd = new ProgressDialog(this);
        pd.setMessage("Carregando carrinho...");
        pd.show();

        CartController controller = new CartController(getBaseContext());
        Cursor cursor = controller.load();
        _list = cursor2List(cursor);
        setValuesToolbarBottom(String.valueOf(priceTotal));

        /*
        int ii, jj;

        for (ii=0; ii<itens.length; ii++){
            for (jj=0; jj<itens[ii].length; jj++){
                Log.i("Log", "produto " + ii + itens[ii][jj]);
            }
        }
        */
        rvProduct.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new CartAdapter(this, this, _list, this);
        rvProduct.setAdapter(cartAdapter);
        pd.dismiss();
    }

    private String[][] getItens(List<Product> list) {
        int l = list.size();
        int i = 0;
        Double total;
        String[][] strings = new String[l][6];
        for (Product product : list) {
            strings[i][0] = String.valueOf(product.getId());
            strings[i][1] = product.getProductName();
            strings[i][2] = product.getType();
            strings[i][3] = String.valueOf(product.getUnit());
            strings[i][4] = String.valueOf(product.getProductPrice());
            total = product.getProductPrice() * product.getUnit();
            strings[i][5] = String.valueOf(total);
            i++;
        }
        return strings;
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

    public void setValuesToolbarBottom(String price) {
        tvPrice.setText(price);
    }

    public Double getValuesToolbarBottom() {
        price = Double.parseDouble(String.valueOf(tvPrice.getText()));
        return price;
    }

    protected List<Product> cursor2List(Cursor cursor){
        List<Product> products = new ArrayList<Product>();
        cursor.moveToFirst();
        do{
            Product product = new Product();
            product.setId(cursor.getString(cursor.getColumnIndex(database.ID)));
            product.setProductName(cursor.getString(cursor.getColumnIndex(database.NAME)));
            product.setType(cursor.getString(cursor.getColumnIndex(database.TYPE)));
            product.setUnit(cursor.getInt(cursor.getColumnIndex(database.UNIT)));
            //product.setProductImage(cursor.getString(cursor.getColumnIndex(database.IMAGE)));
            product.setBakeryId(cursor.getString(cursor.getColumnIndex(database.BAKERY_ID)));
            product.setItensSale(cursor.getInt(cursor.getColumnIndex(database.ITENS_SALE)));
            product.setProductPrice(cursor.getDouble(cursor.getColumnIndex(database.PRICE)));
            products.add(product);
            price = product.getProductPrice() * product.getUnit();
            priceTotal = priceTotal + price;

        } while(cursor.moveToNext());
        return products;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            // se foi uma tentativa de pagamento
            if(requestCode==PagSeguroPayment.PAG_SEGURO_REQUEST_CODE){
                // exibir confirmação de cancelamento
                final String msg = getString(R.string.transaction_cancelled);
                AppUtil.showConfirmDialog(this, msg, null);
            }
        } else if (resultCode == RESULT_OK) {
            // se foi uma tentativa de pagamento
            if(requestCode==PagSeguroPayment.PAG_SEGURO_REQUEST_CODE){
                // exibir confirmação de sucesso
                final String msg = getString(R.string.transaction_succeded);
                AppUtil.showConfirmDialog(this, msg, null);
                /*Intent intentSchedule = new Intent(ProductCartActivity.this, ScheduleActivity.class);
                params.putString("userID", userId);
                params.putString("bakeryID", bakeryId);
                intentSchedule.putExtras(params);
                startActivity(intentSchedule);*/
            }
        }
        else if(resultCode == PagSeguroPayment.PAG_SEGURO_REQUEST_CODE){
            switch (data.getIntExtra(PagSeguroPayment.PAG_SEGURO_EXTRA, 0)){
                case PagSeguroPayment.PAG_SEGURO_REQUEST_SUCCESS_CODE:{
                    final String msg =getString(R.string.transaction_succeded);
                    AppUtil.showConfirmDialog(this,msg,null);
                    /*Intent intentSchedule = new Intent(ProductCartActivity.this, ScheduleActivity.class);
                    params.putString("userID", userId);
                    params.putSerializable("itens", itens);
                    intentSchedule.putExtras(params);
                    startActivity(intentSchedule);*/
                    break;
                }
                case PagSeguroPayment.PAG_SEGURO_REQUEST_FAILURE_CODE:{
                    final String msg = getString(R.string.transaction_error);
                    AppUtil.showConfirmDialog(this,msg,null);
                    break;
                }
                case PagSeguroPayment.PAG_SEGURO_REQUEST_CANCELLED_CODE:{
                    final String msg = getString(R.string.transaction_cancelled);
                    AppUtil.showConfirmDialog(this,msg,null);
                    break;
                }
            }
        }
    }
}
