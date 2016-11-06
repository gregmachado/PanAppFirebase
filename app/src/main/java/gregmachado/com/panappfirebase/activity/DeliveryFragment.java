package gregmachado.com.panappfirebase.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Bakery;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.util.CustomTimePickerDialog;
import gregmachado.com.panappfirebase.util.DateUtil;

/**
 * Created by gregmachado on 03/11/16.
 */
public class DeliveryFragment extends Fragment {

    private static final String TAG = DeliveryFragment.class.getSimpleName();
    private TextView tvTime, tvNoHasDelivery,lbl01, lbl02, lbl03, tvStartTime, tvFinishTime;
    private RadioButton rbToday, rbTomorrow;
    private RadioGroup radioGroup;
    private String today, tomorrow, scheduleDay, scheduleHour, adress, method, creatonDate;
    private Request request;
    private String bakeryId, userId;
    private List<Product> products;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();;
    private ProgressBar simpleProgressBar;
    private Button btnFinish;
    private Spinner spinner;

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
        tvStartTime = (TextView) v.findViewById(R.id.tv_start_time);
        tvFinishTime = (TextView) v.findViewById(R.id.tv_finish_time);
        radioGroup = (RadioGroup) v.findViewById(R.id.rg_date_delivery);
        simpleProgressBar = (ProgressBar) v.findViewById(R.id.simpleProgressBar);
        tvNoHasDelivery = (TextView) v.findViewById(R.id.tv_no_has_delivery);
        lbl01 = (TextView) v.findViewById(R.id.lbl01);
        lbl02 = (TextView) v.findViewById(R.id.lbl02);
        lbl03 = (TextView) v.findViewById(R.id.lbl03);
        spinner = (Spinner) v.findViewById(R.id.sp_adress);
        loadAdress();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adress = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnFinish = (Button) v.findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();
                if(compareTime()){
                    request = initRequest();
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    String requestID = mDatabaseReference.push().getKey();
                    request.setRequestID(requestID);
                    mDatabaseReference.child("users").child(userId).child("requests").child(requestID).setValue(request);
                    Toast.makeText(getContext(), "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Horário do pedido inválido!", Toast.LENGTH_SHORT).show();
                    tvTime.setTextColor(getResources().getColor(R.color.errorColor));
                }

            }
        });
        dateTimeSelect();
        return v;
    }

    private boolean compareTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date dataS = format.parse(tvStartTime.getText().toString().trim());
            Date dataF = format.parse(tvFinishTime.getText().toString().trim());
            Date data = format.parse(tvTime.getText().toString().trim());
            return !(data.after(dataF) || (data.before(dataS)));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    private void loadAdress() {
        //user test
        //userId = "ZTx7CF0LMfbZkR2e8DB2WxkV1KZ2";
        //bakeryId = "-KVuZ4G7BewSuHBfNB7x";
        mDatabaseReference.child("bakeries").child(bakeryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bakery bakery = dataSnapshot.getValue(Bakery.class);
                tvStartTime.setText(bakery.getStartTime());
                tvFinishTime.setText(bakery.getFinishTime());
                if(bakery.isHasDelivery()){
                    mDatabaseReference.child("users").child(userId).child("adress").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                final List<String> adress = new ArrayList<String>();
                                for (DataSnapshot adressSnapshot: dataSnapshot.getChildren()) {
                                    String street = adressSnapshot.child("street").getValue(String.class);
                                    Integer num = adressSnapshot.child("number").getValue(Integer.class);
                                    String number = String.valueOf(num);
                                    String complement = adressSnapshot.child("complement").getValue(String.class);
                                    String district = adressSnapshot.child("district").getValue(String.class);
                                    String city = adressSnapshot.child("city").getValue(String.class);
                                    String reference = adressSnapshot.child("reference").getValue(String.class);
                                    String completAdress = street + ", " + number + " " + complement + " ," +
                                            district + " - " + city + "(" + reference + ")";
                                    adress.add(completAdress);
                                }
                                ArrayAdapter<String> adressAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, adress);
                                adressAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                                spinner.setAdapter(adressAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
                } else {
                    tvNoHasDelivery.setVisibility(View.VISIBLE);
                    lbl01.setVisibility(View.INVISIBLE);
                    lbl02.setVisibility(View.INVISIBLE);
                    lbl03.setVisibility(View.INVISIBLE);
                    tvTime.setVisibility(View.INVISIBLE);
                    rbToday.setVisibility(View.INVISIBLE);
                    rbTomorrow.setVisibility(View.INVISIBLE);
                    radioGroup.setVisibility(View.INVISIBLE);
                    btnFinish.setVisibility(View.INVISIBLE);
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
        request.setAdress(adress);
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
