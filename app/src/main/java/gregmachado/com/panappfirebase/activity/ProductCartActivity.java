package gregmachado.com.panappfirebase.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.CartAdapter;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.interfaces.ItemClickListener;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroAddress;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroAreaCode;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroBrazilianStates;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroBuyer;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroCheckout;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroFactory;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroItem;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroPayment;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroPhone;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroShipping;
import gregmachado.com.panappfirebase.pagSeguro.PagSeguroShippingType;
import gregmachado.com.panappfirebase.util.AppUtil;

/**
 * Created by gregmachado on 02/10/16.
 */
public class ProductCartActivity extends CommonActivity implements ItemClickListener {

    private static final String TAG = ProductCartActivity.class.getSimpleName();
    private RecyclerView rvProduct;
    private CartAdapter cartAdapter;
    private List<Product> _list;
    private TextView tvItens, tvPrice;
    private Double priceTotal = 0.00, price;
    private Bundle params;
    private String bakeryId, productID;
    private int items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
            priceTotal = params.getDouble("total");
            _list = (ArrayList<Product>)
                    getIntent().getSerializableExtra("cart");
        }
        loadCartList();
        setValuesToolbarBottom(String.valueOf(priceTotal));
    }

    @Override
    public void onClick(View view, int position) {
        final Product product = _list.get(position);
        Toast.makeText(this, "Produto: " + product.getProductPrice(), Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void loadCartList() {
        setValuesToolbarBottom(String.valueOf(priceTotal));
        rvProduct.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, this, _list, this);
        rvProduct.setAdapter(cartAdapter);
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

    public void setValuesToolbarBottom(String price) {
        tvPrice.setText(price);
    }

    public Double getValuesToolbarBottom() {
        price = Double.parseDouble(String.valueOf(tvPrice.getText()));
        return price;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            // se foi uma tentativa de pagamento
            if (requestCode == PagSeguroPayment.PAG_SEGURO_REQUEST_CODE) {
                // exibir confirmação de cancelamento
                final String msg = getString(R.string.transaction_cancelled);
                AppUtil.showConfirmDialog(this, msg, null);
            }
        } else if (resultCode == RESULT_OK) {
            // se foi uma tentativa de pagamento
            if (requestCode == PagSeguroPayment.PAG_SEGURO_REQUEST_CODE) {
                // exibir confirmação de sucesso
                final String msg = getString(R.string.transaction_succeded);
                AppUtil.showConfirmDialog(this, msg, null);

            }
        } else if (resultCode == PagSeguroPayment.PAG_SEGURO_REQUEST_CODE) {
            switch (data.getIntExtra(PagSeguroPayment.PAG_SEGURO_EXTRA, 0)) {
                case PagSeguroPayment.PAG_SEGURO_REQUEST_SUCCESS_CODE: {
                    final String msg = getString(R.string.transaction_succeded);
                    AppUtil.showConfirmDialog(this, msg, null);
                    callScheduleActivity();
                    break;
                }
                case PagSeguroPayment.PAG_SEGURO_REQUEST_FAILURE_CODE: {
                    final String msg = getString(R.string.transaction_error);
                    AppUtil.showConfirmDialog(this, msg, null);
                    break;
                }
                case PagSeguroPayment.PAG_SEGURO_REQUEST_CANCELLED_CODE: {
                    final String msg = getString(R.string.transaction_cancelled);
                    AppUtil.showConfirmDialog(this, msg, null);
                    break;
                }
            }
        }
    }

    private void callScheduleActivity() {
        Intent intentSchedule = new Intent(ProductCartActivity.this, ScheduleActivity.class);
        ArrayList<Product> requestItems = cartAdapter.getProducts();
        params.putString("bakeryID", bakeryId);
        intentSchedule.putExtras(params);
        intentSchedule.putExtra("items", requestItems);
        startActivity(intentSchedule);
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        setTitle("Meu carrinho");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvItens = (TextView) findViewById(R.id.tv_units);
        tvPrice = (TextView) findViewById(R.id.tv_total_price_cart);
        rvProduct = (RecyclerView) findViewById(R.id.product_cart_list);
        rvProduct.setItemAnimator(new DefaultItemAnimator());
        registerForContextMenu(rvProduct);
    }

    public void finishBuy(View view) {
        // at this point you should check if the user has internet connection
        // before stating the pagseguro checkout process.(it will need internet connection)

        // simulating an user buying
        if(!(_list.isEmpty())&!(_list==null)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Finalizar?");
            builder.setMessage("Deseja finalizar a compra?");
            builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    for (Product productAux : _list) {
                        productID = productAux.getId();
                        items = productAux.getUnit();
                        mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int itemsSale = Integer.parseInt(dataSnapshot.child("itensSale").getValue().toString());
                                items = itemsSale - items;
                                mDatabaseReference.child("bakeries").child(bakeryId).child("products").child(productID).child("itensSale").setValue(items);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });
                    }
                    final PagSeguroFactory pagseguro = PagSeguroFactory.instance();
                    List<PagSeguroItem> shoppingCart = new ArrayList<>();
                    for (Iterator<Product> iterator = _list.iterator(); iterator.hasNext(); ) {
                        Product product = iterator.next();
                        shoppingCart.add(pagseguro.item(product.getId(), product.getProductName(), BigDecimal.valueOf(product.getProductPrice()), product.getUnit(), 300));
                    }
                    PagSeguroPhone buyerPhone = pagseguro.phone(PagSeguroAreaCode.DDD81, "998187427");
                    PagSeguroBuyer buyer = pagseguro.buyer("Ricardo Ferreira", "14/02/1978", "15061112000", "test@email.com.br", buyerPhone);
                    PagSeguroAddress buyerAddress = pagseguro.address("Av. Boa Viagem", "51", "Apt201", "Boa Viagem", "51030330", "Recife", PagSeguroBrazilianStates.PERNAMBUCO);
                    PagSeguroShipping buyerShippingOption = pagseguro.shipping(PagSeguroShippingType.NOT_DEFINED, buyerAddress);
                    PagSeguroCheckout checkout = pagseguro.checkout("Ref0001", shoppingCart, buyer, buyerShippingOption);
                    // starting payment process
                    new PagSeguroPayment(ProductCartActivity.this, _list, bakeryId).pay(checkout.buildCheckoutXml());
                }
            });
            builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            showToast("Carrinho vazio!");
        }
    }

    @Override
    public void onBackPressed() {
        if(_list.isEmpty()||_list==null){
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sair?");
            builder.setMessage("Se você voltar agora perderá seus itens. Deseja realmente sair?");
            builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });
            builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

}
