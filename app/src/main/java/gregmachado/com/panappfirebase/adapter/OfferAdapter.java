package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
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
import gregmachado.com.panappfirebase.activity.FormOfferActivity;
import gregmachado.com.panappfirebase.activity.ProductCartActivity;
import gregmachado.com.panappfirebase.domain.Offer;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.util.AppUtil;
import gregmachado.com.panappfirebase.util.DialogHandler;
import gregmachado.com.panappfirebase.viewHolder.OfferViewHolder;

/**
 * Created by gregmachado on 19/11/16.
 */
public class OfferAdapter extends FirebaseRecyclerAdapter<Offer, OfferViewHolder> {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String TAG = OfferAdapter.class.getSimpleName();
    private Context mContext;
    private String bakeryID, userName, productID, productName, offerID, bakeryName;
    private DecimalFormat precision = new DecimalFormat("#0.00");
    private Handler handler;
    private TextView tvUnits;
    private ArrayList<Product> products;
    private int items;
    private boolean type;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();

    public OfferAdapter(Query ref, Context context, String userName, boolean type, String bakeryName) {
        super(Offer.class, R.layout.card_offer_user, OfferViewHolder.class, ref);
        this.mContext = context;
        this.userName = userName;
        this.type = type;
        this.bakeryName = bakeryName;
    }

    @Override
    protected void populateViewHolder(final OfferViewHolder viewHolder, final Offer model, final int position) {
        viewHolder.tvProductName.setText(model.getProductName());
        viewHolder.tvPrice.setText(precision.format(model.getPriceInOffer()));
        viewHolder.tvDiscount.setText(String.valueOf(model.getDiscount()));
        viewHolder.tvItensSale.setText(String.valueOf(model.getItensSale()));
        if (!type){
            viewHolder.tvBakeryName.setText(model.getBakeryName());
            viewHolder.tvBakeryName.setVisibility(View.VISIBLE);
        }
        if (model.getProductImage() == null) {
            viewHolder.ivProduct.setImageResource(R.drawable.img_product);
        } else {
            StorageReference mStorage = storage.getReferenceFromUrl("gs://panappfirebase.appspot.com");
            StorageReference imageRef = mStorage.child(model.getProductID());
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
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type) {
                    /*PopupMenu popup = new PopupMenu(mContext, viewHolder.mView);
                    popup.getMenuInflater().inflate(R.menu.popup_menu_offer, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            String action = item.getTitle().toString();
                            offerID = model.getId();
                            productID = model.getProductID();
                            productName = model.getProductName();
                            bakeryID = model.getBakeryId();
                            double price = model.getProductPrice();
                            String imagePath = model.getProductImage();
                            makeAction(action, viewHolder, imagePath, price);
                            return true;
                        }
                    });
                    popup.show();*/
                } else {
                    Log.w(TAG, "You clicked on " + model.getBakeryId() + " / " + model.getId());
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Comprar?");
                    builder.setMessage("Deseja comprar esta oferta?");
                    builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Product product = new Product();
                            product.setProductName(model.getProductName());
                            product.setProductPrice(model.getPriceInOffer());
                            product.setType(model.getType());
                            product.setBakeryId(model.getBakeryId());
                            product.setProductImage(model.getProductImage());
                            product.setId(model.getProductID());
                            product.setUnit(model.getItensSale());
                            bakeryID = model.getBakeryId();
                            products = new ArrayList<Product>();
                            products.add(product);
                            double total = model.getPriceInOffer() * Double.parseDouble(String.valueOf(model.getItensSale()));
                            String offerID = model.getId();
                            String name = model.getBakeryName();
                            Bundle params = new Bundle();
                            params.putDouble("total", total);
                            params.putString("bakeryID", bakeryID);
                            params.putString("offerID", offerID);
                            params.putString("userName", userName);
                            params.putString("bakeryName", bakeryName);
                            params.putBoolean("isOffer", true);
                            Intent intentProductList = new Intent(mContext, ProductCartActivity.class);
                            intentProductList.putExtra("cart", products);
                            intentProductList.putExtras(params);
                            mContext.startActivity(intentProductList);
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
        });

        viewHolder.progressBar.setVisibility(View.GONE);
    }

    private void makeAction(String action, OfferViewHolder viewHolder, String productImage, double price) {
        switch (action) {
            case "Editar":
                Double productPrice = Double.parseDouble(viewHolder.tvPrice.getText().toString());
                int percent = Integer.parseInt(viewHolder.tvDiscount.getText().toString());
                int itemSale = Integer.parseInt(viewHolder.tvItensSale.getText().toString());
                Bundle params = new Bundle();
                params.putString("bakeryID", bakeryID);
                params.putBoolean("update", true);
                params.putString("productName", productName);
                params.putDouble("productPrice", productPrice);
                params.putString("productID", productID);
                params.putString("productImage", productImage);
                params.putString("offerID", offerID);
                params.putInt("items", itemSale);
                params.putInt("percent", percent);
                Intent intentFormProduct = new Intent(mContext, FormOfferActivity.class);
                intentFormProduct.putExtras(params);
                mContext.startActivity(intentFormProduct);
                break;
            case "Excluir":
                DialogHandler appdialog = new DialogHandler();
                appdialog.Confirm(mContext, "Excluir?", "Deseja excluir a oferta?",
                        "NÃO", "SIM", yes(viewHolder, price), no());
                break;
        }
    }

    public Runnable yes(final OfferViewHolder viewHolder, final double price) {
        return new Runnable() {
            public void run() {
                mDatabaseReference.child("offers").child(offerID).removeValue();
                mDatabaseReference.child("bakeries").child(bakeryID).child("products").child(productID).
                        child("inOffer").setValue(false);
                mDatabaseReference.child("bakeries").child(bakeryID).child("products").child(productID).
                        child("productPrice").setValue(price);
                AppUtil.showToast(mContext, productName + " excluido!");
            }
        };
    }

    public Runnable no() {
        return new Runnable() {
            public void run() {
                Log.d("Test", "This from nop proc");
            }
        };
    }

    protected void decrease(OfferViewHolder productViewHolder) {
        if (isValid(productViewHolder)) {
            int value = getValue(productViewHolder) - 1;
            tvUnits.setText(String.valueOf(value));
        }
    }

    protected void increase(OfferViewHolder productViewHolder, Integer itensSale) {
        int value = getValue(productViewHolder) + 1;
        if (value <= itensSale) {
            tvUnits.setText(String.valueOf(value));
        }
    }

    private int getValue(OfferViewHolder productViewHolder) {
        String value = tvUnits.getText().toString();
        return (!value.equals("")) ? Integer.valueOf(value) : 0;
    }

    private boolean isValid(OfferViewHolder productViewHolder) {
        return (getValue(productViewHolder) > 0);
    }
}
