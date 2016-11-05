package gregmachado.com.panappfirebase.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Adress;
import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 09/10/16.
 */
public class FormAdressActivity extends CommonActivity {

    private static final String TAG = FormProductActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private EditText inputNameAdress, inputCep, inputStreet, inputNumber, inputComplement, inputDistrict, inputCity, inputState, inputReference;
    private String cep, adressName, street, complement, district, city, state, reference;
    private Resources resources;
    private String userId;
    private Integer number;
    private Boolean update = false;
    private DatabaseReference mDatabaseReference;
    private Double latitude, longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_adress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_adress);
        setSupportActionBar(toolbar);
        setTitle("Novo Endereço");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDatabaseReference = LibraryClass.getFirebase();
        mDatabaseReference.getRef();
        initViews();
        Intent it = getIntent();
        params = it.getExtras();

        if (params != null) {
            userId = params.getString("userID");
            update = params.getBoolean("update");
            if (update) {
                /*items = params.getInt("items");
                id = params.getLong("productId");
                productName = params.getString("productName");
                productType = params.getString("productType");
                strImage = params.getString("productImage");
                productPrice = params.getDouble("productPrice");
                setTitle("Editar Produto");
                btnAddProduct.setText("ATUALIZAR PRODUTO");
                fillLabels(strImage, productName, productPrice, productType);
                */
            }
        }
    }

    /*private void fillLabels(String strImage, String productName, Double productPrice, String productType) {
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
    */

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(inputNameAdress);
        }
    }

    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }

    private boolean validateFields() {
        adressName = inputNameAdress.getText().toString().trim();
        street = (inputStreet.getText().toString().trim());
        number = Integer.parseInt(inputNumber.getText().toString().trim());
        district = (inputDistrict.getText().toString().trim());
        complement = (inputComplement.getText().toString().trim());
        cep = (inputCep.getText().toString().trim());
        city = (inputCity.getText().toString().trim());
        state = (inputState.getText().toString().trim());
        reference = (inputReference.getText().toString().trim());
        return (!isEmptyFields(adressName, street));
    }

    private boolean isEmptyFields(String name, String street) {
        if (TextUtils.isEmpty(name)) {
            inputNameAdress.requestFocus(); //seta o foco para o campo name
            inputNameAdress.setError(resources.getString(R.string.register_name_required));
            return true;
        } else if (TextUtils.isEmpty(street)) {
            inputStreet.requestFocus(); //seta o foco para o campo email
            inputStreet.setError(resources.getString(R.string.register_field_required));
            return true;
        }
        return false;
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
        inputNameAdress = (EditText) findViewById(R.id.input_adress_name);
        inputNameAdress.addTextChangedListener(textWatcher);
        inputCep = (EditText) findViewById(R.id.input_cep);
        inputCep.addTextChangedListener(textWatcher);
        inputCity = (EditText) findViewById(R.id.input_city);
        inputCity.addTextChangedListener(textWatcher);
        inputComplement = (EditText) findViewById(R.id.input_complement);
        inputComplement.addTextChangedListener(textWatcher);
        inputDistrict = (EditText) findViewById(R.id.input_district);
        inputDistrict.addTextChangedListener(textWatcher);
        inputNumber = (EditText) findViewById(R.id.input_number);
        inputNumber.addTextChangedListener(textWatcher);
        inputReference = (EditText) findViewById(R.id.input_reference);
        inputReference.addTextChangedListener(textWatcher);
        inputState = (EditText) findViewById(R.id.input_state);
        inputState.addTextChangedListener(textWatcher);
        inputStreet = (EditText) findViewById(R.id.input_street);
        inputStreet.addTextChangedListener(textWatcher);
    }

    private Adress initAdress(){
        Adress adress = new Adress();
        adress.setAdressName(adressName);
        adress.setStreet(street);
        adress.setCep(cep);
        adress.setComplement(complement);
        adress.setDistrict(district);
        adress.setNumber(number);
        adress.setState(state);
        adress.setCity(city);
        adress.setReference(reference);
        adress.setUserID(userId);
        return adress;
    }

    public void addAdress(View view) {
        if(validateFields()){
            Adress adress = initAdress();
            getLocation();
            adress.setLatitude(latitude);
            adress.setLongitude(longitude);
            String adressID = mDatabaseReference.push().getKey();
            adress.setId(adressID);
            mDatabaseReference.child("users").child(userId).child("adress").child(adressID).setValue(adress);
            finish();
        }
    }

    public void getLocation() {
        String adressLocation = street + ", " + number + " - " + district;
        List<Address> list = new ArrayList<Address>();
        Geocoder geocoder = new Geocoder(FormAdressActivity.this, Locale.getDefault());
        String error = "";

        try {
            list = (ArrayList<Address>) geocoder.getFromLocationName(adressLocation, 1);
        } catch (IOException e) {
            e.printStackTrace();
            error = "Network problem";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            error = "Illegal arguments";
        }

        if (list != null && list.size() > 0) {
            Address a = list.get(0);
            latitude = a.getLatitude();
            longitude = a.getLongitude();
        }
    }
}
