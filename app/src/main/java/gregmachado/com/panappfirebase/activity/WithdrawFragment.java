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

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.domain.Product;
import gregmachado.com.panappfirebase.domain.Request;
import gregmachado.com.panappfirebase.util.CustomTimePickerDialog;
import gregmachado.com.panappfirebase.util.DateUtil;

/**
 * Created by gregmachado on 03/11/16.
 */
public class WithdrawFragment extends Fragment {

    private TextView tvTime;
    private RadioButton rbToday, rbTomorrow;
    private RadioGroup radioGroup;
    private String today, tomorrow, scheduleDay, scheduleHour, method, creatonDate;
    private Request request;
    private String bakeryId, userId;
    private List<Product> products;
    private DatabaseReference mDatabaseReference;

    public WithdrawFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        method = "withdraw";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bakeryId = getArguments().getString("bakeryID");
        userId = getArguments().getString("userID");
        products = getArguments().getParcelableArrayList("products");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_withdraw, container, false);
        rbToday = (RadioButton) v.findViewById(R.id.rb_today_withdraw);
        rbTomorrow = (RadioButton) v.findViewById(R.id.rb_tomorrow_withdraw);
        tvTime = (TextView) v.findViewById(R.id.tv_time_withdraw);
        radioGroup = (RadioGroup) v.findViewById(R.id.rg_date_withdraw);
        Button btnFinish = (Button) v.findViewById(R.id.btn_finish_withdraw);
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
                if(checkedId == R.id.rb_today_withdraw) {
                    scheduleDay = today;
                } else if(checkedId == R.id.rb_tomorrow_withdraw) {
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
