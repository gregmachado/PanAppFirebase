package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 24/11/16.
 */
public class HistoricViewHolder extends RecyclerView.ViewHolder{

    public TextView tvData, tvMsg;

    public HistoricViewHolder(View v){
        super(v);
        tvData = (TextView) itemView.findViewById(R.id.tv_data);
        tvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
    }
}
