package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 06/11/16.
 */
public class RequestViewHolderUser extends RecyclerView.ViewHolder{
    public TextView tvRequestCode, tvBakeryName, tvDate, tvHour, tvUnits, tvMethod, tvStatus;
    public View mView;

    public RequestViewHolderUser(View v) {
        super(v);
        mView = v;
        tvRequestCode = (TextView) itemView.findViewById(R.id.tv_request_code);
        tvBakeryName = (TextView) itemView.findViewById(R.id.tv_request_bakery_name);
        tvDate = (TextView) itemView.findViewById(R.id.tv_request_date);
        tvHour = (TextView) itemView.findViewById(R.id.tv_request_hour);
        tvUnits = (TextView) itemView.findViewById(R.id.tv_request_itens);
        tvMethod = (TextView) itemView.findViewById(R.id.tv_request_method);
        tvStatus = (TextView) itemView.findViewById(R.id.tv_request_status);
    }
}
