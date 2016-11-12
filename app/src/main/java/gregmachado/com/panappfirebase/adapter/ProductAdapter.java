package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.activity.ProductListActivity;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.viewHolder.ProductViewHolderUser;

/**
 * Created by gregmachado on 10/11/16.
 */
public class ProductAdapter extends FirebaseRecyclerAdapter<Product, ProductViewHolderUser> {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String TAG = ProductAdapter.class.getSimpleName();
    private Context mContext;
    private Handler handler;
    private TextView tvUnits;
    private int items, count = 0;
    private double price, parcialPrice;
    private ArrayList<Product> productsToCart = new ArrayList<>();
    private ProductListActivity productListActivity;
    private String bakeryID;

    public ProductAdapter(Query ref, Context context, ProductListActivity productListActivity, String bakeryID) {
        super(Product.class, R.layout.card_product_user, ProductViewHolderUser.class, ref);
        this.mContext = context;
        this.productListActivity = productListActivity;
        this.bakeryID = bakeryID;
    }

    @Override
    protected void populateViewHolder(final ProductViewHolderUser viewHolder, final Product model, final int position) {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.tvProductName.setText(model.getProductName());
        viewHolder.tvPrice.setText(String.valueOf(model.getProductPrice()));
        viewHolder.tvCategory.setText(model.getType());
        viewHolder.tvItensSale.setText(String.valueOf(model.getItensSale()));
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
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "You clicked on " + model.getBakeryId() + " / " + model.getId());
            }
        });

        viewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
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
                        increase(viewHolder, Integer.valueOf(viewHolder.tvItensSale.getText().toString()));
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
                            increase(viewHolder, Integer.valueOf(viewHolder.tvItensSale.getText().toString()));
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
                        Product product = new Product();
                        product.setProductName(model.getProductName());
                        product.setProductPrice(model.getProductPrice());
                        product.setType(model.getType());
                        product.setBakeryId(model.getBakeryId());
                        product.setProductImage(model.getProductImage());
                        product.setId(model.getId());
                        items = getValue(viewHolder);
                        product.setUnit(items);
                        Log.w(TAG, "Items " + items);
                        price = model.getProductPrice() * items;
                        if (items > 0) {
                            productsToCart.add(product);
                            viewHolder.btnAddCart.setVisibility(View.INVISIBLE);
                            viewHolder.btnRemoveCart.setVisibility(View.VISIBLE);
                            count++;
                            parcialPrice = parcialPrice + price;
                            setValuesToolbarBottom(String.valueOf(count), String.valueOf(parcialPrice));
                            viewHolder.tvUnitInCart.setText(String.valueOf(items));
                            viewHolder.llCart.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
        viewHolder.btnRemoveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iterator<Product> it = productsToCart.iterator();
                while (it.hasNext()) {
                    if (it.next().getId().equals(model.getId())) {
                        it.remove();
                        items = getValue(viewHolder);
                        price = model.getProductPrice() * items;
                    }
                }
                viewHolder.btnAddCart.setVisibility(View.VISIBLE);
                viewHolder.btnRemoveCart.setVisibility(View.INVISIBLE);
                count--;
                parcialPrice = parcialPrice - price;
                setValuesToolbarBottom(String.valueOf(count), String.valueOf(parcialPrice));
                viewHolder.llCart.setVisibility(View.INVISIBLE);
            }
        });
        viewHolder.progressBar.setVisibility(View.GONE);
    }

    protected void decrease(ProductViewHolderUser productViewHolder) {
        if (isValid(productViewHolder)) {
            int value = getValue(productViewHolder)-1;
            tvUnits.setText(String.valueOf(value));
        }
    }

    protected void increase(ProductViewHolderUser productViewHolder, Integer itensSale) {
        int value = getValue(productViewHolder)+1;
        if(value <= itensSale){
            tvUnits.setText(String.valueOf(value));
        }
    }

    private int getValue(ProductViewHolderUser productViewHolder) {
        String value = tvUnits.getText().toString();
        return (!value.equals("")) ? Integer.valueOf(value) : 0;
    }

    private boolean isValid(ProductViewHolderUser productViewHolder) {
        return (getValue(productViewHolder) > 0);
    }

    public void setValuesToolbarBottom(String items, String price) {
        productListActivity.tvItens.setText(items);
        productListActivity.tvPrice.setText(price);
    }

    public ArrayList<Product> getProductsToCart(){
        return productsToCart;
    }
}
