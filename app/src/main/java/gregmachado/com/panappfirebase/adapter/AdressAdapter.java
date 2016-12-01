package gregmachado.com.panappfirebase.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.activity.FormAdressActivity;
import gregmachado.com.panappfirebase.domain.Adress;
import gregmachado.com.panappfirebase.viewHolder.MyAdressViewHolder;

/**
 * Created by gregmachado on 11/11/16.
 */
public class AdressAdapter extends FirebaseRecyclerAdapter<Adress, MyAdressViewHolder> {
    private static final String TAG = AdressAdapter.class.getSimpleName();
    private Context mContext;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();

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
        viewHolder.ibtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adressID = model.getId();
                String userID = model.getUserID();
                Bundle params = new Bundle();
                params.putString("userID", userID);
                params.putString("adressID", adressID);
                params.putBoolean("update", true);
                Intent intent = new Intent(mContext, FormAdressActivity.class);
                intent.putExtras(params);
                mContext.startActivity(intent);
            }
        });
        viewHolder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = model.getUserID();
                final String adressID = model.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Excluir?");
                builder.setMessage("Deseja realmente excluir o item?");
                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        mDatabaseReference.child("users").child(userID).child("adress").child(adressID)
                                .removeValue();
                        Toast.makeText(mContext, "Endereço removido!", Toast.LENGTH_SHORT).show();
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
}
