package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Offer;
import gregmachado.com.panappfirebase.domain.Product;

/**
 * Created by gregmachado on 20/11/16.
 */
public class FormOfferActivity extends CommonActivity {
    private static final String TAG = FormOfferActivity.class.getSimpleName();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Offer offer;
    private Resources resources;
    private EditText inputUnits;
    private ImageView ivImage, ivProductImage;
    private Spinner spProduct;
    private TextView tvProductName, tvPrice, tvPriceInOffer, tvDiscount;
    private String bakeryID, productID, productName, productType, productImage, bakeryName, offerID;
    private int items, percent;
    private Double productPrice, productPriceInOffer, perc;
    private boolean update, selected;
    private SeekBar seekBar;
    private List<String> products;
    private Button btnAddOffer;
    private DecimalFormat precision = new DecimalFormat("#0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_offer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_offer);
        setSupportActionBar(toolbar);
        setTitle("Nova Oferta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        spProduct = (Spinner) findViewById(R.id.spinner_product);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryID = params.getString("bakeryID");
            update = params.getBoolean("update");
            bakeryName = params.getString("bakeryName");
            if (update) {
                spProduct.setClickable(false);
                items = params.getInt("items");
                productID = params.getString("productID");
                productName = params.getString("productName");
                productType = params.getString("productType");
                productPrice = params.getDouble("productPrice");
                productImage = params.getString("productImage");
                offerID = params.getString("offerID");
                percent = params.getInt("percent", percent);
                setTitle(R.string.update_offer);
            }
        }
        populateSpinner();
        resources = getResources();
        initViews();
        offer = new Offer();
    }

    @Override
    protected void initViews() {
        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                callClearErrors(s);
            }
        };
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        ivImage = (ImageView) findViewById(R.id.img);
        ivProductImage = (ImageView) findViewById(R.id.iv_product);
        inputUnits = (EditText) findViewById(R.id.et_units);
        inputUnits.addTextChangedListener(textWatcher);
        btnAddOffer = (Button) findViewById(R.id.btn_save_offer);
        if (update) {
            btnAddOffer.setText(R.string.update_offer);
        }
        tvPrice = (TextView) findViewById(R.id.tv_product_old_price);
        tvPriceInOffer = (TextView) findViewById(R.id.tv_product_price_in_offer);
        tvProductName = (TextView) findViewById(R.id.tv_product_name);
        tvDiscount = (TextView) findViewById(R.id.tv_discount);
        spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                openProgressBar();
                ivProductImage.setVisibility(View.INVISIBLE);
                productName = parent.getItemAtPosition(position).toString();
                selected = true;
                mDatabaseReference.child("bakeries").child(bakeryID).child("products").
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot productSnap : dataSnapshot.getChildren()) {
                                    if (productSnap.child("productName").getValue(String.class).equals(productName)) {
                                        productImage = productSnap.child("productImage").getValue(String.class);
                                        productPrice = productSnap.child("productPrice").getValue(double.class);
                                        productID = productSnap.child("id").getValue(String.class);
                                        productType = productSnap.child("type").getValue(String.class);
                                        items = productSnap.child("itensSale").getValue(Integer.class);
                                        percent = 5;
                                        productPriceInOffer = productPrice * 0.95;
                                    }
                                }
                                fillLabels(productName, productPrice, productID, items,
                                        productPriceInOffer, percent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected = false;
            }
        });
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 5;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                tvDiscount.setText(String.valueOf(progressChanged));
                perc = (double) progressChanged / 100;
                perc = 1 - perc;
                productPriceInOffer = perc * productPrice;
                tvPriceInOffer.setText(precision.format(productPriceInOffer));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                percent = progressChanged;
            }
        });
    }

    private void populateSpinner() {
        mDatabaseReference.child("bakeries").child(bakeryID).child("products").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        products = new ArrayList<String>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String name = child.child("productName").getValue(String.class);
                            products.add(name);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                FormOfferActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                products);
                        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        spProduct.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e(TAG, firebaseError.getMessage());
                    }
                });
    }

    private void fillLabels(String productName, Double productPrice, String productID,
                            int items, Double productPriceInOffer, int percent) {

        tvPrice.setText(precision.format(productPrice));
        tvProductName.setText(productName);
        inputUnits.setText(String.valueOf(items));
        tvPriceInOffer.setText(precision.format(productPriceInOffer));
        tvDiscount.setText(String.valueOf(percent));
        StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
        StorageReference imageRef = mStorage.child(productID);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivProductImage.setImageBitmap(bitmap);
                ivProductImage.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.INVISIBLE);
                closeProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(inputUnits);
        }
    }

    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }

    private boolean validateFields() {
        String p = inputUnits.getText().toString().trim();
        if (!TextUtils.isEmpty(p)) {
            items = Integer.valueOf(inputUnits.getText().toString());
            return true;
        } else {
            inputUnits.setError(resources.getString(R.string.register_field_required));
        }
        return false;
    }

    public void saveOffer(View view) {
        if (validateFields()) {
            if (selected) {
                if (percent > 4){
                    offer.setProductName(productName);
                    offer.setProductPrice(productPrice);
                    offer.setType(productType);
                    offer.setItensSale(items);
                    offer.setBakeryId(bakeryID);
                    offer.setDiscount(percent);
                    offer.setPriceInOffer(productPriceInOffer);
                    offer.setProductImage(productImage);
                    offer.setBakeryName(bakeryName);
                    offer.setProductID(productID);
                    if (update) {
                        openProgressDialog("Aguarde...", "Atualizando Oferta");
                    } else {
                        openProgressDialog("Aguarde...", "Salvando Oferta");
                    }
                    saveProduct();
                } else {
                    showToast("Valor minímo de desconto 5%!");
                }
            } else {
                showToast("Selecione um produto!");
            }
        }
    }

    private void saveProduct() {
        closeProgressDialog();
        if (!update) {
            offerID = mDatabaseReference.push().getKey();
        }
        offer.setId(offerID);
        mDatabaseReference.child("offers").child(offerID).setValue(offer);
        Product product = setProduct();
        mDatabaseReference.child("bakeries").child(bakeryID).child("products").child(productID).
                setValue(product);
        finish();
    }

    private Product setProduct() {
        Product product = new Product();
        product.setOldPrice(productPrice);
        product.setProductName(productName);
        product.setId(productID);
        product.setBakeryId(bakeryID);
        product.setItensSale(items);
        product.setProductImage(productImage);
        product.setProductPrice(productPriceInOffer);
        product.setType(productType);
        product.setInOffer(true);
        product.setDiscount(percent);
        return product;
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
}
