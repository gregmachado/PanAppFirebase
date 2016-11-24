package gregmachado.com.panappfirebase.adapter;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.activity.ProductListActivity;
import gregmachado.com.panappfirebase.domain.Historic;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.viewHolder.HistoricViewHolder;
import gregmachado.com.panappfirebase.viewHolder.ProductViewHolderUser;

/**
 * Created by gregmachado on 24/11/16.
 */
public class HistoricAdapter extends FirebaseRecyclerAdapter<Historic, HistoricViewHolder> {
    private static final String TAG = HistoricAdapter.class.getSimpleName();
    private Context mContext;

    public HistoricAdapter(Query ref, Context context) {
        super(Historic.class, R.layout.item_historic, HistoricViewHolder.class, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(final HistoricViewHolder viewHolder, final Historic model, final int position) {
        viewHolder.tvData.setText(model.getDate());
        viewHolder.tvMsg.setText(model.getMsg());
    }
}
