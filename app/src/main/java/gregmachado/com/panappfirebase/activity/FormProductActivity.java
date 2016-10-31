package gregmachado.com.panappfirebase.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.util.ImagePicker;
import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 30/10/16.
 */
public class FormProductActivity extends AppCompatActivity {

    private Spinner spTypeProduct;
    private EditText inputNameProduct, inputPriceProduct;
    private String strPrice, productName, productType, strBakeryId, strImage;
    private String localFile;
    private Double productPrice;
    private Resources resources;
    private static final String TAG = FormProductActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private String bakeryID, id;
    private Integer items;
    private Product product;
    private ImageView imageProduct;
    private ArrayAdapter<String> dataAdapter;
    private static final int PICK_IMAGE_ID = 234;
    private byte[] image;
    private Boolean update;
    private Bundle params;
    private DatabaseReference mDatabaseReference;
    private List<Product> products;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_product);
        setSupportActionBar(toolbar);
        setTitle("Novo Produto");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent it = getIntent();
        params = it.getExtras();
        mDatabaseReference = LibraryClass.getFirebase();
        mDatabaseReference.getRef();

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
        spTypeProduct = (Spinner) findViewById(R.id.sp_product_type);
        Button btnAddProduct = (Button) findViewById(R.id.btn_save_product);
        List<String> categories = new ArrayList<String>();
        categories.add("Pães");
        categories.add("Bolos");
        categories.add("Cucas");
        categories.add("Bebidas");
        categories.add("Salgados");
        categories.add("Doces");
        categories.add("Frios");
        categories.add("Biscoitos");
        categories.add("Outros");
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, categories);
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
        if (params != null) {
            bakeryID = params.getString("bakeryID");
            update = params.getBoolean("update");
            if (update) {
                items = params.getInt("items");
                id = params.getString("productID");
                productName = params.getString("productName");
                productType = params.getString("productType");
                strImage = params.getString("productImage");
                productPrice = params.getDouble("productPrice");
                setTitle("Editar Produto");
                btnAddProduct.setText("ATUALIZAR PRODUTO");
                fillLabels(strImage, productName, productPrice, productType);
            }
        }
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    product = new Product();
                    product.setProductName(productName);
                    product.setProductPrice(productPrice);
                    product.setType(productType);
                    product.setItensSale(0);
                    if (update) {

                    } else {
                        String productID = mDatabaseReference.push().getKey();
                        product.setId(productID);
                        mDatabaseReference.child("bakeries").child(bakeryID).child("products").child(productID).setValue(product);
                        finish();
                    }
                }
            }
        });
    }

    private void fillLabels(String strImage, String productName, Double productPrice, String productType) {
        inputNameProduct.setText(productName);
        inputPriceProduct.setText(String.valueOf(productPrice));
        byte[] imgBytes = Base64.decode(strImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        imageProduct.setImageBitmap(bitmap);
        if (!(productType == null)) {
            int spinnerPostion = dataAdapter.getPosition(productType);
            spTypeProduct.setSelection(spinnerPostion);
            spinnerPostion = 0;
        }
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
        productName = inputNameProduct.getText().toString().trim();
        productPrice = Double.valueOf(inputPriceProduct.getText().toString().trim());
        return (!isEmptyFields(productName, productPrice));
    }

    private boolean isEmptyFields(String name, Double price) {
        String priceAux = price.toString();
        if (TextUtils.isEmpty(name)) {
            inputNameProduct.requestFocus(); //seta o foco para o campo name
            inputNameProduct.setError(resources.getString(R.string.register_name_required));
            return true;
        } else if (TextUtils.isEmpty(priceAux)) {
            inputPriceProduct.requestFocus(); //seta o foco para o campo email
            inputPriceProduct.setError(resources.getString(R.string.price_product_required));
            return true;
        }
        return false;
    }

    public void loadPhoto(String localPhoto) {
        Bitmap imagePhoto = BitmapFactory.decodeFile(localPhoto);

        //Gerar imagem reduzida
        Bitmap reducedImagePhoto = Bitmap.createScaledBitmap(imagePhoto, 150, 200, true);

        product.setProductImage(localPhoto);

        imageProduct.setImageBitmap(reducedImagePhoto);
    }

    public void setProduct(Product product) {
        inputNameProduct.setText(product.getProductName());
        inputPriceProduct.setText(product.getProductPrice().toString());
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

    public ImageView getPhoto() {
        return imageProduct;
    }

    public byte[] convertImageToByte(ImageView imageProduct) {

        Bitmap bitmap = ((BitmapDrawable) imageProduct.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        image = stream.toByteArray();

        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                imageProduct.setImageBitmap(bitmap);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }
}
