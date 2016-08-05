package chen.personinfo;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.R;
import com.example.chen.myapplication.VideoPlayActivity;

import java.util.List;

import Adapter.SingleAdapter;
import Adapter.SingleLayoutViewholder;
import Model.User;
import Model.VideoModel;
import MyView.HeighListView;

/**
 * 配置个人视频作品的适配器
 * Created by chen on 16/3/23.
 */
public class PersonVideoListAdapter extends PersonInfoAdapter<VideoModel> {


    public PersonVideoListAdapter(User user, Context context, List<VideoModel> mDatas) {
        super(user, context, mDatas);
    }

    @Override
    public ViewGroup configCommonListView(View convertView, int position, ViewGroup parent) {

        HeighListView lvPersonZoneCommon = (HeighListView) parent.findViewById(R.id.lv_person_zone_common_list);
            SingleAdapter<VideoModel> adapter = new SingleAdapter<VideoModel>(context, mDatas,R.layout.aothor_person_zone_video_list_item) {
                @Override
                public void ConfigView(SingleLayoutViewholder holder, VideoModel videoModel) {
                    ((TextView)holder.getView(R.id.tv_person_zone_video_name)).setText(videoModel.getVideoName());
                    ((TextView)holder.getView(R.id.tv_person_zone_video_description)).setText(videoModel.getDescription());
                    ImageView imgThum = (ImageView) holder.getView(R.id.img_person_zone_video_thum);
                    util.getBitmapByAsyncTask(context,imgThum,videoModel.getThumUrl());
                }
            };
            lvPersonZoneCommon.setAdapter(adapter);
        //设置个视频列表的item点击事件，跳转前去播放界面

        lvPersonZoneCommon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, VideoPlayActivity.class);
                //传递视频对象
                intent.putExtra("VideoModel",mDatas.get(i));
                context.startActivity(intent);
            }
        });
        return parent;
    }
}
