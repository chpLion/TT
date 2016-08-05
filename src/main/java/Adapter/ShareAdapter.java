package Adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.R;

import java.util.List;

import DataContext.ImagesUtil;
import DataContext.MethodUtil;
import Model.SharedVideo;

/**
 * Created by chen on 16/2/22.
 * 分享视频的适配器
 */
public class ShareAdapter extends SingleAdapter<SharedVideo> {

    public ShareAdapter(Context context, List<SharedVideo> mDates, int layoutId) {
        super(context, mDates, layoutId);
    }

    @Override
    public void ConfigView(SingleLayoutViewholder holder, SharedVideo sharedVideo) {
        ((TextView)holder.getView(R.id.tv_share_content)).setText(sharedVideo.getDescribe());
        ((TextView)holder.getView(R.id.tv_share_username)).setText(sharedVideo.getUserName());
        ((TextView)holder.getView(R.id.tv_share_time)).setText(MethodUtil.geTimeStr());
        //视频缩略图

        ImageView imageView = (ImageView) holder.getView(R.id.img_share_video);
        ImagesUtil imagesUtil = ImagesUtil.getInstance();
        imagesUtil.getBitmapByAsyncTask(context,imageView,sharedVideo.getThumUrl());
    }
}
