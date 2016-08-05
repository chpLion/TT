package com.example.chen.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.VideoTypeAdapter;
import DataContext.MethodUtil;
import Model.VideoModel;
import MyView.RefreshListview;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class VideoTypeActivity extends Activity {

    TextView tvTitle;
    TextView tvHint;
    int type;
    List<VideoModel> mDatas = new ArrayList<>();
    VideoTypeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_type);

        final Intent intent = this.getIntent();
        type = intent.getIntExtra("type",0);
        tvTitle = (TextView) findViewById(R.id.tv_top_titile);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        //类型的名称
        String typeName = MethodUtil.getTYpeNameFromType(type);
        tvTitle.setText(typeName);
        final RefreshListview allVideoList = (RefreshListview) findViewById(R.id.list_all_video);
        adapter = new VideoTypeAdapter(this,mDatas,R.layout.all_video_list_item);
        allVideoList.setAdapter(adapter);
        getDatasFromBmob();
        ImageView imgReturn = (ImageView) findViewById(R.id.img_return);
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoTypeActivity.this.finish();
            }
        });

        //item的点击事件，跳转到播放界面
        allVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent playeVideo = new Intent(VideoTypeActivity.this,VideoPlayActivity.class);
                //传递视频模型对象
                playeVideo.putExtra("VideoModel",mDatas.get(i));
                startActivity(playeVideo);
            }
        });
        allVideoList.setOnRefreshListener(new RefreshListview.onRefreshListener() {
            @Override
            public void onPullRefresh() {
                getDatasFromBmob();
                adapter.notifyDataSetChanged();
                allVideoList.onRefreshComplete();
            }

            @Override
            public void onUpResfresh() {

            }
        });
    }

    /**
     * 从数据库中获取首页信息数据并适配到listview中
     */
    private void getDatasFromBmob(){

        BmobQuery<VideoModel> query = new BmobQuery<VideoModel>();
        query.setLimit(50);
        query.addWhereEqualTo("videoType",type);
        query.findObjects(this, new FindListener<VideoModel>() {
            @Override
            public void onSuccess(List<VideoModel> list) {
                //将查询到的数据适配到listview中
                mDatas.clear();
                mDatas.addAll(list);
                adapter.notifyDataSetChanged();
                if (list.size() !=0){
                    tvHint.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(VideoTypeActivity.this,s,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
