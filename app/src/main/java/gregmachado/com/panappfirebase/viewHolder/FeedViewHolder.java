package gregmachado.com.panappfirebase.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gregmachado.com.panappfirebase.R;

/**
 * Created by gregmachado on 18/11/16.
 */
public class FeedViewHolder extends RecyclerView.ViewHolder{
    public TextView tvMsg, tvDate, tvHour, tvName;
    public ImageView ivNewFeed;
    public View mView;

    public FeedViewHolder(View v) {
        super(v);
        mView = v;
        tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        tvHour = (TextView) itemView.findViewById(R.id.tv_hour);
        tvMsg = (TextView) itemView.findViewById(R.id.tv_feed_msg);
        tvName = (TextView) itemView.findViewById(R.id.tv_name);
        ivNewFeed = (ImageView) itemView.findViewById(R.id.ic_new_feed);
    }
}
