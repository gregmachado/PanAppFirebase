package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 30/10/16.
 */
public class ProductViewHolderUser extends RecyclerView.ViewHolder {

    public TextView tvProductName, tvPrice, tvCategory, tvItensSale, tvUnitSale;
    public ImageView ivProduct;
    public View mView;
    public Button btnPlus, btnLess, btnAddCart;

    public ProductViewHolderUser(View v) {
        super(v);
        mView = v;
        tvProductName = (TextView) itemView.findViewById(R.id.tv_product);
        tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        tvCategory = (TextView) itemView.findViewById(R.id.tv_type);
        tvItensSale = (TextView) itemView.findViewById(R.id.tv_unit);
        ivProduct = (ImageView) itemView.findViewById(R.id.iv_product);
        btnPlus = (Button) itemView.findViewById(R.id.btn_plus);
        btnLess = (Button) itemView.findViewById(R.id.btn_less);
        btnAddCart = (Button) itemView.findViewById(R.id.btn_add_cart);
        tvUnitSale = (TextView) itemView.findViewById(R.id.tv_unit_sale);
    }
}
