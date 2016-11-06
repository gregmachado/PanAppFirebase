package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Adress;
import gregmachado.com.panappfirebase.domain.Bakery;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.util.CustomTimePickerDialog;

/**
 * Created by gregmachado on 05/11/16.
 */
public class FormEditBakeryActivity extends CommonActivity {
    private static final String TAG = FormEditBakeryActivity.class.getSimpleName();
    private String bakeryId, userId, startTime, finishTime, cnpj, corporateName, fantasyName, phone, email, street, district, state, city;
    private TextView lblStartTime, lblFinishTime, lblCnpj, lblCorporateName;
    private EditText inputFantasyname, inputPhone, inputEmail, inputStreet, inputNumber, inputDistrict, inputState, inputCity;
    private Switch switchDelivery;
    private boolean hasDelivery, isRegister;
    private Double latitude, longitude;
    private int number;
    private Resources resources;
    private List<Product> products;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_edit_bakery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_form_edit_bakery);
        resources = getResources();
        setSupportActionBar(toolbar);
        setTitle("Editar Padaria");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initViews();
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            isRegister = params.getBoolean("isRegister");
            bakeryId = params.getString("bakeryID");
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
        userId = userFirebase.getUid();
        fillLabels();
        dateTimeSelect();
        if (isRegister){
            showToast("Por favor, complete o cadastro!");
        }
    }

    private void fillLabels() {
        mDatabaseReference.child("bakeries").child(bakeryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bakery bakery = dataSnapshot.getValue(Bakery.class);
                lblCorporateName.setText(bakery.getCorporateName());
                lblCnpj.setText(bakery.getCnpj());
                if(bakery.getFinishTime()==null){
                    lblFinishTime.setText(R.string.zero_hour);
                }else{
                    lblFinishTime.setText(bakery.getFinishTime());
                }
                if(bakery.getStartTime()==null){
                    lblStartTime.setText(R.string.zero_hour);
                }else{
                    lblStartTime.setText(bakery.getStartTime());
                }
                inputFantasyname.setText(bakery.getFantasyName());
                inputState.setText(bakery.getAdress().getState());
                inputNumber.setText(String.valueOf(bakery.getAdress().getNumber()));
                inputCity.setText(bakery.getAdress().getCity());
                inputStreet.setText(bakery.getAdress().getStreet());
                inputDistrict.setText(bakery.getAdress().getDistrict());
                inputEmail.setText(bakery.getEmail());
                inputPhone.setText(bakery.getFone());
                products = bakery.getProductList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
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
        lblCnpj = (TextView) findViewById(R.id.lbl_cnpj);
        lblCorporateName = (TextView) findViewById(R.id.lbl_corporate_name);
        lblStartTime = (TextView) findViewById(R.id.tv_start_time);
        lblFinishTime = (TextView) findViewById(R.id.tv_finish_time);
        inputDistrict = (EditText) findViewById(R.id.input_district);
        inputDistrict.addTextChangedListener(textWatcher);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputEmail.addTextChangedListener(textWatcher);
        inputFantasyname = (EditText) findViewById(R.id.input_fantasy_name);
        inputFantasyname.addTextChangedListener(textWatcher);
        inputNumber = (EditText) findViewById(R.id.input_number);
        inputNumber.addTextChangedListener(textWatcher);
        inputPhone = (EditText) findViewById(R.id.input_fone);
        inputPhone.addTextChangedListener(textWatcher);
        inputState = (EditText) findViewById(R.id.input_state);
        inputState.addTextChangedListener(textWatcher);
        inputStreet = (EditText) findViewById(R.id.input_street);
        inputStreet.addTextChangedListener(textWatcher);
        inputCity = (EditText) findViewById(R.id.input_city);
        inputCity.addTextChangedListener(textWatcher);
        switchDelivery = (Switch) findViewById(R.id.switch_delivery);
        switchDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    hasDelivery = true;
                } else {
                    hasDelivery = false;
                }
            }
        });
    }

    public void sendEditBakery(View view) {
        if (validateFields()) {
            openProgressDialog("Aguarde...", "Atualizando padaria!");
            Bakery bakery = initBakery();
            mDatabaseReference.child("bakeries").child(bakeryId).setValue(bakery);
            mDatabaseReference.child("users").child(userId).child("name").setValue(bakery.getFantasyName());
            if(isRegister){
                showToast("Padaria cadastrada!");
            } else {
                showToast("Padaria atualizada!");
            }
            closeProgressDialog();
            finish();
        }
    }

    private Bakery initBakery() {
        Bakery bakery = new Bakery();
        bakery.setId(bakeryId);
        bakery.setUserID(userId);
        bakery.setFone(phone);
        bakery.setCnpj(cnpj);
        bakery.setFantasyName(fantasyName);
        bakery.setStartTime(startTime);
        bakery.setEmail(email);
        bakery.setFinishTime(finishTime);
        bakery.setHasDelivery(hasDelivery);
        Adress adress = new Adress();
        adress.setStreet(street);
        adress.setNumber(number);
        adress.setDistrict(district);
        adress.setCity(city);
        adress.setState(state);
        getLocation();
        adress.setLatitude(latitude);
        adress.setLongitude(longitude);
        bakery.setAdress(adress);
        bakery.setProductList(products);
        return bakery;
    }

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(inputFantasyname);
        }
    }

    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }

    private boolean validateFields() {
        fantasyName = inputFantasyname.getText().toString().trim();
        street = (inputStreet.getText().toString().trim());
        number = Integer.parseInt(inputNumber.getText().toString().trim());
        district = (inputDistrict.getText().toString().trim());
        cnpj = (lblCnpj.getText().toString().trim());
        city = (inputCity.getText().toString().trim());
        state = (inputState.getText().toString().trim());
        corporateName = (lblCorporateName.getText().toString().trim());
        startTime = (lblStartTime.getText().toString().trim());
        finishTime = (lblFinishTime.getText().toString().trim());
        phone = (inputPhone.getText().toString().trim());
        email = (inputEmail.getText().toString().trim());
        return (!isEmptyFields(fantasyName, street, number, district, city, state, phone, email));
    }

    private boolean isEmptyFields(String name, String street, int number, String district, String city, String state,
                                  String phone, String email) {
        if (TextUtils.isEmpty(name)) {
            inputFantasyname.requestFocus(); //seta o foco para o campo name
            inputFantasyname.setError(resources.getString(R.string.register_name_required));
            return true;
        } else if (TextUtils.isEmpty(street)) {
            inputStreet.requestFocus(); //seta o foco para o campo email
            inputStreet.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(email)) {
            inputEmail.requestFocus();
            inputEmail.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(String.valueOf(number))) {
            inputNumber.requestFocus();
            inputNumber.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(district)) {
            inputDistrict.requestFocus();
            inputDistrict.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(city)) {
            inputCity.requestFocus();
            inputCity.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(state)) {
            inputState.requestFocus();
            inputState.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(phone)) {
            inputPhone.requestFocus();
            inputPhone.setError(resources.getString(R.string.register_field_required));
            return true;
        }
        return false;
    }

    public void getLocation() {
        String adressLocation = street + ", " + number + " - " + district;
        List<Address> list = new ArrayList<Address>();
        Geocoder geocoder = new Geocoder(FormEditBakeryActivity.this, Locale.getDefault());
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

    private void dateTimeSelect() {
        lblStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(FormEditBakeryActivity.this, timeSetListenerStart,
                        Calendar.getInstance().get(Calendar.HOUR),
                        CustomTimePickerDialog.getRoundedMinute(Calendar.getInstance().get(Calendar.MINUTE) + CustomTimePickerDialog.TIME_PICKER_INTERVAL), true);
                timePickerDialog.setTitle("Selecione o horário");
                timePickerDialog.show();
            }
        });
        lblFinishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(FormEditBakeryActivity.this, timeSetListenerFinish,
                        Calendar.getInstance().get(Calendar.HOUR),
                        CustomTimePickerDialog.getRoundedMinute(Calendar.getInstance().get(Calendar.MINUTE) + CustomTimePickerDialog.TIME_PICKER_INTERVAL), true);
                timePickerDialog.setTitle("Selecione o horário");
                timePickerDialog.show();
            }
        });
    }

    private CustomTimePickerDialog.OnTimeSetListener timeSetListenerStart = new CustomTimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            lblStartTime.setText(String.format("%02d", hourOfDay) + ":" +String.format("%02d", minute));
        }
    };
    private CustomTimePickerDialog.OnTimeSetListener timeSetListenerFinish = new CustomTimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            lblFinishTime.setText(String.format("%02d", hourOfDay) + ":" +String.format("%02d", minute));
        }
    };
}
