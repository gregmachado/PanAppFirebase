package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Adress;
import gregmachado.com.panappfirebase.viewHolder.MyAdressViewHolder;

/**
 * Created by gregmachado on 11/11/16.
 */
public class AdressAdapter extends FirebaseRecyclerAdapter<Adress, MyAdressViewHolder> {
    private static final String TAG = AdressAdapter.class.getSimpleName();
    private Context mContext;

    public AdressAdapter(Query ref, Context context){
        super(Adress.class, R.layout.card_my_adress, MyAdressViewHolder.class, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(final MyAdressViewHolder viewHolder, final Adress model, final int position) {

        viewHolder.tvStreet.setText(model.getStreet());
        viewHolder.tvComplement.setText(model.getComplement());
        viewHolder.tvDistrict.setText(model.getDistrict());
        viewHolder.tvCityState.setText(String.format("%s/%s", model.getCity(), model.getState()));
        viewHolder.tvNumber.setText(String.valueOf(model.getNumber()));
        viewHolder.tvReference.setText(model.getReference());
        viewHolder.tvCep.setText(model.getCep());
        viewHolder.tvName.setText(model.getAdressName());
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
