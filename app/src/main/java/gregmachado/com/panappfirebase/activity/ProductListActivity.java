package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.viewHolder.ProductViewHolderUser;

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
    private TextView tvItens, tvPrice;
    private String userId;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();
    private FirebaseAuth firebaseAuth;
    private ImageView icProduct;
    private FloatingActionButton btnCart;
    private ArrayList<Product> productsToCart = new ArrayList<>();
    private String productName, category, image;
    private Double price, parcialPrice = 0.00;
    private int unit, items;
    private int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getLayoutInflater().inflate(R.layout.activity_product_admin, frameLayout);
        setContentView(R.layout.activity_product_list);
        firebaseAuth = FirebaseAuth.getInstance();
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
        if(productsToCart!=null){
            productsToCart.clear();
        }
        setValuesToolbarBottom("00", "00,00");
        parcialPrice = 0.00;
        mDatabaseReference.child("bakeries").child(bakeryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeProgressBar();
                if (dataSnapshot.hasChild("products")) {
                    FirebaseRecyclerAdapter<Product, ProductViewHolderUser> adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolderUser>(
                            Product.class,
                            R.layout.card_product_user,
                            ProductViewHolderUser.class,
                            //referencing the node where we want the database to store the data from our Object
                            mDatabaseReference.child("bakeries").child(bakeryId).child("products").getRef()
                    ) {
                        @Override
                        protected void populateViewHolder(final ProductViewHolderUser viewHolder, final Product model, final int position) {
                            if (tvNoProducts.getVisibility() == View.VISIBLE) {
                                tvNoProducts.setVisibility(View.GONE);
                            }
                            if (icProduct.getVisibility() == View.VISIBLE) {
                                icProduct.setVisibility(View.GONE);
                            }
                            viewHolder.tvProductName.setText(model.getProductName());
                            viewHolder.tvPrice.setText(String.valueOf(model.getProductPrice()));
                            viewHolder.tvCategory.setText(model.getType());
                            viewHolder.tvItensSale.setText(String.valueOf(model.getItensSale()));

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.w(TAG, "You clicked on " + model.getProductName());
                                }
                            });

                            viewHolder.btnPlus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    increase(viewHolder, model.getItensSale());
                                }
                            });
                            viewHolder.btnLess.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    decrease(viewHolder);
                                }
                            });
                            viewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(viewHolder.btnAddCart.getText().equals("Adicionar")){
                                        Product product = new Product();
                                        product.setProductName(model.getProductName());
                                        product.setProductPrice(model.getProductPrice());
                                        product.setType(model.getType());
                                        product.setBakeryId(model.getBakeryId());
                                        product.setProductImage(model.getProductImage());
                                        product.setItensSale(model.getItensSale());
                                        product.setId(model.getId());
                                        items = getValue(viewHolder);
                                        product.setUnit(items);
                                        price = model.getProductPrice() * items;
                                        if (items > 0){
                                            productsToCart.add(product);
                                            viewHolder.btnAddCart.setText("Remover");
                                            viewHolder.btnAddCart.setBackgroundResource(R.color.redButton);

                                            count ++;
                                            parcialPrice = parcialPrice + price;
                                            setValuesToolbarBottom(String.valueOf(count), String.valueOf(parcialPrice));

                                            viewHolder.btnPlus.setEnabled(false);
                                            viewHolder.btnLess.setEnabled(false);
                                        }
                                    } else {
                                        Iterator<Product> it = productsToCart.iterator();
                                        while(it.hasNext()){
                                            if(it.next().getId().equals(model.getId())){
                                                it.remove();
                                                items = getValue(viewHolder);
                                                price = model.getProductPrice() * items;
                                            }
                                        }
                                        viewHolder.btnAddCart.setText("Adicionar");
                                        viewHolder.btnAddCart.setBackgroundResource(R.color.colorPrimary);

                                        count --;
                                        parcialPrice = parcialPrice - price;
                                        setValuesToolbarBottom(String.valueOf(count), String.valueOf(parcialPrice));

                                        viewHolder.btnPlus.setEnabled(true);
                                        viewHolder.btnLess.setEnabled(true);
                                    }
                                }
                            });
                        }
                    };
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
            userId = params.getString("userID");
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
    }

    public void openBasket(View view) {
        Intent i = new Intent(ProductListActivity.this, ProductCartActivity.class);
        params.putString("userID", userId);
        params.putString("bakeryID", bakeryId);
        params.putDouble("total", parcialPrice);
        i.putExtra("cart", productsToCart);
        i.putExtras(params);
        startActivity(i);
    }

    private  class SearchFilter implements SearchView.OnQueryTextListener{
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

    public void setValuesToolbarBottom(String items, String price){
        tvItens.setText(items);
        tvPrice.setText(price);
    }

    protected void decrease(ProductViewHolderUser productViewHolder) {
        if (isValid(productViewHolder)) {
            int value = getValue(productViewHolder)-1;
            productViewHolder.tvUnitSale.setText(String.valueOf(value));
        }
    }

    private void refresh(ProductViewHolderUser productViewHolder, int value) {
        productViewHolder.tvUnitSale.setText("0");
    }
    protected void increase(ProductViewHolderUser productViewHolder, Integer itensSale) {
        int value = getValue(productViewHolder)+1;
        if(value <= itensSale){
            productViewHolder.tvUnitSale.setText(String.valueOf(value));
        }
    }

    private int getValue(ProductViewHolderUser productViewHolder) {
        String value = productViewHolder.tvUnitSale.getText().toString();
        return (!value.equals("")) ? Integer.valueOf(value) : 0;
    }

    private boolean isValid(ProductViewHolderUser productViewHolder) {
        return (getValue(productViewHolder) > 0);
    }
}