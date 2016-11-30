package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.concurrent.ExecutionException;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import cieloecommerce.sdk.Merchant;
import cieloecommerce.sdk.ecommerce.CieloEcommerce;
import cieloecommerce.sdk.ecommerce.Customer;
import cieloecommerce.sdk.ecommerce.Environment;
import cieloecommerce.sdk.ecommerce.Payment;
import cieloecommerce.sdk.ecommerce.Sale;
import cieloecommerce.sdk.ecommerce.request.CieloError;
import cieloecommerce.sdk.ecommerce.request.CieloRequestException;
import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 28/11/16.
 */
public class PaymentActivity extends CommonActivity {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    private String idPayment, customerName, flag, name, cardNumber, securityCode, expirationDate;
    private Integer amount;
    private EditText etCardNumber, etExpirationDate, etSecurityCode, etName;
    private Spinner spFlag;
    private ArrayAdapter<String> dataAdapter;
    private Resources resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            idPayment = params.getString("idPayment");
            customerName = params.getString("customer");
            amount = params.getInt("amount");
        }
        initViews();
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_payment);
        setSupportActionBar(toolbar);
        setTitle("Pagamento");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        etCardNumber = (EditText) findViewById(R.id.et_card_number);
        etCardNumber.addTextChangedListener(textWatcher);
        MaskEditTextChangedListener maskCardNumber = new MaskEditTextChangedListener("####.####.####.####", etCardNumber);
        etCardNumber.addTextChangedListener(maskCardNumber);
        etExpirationDate = (EditText) findViewById(R.id.et_date_expiration);
        etExpirationDate.addTextChangedListener(textWatcher);
        MaskEditTextChangedListener maskExpirationDate = new MaskEditTextChangedListener("##/####", etExpirationDate);
        etExpirationDate.addTextChangedListener(maskExpirationDate);
        etName = (EditText) findViewById(R.id.et_name);
        etName.addTextChangedListener(textWatcher);
        etSecurityCode = (EditText) findViewById(R.id.et_code);
        etSecurityCode.addTextChangedListener(textWatcher);
        spFlag = (Spinner) findViewById(R.id.spinner_flag);
        String[] flags = getResources().getStringArray(R.array.flags);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, flags);
        spFlag.setAdapter(dataAdapter);
        spFlag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                flag = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean validateFields() {
        name = etName.getText().toString().trim();
        securityCode = etSecurityCode.getText().toString().trim();
        cardNumber = etCardNumber.getText().toString().trim();
        cardNumber = cardNumber.replaceAll("[^0-9]", "");
        expirationDate = etExpirationDate.getText().toString().trim();
        return (!isEmptyFields(name, securityCode, cardNumber, expirationDate)
                && hasSizeValid(securityCode, cardNumber, expirationDate));
    }

    private boolean isEmptyFields(String name, String securityCode, String cardNumber, String expirationDate) {
        if (TextUtils.isEmpty(name)) {
            etName.requestFocus(); //seta o foco para o campo name
            etName.setError(resources.getString(R.string.register_name_required));
            return true;
        } else if (TextUtils.isEmpty(securityCode)) {
            etSecurityCode.requestFocus();
            etSecurityCode.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(cardNumber)) {
            etCardNumber.requestFocus();
            etCardNumber.setError(resources.getString(R.string.register_field_required));
            return true;
        } else if (TextUtils.isEmpty(expirationDate)) {
            etExpirationDate.requestFocus();
            etExpirationDate.setError(resources.getString(R.string.register_field_required));
            return true;
        }
        return false;
    }

    private boolean hasSizeValid(String securityCode, String cardNumber, String expirationDate) {

        if (!(securityCode.length() > 2)) {
            etSecurityCode.requestFocus();
            etSecurityCode.setError(resources.getString(R.string.field_security_code_size_invalid));
            return false;
        } else if (!(cardNumber.length() > 15)) {
            etCardNumber.requestFocus();
            etCardNumber.setError(resources.getString(R.string.field_card_number_size_invalid));
            return false;
        } else if (!(expirationDate.length() > 6)) {
            etExpirationDate.requestFocus();
            etExpirationDate.setError(resources.getString(R.string.field_verify));
            return false;
        }
        return true;
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

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(etCardNumber);
        }
    }

    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }

    public void finishPayment(View view) {
        openProgressDialog("Finalizando Pagamento...", "Aguarde!");
        if (validateFields()) {
            // Configure seu merchant
            Merchant merchant = new Merchant("8f785e77-1098-4eaf-9483-3e1160d8ab03", "TWYIEDUHYTOWKSDDGPZGJQVSFYDLXGXTIDEDRJGZ");

            // Crie uma instância de Sale informando o ID do pagamento
            Sale sale = new Sale("id");
            Log.i(TAG, idPayment);
            // Crie uma instância de Customer informando o nome do cliente
            Customer customer = sale.customer("Comprador Teste");
            Log.i(TAG, customerName);
            // Crie uma instância de Payment informando o valor do pagamento
            Payment payment = sale.payment(1000);
            Log.i(TAG, String .valueOf(amount));
            // Crie  uma instância de Credit Card utilizando os dados de teste
            // esses dados estão disponíveis no manual de integração
            payment.creditCard("123", "Visa").setExpirationDate("12/2018")
                    .setCardNumber(cardNumber)
                    .setHolder("Fulano de Tal");
            Log.i(TAG, securityCode + flag + expirationDate
                    + cardNumber + name);
            // Crie o pagamento na Cielo
            try {
                // Configure o SDK com seu merchant e o ambiente apropriado para criar a venda
                sale = new CieloEcommerce(merchant, Environment.SANDBOX).createSale(sale);
                // Com a venda criada na Cielo, já temos o ID do pagamento, TID e demais
                // dados retornados pela Cielo
                //String paymentId = sale.getPayment().getPaymentId();
                String returnCode = sale.getPayment().getReturnCode();
                Intent intent = new Intent();
                intent.putExtra("returnCode", returnCode);
                setResult(2, intent);
                closeProgressDialog();
                finish();
                // Com o ID do pagamento, podemos fazer sua captura, se ela não tiver sido capturada ainda
                //sale = new CieloEcommerce(merchant, Environment.SANDBOX).captureSale(paymentId);
                // E também podemos fazer seu cancelamento, se for o caso
                //sale = new CieloEcommerce(merchant, Environment.SANDBOX).cancelSale(paymentId, 15700);
            } catch (ExecutionException | InterruptedException e) {
                // Como se trata de uma AsyncTask, será preciso tratar ExecutionException e InterruptedException
                e.printStackTrace();
            } catch (CieloRequestException e) {
                // Em caso de erros de integração, podemos tratar o erro aqui.
                // os códigos de erro estão todos disponíveis no manual de integração.
                CieloError error = e.getError();
                Log.v("Cielo", error.getCode().toString());
                Log.v("Cielo", error.getMessage());
                if (error.getCode() != 404) {
                    Log.e("Cielo", null, e);
                }
            }
        } else {
            Log.v(TAG, "Erro");
            closeProgressDialog();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        super.onBackPressed();
    }
}
