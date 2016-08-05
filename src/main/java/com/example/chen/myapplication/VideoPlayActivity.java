package com.example.chen.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youku.player.ApiManager;
import com.youku.player.VideoQuality;
import com.youku.player.base.YoukuBasePlayerActivity;
import com.youku.player.base.YoukuPlayer;
import com.youku.player.base.YoukuPlayerView;

import java.util.ArrayList;
import java.util.List;

import Adapter.VideoCommentAdapter;
import DataContext.AppData;
import DataContext.ImagesUtil;
import Model.User;
import Model.VideoComment;
import Model.VideoModel;
import MyView.RefreshListview;
import chen.personinfo.AuthorPersonZoneActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 播放器播放界面，需要继承自YoukuBasePlayerActivity基类
 *
 */
public class VideoPlayActivity extends YoukuBasePlayerActivity{
    //播放器控件
    private YoukuPlayerView mYoukuPlayerView;

    //需要播放的视频id
    private String vid;

    //需要播放的本地视频的id
    private String local_vid;

    //标示是否播放的本地视频
    private boolean isFromLocal = false;

    //需要跳过的次数 用于在上拉加载更多时候确定需要跳过多少条 count = skipcount*20
    private int skipCount = 0;

    //一行功能按钮
    private TextView tvWriteComment,tvCollet,tvDownload;

    //视频id 键
    public static String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    //作者对象键
    public static String KEY_AUTHOR_OBJECT = "KEY_AUTHOR_OBJECT";

    //视频的评论列表
    private RefreshListview lvVideoComment;
    private TextView tvNoCommentHint;//没有评论时候的提示

    private String id = "";

    private float price = 0;
    //视频评论集合
    private List<VideoComment> mDatas = new ArrayList<>();
    VideoCommentAdapter adapter;
    //YoukuPlayer实例，进行视频播放控制
    private YoukuPlayer youkuPlayer;
    private TextView tvAuthorName;
    private ImageView imgVideoAuthorHead;
    ImagesUtil imagesUtil = ImagesUtil.getInstance();
    private VideoModel videoModel = new VideoModel();
    User author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.second);

        lvVideoComment = (RefreshListview) findViewById(R.id.lv_video_play_comment);
        tvNoCommentHint = (TextView) findViewById(R.id.tv_video_play_hint);

        imgVideoAuthorHead = (ImageView) findViewById(R.id.img_video_play_author_head_image);
        tvAuthorName = (TextView) findViewById(R.id.tv_video_play_author_name);
        tvCollet = (TextView) findViewById(R.id.tv_video_play_collect);
        tvWriteComment = (TextView) findViewById(R.id.tv_video_play_write_comment);
        tvDownload = (TextView) findViewById(R.id.tv_video_play_download);

        tvNoCommentHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
                adapter.notifyDataSetChanged();
            }
        });
        tvCollet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imgVideoAuthorHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到作者的个人主页
                Intent intent = new Intent(VideoPlayActivity.this, AuthorPersonZoneActivity.class);
                intent.putExtra(KEY_AUTHOR_OBJECT,author);
                startActivity(intent);
            }
        });

        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDownload();
            }
        });

        tvWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doWrite();
            }
        });

        adapter = new VideoCommentAdapter(this,mDatas,R.layout.video_play_comment);
        lvVideoComment.setAdapter(adapter);
        //通过上个页面传递过来的Intent获取播放参数
        getIntentData(getIntent());

        lvVideoComment.setOnRefreshListener(new RefreshListview.onRefreshListener() {
            @Override
            public void onPullRefresh() {
                loadData();
                adapter.notifyDataSetChanged();
                //设置刷新完成
                lvVideoComment.onRefreshComplete();
            }

            @Override
            public void onUpResfresh() {
                loadMoreDta();
                //设置上拉加载完成
                lvVideoComment.onUpComplete();
            }
        });
        //查询评论表
        loadData();
        //播放器控件
        mYoukuPlayerView = (YoukuPlayerView) this.findViewById(R.id.full_holder);

        //初始化播放器相关数据
        mYoukuPlayerView.initialize(this);

//		mYoukuPlayerView.initialize(this,pid);			//有pid的用户可以使用此接口配置pid参数

    }

    private void doWrite() {

        Intent intent = new Intent();
        if (AppData.user == null){
            intent.setClass(this,LoginActivity.class);
        }
        else{
            intent.setClass(this,WriteVidioCommentActivity.class);
            intent.putExtra(KEY_VIDEO_ID,videoModel.getVideoId());
        }
        startActivity(intent);
    }

    /**
     * 查询更多数据
     */
    private void loadMoreDta() {
        BmobQuery<VideoComment> query = new BmobQuery<>();
        query.addWhereEqualTo("videoId",videoModel.getVideoId());
        query.setLimit(20);
        //skipcount表示已经查询过的次数，故需要跳过次数乘以每次查询的次数 条的记录
        query.setSkip(20*skipCount);
        query.findObjects(this, new FindListener<VideoComment>() {
            @Override
            public void onSuccess(List<VideoComment> list) {
                mDatas.clear();
                mDatas.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);

        //通过Intent获取播放需要的相关参数
        getIntentData(intent);

        //进行播放
        goPlay();
    }


    /**
     * 获取上个页面传递过来的数据
     */
    private void getIntentData(Intent intent){

        if(intent != null){
            //得到传过来的视频模型
            Bundle bundle = intent.getExtras();

            videoModel = (VideoModel) bundle.getSerializable("VideoModel");
            price = videoModel.getPrice();
            author = videoModel.getAuthor();
            String authorName = videoModel.getOwnerName();
            tvAuthorName.setText(authorName);
            //查询作者头像
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("username",authorName);
            query.findObjects(this, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {
                    //获取作者用户对象
                    author = list.get(0);
                    //获取头像url
                    String url = list.get(0).getHeadImageUrl();
                    if (url == null || url.trim().equals("")){
                        //没有上传自定义头像 直接跳过
                        return;
                    }
                    imagesUtil.getBitmapByAsyncTask(VideoPlayActivity.this,imgVideoAuthorHead,url);
                }

                @Override
                public void onError(int i, String s) {

                }
            });
            if(price != 0){
                //当前视频不是免费的，需要付费下载
                //btn_download.setText("购买下载 ￥"+ videoModel.getPrice());
                //启动线程，只播放十秒钟
            }
            vid = videoModel.getVideoId();
        }

    }

    @Override
    public void setPadHorizontalLayout() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInitializationSuccess(YoukuPlayer player) {
        // TODO Auto-generated method stub
        //初始化成功后需要添加该行代码
        addPlugins();

        //实例化YoukuPlayer实例
        youkuPlayer = player;

        //进行播放
        goPlay();
    }

    private void goPlay(){
        try {
            youkuPlayer.playVideo(vid);


        }
        catch (Exception e){
            Toast.makeText(this,"视频出错啦",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onFullscreenListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSmallscreenListener() {
        // TODO Auto-generated method stub

    }




    /**
     * 更改视频的清晰度
     * @param quality
     * 				VideoQuality有四种枚举值：{STANDARD,HIGHT,SUPER,P1080}，分别对应：标清，高清，超清，1080P
     */

    private void change(VideoQuality quality){
        try{
            //通过ApiManager实例更改清晰度设置，返回值（1):成功；（0): 不支持此清晰度
            //接口详细信息可以参数使用文档
            int result = ApiManager.getInstance().changeVideoQuality(quality, VideoPlayActivity.this);
            if(result == 0) Toast.makeText(VideoPlayActivity.this, "不支持此清晰度", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(VideoPlayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 简单展示下载接口的使用方法，用户可以根据自己的
     * 通过DownloadManager下载视频
     */
    private void doDownload(){
        //通过DownloadManager类实现视频下载
//        DownloadManager d = DownloadManager.getInstance();
//        /**
//         * 第一个参数为需要下载的视频id
//         * 第二个参数为该视频的标题title
//         * 第三个对下载视频结束的监听，可以为空null
//         */
//        d.createDownload("XNzgyODExNDY4", "魔女范冰冰扑倒黄晓明", new OnCreateDownloadListener() {
//
//            @Override
//            public void onfinish(boolean isNeedRefresh) {
//                // TODO Auto-generated method stub
//
//            }
//        });

        Intent intent = new Intent();
        if (AppData.user == null){
            //目前未登录
            intent.setClass(VideoPlayActivity.this,LoginActivity.class);
        }
        else{

            intent.setClass(VideoPlayActivity.this,BuyActivity.class);
            Bundle bundle = new Bundle();
            videoModel = (VideoModel) getIntent().getExtras().getSerializable("VideoModel");
            bundle.putSerializable(AppData.VIDEO_MODEL,videoModel);
            intent.putExtras(bundle);
        }
        startActivity(intent);

    }

    /**
     * 视频播放技术线程类
     */
    class VideoTimer extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                sleep(10*1000);
                mYoukuPlayerView.onPause();
                Intent intent = new Intent(VideoPlayActivity.this,BuyActivity.class);
                startActivity(intent);
                VideoPlayActivity.this.finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void loadData(){
        BmobQuery<VideoComment> query = new BmobQuery<>();
        query.addWhereEqualTo("videoId",videoModel.getVideoId());
        query.include("commentUser");
        query.setLimit(20);
        query.findObjects(this, new FindListener<VideoComment>() {
            @Override
            public void onSuccess(List<VideoComment> list) {

                mDatas.clear();
                mDatas.addAll(list);
                adapter.notifyDataSetChanged();
                if (list.size() >0){
                    //当前有数据，所以 暂无评论 的textview需要消失
                    tvNoCommentHint.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

}
