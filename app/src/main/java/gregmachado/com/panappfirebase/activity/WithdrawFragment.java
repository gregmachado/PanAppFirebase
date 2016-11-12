package gregmachado.com.panappfirebase.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Bakery;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.util.CustomTimePickerDialog;
import gregmachado.com.panappfirebase.util.DateUtil;

/**
 * Created by gregmachado on 03/11/16.
 */
public class WithdrawFragment extends Fragment {

    private static final String TAG = WithdrawFragment.class.getSimpleName();
    private TextView tvTime, tvStartTime, tvFinishTime;
    private RadioButton rbToday, rbTomorrow;
    private RadioGroup radioGroup;
    private String today, tomorrow, scheduleDay, scheduleHour, method, creatonDate;
    private Request request;
    private String bakeryId, userId;
    private List<Product> products;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();
    private boolean isToday = true;
    private ScheduleActivity activity;

    public WithdrawFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        method = "withdraw";
        bakeryId = getArguments().getString("bakeryID");
        userId = getArguments().getString("userID");
        products = getArguments().getParcelableArrayList("products");
        Log.w(TAG, "bakeryID: " + bakeryId);
        Log.w(TAG, "userID: " + userId);
        Log.w(TAG, "listSize: " + String.valueOf(products.size()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_withdraw, container, false);
        rbToday = (RadioButton) v.findViewById(R.id.rb_today_withdraw);
        rbTomorrow = (RadioButton) v.findViewById(R.id.rb_tomorrow_withdraw);
        tvTime = (TextView) v.findViewById(R.id.tv_time_withdraw);
        tvStartTime = (TextView) v.findViewById(R.id.tv_start_time);
        tvFinishTime = (TextView) v.findViewById(R.id.tv_finish_time);
        radioGroup = (RadioGroup) v.findViewById(R.id.rg_date_withdraw);
        loadTime();
        Button btnFinish = (Button) v.findViewById(R.id.btn_finish_withdraw);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();
                if (compareTime()) {
                    activity = (ScheduleActivity) getActivity();
                    activity.callPayment(false);
                } else {
                    Toast.makeText(getContext(), "Horário do pedido inválido!", Toast.LENGTH_SHORT).show();
                    tvTime.setTextColor(getResources().getColor(R.color.errorColor));
                }
            }
        });
        dateTimeSelect();
        return v;
    }

    private void loadTime() {
        //user test
        //bakeryId = "-KVuZ4G7BewSuHBfNB7x";
        mDatabaseReference.child("bakeries").child(bakeryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bakery bakery = dataSnapshot.getValue(Bakery.class);
                tvStartTime.setText(bakery.getStartTime());
                tvFinishTime.setText(bakery.getFinishTime());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    private boolean compareTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        try {
            Date dataA = format.parse(hour + ":" + minute);
            Date dataS = format.parse(tvStartTime.getText().toString());
            Date dataF = format.parse(tvFinishTime.getText().toString());
            Date data = format.parse(tvTime.getText().toString());
            Log.w(TAG, "Data Atual: " + String.valueOf(dataA));
            Log.w(TAG, "Data: " + String.valueOf(data));
            Log.w(TAG, "Data Inicial: " + String.valueOf(dataS));
            Log.w(TAG, "Data Final: " + String.valueOf(dataF));
            if (isToday) {
                return !(data.after(dataF) || (data.before(dataS)) || (data.before(dataA)));
            } else {
                return !(data.after(dataF) || (data.before(dataS)));
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public Request initRequest() {
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
                if (checkedId == R.id.rb_today_withdraw) {
                    scheduleDay = today;
                    isToday = true;
                } else if (checkedId == R.id.rb_tomorrow_withdraw) {
                    scheduleDay = tomorrow;
                    isToday = false;
                }
            }
        });
    }

    private CustomTimePickerDialog.OnTimeSetListener timeSetListener = new CustomTimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvTime.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
        }
    };
}
