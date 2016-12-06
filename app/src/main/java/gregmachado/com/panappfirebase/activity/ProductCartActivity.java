package gregmachado.com.panappfirebase.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.CartAdapter;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.interfaces.ItemClickListener;

/**
 * Created by gregmachado on 02/10/16.
 */
public class ProductCartActivity extends CommonActivity implements ItemClickListener {

    private static final String TAG = ProductCartActivity.class.getSimpleName();
    private RecyclerView rvProduct;
    private CartAdapter cartAdapter;
    private ArrayList<Product> _list;
    private TextView tvItens, tvPrice, tvEmptyCart;
    private Double priceTotal, price;
    private Bundle params;
    private String bakeryId, productID, userName, bakeryName, offerID;
    private int items, itemsSale;
    private ImageView icCart;
    private CardView cardView;
    private Button btnFinish;
    private DecimalFormat precision = new DecimalFormat("#0.00");
    private boolean isOffer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
            userName = params.getString("userName");
            bakeryName = params.getString("bakeryName");
            offerID = params.getString("offerID");
            priceTotal = params.getDouble("total");
            isOffer = params.getBoolean("isOffer");
            _list = (ArrayList<Product>)
                    getIntent().getSerializableExtra("cart");
        }
        initViews();
        loadCartList();
    }

    @Override
    public void onClick(View view, int position) {
        final Product product = _list.get(position);
        Log.w(TAG, "Produto: " + product.getProductName());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void loadCartList() {
        if (!(_list.isEmpty()) & !(_list == null)) {
            setValuesToolbarBottom(priceTotal);
            rvProduct.setLayoutManager(new LinearLayoutManager(this));
            cartAdapter = new CartAdapter(this, this, _list, this, priceTotal);
            rvProduct.setAdapter(cartAdapter);
        } else {
            tvEmptyCart.setVisibility(View.VISIBLE);
            icCart.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.INVISIBLE);
            btnFinish.setVisibility(View.INVISIBLE);
        }
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

    public void setValuesToolbarBottom(double price) {
        tvPrice.setText(precision.format(price));
    }

    public double getValuesToolbarBottom() {
        price = Double.valueOf(String.valueOf(tvPrice.getText()));
        return price;
    }

    private void callScheduleActivity() {
        Intent intentSchedule = new Intent(ProductCartActivity.this, ScheduleActivity.class);
        ArrayList<Product> requestItems = cartAdapter.getProducts();
        params.putString("userName", userName);
        params.putString("bakeryName", bakeryName);
        params.putString("bakeryID", bakeryId);
        params.putDouble("total", priceTotal);
        params.putString("offerID", offerID);
        params.putBoolean("isOffer", isOffer);
        intentSchedule.putExtras(params);
        intentSchedule.putExtra("items", requestItems);
        startActivity(intentSchedule);
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        setTitle("Meu carrinho");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvItens = (TextView) findViewById(R.id.tv_units);
        tvPrice = (TextView) findViewById(R.id.tv_total_price_cart);
        rvProduct = (RecyclerView) findViewById(R.id.product_cart_list);
        rvProduct.setItemAnimator(new DefaultItemAnimator());
        registerForContextMenu(rvProduct);
        tvEmptyCart = (TextView) findViewById(R.id.tv_empty_cart);
        icCart = (ImageView) findViewById(R.id.ic_cart);
        cardView = (CardView) findViewById(R.id.card_total);
        btnFinish = (Button) findViewById(R.id.btn_finish);
    }

    public void finishBuy(View view) {
        // at this point you should check if the user has internet connection
        // before stating the pagseguro checkout process.(it will need internet connection)

        // simulating an user buying
        if (!(_list.isEmpty()) & !(_list == null)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Finalizar?");
            builder.setMessage("Deseja finalizar a compra?");
            builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    for (Product productAux : _list) {
                        productID = productAux.getId();
                        items = productAux.getUnit();
                        itemsSale = productAux.getItensSale();
                        items = itemsSale - items;
                        if (!isOffer){
                            mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).
                                    child("itensSale").setValue(items);
                        }
                    }
                    callScheduleActivity();
                }
            });
            builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            showToast("Carrinho vazio!");
        }
    }

    @Override
    public void onBackPressed() {
        if (_list.isEmpty() || _list == null) {
            finish();
        } else {
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
    }
}
