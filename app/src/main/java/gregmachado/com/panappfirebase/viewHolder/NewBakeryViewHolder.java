package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.interfaces.ItemClickListener;

/**
 * Created by gregmachado on 03/12/16.
 */
public class NewBakeryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvBakeryName, tvDistance;
    public ImageView ivBakery;
    public ImageButton ibtnFavoriteOff, ibtnFavoriteOn, ibtnInfo;
    private ItemClickListener clickListener;
    public ProgressBar progressBar;

    public NewBakeryViewHolder(View v, ItemClickListener listener) {
        super(v);
        tvBakeryName = (TextView) itemView.findViewById(R.id.tv_bakery);
        ivBakery = (ImageView) itemView.findViewById(R.id.iv_bakery);
        tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
        ibtnFavoriteOff = (ImageButton) itemView.findViewById(R.id.ibtn_favorite_off);
        ibtnFavoriteOn = (ImageButton) itemView.findViewById(R.id.ibtn_favorite_on);
        ibtnInfo = (ImageButton) itemView.findViewById(R.id.ibtn_info);
        progressBar = (ProgressBar) itemView.findViewById(R.id.simpleProgressBar);
    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(v, getAdapterPosition());
    }
}