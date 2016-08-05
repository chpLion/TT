package Adapter;

import android.content.Context;
import android.widget.TextView;

import com.example.chen.myapplication.R;

import java.util.List;

import Model.RequestVideo;

/**
 * Created by chen on 16/2/22.
 */
public class RequestTradeAdapter extends SingleAdapter<RequestVideo> {
    public RequestTradeAdapter(Context context, List<RequestVideo> mDates, int layoutId) {
        super(context, mDates, layoutId);
    }

    @Override
    public void ConfigView(SingleLayoutViewholder holder, RequestVideo requestVideo) {
        ((TextView)holder.getView(R.id.tv_title)).setText(requestVideo.getTitle());
        ((TextView)holder.getView(R.id.tv_content)).setText(requestVideo.getDecrabe());
        ((TextView)holder.getView(R.id.tv_request_price)).setText(requestVideo.getPrice()+"");
        ((TextView)holder.getView(R.id.tv_request_username)).setText(requestVideo.getUserName());
        ((TextView)holder.getView(R.id.tv_time)).setText(requestVideo.getTimeStr());

    }
}
