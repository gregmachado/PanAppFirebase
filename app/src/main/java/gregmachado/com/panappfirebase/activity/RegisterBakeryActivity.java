package gregmachado.com.panappfirebase.activity;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Adress;
import gregmachado.com.panappfirebase.domain.Bakery;
import gregmachado.com.panappfirebase.domain.User;
import gregmachado.com.panappfirebase.util.Encryption;
import gregmachado.com.panappfirebase.util.ServiceHandler;

/**
 * Created by gregmachado on 25/10/16.
 */

public class RegisterBakeryActivity extends CommonActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DatabaseReference.CompletionListener{

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Resources resources;
    private EditText inputCorporateName, inputFantasyName, inputFone, inputEmail, inputCnpj, inputStreet, inputNumber, inputDiscrict, inputCity, inputAdminPassword;
    private String corporateName, fantasyName, fone, email, cnpj, street, district, city, adminPassword, idString, numberString, cnpjAux;
    private int number;
    private static final String TAG = RegisterBakeryActivity.class.getSimpleName();
    private ProgressDialog progressDialog, pDialog;
    // URL to get contacts JSON
    private static String url = "http://receitaws.com.br/v1/cnpj/";
    // JSON Node names
    private static final String TAG_FANTASY = "fantasia";
    private static final String TAG_NAME = "nome";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_STREET = "logradouro";
    private static final String TAG_DISTRICT = "bairro";
    private static final String TAG_PHONE = "telefone";
    private static final String TAG_NUMBER = "numero";
    private static final String TAG_CITY = "municipio";
    private String nameJson;
    private String numberJson;
    private String emailJson;
    private String fantasyJson;
    private String phoneJson;
    private String streetJson;
    private String districtJson;
    private String cityJson;
    private User user;
    private Bakery bakery;
    private Adress adress;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseReference;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> bakerieList;
    private String code;
    private String adressLocation = "";
    private Location l;
    private Double latitude, longitude;
    private String bakeryID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_bakery);
        callConnection();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        bakerieList = new ArrayList<HashMap<String, String>>();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null || user.getId() != null) {
                    return;
                }
                String id = firebaseUser.getUid();
                user.setId(id);
                bakery.setUserID(user.getId());
                user.saveDB(RegisterBakeryActivity.this);
                //bakery.saveDB(RegisterBakeryActivity.this);
                String bakeryID = mDatabaseReference.push().getKey();
                bakery.setId(bakeryID);
                mDatabaseReference.child("bakeries").child(bakeryID).setValue(bakery);
                mDatabaseReference.child("users").child(id).child("bakeryID").setValue(bakeryID);
            }
        };
        initViews();
        initWatchers();
    }

    /**
     * Chama o método para limpar erros
     *
     * @param s Editable
     */
    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(inputCorporateName);
        }
    }

    /**
     * Efetua a validação dos campos.Nesse caso, valida se os campos não estão vazios e se tem
     * tamanho permitido.
     * Nesse método você poderia colocar outros tipos de validações de acordo com a sua necessidade.
     *
     * @return boolean que indica se os campos foram validados com sucesso ou não
     */
    private boolean validateFields() {
        corporateName = inputCorporateName.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        fantasyName = inputFantasyName.getText().toString().trim();
        fone = inputFone.getText().toString().trim();
        cnpj = inputCnpj.getText().toString().trim();
        street = inputStreet.getText().toString().trim();
        number = Integer.parseInt(inputNumber.getText().toString().trim());
        String numberAux = inputNumber.getText().toString().trim();
        district = inputDiscrict.getText().toString().trim();
        city = inputCity.getText().toString().trim();
        adminPassword = inputAdminPassword.getText().toString().trim();
        return (!isEmptyFields(email, fantasyName, fone, cnpj, street, district, numberAux, city, adminPassword) && hasSizeValid(fone, cnpj) && emailValid(email));
    }

    private boolean isEmptyFields(String email, String fantasyName, String fone, String cnpj, String street, String district, String number, String city, String adminPassword) {
        if (TextUtils.isEmpty(email)) {
            inputEmail.requestFocus(); //seta o foco para o campo email
            inputEmail.setError(resources.getString(R.string.register_email_required));
            return true;
        } else if (TextUtils.isEmpty(fantasyName)) {
            inputFantasyName.requestFocus(); //seta o foco para o campo
            inputFantasyName.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(fone)) {
            inputFone.requestFocus(); //seta o foco para o campo
            inputFone.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(cnpj)) {
            inputCnpj.requestFocus(); //seta o foco para o campo
            inputCnpj.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(street)) {
            inputStreet.requestFocus(); //seta o foco para o campo
            inputStreet.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(district)) {
            inputDiscrict.requestFocus(); //seta o foco para o campo
            inputDiscrict.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(number)) {
            inputNumber.requestFocus(); //seta o foco para o campo
            inputNumber.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(city)) {
            inputCity.requestFocus(); //seta o foco para o campo
            inputCity.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(adminPassword)) {
            inputAdminPassword.requestFocus(); //seta o foco para o campo
            inputAdminPassword.setError(resources.getString(R.string.register_field_required));
            return true;
        }
        return false;
    }

    private boolean hasSizeValid(String fone, String cnpj) {

        if (!(fone.length() > 11)) {
            inputFone.requestFocus();
            inputFone.setError(resources.getString(R.string.register_fone_size_invalid));
            return false;
        } else if (!(cnpj.length() > 4)) {
            inputCnpj.requestFocus();
            inputCnpj.setError(resources.getString(R.string.register_cnpj_size_invalid));
            return false;
        }
        return true;
    }

    /**
     * Limpa os ícones e as mensagens de erro dos campos desejados
     *
     * @param editTexts lista de campos do tipo EditText
     */
    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }

    private boolean emailValid(String email) {

        String Expn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        if (email.matches(Expn) && email.length() > 0) {
            return true;
        } else {
            inputEmail.requestFocus();
            inputEmail.setError(resources.getString(R.string.register_email_char_invalid));
            return false;
        }
    }

    public void searchByCNPJ(View view) {
        cnpjAux = inputCnpj.getText().toString().trim();
        // Calling async task to get json
        new GetBakery().execute();
    }

    public void addBakery(View view) {
        if (validateFields()) {
            openProgressDialog("Cadastrando...", "Aguarde um momento!");
            initUser();
            initBakery();
            saveUser();
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetBakery extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            openProgressDialog("Buscando dados...", "Aguarde!");
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            url = url + cnpjAux;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);
                    fantasyJson = c.optString(TAG_FANTASY);
                    nameJson = c.optString(TAG_NAME);
                    emailJson = c.optString(TAG_EMAIL);
                    streetJson = c.optString(TAG_STREET);
                    numberJson = c.optString(TAG_NUMBER);
                    phoneJson = c.optString(TAG_PHONE);
                    districtJson = c.optString(TAG_DISTRICT);
                    cityJson = c.optString(TAG_CITY);

                    // tmp hashmap for single contact
                    HashMap<String, String> bakery = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    bakery.put(TAG_FANTASY, fantasyJson);
                    bakery.put(TAG_NAME, nameJson);
                    bakery.put(TAG_EMAIL, emailJson);
                    bakery.put(TAG_PHONE, phoneJson);
                    bakery.put(TAG_CITY, cityJson);
                    bakery.put(TAG_DISTRICT, districtJson);
                    bakery.put(TAG_NUMBER, numberJson);
                    bakery.put(TAG_STREET, streetJson);
                    adressLocation = streetJson + ", " + numberJson;
                    List<Address> list = new ArrayList<Address>();
                    Geocoder geocoder = new Geocoder(RegisterBakeryActivity.this, Locale.getDefault());
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
                    } else {

                    }

                    // adding contact to contact list
                    bakerieList.add(bakery);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            closeProgressDialog();
            setEditText(fantasyJson, nameJson, emailJson, streetJson, numberJson, districtJson, cityJson, phoneJson);
        }
    }

    private void setEditText(String fantasy, String name, String email, String street, String number, String district, String city, String phone) {
        inputDiscrict.setText(district);
        inputCity.setText(city);
        inputEmail.setText(email);
        inputFantasyName.setText(fantasy);
        inputFone.setText(phone);
        inputCorporateName.setText(name);
        inputStreet.setText(street);
        inputNumber.setText(number);
    }

    private synchronized void callConnection(){
        Log.i("LOG", "RegisterBakeryActivity.callConnection()");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "AddressLocationActivity.onConnected(" + bundle + ")");

        //l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(l != null){
            mLastLocation = l;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "AddressLocationActivity.onConnectionSuspended(" + i + ")");
    }

    @Override
    protected void initViews() {
        inputCorporateName = (EditText) findViewById(R.id.input_corporate_name);
        inputCorporateName.setKeyListener(null);
        inputFantasyName = (EditText) findViewById(R.id.input_fantasy_name);
        inputFone = (EditText) findViewById(R.id.input_fone);
        MaskEditTextChangedListener maskFone = new MaskEditTextChangedListener("(##)####-####", inputFone);
        inputFone.addTextChangedListener(maskFone);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputCnpj = (EditText) findViewById(R.id.input_cnpj);
        inputStreet = (EditText) findViewById(R.id.input_street);
        inputNumber = (EditText) findViewById(R.id.input_number);
        inputDiscrict = (EditText) findViewById(R.id.input_district);
        inputCity = (EditText) findViewById(R.id.input_city);
        inputAdminPassword = (EditText) findViewById(R.id.input_admin_password);
    }

    protected void initUser() {
        user = new User();
        user.setName(fantasyName);
        user.setEmail(email);
        final Encryption cripto = Encryption.getInstance(adminPassword);
        user.setPassword(cripto.getEncryptPassword());
        user.setType(true);
        user.setSendNotification(true);
    }

    private void initBakery(){
        bakery = new Bakery();
        adress = new Adress(user.getId(),street, district, city, number, latitude, longitude);
        bakery.setEmail(email);
        bakery.setAdress(adress);
        bakery.setCnpj(cnpj);
        bakery.setCorporateName(corporateName);
        bakery.setFantasyName(fantasyName);
        bakery.setFone(fone);
        bakery.setUserID(user.getId());
    }

    private void initWatchers(){

        resources = getResources();
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
        inputCorporateName.addTextChangedListener(textWatcher);
        inputFantasyName.addTextChangedListener(textWatcher);
        inputFone.addTextChangedListener(textWatcher);
        inputEmail.addTextChangedListener(textWatcher);
        inputCnpj.addTextChangedListener(textWatcher);
        inputStreet.addTextChangedListener(textWatcher);
        inputNumber.addTextChangedListener(textWatcher);
        inputDiscrict.addTextChangedListener(textWatcher);
        inputCity.addTextChangedListener(textWatcher);
        inputAdminPassword.addTextChangedListener(textWatcher);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOG", "AddressLocationActivity.onConnectionFailed(" + connectionResult + ")");
    }

    private void saveUser() {
        mAuth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    closeProgressDialog();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar(e.getMessage());
            }
        });
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        //bakery.saveDB(RegisterBakeryActivity.this);
        mAuth.signOut();
        closeProgressDialog();
        showToast("Padaria registrada com sucesso!");
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}