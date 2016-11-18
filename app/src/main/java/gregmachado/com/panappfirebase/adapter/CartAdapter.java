package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.activity.ProductCartActivity;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.interfaces.ItemClickListener;
import gregmachado.com.panappfirebase.viewHolder.CartViewHolder;

/**
 * Created by gregmachado on 02/10/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private Context mContext;
    private final List<Product> products;
    private Double price, parcialPrice;
    private int items;
    private ItemClickListener clickListener;
    private ProductCartActivity productCartActivity;
    private DecimalFormat precision = new DecimalFormat("#0.00");

    public CartAdapter(ProductCartActivity productCartActivity, Context contexts, List<Product> products,
                       ItemClickListener listener, double parcialPrice) {
        this.products = products;
        this.mContext = contexts;
        this.clickListener = listener;
        this.productCartActivity = productCartActivity;
        this.parcialPrice = parcialPrice;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        final View v = layoutInflater.inflate(R.layout.card_cart, viewGroup, false);
        return new CartViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder productViewHolder, final int i) {
        productViewHolder.tvProductName.setText(products.get(i).getProductName());
        productViewHolder.tvPrice.setText(precision.format(products.get(i).getProductPrice()));
        productViewHolder.tvUnits.setText(String.valueOf(products.get(i).getUnit()));
        Double subTotal = products.get(i).getProductPrice() * products.get(i).getUnit();
        productViewHolder.tvSubTotal.setText(precision.format(subTotal));

        productViewHolder.btnRemoveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Excluir?");
                builder.setMessage("Deseja realmente excluir o item?");
                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        items = products.get(i).getUnit();
                        price = products.get(i).getProductPrice() * items;
                        String itemLabel = products.get(i).getProductName();
                        products.remove(i);
                        notifyItemRemoved(i);
                        notifyItemRangeChanged(i, products.size());
                        parcialPrice = parcialPrice - price;
                        productCartActivity.setValuesToolbarBottom(parcialPrice);
                        Toast.makeText(mContext, "Removed : " + itemLabel, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private int getValue(CartViewHolder productViewHolder) {
        String value = productViewHolder.tvUnits.getText().toString();
        return (!value.equals("")) ? Integer.valueOf(value) : 0;
    }

    public ArrayList<Product> getProducts(){
        return (ArrayList<Product>) products;
    }
}
