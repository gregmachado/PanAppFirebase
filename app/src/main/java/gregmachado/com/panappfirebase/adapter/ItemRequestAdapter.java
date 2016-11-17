package gregmachado.com.panappfirebase.adapter;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.viewHolder.ItemRequestViewHolder;

/**
 * Created by gregmachado on 17/11/16.
 */
public class ItemRequestAdapter extends FirebaseRecyclerAdapter<Product, ItemRequestViewHolder> {

    private static final String TAG = ItemRequestAdapter.class.getSimpleName();
    private Context mContext;

    public ItemRequestAdapter(Query ref, Context context) {
        super(Product.class, R.layout.activity_request_detail, ItemRequestViewHolder.class, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(final ItemRequestViewHolder viewHolder, final Product model, final int position) {
        viewHolder.tvItems.setText(model.getProductName());
        viewHolder.tvPrice.setText(String.valueOf(model.getProductPrice() * model.getUnit()));
        viewHolder.tvUnits.setText(model.getUnit());
    }
}