package chen.trade;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.R;

import java.util.List;

import Adapter.CommonViewHolder;
import Adapter.MutiLayoutAdapter;
import DataContext.ImagesUtil;
import Model.ReplyModel;
import Model.RequestVideo;

/**
 * Created by chen on 16/2/23.
 */
public class RequestDetailAdapter extends MutiLayoutAdapter<ReplyModel> {

    private static final int TYPE_TOP = 0;
    private static final int TYPE_item = 1;

    private RequestVideo requestVideo;
    public RequestDetailAdapter(Context context, List<ReplyModel> mDates, int count,RequestVideo requestVideo) {
        super(context, mDates, count);
        this.requestVideo = requestVideo;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        }
        else{
            return TYPE_item;
        }
    }

    @Override
    public CommonViewHolder getViewHolderByPosition(int position, View convertView, ViewGroup parent) {

        CommonViewHolder holder = null;
        if (position ==0){
            holder = CommonViewHolder.getInstance(context,parent, R.layout.request_trade_item,position,convertView);
        }else{
            holder = CommonViewHolder.getInstance(context,parent,R.layout.reply_layout,position,convertView);
        }
        return holder;
    }

    @Override
    public void ConfigView(CommonViewHolder holder, List<ReplyModel> mDatas, int position) {
        int type = getItemViewType(position);
        switch (type){
            case TYPE_TOP:{
                //加载第一项 即为发布的需求
                ((TextView)holder.getView(R.id.tv_title)).setText(requestVideo.getTitle());
                ((TextView)holder.getView(R.id.tv_content)).setText(requestVideo.getDecrabe());
                ((TextView)holder.getView(R.id.tv_request_price)).setText(requestVideo.getPrice()+"");
                ((TextView)holder.getView(R.id.tv_request_username)).setText(requestVideo.getUserName());
                ((TextView)holder.getView(R.id.tv_time)).setText(requestVideo.getTimeStr());
                break;
            }
            case TYPE_item:{
                //回复人的用户名
                ReplyModel replyModel = mDatas.get(position-1);
                ((TextView) holder.getView(R.id.tv_request_username)).setText(replyModel.getUserName());
                ((TextView) holder.getView(R.id.tv_request_time)).setText(replyModel.getTime());
                ((TextView) holder.getView(R.id.tv_request_comment)).setText(replyModel.getContent());
                //选择图片
                ImageView imageView = (ImageView) holder.getView(R.id.img_request_headimage);
                ImagesUtil imagesUtil = ImagesUtil.getInstance();
                if (replyModel.getHeadUrl()!=null){
                    imagesUtil.getBitmapByAsyncTask(context,imageView,replyModel.getHeadUrl());
                }
                break;
            }
        }
    }
}
