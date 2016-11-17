package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 17/11/16.
 */
public class ItemRequestViewHolder extends RecyclerView.ViewHolder{
    public TextView tvUnits, tvItems, tvPrice;
    public View mView;

    public ItemRequestViewHolder(View v) {
        super(v);
        mView = v;
        tvItems = (TextView) itemView.findViewById(R.id.tv_item_name);
        tvUnits = (TextView) itemView.findViewById(R.id.tv_units);
        tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
    }
}
