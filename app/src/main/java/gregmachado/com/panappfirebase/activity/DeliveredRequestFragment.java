package gregmachado.com.panappfirebase.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.RequestAdapter;
import gregmachado.com.panappfirebase.domain.Request;

/**
 * Created by gregmachado on 16/10/16.
 */
public class DeliveredRequestFragment extends Fragment {

    private static final String TAG = DeliveredRequestFragment.class.getSimpleName();
    private TextView tvNoDeliveredRequest;
    private String id;
    private ProgressBar simpleProgressBar;
    private ImageView ic_request;
    private RecyclerView rvRequest;
    private List<Request> list;
    private Boolean type;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();
    private RequestAdapter adapter;

    public DeliveredRequestFragment() {
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
        View v = inflater.inflate(R.layout.fragment_delivered_request, container, false);
        simpleProgressBar = (ProgressBar) v.findViewById(R.id.simpleProgressBar);
        ic_request = (ImageView) v.findViewById(R.id.ic_delivered_request);
        ic_request.setVisibility(View.INVISIBLE);
        tvNoDeliveredRequest = (TextView) v.findViewById(R.id.tv_no_delivered_request);
        rvRequest = (RecyclerView) v.findViewById(R.id.rv_delivered_request);
        rvRequest.setItemAnimator(new DefaultItemAnimator());
        rvRequest.setLayoutManager(new LinearLayoutManager(getContext()));
        id = getArguments().getString("id");
        type = getArguments().getBoolean("type");
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRequest();
    }

    private void loadRequest() {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("requests")) {
                    if (dataSnapshot.child("requests").hasChild(id)) {
                        if (tvNoDeliveredRequest.getVisibility() == View.VISIBLE) {
                            tvNoDeliveredRequest.setVisibility(View.GONE);
                        }
                        if (ic_request.getVisibility() == View.VISIBLE) {
                            ic_request.setVisibility(View.GONE);
                        }
                        adapter = new RequestAdapter(mDatabaseReference.child("requests").child(id).
                                orderByChild("delivered").equalTo(true), getContext(), id, type
                        ) {
                        };
                        simpleProgressBar.setVisibility(View.GONE);
                        rvRequest.setAdapter(adapter);
                    } else {
                        showNoRequestScreen();
                    }
                } else {
                    showNoRequestScreen();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    private void showNoRequestScreen() {
        simpleProgressBar.setVisibility(View.GONE);
        tvNoDeliveredRequest.setVisibility(View.VISIBLE);
        ic_request.setVisibility(View.VISIBLE);
    }
}


