package chen.upload;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.R;

import java.util.List;

import Adapter.SingleAdapter;
import Adapter.SingleLayoutViewholder;
import DataContext.ImagesUtil;
import DataContext.MethodUtil;
import Model.VideoModel;

/**
 * Created by chen on 16/3/20.
 */
public class UploadFinishedAdapter extends SingleAdapter<VideoModel> {
    ImagesUtil imagesUtil = ImagesUtil.getInstance();
    public UploadFinishedAdapter(Context context, List<VideoModel> mDates, int layoutId) {
        super(context, mDates, layoutId);
    }

    @Override
    public void ConfigView(SingleLayoutViewholder holder, VideoModel info) {

        ((TextView) holder.getView(R.id.tv_my_upload_video_name)).setText(info.getVideoName());
        ((TextView) holder.getView(R.id.tv_my_upload_video_description)).setText(info.getDescription());
        ((TextView) holder.getView(R.id.tv_my_upload_video_length)).setText(info.getVideoLength());
        ((TextView) holder.getView(R.id.tv_my_upload_video_type)).setText(MethodUtil.getTYpeNameFromType(info.getVideoType()));
        ((TextView) holder.getView(R.id.tv_my_upload_video_price)).setText("￥"+info.getPrice());

        //配置图片，首先看是否能够从文件换从中读取
        Bitmap bitmap = null;
        ImageView imgMyUploadThum = (ImageView)holder.getView(R.id.img_my_upload_video);

        if (false){
            //当前图片路径依然存在
            imgMyUploadThum.setImageBitmap(bitmap);
        }
        else{
            //缓存可能已经清空，故需要从网络中下载
            //为imagview设置tag
            imgMyUploadThum.setTag(info.getThumUrl());
            imagesUtil.getBitmapByAsyncTask(context,imgMyUploadThum,info.getThumUrl(),info.getThumUrl());
        }
    }
}
