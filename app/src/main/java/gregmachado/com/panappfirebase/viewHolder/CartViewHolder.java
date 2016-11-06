package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.interfaces.ItemClickListener;


/**
 * Created by gregmachado on 02/10/16.
 */
public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tvProductName, tvPrice, tvUnits, tvSubTotal;
    public ImageButton btnRemoveCart;
    private ItemClickListener clickListener;

    public CartViewHolder(View itemView, ItemClickListener listener) {
        super(itemView);
        this.clickListener = listener;
        tvProductName = (TextView) itemView.findViewById(R.id.tv_product_cart);
        tvPrice = (TextView) itemView.findViewById(R.id.tv_price_cart);
        tvSubTotal = (TextView) itemView.findViewById(R.id.tv_price_cart_sub);
        btnRemoveCart = (ImageButton) itemView.findViewById(R.id.btn_remove_cart);
        tvUnits = (TextView) itemView.findViewById(R.id.tv_unit_cart);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(v, getAdapterPosition());
    }
}

