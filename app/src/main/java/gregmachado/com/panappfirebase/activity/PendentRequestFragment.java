package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.viewHolder.RequestViewHolderUser;

/**
 * Created by gregmachado on 16/10/16.
 */
public class PendentRequestFragment extends Fragment {

    private static final String TAG = PendentRequestFragment.class.getSimpleName();
    private Bundle params;
    private TextView txtAnswer;
    private String userId, bakeryId;
    private ProgressBar simpleProgressBar;
    private ImageView ic_request;
    private RecyclerView rvRequest;
    private List<Request> list;
    private Boolean isAdmin;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();
    private FirebaseAuth firebaseAuth;

    public PendentRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pendent_request, container, false);

        simpleProgressBar = (ProgressBar) v.findViewById(R.id.simpleProgressBar);
        ic_request = (ImageView) v.findViewById(R.id.ic_favorite);
        ic_request.setVisibility(View.INVISIBLE);
        txtAnswer = (TextView) v.findViewById(R.id.tv_answer);
        rvRequest = (RecyclerView) v.findViewById(R.id.request_list);
        rvRequest.setItemAnimator(new DefaultItemAnimator());
        Resources resources = getResources();
        Intent it = getActivity().getIntent();
        params = it.getExtras();
        if (params != null) {
            bakeryId = params.getString("bakeryId");
            isAdmin = params.getBoolean("isAdmin");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
        userId = userFirebase.getUid();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRequest();
    }

    private void loadRequest() {
        simpleProgressBar.setVisibility(View.VISIBLE);
        mDatabaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                simpleProgressBar.setVisibility(View.INVISIBLE);
                if (dataSnapshot.hasChild("requests")) {
                    FirebaseRecyclerAdapter<Request, RequestViewHolderUser> adapter = new FirebaseRecyclerAdapter<Request, RequestViewHolderUser>(
                            Request.class,
                            R.layout.card_request_user,
                            RequestViewHolderUser.class,
                            //referencing the node where we want the database to store the data from our Object
                            mDatabaseReference.child("users").child(userId).child("requests").getRef()
                    ) {
                        @Override
                        protected void populateViewHolder(final RequestViewHolderUser viewHolder, final Request model, final int position) {
                            if (txtAnswer.getVisibility() == View.VISIBLE) {
                                txtAnswer.setVisibility(View.GONE);
                            }
                            if (ic_request.getVisibility() == View.VISIBLE) {
                                ic_request.setVisibility(View.GONE);
                            }
                            viewHolder.tvRequestCode.setText(model.getRequestID());
                            viewHolder.tvMethod.setText(model.getMethod());
                            viewHolder.tvHour.setText(model.getScheduleHour());
                            viewHolder.tvBakeryName.setText(model.getBakeryID());
                            viewHolder.tvDate.setText(model.getScheduleDate());
                            viewHolder.tvUnits.setText(model.getProductList().size());
                            viewHolder.tvStatus.setText("Pendente");

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.w(TAG, "You clicked on " + model.getRequestID());
                                }
                            });
                        }
                    };

                    rvRequest.setAdapter(adapter);
                } else {
                    txtAnswer.setVisibility(View.VISIBLE);
                    ic_request.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
        simpleProgressBar.setVisibility(View.GONE);
    }
}
