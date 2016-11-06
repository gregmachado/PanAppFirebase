package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.viewHolder.ProductViewHolder;

/**
 * Created by gregmachado on 30/10/16.
 */
public class ProductAdminActivity extends CommonActivity {

    private static final String TAG = ProductAdminActivity.class.getSimpleName();
    private RecyclerView rvProductAdmin;
    private String bakeryId;
    private TextView tvNoProducts;
    private ImageView icProduct;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getLayoutInflater().inflate(R.layout.activity_product_admin, frameLayout);
        setContentView(R.layout.activity_product_admin);
        initViews();
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
        }
        mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product);
        setSupportActionBar(toolbar);
        setTitle("Meus Produtos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvNoProducts = (TextView) findViewById(R.id.tv_no_products_admin);
        icProduct = (ImageView) findViewById(R.id.ic_product);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        rvProductAdmin = (RecyclerView) findViewById(R.id.rv_product_admin);
        if (rvProductAdmin != null) {
            //to enable optimization of recyclerview
            rvProductAdmin.setHasFixedSize(true);
        }
        rvProductAdmin.setItemAnimator(new DefaultItemAnimator());
        //registerForContextMenu(rvProductAdmin);
        rvProductAdmin.setLayoutManager(new LinearLayoutManager(ProductAdminActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "ID: " + bakeryId);
        openProgressBar();
        mDatabaseReference.child("bakeries").child(bakeryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeProgressBar();
                if (dataSnapshot.hasChild("products")) {
                    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                            Product.class,
                            R.layout.card_product,
                            ProductViewHolder.class,
                            //referencing the node where we want the database to store the data from our Object
                            mDatabaseReference.child("bakeries").child(bakeryId).child("products").getRef()
                    ) {
                        @Override
                        protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, final int position) {
                            if (tvNoProducts.getVisibility() == View.VISIBLE) {
                                tvNoProducts.setVisibility(View.GONE);
                            }
                            if (icProduct.getVisibility() == View.VISIBLE) {
                                icProduct.setVisibility(View.GONE);
                            }
                            viewHolder.tvProductName.setText(model.getProductName());
                            viewHolder.tvUnits.setText(String.valueOf(model.getUnit()));
                            viewHolder.tvItensSale.setText(String.valueOf(model.getItensSale()));
                            viewHolder.tvProductType.setText(model.getType());
                            viewHolder.tvProductPrice.setText(String.valueOf(model.getProductPrice()));
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
                                    Log.w(TAG, "You clicked on " + model.getProductName());
                                }
                            });
                            viewHolder.btnPlus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    increase(viewHolder);
                                }
                            });
                            viewHolder.btnLess.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    decrease(viewHolder);
                                }
                            });
                            viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    //setPosition(productViewHolder.getPosition());
                                    return false;
                                }
                            });
                            viewHolder.btnSale.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String productID = model.getId();
                                    int unit = model.getItensSale();
                                    Integer items = getValue(viewHolder);
                                    Log.w(TAG, "id produto: " + productID);
                                    if (items > 0) {
                                        items = items + unit;
                                        mDatabaseReference.child("bakeries").child(bakeryId).child("products")
                                                .child(productID).child("itensSale").setValue(items);
                                        refresh(viewHolder, items);
                                    }
                                }
                            });
                        }
                    };
                    rvProductAdmin.setAdapter(adapter);
                } else {
                    tvNoProducts.setVisibility(View.VISIBLE);
                    icProduct.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
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

    public void newProduct(View view) {
        params.putString("bakeryID", bakeryId);
        params.putBoolean("update", false);
        Intent intentFormProduct = new Intent(ProductAdminActivity.this, FormProductActivity.class);
        intentFormProduct.putExtras(params);
        startActivity(intentFormProduct);
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

    protected void decrease(ProductViewHolder productViewHolder) {
        if (isValid(productViewHolder)) {
            int value = getValue(productViewHolder)-1;
            productViewHolder.tvUnits.setText(String.valueOf(value));
        }
    }

    protected void increase(ProductViewHolder productViewHolder) {
        int value = getValue(productViewHolder)+1;
        productViewHolder.tvUnits.setText(String.valueOf(value));
    }

    private int getValue(ProductViewHolder productViewHolder) {
        String value = productViewHolder.tvUnits.getText().toString();
        return (!value.equals("")) ? Integer.valueOf(value) : 0;
    }

    private boolean isValid(ProductViewHolder productViewHolder) {
        return (getValue(productViewHolder) > 0);
    }

    private void refresh(ProductViewHolder productViewHolder, int value) {
        productViewHolder.tvUnits.setText("0");
        //productViewHolder.tvItensSale.setText(String.valueOf(value));
    }
}