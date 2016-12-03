package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.User;
import gregmachado.com.panappfirebase.util.ImagePicker;

/**
 * Created by gregmachado on 28/11/16.
 */
public class SettingsActivity extends CommonActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private TextView lblName, lblEmail, lblAddPhoto, tvDistance;
    private EditText etName;
    private ImageView ivUser, ivAddPhoto;
    private Button btnSaveChanges;
    private SeekBar seekBar;
    private Switch switchNotification;
    private String name, email, id;
    private Resources resources;
    private boolean sendNotification, noPhoto, changeName = false;
    private int distance;
    private User user;
    private static final int PICK_IMAGE_ID = 235;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            id = params.getString("id");
        }
        user = new User();
        initViews();
        loadInfo();
    }

    private void loadInfo() {
        mDatabaseReference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                name = user.getName();
                lblName.setText(user.getName());
                lblEmail.setText(user.getEmail());
                distance = user.getDistanceForSearchBakery();
                tvDistance.setText(String.valueOf(distance));
                seekBar.setProgress(distance);
                sendNotification = user.isSendNotification();
                if (sendNotification) {
                    switchNotification.setChecked(true);
                }
                if (user.getImage() == null) {
                    ivAddPhoto.setVisibility(View.VISIBLE);
                    lblAddPhoto.setVisibility(View.VISIBLE);
                    noPhoto = true;
                } else {
                    noPhoto = false;
                    StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
                    StorageReference imageRef = mStorage.child(id);
                    final long ONE_MEGABYTE = 1024 * 1024;
                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ivUser.setImageBitmap(bitmap);
                            ivAddPhoto.setVisibility(View.INVISIBLE);
                            lblAddPhoto.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            showToast("Erro ao carregar imagem!");
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        setTitle("Configurações");
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
        lblEmail = (TextView) findViewById(R.id.lbl_email);
        lblName = (TextView) findViewById(R.id.lbl_name);
        lblAddPhoto = (TextView) findViewById(R.id.lbl_add_photo);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        btnSaveChanges = (Button) findViewById(R.id.btn_save_changes);
        etName = (EditText) findViewById(R.id.et_name);
        etName.addTextChangedListener(textWatcher);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 20;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                tvDistance.setText(String.valueOf(progressChanged));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                distance = progressChanged;
            }
        });
        switchNotification = (Switch) findViewById(R.id.switch_notification);
        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                sendNotification = isChecked;
                //btnSaveChanges.setVisibility(View.VISIBLE);
            }
        });
        ivUser = (ImageView) findViewById(R.id.iv_user);
        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(SettingsActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
        ivAddPhoto = (ImageView) findViewById(R.id.iv_add_photo);
    }

    public void editName(View view) {
        lblName.setVisibility(View.INVISIBLE);
        etName.setVisibility(View.VISIBLE);
        etName.setText(name);
        changeName = true;
    }

    public void saveChanges(View view) {
        if (validateFields()) {
            user.setName(name);
            user.setSendNotification(sendNotification);
            user.setDistanceForSearchBakery(distance);
            mStorageRef = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com").child(id);
            if (!noPhoto) {
                mStorageRef.delete().addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                    }
                });
            }
            ivUser.setDrawingCacheEnabled(true);
            ivUser.buildDrawingCache();
            Bitmap bitmap = ivUser.getDrawingCache();
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
                        user.setImage(downloadUrl.toString());
                    }
                }
            });

            mDatabaseReference.child("users").child(id).child("name").setValue(name);
            mDatabaseReference.child("users").child(id).child("distanceForSearchBakery").setValue(distance);
            mDatabaseReference.child("users").child(id).child("sendNotification").setValue(sendNotification);
            mDatabaseReference.child("users").child(id).child("image").setValue(user.getImage());
            showToast("Usuário atualizado!");
            Intent intentHomeUser = new Intent(SettingsActivity.this, UserMainActivity.class);
            params = new Bundle();
            params.putString("name", name);
            params.putString("email", email);
            intentHomeUser.putExtras(params);
            startActivity(intentHomeUser);
            finish();
        } else {
            showToast("Erro ao enviar dados!");
        }
    }

    private boolean validateFields() {
        if (distance > 5) {
            if (changeName) {
                name = etName.getText().toString().trim();
                return (!isEmptyFields(name));
            } else {
                return true;
            }
        } else {
            showToast("Distância minima de 5 kms!");
            return false;
        }
    }

    private boolean isEmptyFields(String name) {
        if (TextUtils.isEmpty(name)) {
            etName.requestFocus(); //seta o foco para o campo name
            etName.setError(resources.getString(R.string.register_name_required));
            return true;
        }
        return false;
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
            clearErrorFields(etName);
        }
    }

    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                Bitmap reducedImagePhoto = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                ivUser.setImageBitmap(reducedImagePhoto);
                lblAddPhoto.setVisibility(View.INVISIBLE);
                ivAddPhoto.setVisibility(View.INVISIBLE);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
