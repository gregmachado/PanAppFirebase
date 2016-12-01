package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.User;
import gregmachado.com.panappfirebase.util.Encryption;

/**
 * Created by gregmachado on 24/10/16.
 */
public class LoginEmailActivity extends CommonActivity {

    private static final String TAG = LoginEmailActivity.class.getSimpleName();
    private Resources resources;
    private AutoCompleteTextView inputEmail;
    private EditText inputPassword;
    private String email;
    private String password;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private User user;
    private FirebaseUser userFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = getFirebaseAuthResultHandler();
        initViews();
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

    @Override
    protected void initViews() {
        inputEmail = (AutoCompleteTextView) findViewById(R.id.et_login_email);
        inputPassword = (EditText) findViewById(R.id.et_login_senha);
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
        inputEmail.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                userFirebase = firebaseAuth.getCurrentUser();

                if (userFirebase == null) {
                    return;
                }

                if (user.getId() == null && isNameOk(user, userFirebase)) {
                    user.setId(userFirebase.getUid());
                    user.setNameIfNull(userFirebase.getDisplayName());
                    user.setEmailIfNull(userFirebase.getEmail());
                    user.saveDB();
                }
                if (userFirebase.isEmailVerified()) {
                    Log.i(TAG, "Email is verified");
                    callMainActivity();
                } else {
                    Log.i(TAG, "Email is not verified");
                    closeProgressDialog();
                    showToast("Verifique seu email para validar a conta!");
                }
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

    private void initUser() {
        user = new User();
        user.setEmail(inputEmail.getText().toString());
        final Encryption cripto = Encryption.getInstance(inputPassword.getText().toString());
        user.setPassword(cripto.getEncryptPassword());
    }

    private void verifyUserLogged() {
        if (firebaseAuth.getCurrentUser() != null) {
            callMainActivity();
        } else {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    private void verifyLogin() {
        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            closeProgressDialog();
                            showToast("Erro ao realizar autenticação. Verifique email e senha!");
                            return;
                        }
                    }
                });
    }

    private void callMainActivity() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String id = firebaseUser.getUid();
        mDatabaseReference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String name = user.getName();
                String email = user.getEmail();
                String bakeryID = user.getBakeryID();
                boolean type = user.isType();
                boolean firstOpen = user.isFirstOpen();
                params = new Bundle();
                params.putString("name", name);
                params.putString("email", email);
                params.putString("bakeryID", bakeryID);
                params.putBoolean("type", type);
                params.putBoolean("firstOpen", firstOpen);
                closeProgressDialog();
                if (!user.isType()) {
                    Intent intentHomeUser = new Intent(LoginEmailActivity.this, UserMainActivity.class);
                    intentHomeUser.putExtras(params);
                    startActivity(intentHomeUser);
                } else {
                    Intent intentHomeAdmin = new Intent(LoginEmailActivity.this, AdminMainActivity.class);
                    intentHomeAdmin.putExtras(params);
                    startActivity(intentHomeAdmin);
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        showSnackbar(connectionResult.getErrorMessage());
    }

    private boolean isNameOk(User user, FirebaseUser firebaseUser) {
        return (user.getName() != null || firebaseUser.getDisplayName() != null);
    }

    public void forgotPass(View view) {
        View dialoglayout = LayoutInflater.from(LoginEmailActivity.this).inflate(R.layout.dialog_reset_password, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginEmailActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setView(dialoglayout);
        final EditText inputEmail = (EditText) dialoglayout.findViewById(R.id.input_email);
        Button btnCancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btnSend = (Button) dialoglayout.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    showToast("Informe um email!");
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        showToast("Em instantes você receberá o email!");
                                        dialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
        dialog.show();
    }

    public void signUp(View view) {
        Intent intent = new Intent(LoginEmailActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void signIn(View view) {
        if (validateFields()) {
            openProgressDialog("Autenticando...", "Aguarde um momento!");
            initUser();
            verifyLogin();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed");
        firebaseAuth.signOut();
    }
}
