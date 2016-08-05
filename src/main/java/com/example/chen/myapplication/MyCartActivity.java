package com.example.chen.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.VideoModelAdapter;
import DataContext.AppData;
import Model.MyCart;
import Model.VideoModel;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class MyCartActivity extends Activity {
    
    private Context context;    //上下文环境
    private Button btnLookMore; //返回首页
    private List<VideoModel> listVideo; //视频列表
    private Handler handler;
    VideoModelAdapter videoModelAdapter;
    private TextView textSel;   //全选或者取消选择
    private CheckBox checkBoxAll; //全选框
    private TextView textTotal; //总价
    private Button btnDel;  //删除
    private Button btnBuy;  //购买

    private final static int EMPTY = 1; //数据为空
    private final static int NET_ERROR = 2; //网络错误
    private final static int FIND_OK = 3; //查询成功刷新数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        context = MyCartActivity.this;

        //绑定控件
        final ListView listView = (ListView) findViewById(R.id.listView);
        textSel = (TextView) findViewById(R.id.textALlSel);
        checkBoxAll = (CheckBox) findViewById(R.id.checkboxSelAll);
        textTotal = (TextView) findViewById(R.id.textViewTotal);
        btnDel = (Button) findViewById(R.id.buttonDel);
        btnBuy = (Button) findViewById(R.id.buttonBuy);

        //设置listview
        listVideo = new ArrayList<VideoModel>();
        videoModelAdapter = new VideoModelAdapter(
                context,listVideo, AppData.ORDER_LAYOUT);
        listView.setAdapter(videoModelAdapter);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == EMPTY){
                    TextView textHint = new TextView(context);
                    textHint.setText("这里空空如也，去主页看看吧");
                    textHint.setTextSize(25);
                    textHint.setGravity(Gravity.CENTER);
                }
                if (msg.what == NET_ERROR){
                    //跳转到无网络页面
                }
                if (msg.what == FIND_OK){
                    //加载完毕显示
                    videoModelAdapter.notifyDataSetChanged();
                }
            }
        };

        //listview点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * 转到详情页
                 */
                Toast.makeText(context,"跳转去播放"+listVideo.get(position).getVideoId(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //查找用户购物车数据
        BmobQuery<MyCart> payMentBmobQuery = new BmobQuery<MyCart>();
        payMentBmobQuery.addWhereEqualTo("userName", AppData.user.getUsername());
        payMentBmobQuery.findObjects(context, new FindListener<MyCart>() {
            @Override
            public void onSuccess(List<MyCart> list) {
                if (list.size()==0){
                    handler.sendEmptyMessage(EMPTY);    //空数据
                }else {
                    for (int i=0;i<list.size();i++){
                        addVideoById(list.get(i).getVideoId());
                    }
                    handler.sendEmptyMessage(FIND_OK);  //通知修改界面
                }
            }

            @Override
            public void onError(int i, String s) {
                handler.sendEmptyMessage(NET_ERROR);    //报告网络错误
            }
        });


        //全选或全部取消事件
        checkBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //删除事件
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //购买事件
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void addVideoById(String videoId){
        final BmobQuery<VideoModel> videoModelBmobQuery = new BmobQuery<VideoModel>();
        //设置查询为缓存查询优先如果没有数据差从网上获取
        videoModelBmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        videoModelBmobQuery.addWhereEqualTo("videoId",videoId);
        videoModelBmobQuery.findObjects(context, new FindListener<VideoModel>() {
            @Override
            public void onSuccess(List<VideoModel> list) {
                listVideo.add(list.get(0)); //添加数据
            }

            @Override
            public void onError(int i, String s) {
            }
        });

    }

}
