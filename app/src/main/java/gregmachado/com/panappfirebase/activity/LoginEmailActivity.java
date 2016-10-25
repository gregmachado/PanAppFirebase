package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.User;
import gregmachado.com.panappfirebase.util.Encryption;

/**
 * Created by gregmachado on 24/10/16.
 */
public class LoginEmailActivity extends CommonActivity {

    private Resources resources;
    private AutoCompleteTextView inputEmail;
    private EditText inputPassword;
    private String email;
    private String password;
    private CheckBox cbRememberMe;
    private Bundle params;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private User user;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        params = new Bundle();
        resources = getResources();
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = getFirebaseAuthResultHandler();
        initViews();
        initWatchers();

        Button btnLogin = (Button) findViewById(R.id.btn_sign);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    openProgressBar();
                    initUser();
                    verifyLogin();
                }
            }
        });

        Button btnRegister = (Button) findViewById(R.id.btn_sign_up);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginEmailActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyUserLogged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if (userFirebase == null) {
                    return;
                }

                if (user.getId() == null && isNameOk(user, userFirebase)) {

                    user.setId(userFirebase.getUid());
                    user.setNameIfNull(userFirebase.getDisplayName());
                    user.setEmailIfNull(userFirebase.getEmail());
                    user.saveDB();
                }

                callMainActivity();
            }
        };
        return (callback);
    }


    /**
     * Chama o método para limpar erros
     *
     * @param s Editable
     */
    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(inputEmail);
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
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
        return (!isEmptyFields(email, password) && emailValid(email));
    }

    private boolean isEmptyFields(String email, String password) {
        if (TextUtils.isEmpty(email)) {
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

    @Override
    protected void initViews() {
        inputEmail = (AutoCompleteTextView) findViewById(R.id.et_login_email);
        inputPassword = (EditText) findViewById(R.id.et_login_senha);
        cbRememberMe = (CheckBox) findViewById(R.id.cb_remember_me);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
    }

    private void initWatchers() {
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
        inputEmail.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);
    }

    @Override
    protected void initUser() {
        user = new User();
        user.setEmail(inputEmail.getText().toString());
        final Encryption cripto = Encryption.getInstance(inputPassword.getText().toString());
        user.setPassword(cripto.getEncryptPassword());
    }

    private void verifyUserLogged(){
        if (firebaseAuth.getCurrentUser() != null) {
            callMainActivity();
        } else {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    private void verifyLogin(){
        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            closeProgressBar();

                            return;
                        }
                    }
                });
    }

    private void callMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showSnackbar(connectionResult.getErrorMessage());
    }

    private boolean isNameOk(User user, FirebaseUser firebaseUser) {
        return (user.getName() != null || firebaseUser.getDisplayName() != null);
    }
}
