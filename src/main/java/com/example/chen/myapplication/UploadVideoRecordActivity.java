package com.example.chen.myapplication;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapter.FragAdapter;
import DataContext.AppData;
import Model.VideoModel;
import chen.upload.UploadFinishedFragment;
import chen.upload.UploadService;
import chen.upload.UploadingVideoFragment;

public class UploadVideoRecordActivity extends FragmentActivity {

    private ViewPager viewPager;
    private FragAdapter adapter;
    private TextView tab1,tab2;//两个顶部的tab
    int offset,imageWidth;//偏移量和图片宽度
    ImageView imageView;//需要动画的图片
    private VideoModel videoUpload;//正在上传的视频
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video_record);


        if (!AppData.isStartService){
            //startService(intent);
            Intent intent = getIntent();
            videoUpload = (VideoModel) intent.getSerializableExtra(UpLoadVedioActivty.KEY_VIDEO_MODEL);
            filePath = intent.getStringExtra(UpLoadVedioActivty.KEY_FILE_PATH);
            if (videoUpload != null){
                //说明是上传视频
                intent.setClass(this, UploadService.class);
                intent.setAction(UploadService.ACTION_START);
                startService(intent);
                AppData.isStartService = true;
            }

        }

//        if (!AppData.isStartService){
//            //还没有启动service的情况下
//            Intent intent = getIntent();
//            videoUpload = (VideoModel) intent.getSerializableExtra(UpLoadVedioActivty.KEY_VIDEO_MODEL);
//            String filePath = intent.getStringExtra(UpLoadVedioActivty.KEY_FILE_PATH);
//            String videoPath = intent.getStringExtra(UpLoadVedioActivty.KEY_VIDEO_PATH);
//            if (videoUpload != null){
//                //说明是需要上传视频的意图
//                intent = new Intent(this,UploadService.class);
//                intent.putExtra(UpLoadVedioActivty.KEY_FILE_PATH,filePath);
//                intent.putExtra(UpLoadVedioActivty.KEY_VIDEO_PATH,videoPath);
//                intent.putExtra(UpLoadVedioActivty.KEY_VIDEO_MODEL,videoUpload);
//
//                startService(intent);
//                //设置标志是否启动service为true
//                AppData.isStartService = true;
//            }
//
//        }

        //返回图片
        ImageView imgReturn = (ImageView) findViewById(R.id.img_return);
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadVideoRecordActivity.this.finish();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.vp_upload_record_layout);
        TextView tvTitle = (TextView) findViewById(R.id.tv_top_titile);
        tvTitle.setText("我的上传");
        tab1 = (TextView) findViewById(R.id.tv_tab1);
        tab2 = (TextView) findViewById(R.id.tv_tab2);

        tab1.setTextColor(getResources().getColor(R.color.tab_color));
        tab2.setTextColor(getResources().getColor(R.color.main_color));

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        tab1.setText("正在上传");
        tab2.setText("已经上传");

        List<Fragment>fragments = new ArrayList<>();
        fragments.add(new UploadingVideoFragment());
        fragments.add(new UploadFinishedFragment());

        adapter = new FragAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);

        //初始化需啊哟动画的图片
        initImageView();
        //设置viewpager滑动时间
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            Animation animation = null;
            int one = offset*2+imageWidth; //切换一页的偏移量
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    tab1.setTextColor(getResources().getColor(R.color.tab_color));
                    tab2.setTextColor(getResources().getColor(R.color.main_color));
                    animation = new TranslateAnimation(one,0,0,0);

                }
                else{
                    tab1.setTextColor(getResources().getColor(R.color.main_color));
                    tab2.setTextColor(getResources().getColor(R.color.tab_color));
                    animation = new TranslateAnimation(0,one,0,0);

                }
                animation.setFillAfter(true);   //设置图片停止在动画结束的地方
                animation.setDuration(300);
                imageView.startAnimation(animation);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    /**
     * 初始化动画
     */
    private void initImageView(){
        imageView = (ImageView) findViewById(R.id.img_cusor_enjoy);
        imageWidth  = imageView.getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;   //获取分辨率宽度
        offset = (screenW/2 - imageWidth)/2-imageWidth/3;    //计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);    //设置移动效果和距离
        imageView.setImageMatrix(matrix);
    }


    public VideoModel getVideoUpload() {
        return videoUpload;
    }

    public String getFilePath() {
        return filePath;
    }
}
