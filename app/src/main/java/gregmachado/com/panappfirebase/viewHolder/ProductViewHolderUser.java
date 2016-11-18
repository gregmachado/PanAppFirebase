package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 30/10/16.
 */
public class ProductViewHolderUser extends RecyclerView.ViewHolder {

    public TextView tvProductName, tvPrice, tvCategory, tvItensSale, tvUnitInCart;
    public ImageView ivProduct;
    public View mView;
    public Button btnAddCart, btnRemoveCart;
    public LinearLayout llCart;
    public ProgressBar progressBar;

    public ProductViewHolderUser(View v) {
        super(v);
        mView = v;
        tvProductName = (TextView) itemView.findViewById(R.id.tv_product);
        tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        tvCategory = (TextView) itemView.findViewById(R.id.tv_type);
        tvItensSale = (TextView) itemView.findViewById(R.id.tv_unit);
        ivProduct = (ImageView) itemView.findViewById(R.id.iv_product);
        btnAddCart = (Button) itemView.findViewById(R.id.btn_add_cart);
        tvUnitInCart = (TextView) itemView.findViewById(R.id.tv_unit_in_cart);
        btnRemoveCart = (Button) itemView.findViewById(R.id.btn_remove_cart);
        llCart = (LinearLayout) itemView.findViewById(R.id.ll_cart);
        progressBar = (ProgressBar) itemView.findViewById(R.id.photo_progressbar);
    }
}
