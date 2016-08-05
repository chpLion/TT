package com.example.chen.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapter.VideoModelAdapter;
import DataContext.AppData;
import Model.PayMent;
import Model.VideoModel;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class MyOrderActivity extends Activity {

    private Context context;    //上下文环境
    private List<VideoModel> listVideo; //视频列表
    private Handler handler;
    VideoModelAdapter videoModelAdapter;

    private final static int EMPTY = 1; //数据为空
    private final static int NET_ERROR = 2; //网络错误
    private final static int FIND_OK = 3; //查询成功刷新数据
    private static  int size = 0;

    private TextView tvTtile;
    private ImageView imgReturn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        context = MyOrderActivity.this;

        //绑定控件
        final ListView listView = (ListView) findViewById(R.id.listView);

        tvTtile = (TextView) findViewById(R.id.tv_top_titile);
        tvTtile.setText("我的订单");
        imgReturn = (ImageView) findViewById(R.id.img_return);
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyOrderActivity.this.finish();
            }
        });
        //设置listview
        listVideo = new ArrayList<VideoModel>();
        videoModelAdapter = new VideoModelAdapter(
                context,listVideo, AppData.ORDER_LAYOUT);
        listView.setAdapter(videoModelAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * 前往播放
                 */
                Intent intent = new Intent(MyOrderActivity.this,VideoPlayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("VideoModel",listVideo.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == EMPTY){
                    TextView textHint = new TextView(context);
                    textHint.setText("这里空空如也，去看看吧");
                    textHint.setTextSize(25);
                    textHint.setGravity(Gravity.CENTER);
                }
                if (msg.what == NET_ERROR){
                    //跳转到无网络页面
                }
                if (msg.what == FIND_OK){
                    //加载完毕显示
                    if (listVideo.size() == size) {
                        videoModelAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        //查找用户订单数据
        BmobQuery<PayMent> payMentBmobQuery = new BmobQuery<PayMent>();
        payMentBmobQuery.addWhereEqualTo("userName", AppData.user.getUsername());
        payMentBmobQuery.findObjects(context, new FindListener<PayMent>() {
            @Override
            public void onSuccess(List<PayMent> list) {
                if (list.size() == 0) {
                    handler.sendEmptyMessage(EMPTY);    //空数据
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        addVideoById(list.get(i).getVideoId());
                        size = list.size();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                handler.sendEmptyMessage(NET_ERROR);    //报告网络错误
            }
        });

            }

    public void addVideoById(String videoId){
        final BmobQuery<VideoModel> videoModelBmobQuery = new BmobQuery<VideoModel>();
        videoModelBmobQuery.addWhereEqualTo("videoId",videoId);
        videoModelBmobQuery.findObjects(context, new FindListener<VideoModel>() {
            @Override
            public void onSuccess(List<VideoModel> list) {
                listVideo.add(list.get(0)); //添加数据
                handler.sendEmptyMessage(FIND_OK);  //通知修改界面
            }

            @Override
            public void onError(int i, String s) {
                handler.sendEmptyMessage(NET_ERROR);
            }
        });

    }

}
