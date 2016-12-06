package gregmachado.com.panappfirebase.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.util.ImagePicker;

/**
 * Created by gregmachado on 30/10/16.
 */
public class FormProductActivity extends CommonActivity {

    private Spinner spTypeProduct;
    private EditText inputNameProduct, inputPriceProduct;
    private String strPrice, productName, productType, productImage;
    private String productID;
    private Double productPrice;
    private Resources resources;
    private static final String TAG = FormProductActivity.class.getSimpleName();
    private String bakeryID;
    private Integer items;
    private Product product;
    private ImageView imageProduct, imageAddPhoto;
    private ArrayAdapter<String> dataAdapter;
    private static final int PICK_IMAGE_ID = 234;
    private byte[] image;
    private Boolean update, noPhoto;
    private List<Product> products;
    private StorageReference mStorageRef;
    private TextView tvAddPhoto;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_product);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryID = params.getString("bakeryID");
            update = params.getBoolean("update");
        }
        noPhoto = true;
        initViews();
        product = new Product();
        resources = getResources();
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_product);
        setSupportActionBar(toolbar);
        setTitle("Novo Produto");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        imageProduct = (ImageView) findViewById(R.id.img_product);
        imageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(FormProductActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });

        inputNameProduct = (EditText) findViewById(R.id.et_product_name);
        inputNameProduct.addTextChangedListener(textWatcher);
        inputPriceProduct = (EditText) findViewById(R.id.et_product_price);
        tvAddPhoto = (TextView) findViewById(R.id.tv_add_photo);
        imageAddPhoto = (ImageView) findViewById(R.id.img_add_photo);
        spTypeProduct = (Spinner) findViewById(R.id.spinner_category);
        Button btnAddProduct = (Button) findViewById(R.id.btn_save_product);
        String[] categories = getResources().getStringArray(R.array.category);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, categories);
        spTypeProduct.setAdapter(dataAdapter);
        spTypeProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (update) {
            items = params.getInt("items");
            productID = params.getString("productID");
            productName = params.getString("productName");
            productType = params.getString("productType");
            productPrice = params.getDouble("productPrice");
            productImage = params.getString("productImage");
            setTitle("Editar Produto");
            btnAddProduct.setText("ATUALIZAR PRODUTO");
            fillLabels(productName, productPrice, productType);
        }
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    product.setProductName(productName);
                    product.setProductPrice(productPrice);
                    product.setType(productType);
                    if (update){
                        product.setItensSale(items);
                    } else {
                        product.setItensSale(0);
                    }
                    product.setBakeryId(bakeryID);
                    if (update) {
                        openProgressDialog("Aguarde...", "Atualizando Produto");
                    } else {
                        openProgressDialog("Aguarde...", "Salvando Produto");
                    }
                    uploadImageAndSaveProduct();
                }
            }
        });
    }

    private void uploadImageAndSaveProduct() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (update) {
            noPhoto = false;
            product.setId(productID);
        } else {
            productID = mDatabaseReference.push().getKey();
            product.setId(productID);
        }
        mStorageRef = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com").child(productID);
        if (noPhoto) {
            saveProduct();

        } else {
            mStorageRef.delete().addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                }
            });
        }
        imageProduct.setDrawingCacheEnabled(true);
        imageProduct.buildDrawingCache();
        Bitmap bitmap = imageProduct.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                closeProgressDialog();
                showToast("Erro ao gravar imagem!");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                if (downloadUrl != null) {
                    product.setProductImage(downloadUrl.toString());
                }
                saveProduct();
            }
        });
    }

    private void saveProduct() {
        closeProgressDialog();
        if (update) {
            product.setItensSale(items);
        }
        mDatabaseReference.child("bakeries").child(bakeryID).child("products").child(productID).setValue(product);
        if (update){
            showToast("Produto " + productName + " atualizado!");
        } else {
            showToast("Produto " + productName + " adicionado!");
        }
        finish();
    }

    private void fillLabels(String productName, Double productPrice, String productType) {
        inputNameProduct.setText(productName);
        inputPriceProduct.setText(String.valueOf(productPrice));
        //spTypeProduct.setText(productType);
        tvAddPhoto.setVisibility(View.INVISIBLE);
        imageAddPhoto.setVisibility(View.INVISIBLE);
        StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
        StorageReference imageRef = mStorage.child(productID);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageProduct.setImageBitmap(bitmap);
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
            clearErrorFields(inputNameProduct);
        }
    }

    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }

    private boolean validateFields() {
        String p = inputPriceProduct.getText().toString().trim();
        if (!TextUtils.isEmpty(p)) {
            productName = inputNameProduct.getText().toString().trim();
            productPrice = Double.parseDouble(inputPriceProduct.getText().toString().trim());
            return (!isEmptyFields(productName, productPrice, productType));
        } else {
            inputPriceProduct.setError(resources.getString(R.string.price_product_required));
        }
        return false;
    }

    private boolean isEmptyFields(String name, Double price, String productType) {
        String priceAux = price.toString();
        if (TextUtils.isEmpty(name)) {
            inputNameProduct.requestFocus(); //seta o foco para o campo name
            inputNameProduct.setError(resources.getString(R.string.register_name_required));
            return true;
        } else if (TextUtils.isEmpty(priceAux)) {
            inputPriceProduct.requestFocus(); //seta o foco para o campo email
            inputPriceProduct.setError(resources.getString(R.string.price_product_required));
            return true;
        } else if (TextUtils.isEmpty(productType)) {
            spTypeProduct.requestFocus();
            //spTypeProduct.setError(resources.getString(R.string.register_field_required));
            return true;
        }
        return false;
    }

    public void loadPhoto(String localPhoto) {
        Bitmap imagePhoto = BitmapFactory.decodeFile(localPhoto);
        //Gerar imagem reduzida
        Bitmap reducedImagePhoto = Bitmap.createScaledBitmap(imagePhoto, 200, 150, true);
        product.setProductImage(localPhoto);
        imageProduct.setImageBitmap(reducedImagePhoto);
    }

    public void setProduct(Product product) {
        inputNameProduct.setText(product.getProductName());
        inputPriceProduct.setText(String.valueOf(product.getProductPrice()));
        String compareValue = product.getType();
        if (!compareValue.equals(null)) {
            int spinnerPostion = dataAdapter.getPosition(compareValue);
            spTypeProduct.setSelection(spinnerPostion);
            spinnerPostion = 0;
        }
        this.product = product;
        if (product.getProductImage() != null) {
            loadPhoto(product.getProductImage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_IMAGE_ID:
                if (resultCode == Activity.RESULT_CANCELED){
                    return;
                } else {
                    Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                    Bitmap reducedImagePhoto = Bitmap.createScaledBitmap(bitmap, 200, 150, true);
                    imageProduct.setImageBitmap(reducedImagePhoto);
                    tvAddPhoto.setVisibility(View.INVISIBLE);
                    imageAddPhoto.setVisibility(View.INVISIBLE);
                    break;
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
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
}
