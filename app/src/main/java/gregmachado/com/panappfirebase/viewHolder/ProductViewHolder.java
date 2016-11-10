package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 30/10/16.
 */
public class ProductViewHolder extends RecyclerView.ViewHolder{

    public TextView tvProductName, tvProductPrice, tvProductType, tvItensSale, tvUnits;
    public ImageView ivProduct;
    public View mView;
    public Button btnSale;
    public ImageButton btnPlus, btnLess, btnMore;

    public ProductViewHolder(View v) {
        super(v);
        mView = v;
        tvProductName = (TextView) itemView.findViewById(R.id.tv_product);
        tvProductPrice = (TextView) itemView.findViewById(R.id.tv_price);
        tvProductType = (TextView) itemView.findViewById(R.id.tv_type);
        ivProduct = (ImageView) itemView.findViewById(R.id.iv_product);
        tvItensSale = (TextView) itemView.findViewById(R.id.tv_unit);
        tvUnits = (TextView) itemView.findViewById(R.id.tv_unit_sale);
        btnPlus = (ImageButton) itemView.findViewById(R.id.btn_plus);
        btnLess = (ImageButton) itemView.findViewById(R.id.btn_less);
        btnSale = (Button) itemView.findViewById(R.id.btn_sale);
        btnMore = (ImageButton) itemView.findViewById(R.id.btn_more);
    }
}
