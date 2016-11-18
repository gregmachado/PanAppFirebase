package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.viewHolder.ItemRequestViewHolder;

/**
 * Created by gregmachado on 17/11/16.
 */
public class ItemRequestAdapter extends RecyclerView.Adapter<ItemRequestViewHolder> {

    private static final String TAG = ItemRequestAdapter.class.getSimpleName();
    private Context mContext;
    private List<Product> products;
    private DecimalFormat precision = new DecimalFormat("#0.00");

    public ItemRequestAdapter(List<Product> products, Context context) {
        this.products = products;
        this.mContext = context;
    }

    @Override
    public ItemRequestViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        final View v = layoutInflater.inflate(R.layout.item_request_detail, viewGroup, false);
        return new ItemRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemRequestViewHolder holder, int position) {
        holder.tvItems.setText(products.get(position).getProductName());
        double total = products.get(position).getProductPrice() * products.get(position).getUnit();
        holder.tvPrice.setText(precision.format(total));
        holder.tvUnits.setText(String.valueOf(products.get(position).getUnit()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}