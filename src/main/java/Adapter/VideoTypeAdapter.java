package Adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import DataContext.ImagesUtil;
import Model.VideoModel;
import MyView.MyGridView;

/**
 * Created by chen on 16/2/27.
 */
public class VideoTypeAdapter extends SingleAdapter<VideoModel> {

    public VideoTypeAdapter(Context context, List<VideoModel> mDates, int layoutId) {
        super(context, mDates, layoutId);
    }

    @Override
    public void ConfigView(SingleLayoutViewholder holder, VideoModel vedioModel) {
        //gridview
        MyGridView myGridView = (MyGridView) holder.getView(R.id.vedio_grid);
        final ImagesUtil imagesUtil = ImagesUtil.getInstance();
        //配置4个为一组的数据
        List<VideoModel> vedioModels = new ArrayList<>();
        for (int i=0;i<4;i++){
            if (4*holder.getPosition()+i<mDates.size()) {
                vedioModels.add(mDates.get(4 * holder.getPosition() + i));
            }
            else{
                break;
            }
        }
        myGridView.setNumColumns(2);
        myGridView.setAdapter(new SingleAdapter<VideoModel>(context,vedioModels,R.layout.grid_item) {
            @Override
            public void ConfigView(SingleLayoutViewholder holder, VideoModel vedioModel) {
                if (vedioModel.getPrice() == 0){
                    ((TextView)holder.getView(R.id.tv_price)).setText("免费");
                }
                else{
                    ((TextView)holder.getView(R.id.tv_price)).setText("¥"+vedioModel.getPrice()+"");

                }
                ((TextView)holder.getView(R.id.tv_vedio_length)).setText(vedioModel.getVideoLength());
                ((TextView)holder.getView(R.id.tv_vedio_name)).setText(vedioModel.getVideoName());
                //异步加载视频缩略图
                if (vedioModel.getThumUrl()!=null) {
                    imagesUtil.getBitmapByAsyncTask(context, ((ImageView) holder.getView(R.id.img_vedio)), vedioModel.getThumUrl());
                }
            }
        });
    }
}
