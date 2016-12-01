package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
import gregmachado.com.panappfirebase.util.ImagePicker;

/**
 * Created by gregmachado on 05/11/16.
 */
public class FormEditBakeryActivity extends CommonActivity {
    private static final String TAG = FormEditBakeryActivity.class.getSimpleName();
    private String bakeryId, userId, startTime, finishTime, cnpj, corporateName, fantasyName, phone, email, street, district, state, city;
    private TextView lblStartTime, lblFinishTime, lblCnpj, lblCorporateName, tvAddPhoto;
    private TextView inputEmail, inputStreet, inputNumber, inputDistrict, inputState, inputCity;
    private EditText inputFantasyname, inputPhone;
    private Switch switchDelivery;
    private boolean hasDelivery, isRegister, noPhoto;
    private Double latitude, longitude;
    private int number;
    private Resources resources;
    private List<Product> products;
    private ImageView ivBakery, ivAddPhoto;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private static final int PICK_IMAGE_ID = 235;

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
            userId = params.getString("userID");
        }
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
                if(bakery.getBakeryImage() == null){
                    ivAddPhoto.setVisibility(View.VISIBLE);
                    tvAddPhoto.setVisibility(View.VISIBLE);
                    noPhoto = true;
                } else {
                    StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
                    StorageReference imageRef = mStorage.child(bakeryId);
                    final long ONE_MEGABYTE = 1024 * 1024;
                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ivBakery.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
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
        inputDistrict = (TextView) findViewById(R.id.input_district);
        inputEmail = (TextView) findViewById(R.id.input_email);
        inputFantasyname = (EditText) findViewById(R.id.input_fantasy_name);
        inputFantasyname.addTextChangedListener(textWatcher);
        inputNumber = (TextView) findViewById(R.id.input_number);
        inputPhone = (EditText) findViewById(R.id.input_fone);
        inputPhone.addTextChangedListener(textWatcher);
        inputState = (TextView) findViewById(R.id.input_state);
        inputStreet = (TextView) findViewById(R.id.input_street);
        inputCity = (TextView) findViewById(R.id.input_city);
        switchDelivery = (Switch) findViewById(R.id.switch_delivery);
        switchDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                hasDelivery = isChecked;
            }
        });
        ivAddPhoto = (ImageView) findViewById(R.id.iv_add_photo);
        ivBakery = (ImageView) findViewById(R.id.iv_bakery);
        tvAddPhoto = (TextView) findViewById(R.id.tv_add_photo);
        ivBakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(FormEditBakeryActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
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
            Intent intent = new Intent(FormEditBakeryActivity.this, LoginEmailActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private Bakery initBakery() {
        final Bakery bakery = new Bakery();
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
        if (!noPhoto) {
            mStorageRef = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com").child(bakeryId);
            mStorageRef.delete().addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                }
            });
        }
        ivBakery.setDrawingCacheEnabled(true);
        ivBakery.buildDrawingCache();
        Bitmap bitmap = ivBakery.getDrawingCache();
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
                    bakery.setBakeryImage(downloadUrl.toString());
                }
            }
        });
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
        startTime = (lblStartTime.getText().toString().trim());
        finishTime = (lblFinishTime.getText().toString().trim());
        return (!isEmptyFields(fantasyName, phone));
    }

    private boolean isEmptyFields(String name, String phone) {
        if (TextUtils.isEmpty(name)) {
            inputFantasyname.requestFocus(); //seta o foco para o campo name
            inputFantasyname.setError(resources.getString(R.string.register_name_required));
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                ivBakery.setImageBitmap(bitmap);
                tvAddPhoto.setVisibility(View.INVISIBLE);
                ivAddPhoto.setVisibility(View.INVISIBLE);
                noPhoto = false;
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
