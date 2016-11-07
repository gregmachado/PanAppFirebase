package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Request;

/**
 * Created by gregmachado on 16/10/16.
 */
public class DeliveredRequestFragment extends Fragment {

    private static final String TAG = DeliveredRequestFragment.class.getSimpleName();
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

    }

    private void getRequest(Request requestAux, Request request) {
        request.setDelivered(requestAux.getDelivered());
        request.setAdress(requestAux.getAdress());
        request.setMethod(requestAux.getMethod());
        request.setScheduleHour(requestAux.getScheduleHour());
        request.setScheduleDate(requestAux.getScheduleDate());
        request.setBakeryID(requestAux.getBakeryID());
        request.setCreationDate(requestAux.getCreationDate());
        request.setUserID(requestAux.getUserID());
    }
}
