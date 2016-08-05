package chen.personinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chen.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import DataContext.ImagesUtil;
import Model.User;

/**
 * Created by chen on 16/3/23.
 */
public abstract class PersonInfoAdapter<T> extends BaseAdapter {


    protected Context context;
    protected User user;
    private int TYPE_TOP = 0;
    private int TYPE_COMMIN = 1;
    protected ImagesUtil util = ImagesUtil.getInstance();
    //具体的需要配置的数据列表数据源
    protected List<T> mDatas = new ArrayList<>();

    public PersonInfoAdapter(User user, Context context,List<T>mDatas) {
        this.user = user;
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_TOP;
        }else{
            return TYPE_COMMIN;
        }
    }

    @Override
    public View getView(int positon, View convertView, ViewGroup viewGroup) {


        int type = getItemViewType(positon);
        LinearLayout ll = null;
        if (type == TYPE_TOP){
            //顶部布局
            ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.person_zone_top_item,null);
            ((TextView)ll.findViewById(R.id.tv_author_person_zone_username)).setText(user.getUsername());
            ((TextView)ll.findViewById(R.id.tv_author_person_zone_description)).setText(user.getDescription());
            ((TextView)ll.findViewById(R.id.tv_author_person_zone_moto)).setText(user.getMoto());
            ((TextView)ll.findViewById(R.id.tv_author_person_zone_fans_count)).setText(user.getFansCount());
            //头像
            ImageView imgHead = (ImageView) ll.findViewById(R.id.img_author_person_zone_head);
            util.getBitmapByAsyncTask(context,imgHead,user.getHeadImageUrl());
        }
        else{
            //设置一般数据列表

//            ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.author_persoon_zone_common_list_layout,null);
//            HeighListView lvPersonZoneCommon = (HeighListView) ll.findViewById(R.id.lv_person_zone_common_list);
//
//            SingleAdapter<VideoModel> adapter = new SingleAdapter<VideoModel>(context,(List<VideoModel>) mDatas,R.layout.aothor_person_zone_video_list_item) {
//                @Override
//                public void ConfigView(SingleLayoutViewholder holder, VideoModel videoModel) {
//                    ((TextView)holder.getView(R.id.tv_person_zone_video_name)).setText(videoModel.getVideoName());
//                    ((TextView)holder.getView(R.id.tv_person_zone_video_description)).setText(videoModel.getDescription());
//                    ImageView imgThum = (ImageView) holder.getView(R.id.img_person_zone_video_thum);
//                    util.getBitmapByAsyncTask(context,imgThum,videoModel.getThumUrl());
//                }
//            };
//            lvPersonZoneCommon.setAdapter(adapter);
            ll = (LinearLayout) configCommonListView(convertView,positon,ll);

        }
        return  ll;
    }
    public abstract ViewGroup configCommonListView(View convertView, int position, ViewGroup parent);
}
