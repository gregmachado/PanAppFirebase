package gregmachado.com.panappfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import gregmachado.com.panappfirebase.R;
import gregmachado.com.panappfirebase.adapter.TalkAdapter;
import gregmachado.com.panappfirebase.domain.SocialMedia;
import gregmachado.com.panappfirebase.util.DividerItemDecoration;

/**
 * Created by gregmachado on 01/12/16.
 */
public class TalkWithUsActivity extends CommonActivity{
    private static final String TAG = TalkWithUsActivity.class.getSimpleName();
    private RecyclerView rvTalk;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<SocialMedia> talkList = new ArrayList();
    private EditText inputMessage;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it = getIntent();
        params = it.getExtras();
        if (params != null) {
            id = params.getString("id");
        }
        setContentView(R.layout.activity_talk_with_us);
        initViews();
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_talk);
        setSupportActionBar(toolbar);
        setTitle("Fale Conosco");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        talkList = loadList();
        rvTalk = (RecyclerView) findViewById(R.id.rv_talk);
        layoutManager = new LinearLayoutManager(this);
        rvTalk.setHasFixedSize(true);
        rvTalk.setLayoutManager(layoutManager);
        rvTalk.addItemDecoration(new DividerItemDecoration(this));
        adapter = new TalkAdapter(TalkWithUsActivity.this, talkList);
        rvTalk.setAdapter(adapter);
        inputMessage = (EditText) findViewById(R.id.input_msg);
    }

    public List<SocialMedia> loadList(){
        List<SocialMedia> listAux = new ArrayList<>();
        listAux.add(new SocialMedia(R.drawable.ic_email_black_24dp, "pan.app.info@gmail.com"));
        listAux.add(new SocialMedia(R.drawable.ic_whatsapp, "(54) 99602-8851"));
        return(listAux);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        String msg = inputMessage.getText().toString();
        if (msg.length() > 8) {
            String msgID = mDatabaseReference.push().getKey();
            mDatabaseReference.child("message").child(id).child(msgID).setValue(msg);
            inputMessage.setText("");
            showToast("Obrigado! Sua mensagem foi enviada!");
        }
    }
}
