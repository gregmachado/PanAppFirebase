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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Adress;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.util.CustomTimePickerDialog;
import gregmachado.com.panappfirebase.util.DateUtil;
import gregmachado.com.panappfirebase.viewHolder.AdressViewHolder;

/**
 * Created by gregmachado on 03/11/16.
 */
public class DeliveryFragment extends Fragment {

    private static final String TAG = DeliveryFragment.class.getSimpleName();
    private TextView tvTime, tvStreet, tvNumber, tvDistrict, tvComplement, tvCity;
    private RadioButton rbToday, rbTomorrow;
    private RadioGroup radioGroup;
    private String today, tomorrow, scheduleDay, scheduleHour, adress, method, creatonDate;
    private Request request;
    private String bakeryId, userId;
    private List<Product> products;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();;
    private ProgressBar simpleProgressBar;
    private RecyclerView rvAdress;

    public DeliveryFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        method = "delivery";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bakeryId = getArguments().getString("bakeryID");
        userId = getArguments().getString("userID");
        products = getArguments().getParcelableArrayList("products");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_delivery, container, false);
        rbToday = (RadioButton) v.findViewById(R.id.rb_today);
        rbTomorrow = (RadioButton) v.findViewById(R.id.rb_tomorrow);
        tvTime = (TextView) v.findViewById(R.id.tv_time);
        radioGroup = (RadioGroup) v.findViewById(R.id.rg_date_delivery);
        simpleProgressBar = (ProgressBar) v.findViewById(R.id.simpleProgressBar);
        rvAdress = (RecyclerView) v.findViewById(R.id.rv_adress_delivery);
        rvAdress.setItemAnimator(new DefaultItemAnimator());
        rvAdress.setLayoutManager(new LinearLayoutManager(getContext()));
        loadAdress();
        Button btnFinish = (Button) v.findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();
                request = initRequest();
                /*mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                String requestID = mDatabaseReference.push().getKey();
                request.setRequestID(requestID);
                mDatabaseReference.child("users").child(userId).child("requests").child(requestID).setValue(request);*/
                Log.i("itens: ", request.getBakeryID() + request.getCreationDate() + request.getMethod() + request.getRequestID() +
                        request.getScheduleDate() + request.getUserID() + request.getDelivered() + request.getProductList() +
                        request.getScheduleHour());
            }
        });
        dateTimeSelect();
        return v;
    }

    private void loadAdress() {
        simpleProgressBar.setVisibility(View.VISIBLE);
        mDatabaseReference.child("users").child(userId).child("adress").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    simpleProgressBar.setVisibility(View.GONE);
                    FirebaseRecyclerAdapter<Adress, AdressViewHolder> adapter = new FirebaseRecyclerAdapter<Adress, AdressViewHolder>(
                            Adress.class,
                            R.layout.card_adress,
                            AdressViewHolder.class,
                            //referencing the node where we want the database to store the data from our Object
                            mDatabaseReference.child("users").child(userId).child("adress").getRef()
                    ) {
                        @Override
                        protected void populateViewHolder(final AdressViewHolder viewHolder, final Adress model, final int position) {

                            viewHolder.tvStreet.setText(model.getStreet());
                            viewHolder.tvComplement.setText(model.getComplement());
                            viewHolder.tvDistrict.setText(model.getDistrict());
                            viewHolder.tvCity.setText(model.getCity());
                            viewHolder.tvNumber.setText(model.getNumber());
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    viewHolder.icCheck.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    };
                    rvAdress.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    private Request initRequest() {
        Request request = new Request();
        request.setBakeryID(bakeryId);
        request.setUserID(userId);
        request.setCreationDate(creatonDate);
        request.setScheduleDate(scheduleDay);
        request.setScheduleHour(scheduleHour);
        request.setMethod(method);
        request.setDelivered(false);
        request.setProductList(products);
        return request;
    }

    private void getValues() {
        creatonDate = DateUtil.getTodayDate();
        scheduleHour = String.valueOf(tvTime.getText());
    }

    private void dateTimeSelect() {
        today = DateUtil.getToday();
        scheduleDay = today;
        rbToday.setText(String.format("%s%s", rbToday.getText(), today));
        tomorrow = DateUtil.getTomorrowDay();
        rbTomorrow.setText(String.format("%s%s", rbTomorrow.getText(), tomorrow));
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(getContext(), timeSetListener,
                        Calendar.getInstance().get(Calendar.HOUR),
                        CustomTimePickerDialog.getRoundedMinute(Calendar.getInstance().get(Calendar.MINUTE) + CustomTimePickerDialog.TIME_PICKER_INTERVAL), true);
                timePickerDialog.setTitle("Selecione o horário");
                timePickerDialog.show();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_today) {
                    scheduleDay = today;
                } else if(checkedId == R.id.rb_tomorrow) {
                    scheduleDay = tomorrow;
                }
            }
        });
    }

    private CustomTimePickerDialog.OnTimeSetListener timeSetListener = new CustomTimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvTime.setText(String.format("%02d", hourOfDay) + ":" +String.format("%02d", minute));
        }
    };
}
