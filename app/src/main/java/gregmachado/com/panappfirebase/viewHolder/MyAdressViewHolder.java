package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 05/11/16.
 */
public class MyAdressViewHolder extends RecyclerView.ViewHolder{

    public TextView tvStreet, tvNumber, tvComplement, tvDistrict, tvCityState, tvName, tvCep, tvReference;
    public View mView;

    public MyAdressViewHolder(View v) {
        super(v);
        mView = v;
        tvStreet = (TextView) itemView.findViewById(R.id.tv_street);
        tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
        tvCityState = (TextView) itemView.findViewById(R.id.tv_city_state);
        tvComplement = (TextView) itemView.findViewById(R.id.tv_complement);
        tvDistrict = (TextView) itemView.findViewById(R.id.tv_district);
        tvName = (TextView) itemView.findViewById(R.id.tv_adress_name);
        tvCep = (TextView) itemView.findViewById(R.id.tv_cep);
        tvReference = (TextView) itemView.findViewById(R.id.tv_reference);
    }
}