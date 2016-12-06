package gregmachado.com.panappfirebase.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.User;
import gregmachado.com.panappfirebase.util.Encryption;

/**
 * Created by gregmachado on 17/06/16.
 */
public class RegisterActivity extends CommonActivity implements DatabaseReference.CompletionListener{

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Resources resources;
    private EditText inputName, inputEmail, inputPassword;
    private String name, email, password;
    private CheckBox cbTerms;
    private User user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null || user.getId() != null) {
                    return;
                }
                user.setId(firebaseUser.getUid());
                user.setFirstOpen(true);
                user.setDistanceForSearchBakery(20);
                user.setSendNotification(true);
                user.saveDB(RegisterActivity.this);
                firebaseUser.sendEmailVerification();
            }
        };
        initViews();
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

    /**
     * Chama o método para limpar erros
     *
     * @param s Editable
     */
    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(inputName);
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
        name = inputName.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
        return (!isEmptyFields(name, email, password) && hasSizeValid(name, password) && emailValid(email));
    }

    private boolean isEmptyFields(String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            inputName.requestFocus(); //seta o foco para o campo name
            inputName.setError(resources.getString(R.string.register_name_required));
            return true;
        } else if (TextUtils.isEmpty(email)) {
            inputEmail.requestFocus(); //seta o foco para o campo email
            inputEmail.setError(resources.getString(R.string.register_email_required));
            return true;
        } else if (TextUtils.isEmpty(password)) {
            inputPassword.requestFocus(); //seta o foco para o campo password
            inputPassword.setError(resources.getString(R.string.register_password_required));
            return true;
        }
        return false;
    }

    private boolean hasSizeValid(String name, String password) {

        if (!(name.length() > 2)) {
            inputName.requestFocus();
            inputName.setError(resources.getString(R.string.register_name_size_invalid));
            return false;
        } else if (!(password.length() > 4)) {
            inputPassword.requestFocus();
            inputPassword.setError(resources.getString(R.string.register_pass_size_invalid));
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
                showToast("Este email já está cadastrado no sistema!");
            }
        });
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mAuth.signOut();
        showToast("Conta criada com sucesso! Verifique seu email para validar a conta!");
        closeProgressDialog();
        finish();
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void initViews() {
        inputName = (EditText) findViewById(R.id.et_cadastre_name);
        inputEmail = (EditText) findViewById(R.id.et_cadastre_email);
        inputPassword = (EditText) findViewById(R.id.et_cadastre_pass);
        cbTerms = (CheckBox) findViewById(R.id.cb_terms);
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
        inputName.addTextChangedListener(textWatcher);
        inputEmail.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);
    }

    protected void initUser() {
        user = new User();
        user.setName(name);
        user.setEmail(email);
        final Encryption cripto = Encryption.getInstance(password);
        user.setPassword(cripto.getEncryptPassword());
        user.setType(false);
        user.setSendNotification(true);
        user.setDistanceForSearchBakery(20);
    }

    public void createAccount(View view) {
        if (validateFields()) {
            if (cbTerms.isChecked()) {
                openProgressDialog("Cadastrando...", "Aguarde um momento!");
                initUser();
                saveUser();
            } else{
                showToast("Por favor, leia e aceite os termos de uso!");
            }
        }
    }
}
