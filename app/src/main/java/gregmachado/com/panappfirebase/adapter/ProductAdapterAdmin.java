package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.activity.FormProductActivity;
import gregmachado.com.panappfirebase.activity.ProductAdminActivity;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.util.AppUtil;
import gregmachado.com.panappfirebase.util.DialogHandler;
import gregmachado.com.panappfirebase.viewHolder.ProductViewHolder;

/**
 * Created by gregmachado on 10/11/16.
 */
public class ProductAdapterAdmin extends FirebaseRecyclerAdapter<Product, ProductViewHolder> {

    private static final String TAG = ProductAdapter.class.getSimpleName();
    private Context mContext;
    private Handler handler;
    private TextView tvUnits;
    private int items, count = 0;
    private double price, parcialPrice;
    private ArrayList<Product> productsToCart = new ArrayList<>();
    private ProductAdminActivity productListActivity;
    private String bakeryID, productID, productName;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();
    private DecimalFormat precision = new DecimalFormat("#0.00");

    public ProductAdapterAdmin(Query ref, Context context, ProductAdminActivity productListActivity, String bakeryID) {
        super(Product.class, R.layout.card_product, ProductViewHolder.class, ref);
        this.mContext = context;
        this.productListActivity = productListActivity;
        this.bakeryID = bakeryID;
    }

    @Override
    protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, final int position) {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.tvProductName.setText(model.getProductName());
        viewHolder.tvItensSale.setText(String.valueOf(model.getItensSale()));
        viewHolder.tvProductType.setText(model.getType());
        viewHolder.tvProductPrice.setText(precision.format(model.getProductPrice()));
        if(model.getProductImage() == null){
            viewHolder.ivProduct.setImageResource(R.drawable.img_product);
        } else {
            StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
            StorageReference imageRef = mStorage.child(model.getId());
            final long ONE_MEGABYTE = 1024 * 1024;
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    viewHolder.ivProduct.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        if (model.isInOffer()){
            viewHolder.tvProductPrice.setTextColor(mContext.getResources().getColor(R.color.black_50_opacity));
            viewHolder.tvProductPrice.setText(precision.format(model.getOldPrice()));
            viewHolder.tvPriceInOffer.setVisibility(View.VISIBLE);
            viewHolder.tvPriceInOffer.setText(precision.format(model.getProductPrice()));
            viewHolder.tvDiscount.setText(String.valueOf(model.getDiscount()));
            viewHolder.tvDiscount.setVisibility(View.VISIBLE);
            viewHolder.icOffer.setVisibility(View.VISIBLE);
            viewHolder.lblPercent.setVisibility(View.VISIBLE);
        }
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "You clicked on " + model.getProductName());
            }
        });
        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //setPosition(productViewHolder.getPosition());
                return false;
            }
        });
        viewHolder.btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialoglayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_units, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final AlertDialog dialog = builder.create();
                dialog.setView(dialoglayout);
                tvUnits = (TextView) dialoglayout.findViewById(R.id.tv_unit_sale);
                ImageButton btnPlus = (ImageButton) dialoglayout.findViewById(R.id.btn_plus);
                btnPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        increase(viewHolder);
                    }
                });
                btnPlus.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                if (handler != null) return true;
                                handler = new Handler();
                                handler.postDelayed(actionPlus, 100);
                                break;

                            case MotionEvent.ACTION_UP:
                                if (handler == null) return true;
                                handler.removeCallbacks(actionPlus);
                                handler = null;
                                break;
                        }
                        return false;
                    }

                    Runnable actionPlus = new Runnable() {

                        @Override
                        public void run() {
                            increase(viewHolder);
                            handler.postDelayed(this, 100);
                        }
                    };
                });
                ImageButton btnLess = (ImageButton) dialoglayout.findViewById(R.id.btn_less);
                btnLess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        decrease(viewHolder);
                    }
                });
                btnLess.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                if (handler != null) return true;
                                handler = new Handler();
                                handler.postDelayed(actionLess, 100);
                                break;

                            case MotionEvent.ACTION_UP:
                                if (handler == null) return true;
                                handler.removeCallbacks(actionLess);
                                handler = null;
                                break;
                        }
                        return false;
                    }

                    Runnable actionLess = new Runnable() {

                        @Override
                        public void run() {
                            decrease(viewHolder);
                            handler.postDelayed(this, 100);
                        }
                    };
                });
                Button btnCancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnAddToCart = (Button) dialoglayout.findViewById(R.id.btn_add_cart);
                btnAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String productID = model.getId();
                        int unit = model.getItensSale();
                        Integer items = getValue(viewHolder);
                        Log.w(TAG, "id produto: " + productID);
                        if (items > 0) {
                            items = items + unit;
                            mDatabaseReference.child("bakeries").child(bakeryID).child("products")
                                    .child(productID).child("itensSale").setValue(items);
                            refresh(viewHolder, items);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
        viewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, viewHolder.btnMore);
                popup.getMenuInflater().inflate(R.menu.popup_menu_product, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String action = item.getTitle().toString();
                        productID = model.getId();
                        productName = model.getProductName();
                        String imagePath = model.getProductImage();
                        makeAction(action, viewHolder, imagePath);
                        return true;
                    }
                });
                popup.show();
            }
        });
        viewHolder.progressBar.setVisibility(View.GONE);
    }

    private void makeAction(String action, ProductViewHolder viewHolder, String productImage) {
        switch (action){
            case "Editar":
                String productType = viewHolder.tvProductType.getText().toString();
                Double productPrice = Double.parseDouble(viewHolder.tvProductPrice.getText().toString());
                Bundle params = new Bundle();
                params.putString("bakeryID", bakeryID);
                params.putBoolean("update", true);
                params.putString("productName", productName);
                params.putString("productType", productType);
                params.putDouble("productPrice", productPrice);
                params.putString("productID", productID);
                params.putString("productImage", productImage);
                params.putInt("items", items);
                Intent intentFormProduct = new Intent(mContext, FormProductActivity.class);
                intentFormProduct.putExtras(params);
                mContext.startActivity(intentFormProduct);
                break;
            case "Remover Estoque":
                DialogHandler appdialogRemove = new DialogHandler();
                appdialogRemove.Confirm(mContext, "Remover?", "Deseja remover todo estoque do item?",
                        "NÃO", "SIM", yes(1, viewHolder), no());
                break;
            case "Excluir":
                DialogHandler appdialog = new DialogHandler();
                appdialog.Confirm(mContext, "Excluir?", "Deseja excluir o item?",
                        "NÃO", "SIM", yes(2, viewHolder), no());
                break;
        }
    }

    public Runnable yes(final Integer answer, final ProductViewHolder viewHolder){
        return new Runnable() {
            public void run() {
                if (answer == 2){
                    mDatabaseReference.child("bakeries").child(bakeryID).child("products")
                            .child(productID).removeValue();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    mStorageRef = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com").child(productID);
                    mStorageRef.delete().addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {}
                    });
                    AppUtil.showToast(mContext, productName + " excluido!");
                } else {
                    if(viewHolder.tvItensSale.getText() != "0"){
                        mDatabaseReference.child("bakeries").child(bakeryID).child("products")
                                .child(productID).child("itensSale").setValue(0);
                        refresh(viewHolder, items);
                        AppUtil.showToast(mContext, "Estoque de " + productName + " removido!");
                    } else {
                        AppUtil.showToast(mContext, "Este produto não possui estoque");
                    }
                }
            }
        };
    }

    public Runnable no(){
        return new Runnable() {
            public void run() {
                Log.d("Test", "This from nop proc");
            }
        };
    }

    protected void decrease(ProductViewHolder productViewHolder) {
        if (isValid(productViewHolder)) {
            int value = getValue(productViewHolder) - 1;
            tvUnits.setText(String.valueOf(value));
        }
    }

    protected void increase(ProductViewHolder productViewHolder) {
        int value = getValue(productViewHolder) + 1;
        tvUnits.setText(String.valueOf(value));
    }

    private int getValue(ProductViewHolder productViewHolder) {
        String value = tvUnits.getText().toString();
        return (!value.equals("")) ? Integer.valueOf(value) : 0;
    }

    private boolean isValid(ProductViewHolder productViewHolder) {
        return (getValue(productViewHolder) > 0);
    }

    private void refresh(ProductViewHolder productViewHolder, int value) {
        productViewHolder.tvItensSale.setText(String.valueOf(value));
    }
}
