package Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.R;
import com.example.chen.myapplication.VideoPlayActivity;
import com.example.chen.myapplication.VideoTypeActivity;

import java.util.ArrayList;
import java.util.List;

import DataContext.ImagesUtil;
import Model.FirstPageTitle;
import Model.VideoModel;
import MyView.MyGridView;

/**
 * Created by chen on 16/2/4.
 *
 * 首页列表的适配器
 */
public class VideoAdapter extends MutiLayoutAdapter<VideoModel> {

    private static final int TOP_TYPE = 0;//顶部类型
    private static final int COMMAN_TYPE = 1;//一般item类型
    private static final int LOAD_COMPLETE = 0;
    private ImagesUtil imagesUtil;
    private int colums[] = {3,2,2,3,3,2,2,3,3,2,3,2};
    private List<FirstPageTitle> titles = new ArrayList<>();//存储列表标题的集合

    public VideoAdapter(Context context, List<VideoModel> mDates) {
        super(context, mDates);
    }

    public VideoAdapter(Context context, List<VideoModel> mDates, List<FirstPageTitle> mTitles, int count) {
        super(context, mDates, count);
        this.titles = mTitles;
        imagesUtil = ImagesUtil.getInstance();
    }

    @Override
    public int getCount() {
        return titles.size()+1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0){
            //是顶部
            return TOP_TYPE;
        }
        else{
            return COMMAN_TYPE;
        }
    }

    @Override
    public CommonViewHolder getViewHolderByPosition(int position, View convertView, ViewGroup parent) {
        //根据不同的位置加载不同的holder
        CommonViewHolder holder = null;

        if (position == 0){
            //是第一项 需要加载头条布局
            holder = CommonViewHolder.getInstance(context,parent, R.layout.vedio_list_top_item,position,convertView);
        }
        else{
            //加载列表项
            holder = CommonViewHolder.getInstance(context,parent,R.layout.vedio_list_item,position,convertView);
        }
        return holder;
    }

    @Override
    public void ConfigView(CommonViewHolder holder, List<VideoModel> videoModels, final int position) {

        //获取当前需要加载的类型
        int type = getItemViewType(holder.getPosition());
        switch (type){
            case TOP_TYPE:{
                //((TextView)holder.getView(R.id.tv_text)).setText("123:"+type);
                break;
            }
            case COMMAN_TYPE:{

                ((TextView)holder.getView(R.id.tv_vedio_type)).setText(titles.get(position-1).getTitle());
                ((TextView)holder.getView(R.id.tv_type_click)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取当前点击的item项表示的视频类别
                        int type = titles.get(position -1 ).getTag();
                        //作为参数 并且跳转
                        Intent i = new Intent(context, VideoTypeActivity.class);
                        i.putExtra("type",type);
                        context.startActivity(i);
                    }
                });
                final List<VideoModel> videos = new ArrayList<>();
                for (int i=0;i<mDates.size();i++){
                    //查找在获取的数据中属于当前分类的视频对象
                    if (titles.get(position-1).getTag() == mDates.get(i).getVideoType()){
                        //是属于当前分类 将对象放入集合
                        if (videos.size() == 6){
                            //一个gridview里面最多有6项
                            break;
                        }
                        videos.add(mDates.get(i));
                    }
                }

                SingleLayoutAdapter<VideoModel> adapter = new SingleLayoutAdapter<VideoModel>(context,videos,R.layout.grid_item) {
                    @Override
                    public void ConfigView(CommonViewHolder holder, VideoModel videoModel) {
                        if (videoModel.getPrice() == 0){
                            ((TextView)holder.getView(R.id.tv_price)).setText("免费");
                        }
                        else{
                            ((TextView)holder.getView(R.id.tv_price)).setText("¥"+videoModel.getPrice()+"");

                        }
                        ((TextView)holder.getView(R.id.tv_vedio_length)).setText(videoModel.getVideoLength());
                        ((TextView)holder.getView(R.id.tv_vedio_name)).setText(videoModel.getVideoName());
                        ImageView imageView = (ImageView) holder.getView(R.id.img_vedio);
                        //异步加载视频缩略图
                        if (videoModel.getThumUrl()!=null) {
                            imagesUtil.getBitmapByAsyncTask(context, imageView, videoModel.getThumUrl());
                        }
                    }
                };

                MyGridView gridView = (MyGridView) holder.getView(R.id.vedio_grid);


                int num = colums[(position-1)%13];
                gridView.setNumColumns(num);

                gridView.setAdapter(adapter);
                //视频列表点击跳转到播放界面
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //跳转到播放界面 并将当前视频对象作为参数
                        Intent intent = new Intent(context, VideoPlayActivity.class);
                        Bundle bundle = new Bundle();

                        //传递选中的item项的视频对象
                        bundle.putSerializable("VideoModel",videos.get(position));
                        intent.putExtras(bundle);

                        context.startActivity(intent);
                    }
                });
                break;
            }
        }
    }

}
