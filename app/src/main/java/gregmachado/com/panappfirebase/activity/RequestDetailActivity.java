package gregmachado.com.panappfirebase.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.ItemRequestAdapter;
import gregmachado.com.panappfirebase.domain.Feed;
import gregmachado.com.panappfirebase.domain.Historic;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.util.DateUtil;
import gregmachado.com.panappfirebase.util.DividerItemDecoration;

/**
 * Created by gregmachado on 16/11/16.
 */
public class RequestDetailActivity extends CommonActivity {
    private static final String TAG = RequestDetailActivity.class.getSimpleName();
    private String id, userName, bakeryName, requestID, userID, bakeryID, code;
    private TextView tvCode, tvName, tvTotal, tvStatus;
    private RecyclerView rvItems;
    private ItemRequestAdapter adapter;
    private boolean type;
    private Request request;
    private View dialoglayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            id = params.getString("bakeryID");
            userName = params.getString("userName");
            bakeryName = params.getString("bakeryName");
            requestID = params.getString("requestID");
            type = params.getBoolean("type");
            code = params.getString("code");
        }
        initViews();
        rvItems.setHasFixedSize(true);
        rvItems.setItemAnimator(new DefaultItemAnimator());
        rvItems.setLayoutManager(new LinearLayoutManager(RequestDetailActivity.this));
        rvItems.addItemDecoration(new DividerItemDecoration(this));
        loadItems();
    }

    private void loadItems() {
        openProgressBar();
        mDatabaseReference.child("requests").child(id).child(requestID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeProgressBar();
                request = dataSnapshot.getValue(Request.class);
                tvStatus.setText(request.getStatus());
                tvCode.setText(request.getRequestCode());
                if (type){
                    tvName.setText(userName);
                } else {
                    tvName.setText(bakeryName);
                }
                tvTotal.setText(String.valueOf(request.getTotal()));

                List<Product> productList = new ArrayList<Product>();
                for (DataSnapshot productSnapshot : dataSnapshot.child("productList").getChildren()) {
                    Product product = new Product();
                    product.setUnit(productSnapshot.child("unit").getValue(Integer.class));
                    product.setProductName(productSnapshot.child("productName").getValue(String.class));
                    product.setProductPrice(productSnapshot.child("productPrice").getValue(double.class));
                    productList.add(product);
                }
                adapter = new ItemRequestAdapter(productList, RequestDetailActivity.this);
                rvItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_request_detail);
        setSupportActionBar(toolbar);
        setTitle("Detalhes do Pedido");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        tvCode = (TextView) findViewById(R.id.tv_request_code);
        tvName = (TextView) findViewById(R.id.tv_request_bakery_name);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        rvItems = (RecyclerView) findViewById(R.id.rv_items);
        Button btnChangeStatus = (Button) findViewById(R.id.btn_change_status);
        if(!type){
            btnChangeStatus.setVisibility(View.INVISIBLE);
        }
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

    public void changeStatus(View view) {
        requestID = request.getRequestID();
        bakeryID = request.getBakeryID();
        userID = request.getUserID();
        final int situation = request.getSituation();
        String method = request.getMethod();
        if (method.equals("Retirada")){
            dialoglayout = LayoutInflater.from(this).inflate(R.layout.dialog_update_status_withdraw, null);
            final CheckBox cbReceived = (CheckBox) dialoglayout.findViewById(R.id.cb_received);
            final CheckBox cbReady = (CheckBox) dialoglayout.findViewById(R.id.cb_ready);
            final CheckBox cbRetired = (CheckBox) dialoglayout.findViewById(R.id.cb_retired);
            Button btnUpdateStatus = (Button) dialoglayout.findViewById(R.id.btn_update);
            switch (situation){
                case 1:
                    cbReceived.setChecked(true);
                    cbReceived.setClickable(false);
                    break;
                case 2:
                    cbReceived.setChecked(true);
                    cbReceived.setClickable(false);
                    cbReady.setChecked(true);
                    cbReady.setClickable(false);
                    break;
                default:
                    break;
            }
            btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (situation){
                        case 1:
                            if (cbReady.isChecked() && !cbRetired.isChecked()){
                                setRequest("Pronto para retirada!", 2, false, getString(R.string.msg_ready_from_withdraw));
                            } else if (!cbReady.isChecked() && cbRetired.isChecked() || (
                                    cbReady.isChecked() && cbRetired.isChecked())) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetailActivity.this);
                                builder.setTitle("Cuidado!");
                                builder.setMessage("O usuáio retirou o pedido?");
                                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        setRequest("Pedido retirado!", 3, true, getString(R.string.msg_was_withdrawn));
                                    }
                                });
                                builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else if (!cbReady.isChecked() && !cbRetired.isChecked()){
                                showToast(getString(R.string.msg_status_no_update));
                            }
                            break;
                        case 2:
                            if (cbRetired.isChecked()) {
                                setRequest("Pedido retirado!", 3, true, getString(R.string.msg_was_withdrawn));
                            } else {
                                showToast(getString(R.string.msg_status_no_update));
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        } else {
            dialoglayout = LayoutInflater.from(this).inflate(R.layout.dialog_update_status_delivery, null);
            final CheckBox cbReceived = (CheckBox) dialoglayout.findViewById(R.id.cb_received);
            final CheckBox cbReady = (CheckBox) dialoglayout.findViewById(R.id.cb_ready);
            final CheckBox cbInTransit = (CheckBox) dialoglayout.findViewById(R.id.cb_in_transit);
            final CheckBox cbDelivered = (CheckBox) dialoglayout.findViewById(R.id.cb_delivered);
            Button btnUpdateStatus = (Button) dialoglayout.findViewById(R.id.btn_update);
            switch (situation){
                case 1:
                    cbReceived.setChecked(true);
                    cbReceived.setClickable(false);
                    break;
                case 2:
                    cbReceived.setChecked(true);
                    cbReceived.setClickable(false);
                    cbReady.setChecked(true);
                    cbReady.setClickable(false);
                    break;
                case 3:
                    cbReceived.setChecked(true);
                    cbReceived.setClickable(false);
                    cbReady.setChecked(true);
                    cbReady.setClickable(false);
                    cbInTransit.setChecked(true);
                    cbInTransit.setClickable(false);
                    break;
                default:
                    break;
            }
            btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (situation){
                        case 1:
                            if (cbReady.isChecked() && !cbInTransit.isChecked() && !cbDelivered.isChecked()){
                                setRequest("Pronto para envio!", 2, false, getString(R.string.msg_ready_from_delivery));
                            } else if (!cbReady.isChecked() && cbInTransit.isChecked() && !cbDelivered.isChecked()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetailActivity.this);
                                builder.setTitle("Cuidado! Você está pulando uma etapa!");
                                builder.setMessage("O pedido está a caminho?");
                                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        setRequest("Em trânsito!", 3, false, getString(R.string.msg_on_the_way));
                                    }
                                });
                                builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else if (!cbReady.isChecked() && !cbInTransit.isChecked() && cbDelivered.isChecked() ||
                                    (cbReady.isChecked() && cbInTransit.isChecked() && cbDelivered.isChecked())) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetailActivity.this);
                                builder.setTitle("Cuidado! Você está pulando uma etapa!");
                                builder.setMessage("O pedido foi entregue?");
                                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        setRequest("Pedido entregue!", 4, true, getString(R.string.msg_was_delivered));
                                    }
                                });
                                builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else if (!cbReady.isChecked() && !cbInTransit.isChecked() && !cbDelivered.isChecked()){
                                showToast(getString(R.string.msg_status_no_update));
                            }
                            break;
                        case 2:
                            if (cbInTransit.isChecked() && !cbDelivered.isChecked()) {
                                setRequest("Em trânsito!", 3, false, getString(R.string.msg_on_the_way));
                            } else if (!cbInTransit.isChecked() && cbDelivered.isChecked() ||
                                    (cbInTransit.isChecked() && cbDelivered.isChecked())){
                                AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetailActivity.this);
                                builder.setTitle("Cuidado! Você está pulando uma etapa!");
                                builder.setMessage("O pedido foi entregue?");
                                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        setRequest("Pedido entregue!", 4, true, getString(R.string.msg_was_delivered));
                                    }
                                });
                                builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else if (!cbInTransit.isChecked() && !cbDelivered.isChecked()){
                                showToast(getString(R.string.msg_status_no_update));
                            }
                            break;
                        case 3:
                            if (cbDelivered.isChecked()){
                                setRequest("Pedido entregue!", 4, true, getString(R.string.msg_was_delivered));
                            } else {
                                showToast(getString(R.string.msg_status_no_update));
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setView(dialoglayout);
        Button btnCancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setRequest(String status, int situation, boolean delivered, String msg){
        request.setStatus(status);
        request.setSituation(situation);
        request.setDelivered(delivered);
        request.setOpen(false);
        mDatabaseReference.child("requests").child(userID).child(requestID).setValue(request);
        mDatabaseReference.child("requests").child(bakeryID).child(requestID).setValue(request);
        newFeed(msg);
        newHistoric(msg);
        showToast(getResources().getString(R.string.msg_update_status));
    }

    private void newHistoric(String msg) {
        String historicID = mDatabaseReference.push().getKey();
        String date = DateUtil.getTodayDate();
        //user historic
        String hUser = "Pedido " + code + " realizado no estabelecimento " + bakeryName + " " + msg + "!";
        Historic historicUser = new Historic(historicID, date, userName, bakeryName, hUser);
        mDatabaseReference.child("users").child(userID).child("historic").child(historicID).setValue(historicUser);
        //bakery historic
        String hBakery = "Pedido " + code + " realizado por " + userName + " " + msg + "!";
        Historic historicBakery = new Historic(historicID, date, userName, bakeryName, hBakery);
        mDatabaseReference.child("bakeries").child(bakeryID).child("historic").child(historicID).setValue(historicBakery);
    }

    private void newFeed(String msg) {
        String feedID = mDatabaseReference.push().getKey();
        String date = DateUtil.getTodayDate();
        String msgUser = "Seu pedido " + msg + "!";
        Feed feedUser = new Feed(feedID, bakeryID, userID, date, userName, bakeryName, msgUser, 1, null);
        //save user feed
        mDatabaseReference.child("users").child(userID).child("feed").child(feedID).setValue(feedUser);
    }
}
