package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;


/**
 * Created by gregmachado on 11/09/16.
 */
public class BakeryViewHolder extends RecyclerView.ViewHolder{

    public TextView tvBakeryName, tvDistance;
    public ImageView ivBakery;
    public ImageButton ibtnFavoriteOff, ibtnFavoriteOn, ibtnInfo;
    public View mView;

    public BakeryViewHolder(View v) {
        super(v);
        mView = v;
        tvBakeryName = (TextView) itemView.findViewById(R.id.tv_bakery);
        ivBakery = (ImageView) itemView.findViewById(R.id.iv_bakery);
        tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
        ibtnFavoriteOff = (ImageButton) itemView.findViewById(R.id.ibtn_favorite_off);
        ibtnFavoriteOn = (ImageButton) itemView.findViewById(R.id.ibtn_favorite_on);
        ibtnInfo = (ImageButton) itemView.findViewById(R.id.ibtn_info);
    }
}
