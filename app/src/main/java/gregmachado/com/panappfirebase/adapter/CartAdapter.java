package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.activity.ProductCartActivity;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.interfaces.ItemClickListener;
import gregmachado.com.panappfirebase.viewHolder.CartViewHolder;

/**
 * Created by gregmachado on 02/10/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private Context mContext;
    private final List<Product> products;
    private Double price, parcialPrice = 0.00;
    private int unit, items;
    private ItemClickListener clickListener;
    private int position;
    private ProductCartActivity productCartActivity;
    private int count = 0;

    public CartAdapter(ProductCartActivity productCartActivity, Context contexts, List<Product> products, ItemClickListener listener) {
        this.products = products;
        this.mContext = contexts;
        this.clickListener = listener;
        this.productCartActivity = productCartActivity;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        final View v = layoutInflater.inflate(R.layout.card_cart, viewGroup, false);
        return new CartViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder productViewHolder, final int i) {
        productViewHolder.tvProductName.setText(products.get(i).getProductName());
        productViewHolder.tvPrice.setText(String.valueOf(products.get(i).getProductPrice()));
        productViewHolder.tvUnits.setText(String.valueOf(products.get(i).getUnit()));

        String strBase64 = products.get(i).getProductImage();
        byte[] imgBytes = Base64.decode(strBase64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);

        //productViewHolder.ivProduct.setImageBitmap(bitmap);

        productViewHolder.btnRemoveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items = getValue(productViewHolder);
                price = products.get(i).getProductPrice() * items;
                String itemLabel = products.get(position).getProductName();
                products.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, products.size());
                parcialPrice = productCartActivity.getValuesToolbarBottom();
                parcialPrice = parcialPrice - price;
                productCartActivity.setValuesToolbarBottom(String.valueOf(parcialPrice));
                Toast.makeText(mContext, "Removed : " + itemLabel, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private int getValue(CartViewHolder productViewHolder) {
        String value = productViewHolder.tvUnits.getText().toString();
        return (!value.equals("")) ? Integer.valueOf(value) : 0;
    }

}
