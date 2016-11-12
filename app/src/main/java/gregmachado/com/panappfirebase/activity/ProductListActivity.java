package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.ProductAdapter;
import gregmachado.com.panappfirebase.domain.Product;

/**
 * Created by gregmachado on 30/10/16.
 */
public class ProductListActivity extends CommonActivity {

    private static final String TAG = ProductListActivity.class.getSimpleName();
    private RecyclerView rvProduct;
    private String bakeryId, id;
    private Bundle params;
    private TextView tvNoProducts;
    private List<Product> list;
    private ArrayList<Product> listCart;
    private Product product;
    private String name;
    public TextView tvItens, tvPrice;
    private ImageView icProduct;
    private FloatingActionButton btnCart;
    private ArrayList<Product> productsToCart = new ArrayList<>();
    private String productName, category, image;
    private Double price, parcialPrice = 0.00;
    private int unit, items;
    private int count = 0;
    private Handler handler;
    private TextView tvUnits;
    private ProductAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        initViews();
        if (rvProduct != null) {
            //to enable optimization of recyclerview
            rvProduct.setHasFixedSize(true);
        }
        rvProduct.setItemAnimator(new DefaultItemAnimator());
        //registerForContextMenu(rvProduct);
        rvProduct.setLayoutManager(new LinearLayoutManager(ProductListActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        openProgressBar();
        if (productsToCart != null) {
            productsToCart.clear();
        }
        setValuesToolbarBottom("00", "00,00");
        parcialPrice = 0.00;
        mDatabaseReference.child("bakeries").child(bakeryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeProgressBar();
                if (dataSnapshot.hasChild("products")) {
                    if (tvNoProducts.getVisibility() == View.VISIBLE) {
                        tvNoProducts.setVisibility(View.GONE);
                    }
                    if (icProduct.getVisibility() == View.VISIBLE) {
                        icProduct.setVisibility(View.GONE);
                    }
                    adapter = new ProductAdapter(mDatabaseReference.child("bakeries").child(bakeryId).child("products").getRef(),
                            ProductListActivity.this, ProductListActivity.this, bakeryId
                    ) {};
                    rvProduct.setAdapter(adapter);
                } else {
                    tvNoProducts.setVisibility(View.VISIBLE);
                    icProduct.setVisibility(View.VISIBLE);
                    btnCart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        SearchView mSearchView = new SearchView(this);
        //Define um texto de ajuda:
        mSearchView.setQueryHint("Digite aqui...");

        // exemplos de utilização:
        mSearchView.setOnQueryTextListener(new SearchFilter());
        return true;
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
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product);
        setSupportActionBar(toolbar);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
            name = params.getString("name");
        }
        setTitle("Produtos - " + name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvNoProducts = (TextView) findViewById(R.id.tv_no_products);
        icProduct = (ImageView) findViewById(R.id.ic_product);
        tvItens = (TextView) findViewById(R.id.tv_units);
        tvPrice = (TextView) findViewById(R.id.tv_total_price);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        rvProduct = (RecyclerView) findViewById(R.id.rv_product);
        btnCart = (FloatingActionButton) findViewById(R.id.btn_cart);
    }

    public void openBasket(View view) {
        Intent i = new Intent(ProductListActivity.this, ProductCartActivity.class);
        params.putString("bakeryID", bakeryId);
        params.putDouble("total", parcialPrice);
        productsToCart = adapter.getProductsToCart();
        i.putExtra("cart", productsToCart);
        i.putExtras(params);
        startActivity(i);
    }

    private class SearchFilter implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            Log.i("Script", "onQueryTextChange" + newText);
            return false;
        }
    }

    public void setValuesToolbarBottom(String items, String price) {
        tvItens.setText(items);
        tvPrice.setText(price);
    }
}