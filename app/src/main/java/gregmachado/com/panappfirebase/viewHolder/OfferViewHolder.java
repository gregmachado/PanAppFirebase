package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 19/11/16.
 */
public class OfferViewHolder extends RecyclerView.ViewHolder {

    public TextView tvProductName, tvPrice, tvItensSale, tvDiscount, tvBakeryName;
    public ImageView ivProduct;
    public View mView;
    public ProgressBar progressBar;

    public OfferViewHolder(View v) {
        super(v);
        mView = v;
        tvProductName = (TextView) itemView.findViewById(R.id.tv_product);
        tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        tvItensSale = (TextView) itemView.findViewById(R.id.tv_unit);
        ivProduct = (ImageView) itemView.findViewById(R.id.iv_product);
        tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount);
        progressBar = (ProgressBar) itemView.findViewById(R.id.photo_progressbar);
        tvBakeryName = (TextView) itemView.findViewById(R.id.tv_bakery);
    }
}
