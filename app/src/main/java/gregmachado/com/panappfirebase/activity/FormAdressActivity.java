package gregmachado.com.panappfirebase.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Adress;
import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 09/10/16.
 */
public class FormAdressActivity extends CommonActivity {

    private static final String TAG = FormProductActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private EditText inputNameAdress, inputCep, inputStreet, inputNumber, inputComplement,
            inputDistrict, inputCity, inputReference, inputState;
    private String cep, adressName, street, complement, district, city, state, reference;
    private Resources resources;
    private String userId, adressID;
    private Integer number;
    private Boolean update = false, isRegister;
    private Spinner spState;
    private DatabaseReference mDatabaseReference;
    private Double latitude, longitude;
    private ArrayAdapter<String> dataAdapter;
    private Location location;
    private LocationManager locationManager;
    private Address a;
    private Adress adress;
    private Button btnSaveAdress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_adress);
        resources = getResources();
        adress = new Adress();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_adress);
        setSupportActionBar(toolbar);
        setTitle("Novo Endereço");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDatabaseReference = LibraryClass.getFirebase();
        mDatabaseReference.getRef();
        Intent it = getIntent();
        params = it.getExtras();

        if (params != null) {
            adressID = params.getString("adressID");
            userId = params.getString("userID");
            update = params.getBoolean("update");
            isRegister = params.getBoolean("isRegister");
        }
        initViews();
        if (update) {
            setTitle("Editar Endereço");
            fillLabels();
        }
        if (isRegister){
            showToast("Por favor, cadastre seu primeiro endereço!");
        }
    }

    private void fillLabels() {
        mDatabaseReference.child("users").child(userId).child("adress")
                .child(adressID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adress = dataSnapshot.getValue(Adress.class);
                inputState.setText(adress.getState());
                inputNumber.setText(String.valueOf(adress.getNumber()));
                inputCity.setText(adress.getCity());
                inputStreet.setText(adress.getStreet());
                inputDistrict.setText(adress.getDistrict());
                inputCep.setText(adress.getCep());
                inputComplement.setText(adress.getComplement());
                inputReference.setText(adress.getReference());
                inputNameAdress.setText(adress.getAdressName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }


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
        String numb = (inputNumber.getText().toString().trim());
        district = (inputDistrict.getText().toString().trim());
        complement = (inputComplement.getText().toString().trim());
        cep = (inputCep.getText().toString().trim());
        city = (inputCity.getText().toString().trim());
        reference = (inputReference.getText().toString().trim());
        state = (inputState.getText().toString().trim());
        return (!isEmptyFields(adressName, street, numb, cep, district, city));
    }

    private boolean isEmptyFields(String name, String street, String numb, String cep, String district, String city) {
        if (TextUtils.isEmpty(name)) {
            inputNameAdress.requestFocus(); //seta o foco para o campo name
            inputNameAdress.setError(resources.getString(R.string.register_name_required));
            return true;
        } else if (TextUtils.isEmpty(street)) {
            inputStreet.requestFocus();
            inputStreet.setError(resources.getString(R.string.register_field_required));
            return true;
        }else if (TextUtils.isEmpty(numb)) {
            inputNumber.requestFocus();
            inputNumber.setError(resources.getString(R.string.register_field_required));
            return true;
        }else if (TextUtils.isEmpty(cep)) {
            inputCep.requestFocus();
            inputCep.setError(resources.getString(R.string.register_field_required));
            return true;
        }else if (TextUtils.isEmpty(district)) {
            inputDistrict.requestFocus();
            inputDistrict.setError(resources.getString(R.string.register_field_required));
            return true;
        }else if (TextUtils.isEmpty(city)) {
            inputCity.requestFocus();
            inputCity.setError(resources.getString(R.string.register_field_required));
            return true;
        }
        number = Integer.parseInt(numb);
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
        MaskEditTextChangedListener maskCep = new MaskEditTextChangedListener("#####-###", inputCep);
        inputCep.addTextChangedListener(maskCep);
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
        btnSaveAdress = (Button) findViewById(R.id.btn_add_adress);
        if (update){
            btnSaveAdress.setText("Atualizar endereço");
        }
    }

    private Adress initAdress() {
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
        if (validateFields()) {
            initAdress();
            getLocation();
            adress.setLatitude(latitude);
            adress.setLongitude(longitude);
            if (!update){
                adressID = mDatabaseReference.push().getKey();
                adress.setId(adressID);
            }
            mDatabaseReference.child("users").child(userId).child("adress").child(adressID).setValue(adress);
            mDatabaseReference.child("users").child(userId).child("firstOpen").setValue(false);
            showToast("Endereço cadastrado");
            Intent intent = new Intent();
            intent.putExtra("firstOpen", false);
            setResult(5, intent);
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

    public void getMyLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        try {
            a = searchAddress(latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStreet.setText(a.getAddressLine(0));
        inputCity.setText(a.getLocality());
        inputCep.setText(a.getPostalCode());
        inputDistrict.setText(a.getSubLocality());
        inputState.setText(a.getAdminArea());
    }

    private Address searchAddress(double latitude, double longitude) throws IOException {

        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (addresses.size() > 0) {
            address = addresses.get(0);
        }
        return address;
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
