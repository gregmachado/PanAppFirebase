package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 05/11/16.
 */
public class AdressViewHolder extends RecyclerView.ViewHolder{

    public TextView tvStreet, tvNumber, tvComplement, tvDistrict, tvCity;
    public ImageView icCheck;
    public View mView;

    public AdressViewHolder(View v) {
        super(v);
        mView = v;
        tvStreet = (TextView) itemView.findViewById(R.id.tv_street);
        tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
        tvCity = (TextView) itemView.findViewById(R.id.tv_city);
        tvComplement = (TextView) itemView.findViewById(R.id.tv_complement);
        tvDistrict = (TextView) itemView.findViewById(R.id.tv_district);
        icCheck = (ImageView) itemView.findViewById(R.id.ic_check);
    }
}