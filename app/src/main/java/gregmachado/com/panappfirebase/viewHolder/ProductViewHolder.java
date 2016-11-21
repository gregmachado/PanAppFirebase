package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 30/10/16.
 */
public class ProductViewHolder extends RecyclerView.ViewHolder{

    public TextView tvProductName, tvProductPrice, tvProductType, tvItensSale, tvDiscount, tvPriceInOffer, lblPercent;
    public ImageView ivProduct, icOffer;
    public View mView;
    public Button btnSale;
    public ImageButton btnMore;
    public ProgressBar progressBar;

    public ProductViewHolder(View v) {
        super(v);
        mView = v;
        tvProductName = (TextView) itemView.findViewById(R.id.tv_product);
        tvProductPrice = (TextView) itemView.findViewById(R.id.tv_price);
        tvProductType = (TextView) itemView.findViewById(R.id.tv_type);
        ivProduct = (ImageView) itemView.findViewById(R.id.iv_product);
        tvItensSale = (TextView) itemView.findViewById(R.id.tv_unit);
        btnSale = (Button) itemView.findViewById(R.id.btn_sale);
        btnMore = (ImageButton) itemView.findViewById(R.id.btn_more);
        progressBar = (ProgressBar) itemView.findViewById(R.id.photo_progressbar);
        tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount);
        tvPriceInOffer = (TextView) itemView.findViewById(R.id.tv_price_in_offer);
        lblPercent = (TextView) itemView.findViewById(R.id.lbl_percent);
        icOffer = (ImageView) itemView.findViewById(R.id.ic_offer);
    }
}
