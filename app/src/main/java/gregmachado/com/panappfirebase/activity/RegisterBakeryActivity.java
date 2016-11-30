package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
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
        GoogleApiClient.OnConnectionFailedListener, DatabaseReference.CompletionListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Resources resources;
    private EditText inputCnpj, inputAdminPassword;
    private String corporateName, fantasyName, fone, email, cnpj, street, district, city, adminPassword, cnpjAux;
    private int number;
    private static final String TAG = RegisterBakeryActivity.class.getSimpleName();
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
    private DatabaseReference mDatabaseReference;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> bakerieList;
    private String code;
    private String adressLocation = "";
    private Location l;
    private Double latitude, longitude;
    private String bakeryID;
    private TextView tvResult;
    private ImageView icFound, icNotFound;
    private Button btnAddBakery;

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
            clearErrorFields(inputAdminPassword);
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
        cnpj = inputCnpj.getText().toString().trim();
        adminPassword = inputAdminPassword.getText().toString().trim();
        return (!isEmptyFields(adminPassword) && hasSizeValid(cnpj, adminPassword));
    }

    private boolean isEmptyFields(String adminPassword) {
        if (TextUtils.isEmpty(adminPassword)) {
            inputAdminPassword.requestFocus();
            inputAdminPassword.setError(resources.getString(R.string.register_password_required));
            return true;
        }
            return false;
    }

    private boolean hasSizeValid(String cnpj, String adminPassword) {

        if (!(cnpj.length() > 13)) {
            inputCnpj.requestFocus();
            inputCnpj.setError(resources.getString(R.string.register_cnpj_size_invalid));
            return false;
        } else if (!(adminPassword.length() > 4)) {
            inputAdminPassword.requestFocus();
            inputAdminPassword.setError(resources.getString(R.string.register_pass_size_invalid));
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

    public void searchByCNPJ(View view) {
        cnpjAux = inputCnpj.getText().toString().trim();
        cnpjAux = cnpjAux.replaceAll("[^0-9]", "");
        // Calling async task to get json
        new GetBakery().execute();
    }

    public void addBakery(View view) {
        setBakery(fantasyJson, nameJson, emailJson, streetJson, numberJson, districtJson, cityJson, phoneJson);
        if (validateFields()) {
            openProgressDialog("Cadastrando...", "Aguarde um momento!");
            initUser();
            initBakery();
            saveUser();
        }
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetBakery extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            openProgressDialog("Buscando dados...", "Aguarde!");
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            String newUrl = "";
            newUrl = url + cnpjAux;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(newUrl, ServiceHandler.GET);

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
                    return !jsonStr.contains("ERROR");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                tvResult.setText(R.string.bakery_found);
                icNotFound.setVisibility(View.INVISIBLE);
                icFound.setVisibility(View.VISIBLE);
                btnAddBakery.setEnabled(true);
            } else {
                tvResult.setText(R.string.bakery_not_found);
                icFound.setVisibility(View.INVISIBLE);
                icNotFound.setVisibility(View.VISIBLE);
                btnAddBakery.setEnabled(false);
            }
            closeProgressDialog();
        }
    }

    private void setBakery(String fantasyJ, String nameJ, String emailJ, String streetJ, String numberJ,
                           String districtJ, String cityJ, String phoneJ) {
        district = districtJ;
        city = cityJ;
        email = emailJ;
        fantasyName = fantasyJ;
        fone = phoneJ;
        corporateName = nameJ;
        street = streetJ;
        number = Integer.parseInt(numberJ);
    }

    private synchronized void callConnection() {
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

        if (l != null) {
            mLastLocation = l;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "AddressLocationActivity.onConnectionSuspended(" + i + ")");
    }

    @Override
    protected void initViews() {
        inputCnpj = (EditText) findViewById(R.id.input_cnpj);
        MaskEditTextChangedListener maskCnpj = new MaskEditTextChangedListener("##.###.###/####-##", inputCnpj);
        inputCnpj.addTextChangedListener(maskCnpj);
        inputAdminPassword = (EditText) findViewById(R.id.input_admin_password);
        tvResult = (TextView) findViewById(R.id.tv_result);
        icFound = (ImageView) findViewById(R.id.ic_bakery_found);
        icNotFound = (ImageView) findViewById(R.id.ic_bakery_not_found);
        btnAddBakery = (Button) findViewById(R.id.btn_add_bakery);
    }

    protected void initUser() {
        user = new User();
        user.setName(fantasyName);
        user.setEmail("testepadaria09@gmail.com");
        final Encryption cripto = Encryption.getInstance(adminPassword);
        user.setPassword(cripto.getEncryptPassword());
        user.setType(true);
        user.setSendNotification(true);
    }

    private void initBakery() {
        bakery = new Bakery();
        adress = new Adress(user.getId(), street, district, city, number, latitude, longitude);
        bakery.setEmail("testepadaria09@gmail.com");
        bakery.setAdress(adress);
        bakery.setCnpj(cnpj);
        bakery.setCorporateName(corporateName);
        bakery.setFantasyName(fantasyName);
        bakery.setFone(fone);
        bakery.setUserID(user.getId());
    }

    private void initWatchers() {

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
        inputCnpj.addTextChangedListener(textWatcher);
        inputAdminPassword.addTextChangedListener(textWatcher);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOG", "AddressLocationActivity.onConnectionFailed(" + connectionResult + ")");
    }

    private void saveUser() {
        String id = mDatabaseReference.push().getKey();
        user.setId(id);
        bakery.setUserID(id);
        //user.saveDB(RegisterBakeryActivity.this);
        //bakery.saveDB(RegisterBakeryActivity.this);
        bakeryID = mDatabaseReference.push().getKey();
        bakery.setId(bakeryID);
        mDatabaseReference.child("users").child(id).setValue(user);
        mDatabaseReference.child("bakeries").child(bakeryID).setValue(bakery);
        callEditBakery();
    }

    private void callEditBakery() {
        params.putString("bakeryID", bakeryID);
        params.putBoolean("isRegister", true);
        Intent intentFormEditBakery = new Intent(RegisterBakeryActivity.this, FormEditBakeryActivity.class);
        intentFormEditBakery.putExtras(params);
        closeProgressDialog();
        //showToast("Padaria registrada com sucesso!");
        startActivity(intentFormEditBakery);
    }
}