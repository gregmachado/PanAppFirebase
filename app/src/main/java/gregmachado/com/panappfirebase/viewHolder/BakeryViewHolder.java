package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;


/**
 * Created by gregmachado on 11/09/16.
 */
public class BakeryViewHolder extends RecyclerView.ViewHolder{

    public TextView tvBakeryName, tvPhone, tvDistrict, tvDistance;
    public ImageView ivBakery;
    public Button btnMore;
    public ImageButton ibtnFavorite;
    public View mView;

    public BakeryViewHolder(View v) {
        super(v);
        mView = v;
        tvBakeryName = (TextView) itemView.findViewById(R.id.tv_bakery);
        tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);
        tvDistrict = (TextView) itemView.findViewById(R.id.tv_district);
        ivBakery = (ImageView) itemView.findViewById(R.id.iv_bakery);
        tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
        btnMore = (Button) itemView.findViewById(R.id.btn_more);
        ibtnFavorite = (ImageButton) itemView.findViewById(R.id.ibtn_favorite);
    }
}
