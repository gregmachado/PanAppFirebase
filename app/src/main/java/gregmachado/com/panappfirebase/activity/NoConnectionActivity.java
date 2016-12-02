package gregmachado.com.panappfirebase.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 02/12/16.
 */
public class NoConnectionActivity extends CommonActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_connection);
        initViews();
    }

    @Override
    protected void initViews() {
        ImageView ivNoConnection = (ImageView) findViewById(R.id.ic_no_connection);
        TextView tvNoConnection = (TextView) findViewById(R.id.tv_no_connection);
    }
}
