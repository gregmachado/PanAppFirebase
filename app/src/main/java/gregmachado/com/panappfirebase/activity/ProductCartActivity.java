package gregmachado.com.panappfirebase.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    private List<Product> _list;
    private TextView tvItens, tvPrice;
    private Double priceTotal = 0.00, price;
    private Bundle params;
    private String bakeryId, productID, userName, bakeryName;
    private int items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
            userName = params.getString("userName");
            bakeryName = params.getString("bakeryName");
            priceTotal = params.getDouble("total");
            _list = (ArrayList<Product>)
                    getIntent().getSerializableExtra("cart");
        }
        loadCartList();
        setValuesToolbarBottom(String.valueOf(priceTotal));
    }

    @Override
    public void onClick(View view, int position) {
        final Product product = _list.get(position);
        Toast.makeText(this, "Produto: " + product.getProductPrice(), Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void loadCartList() {
        setValuesToolbarBottom(String.valueOf(priceTotal));
        rvProduct.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, this, _list, this);
        rvProduct.setAdapter(cartAdapter);
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

    private void callScheduleActivity() {
        Intent intentSchedule = new Intent(ProductCartActivity.this, ScheduleActivity.class);
        ArrayList<Product> requestItems = cartAdapter.getProducts();
        params.putString("userName", userName);
        params.putString("bakeryName", bakeryName);
        params.putString("bakeryID", bakeryId);
        params.putDouble("total", priceTotal);
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
    }

    public void finishBuy(View view) {
        // at this point you should check if the user has internet connection
        // before stating the pagseguro checkout process.(it will need internet connection)

        // simulating an user buying
        if(!(_list.isEmpty())&!(_list==null)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Finalizar?");
            builder.setMessage("Deseja finalizar a compra?");
            builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    for (Product productAux : _list) {
                        productID = productAux.getId();
                        items = productAux.getUnit();
                        mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int itemsSale = Integer.parseInt(dataSnapshot.child("itensSale").getValue().toString());
                                items = itemsSale - items;
                                mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).child("itensSale").setValue(items);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });
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
        if(_list.isEmpty()||_list==null){
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
