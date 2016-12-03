package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.MessageFormat;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Bakery;

/**
 * Created by gregmachado on 05/11/16.
 */
public class MyBakeryActivity extends CommonActivity{
    private static final String TAG = MyBakeryActivity.class.getSimpleName();
    private TextView tvName, tvAdress, tvPhone, tvCnpj, tvCity, tvState, tvEmail, tvHasDelivery, tvStartTime, tvFinishTime, tvNoPhoto;
    private ImageView ivBakery;
    private String bakeryId;
    private CardView cardBakery;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bakery);
        initViews();
        loadBakery();
    }

    private void loadBakery() {
        openProgressBar();
        mDatabaseReference.child("bakeries").child(bakeryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bakery bakery = dataSnapshot.getValue(Bakery.class);
                tvName.setText(bakery.getFantasyName());
                tvPhone.setText(bakery.getFone());
                tvStartTime.setText(bakery.getStartTime());
                tvFinishTime.setText(bakery.getFinishTime());
                String hasDelivery;
                if(bakery.isHasDelivery()){
                    hasDelivery = "Sim";
                } else {
                    hasDelivery = "Não";
                }
                tvHasDelivery.setText(hasDelivery);
                tvEmail.setText(bakery.getEmail());
                tvAdress.setText(MessageFormat.format("{0}, {1}, {2}", bakery.getAdress().getStreet(), bakery.getAdress().
                        getNumber(), bakery.getAdress().getDistrict()));
                tvCity.setText(bakery.getAdress().getCity());
                tvCnpj.setText(bakery.getCnpj());
                tvState.setText(bakery.getAdress().getState());
                if (bakery.getBakeryImage() == null) {
                    tvNoPhoto.setVisibility(View.VISIBLE);
                } else {
                    StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
                    StorageReference imageRef = mStorage.child(bakeryId);
                    final long ONE_MEGABYTE = 1024 * 1024;
                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ivBakery.setImageBitmap(bitmap);
                            tvNoPhoto.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            showToast("Erro ao carregar imagem!");
                        }
                    });
                }
                closeProgressBar();
                cardBakery.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_my_bakery);
        setSupportActionBar(toolbar);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryID");
        }
        setTitle("Minha Padaria");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvName = (TextView) findViewById(R.id.tv_bakery_name);
        tvAdress = (TextView) findViewById(R.id.tv_bakery_adress);
        tvCity = (TextView) findViewById(R.id.tv_bakery_city);
        tvCnpj = (TextView) findViewById(R.id.tv_bakery_cnpj);
        tvEmail = (TextView) findViewById(R.id.tv_bakery_email);
        tvFinishTime = (TextView) findViewById(R.id.tv_finish_time);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvState = (TextView) findViewById(R.id.tv_bakery_state);
        tvHasDelivery = (TextView) findViewById(R.id.tv_has_delivery);
        tvPhone = (TextView) findViewById(R.id.tv_bakery_fone);
        tvNoPhoto = (TextView) findViewById(R.id.tv_no_photo);
        ivBakery = (ImageView) findViewById(R.id.iv_my_bakery);
        cardBakery = (CardView) findViewById(R.id.cv_my_bakery);
        cardBakery.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
    }

    public void editBakery(View view) {
        params.putString("bakeryID", bakeryId);
        Intent intentFormEditBakery = new Intent(MyBakeryActivity.this, FormEditBakeryActivity.class);
        intentFormEditBakery.putExtras(params);
        startActivity(intentFormEditBakery);
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
}
