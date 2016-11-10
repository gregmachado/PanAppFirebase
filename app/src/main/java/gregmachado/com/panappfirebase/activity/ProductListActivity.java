package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    public TextView tvItens, tvPrice;
    private ImageView icProduct;
    private FloatingActionButton btnCart;
    private ArrayList<Product> productsToCart = new ArrayList<>();
    private String productName, category, image;
    private Double price, parcialPrice = 0.00;
    private int unit, items;
    private int count = 0;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorage;
    private Handler handler;
    private TextView tvUnits;


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
        mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
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
                            StorageReference imageRef = mStorage.child(model.getId());
                            final long ONE_MEGABYTE = 1024 * 1024;
                            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    viewHolder.ivProduct.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.w(TAG, "You clicked on " + model.getProductPrice());
                                }
                            });

                            viewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialoglayout = inflater.inflate(R.layout.dialog_units, null);
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProductListActivity.this);
                                    final AlertDialog dialog = builder.create();
                                    dialog.setView(dialoglayout);

                                    tvUnits = (TextView) dialoglayout.findViewById(R.id.tv_unit_sale);
                                    ImageButton btnPlus = (ImageButton) dialoglayout.findViewById(R.id.btn_plus);
                                    btnPlus.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            increase(viewHolder, Integer.valueOf(viewHolder.tvItensSale.getText().toString()));
                                        }
                                    });
                                    btnPlus.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            switch(event.getAction()) {

                                                case MotionEvent.ACTION_DOWN:
                                                    if (handler != null) return true;
                                                    handler = new Handler();
                                                    handler.postDelayed(actionPlus, 100);
                                                    break;

                                                case MotionEvent.ACTION_UP:
                                                    if (handler == null) return true;
                                                    handler.removeCallbacks(actionPlus);
                                                    handler = null;
                                                    break;
                                            }
                                            return false;
                                        }
                                        Runnable actionPlus = new Runnable() {

                                            @Override
                                            public void run() {
                                                increase(viewHolder, Integer.valueOf(viewHolder.tvItensSale.getText().toString()));
                                                handler.postDelayed(this, 100);
                                            }
                                        };
                                    });
                                    ImageButton btnLess = (ImageButton) dialoglayout.findViewById(R.id.btn_less);
                                    btnLess.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            decrease(viewHolder);
                                        }
                                    });
                                    btnLess.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            switch(event.getAction()) {

                                                case MotionEvent.ACTION_DOWN:
                                                    if (handler != null) return true;
                                                    handler = new Handler();
                                                    handler.postDelayed(actionLess, 100);
                                                    break;

                                                case MotionEvent.ACTION_UP:
                                                    if (handler == null) return true;
                                                    handler.removeCallbacks(actionLess);
                                                    handler = null;
                                                    break;
                                            }
                                            return false;
                                        }
                                        Runnable actionLess = new Runnable() {

                                            @Override
                                            public void run() {
                                                decrease(viewHolder);
                                                handler.postDelayed(this, 100);
                                            }
                                        };
                                    });
                                    Button btnCancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
                                    btnCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    Button btnAddToCart = (Button) dialoglayout.findViewById(R.id.btn_add_cart);
                                    btnAddToCart.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Product product = new Product();
                                            product.setProductName(model.getProductName());
                                            product.setProductPrice(model.getProductPrice());
                                            product.setType(model.getType());
                                            product.setBakeryId(model.getBakeryId());
                                            product.setProductImage(model.getProductImage());
                                            product.setId(model.getId());
                                            items = getValue(viewHolder);
                                            product.setUnit(items);
                                            price = model.getProductPrice() * items;
                                            if (items > 0) {
                                                productsToCart.add(product);
                                                viewHolder.btnAddCart.setVisibility(View.INVISIBLE);
                                                viewHolder.btnRemoveCart.setVisibility(View.VISIBLE);

                                                count++;
                                                parcialPrice = parcialPrice + price;
                                                setValuesToolbarBottom(String.valueOf(count), String.valueOf(parcialPrice));

                                                viewHolder.tvUnitInCart.setText(String.valueOf(items));
                                                viewHolder.llCart.setVisibility(View.VISIBLE);
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                    dialog.show();
                                }
                            });
                            viewHolder.btnRemoveCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Iterator<Product> it = productsToCart.iterator();
                                    while (it.hasNext()) {
                                        if (it.next().getId().equals(model.getId())) {
                                            it.remove();
                                            items = getValue(viewHolder);
                                            price = model.getProductPrice() * items;
                                        }
                                    }
                                    viewHolder.btnAddCart.setVisibility(View.VISIBLE);
                                    viewHolder.btnRemoveCart.setVisibility(View.INVISIBLE);

                                    count--;
                                    parcialPrice = parcialPrice - price;
                                    setValuesToolbarBottom(String.valueOf(count), String.valueOf(parcialPrice));

                                    viewHolder.llCart.setVisibility(View.INVISIBLE);
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
        params.putString("bakeryID", bakeryId);
        params.putDouble("total", parcialPrice);
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

    protected void decrease(ProductViewHolderUser productViewHolder) {
        if (isValid(productViewHolder)) {
            int value = getValue(productViewHolder)-1;
            tvUnits.setText(String.valueOf(value));
        }
    }

    protected void increase(ProductViewHolderUser productViewHolder, Integer itensSale) {
        int value = getValue(productViewHolder)+1;
        if(value <= itensSale){
            tvUnits.setText(String.valueOf(value));
        }
    }

    private int getValue(ProductViewHolderUser productViewHolder) {
        String value = tvUnits.getText().toString();
        return (!value.equals("")) ? Integer.valueOf(value) : 0;
    }

    private boolean isValid(ProductViewHolderUser productViewHolder) {
        return (getValue(productViewHolder) > 0);
    }
}