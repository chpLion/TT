package com.example.chen.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapter.SingleAdapter;
import Adapter.SingleLayoutViewholder;
import Model.VideoInfo;

public class SelectLocalVedioActivity extends Activity {

    private List<VideoInfo> videoInfoList = new ArrayList<>();//本地视频集合
    public static final String VEDIO_NAME = "vedioName";
    public static final String VEDIO_SIZE = "vedioSize";
    public static final String VEDIO_DURATION = "vedioDuration";
    public static final String VEDIO_URL = "vedioUrl";
    public static final int COMPLETE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_local_vedio);

        TextView tvHint = (TextView) findViewById(R.id.tv_hint);

        ListView vedioList = (ListView) findViewById(R.id.list_local_vedio);
        //获取视频
        videoInfoList = getLocalVedio();

        SingleAdapter<VideoInfo> adapter = new SingleAdapter<VideoInfo>(this, videoInfoList,R.layout.local_vedio_list_item) {
            @Override
            public void ConfigView(SingleLayoutViewholder holder, VideoInfo vedioInfo) {
                ((TextView)holder.getView(R.id.tv_local_vedio_duration)).setText(vedioInfo.getDuration()+"");
                ((ImageView)holder.getView(R.id.img_thumbalin)).setImageBitmap(vedioInfo.getThumImage());
                ((TextView)holder.getView(R.id.tv_local_vedio_size)).setText(vedioInfo.getSize()+"");
                ((TextView)holder.getView(R.id.tv_local_vedio_name)).setText(vedioInfo.getVedioName());
            }
        };

        vedioList.setAdapter(adapter);
        if (videoInfoList.size() == 0){
            tvHint.setVisibility(View.VISIBLE);
        }

        //点击列表项选中事件
        vedioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取选中的item的视频信息对象
                VideoInfo videoInfo = videoInfoList.get(position);
                Intent intent = new Intent();
                //获取视频信息 并通过intent回传给上传界面
                intent.putExtra(SelectLocalVedioActivity.VEDIO_NAME, videoInfo.getVedioName());
                intent.putExtra(SelectLocalVedioActivity.VEDIO_DURATION, videoInfo.getDuration());
                intent.putExtra(SelectLocalVedioActivity.VEDIO_SIZE, videoInfo.getSize());
                intent.putExtra(SelectLocalVedioActivity.VEDIO_URL, videoInfo.getData());

                setResult(SelectLocalVedioActivity.COMPLETE,intent);
                finish();
            }
        });
    }


    /**
     * 获取本地所有视频以及其信息
     * @return
     */
    private List<VideoInfo> getLocalVedio(){
        List<VideoInfo> temp = new ArrayList<>();
        String progress[]={

                MediaStore.Video.Media.DISPLAY_NAME,//视频的名字
                MediaStore.Video.Media.SIZE,//大小
                MediaStore.Video.Media.DURATION,//长度
                MediaStore.Video.Media.DATA,//播放地址
        };

        //获取数据提供者,this是上下文
        ContentResolver cr = this.getContentResolver();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            //有sd卡的情况
            Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,progress,null,null,null);
            while(cursor.moveToNext()){
                // 到视频文件的信息
                String name = cursor.getString(0);//得到视频的名字
                long size = cursor.getLong(1);//得到视频的大小
                long durantion = cursor.getLong(2);//得到视频的时间长度
                String data = cursor.getString(3);//得到视频的路径，可以转化为uri进行视频播放
                //使用静态方法获取视频的缩略图
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.MINI_KIND);
                VideoInfo videoInfo = new VideoInfo();
                //创建视频信息对象
                videoInfo.setVedioName(name);
                videoInfo.setData(data);
                videoInfo.setDuration(durantion);
                videoInfo.setSize(size);
                videoInfo.setThumImage(thumbnail);

                temp.add(videoInfo);
            }
        }
        //不论是否有sd卡都要查询手机内存
        Cursor cursor = cr.query(MediaStore.Video.Media.INTERNAL_CONTENT_URI,progress,null,null,null);
        while(cursor.moveToNext()){
            // 到视频文件的信息
            String name = cursor.getString(0);//得到视频的名字
            long size = cursor.getLong(1);//得到视频的大小
            long durantion = cursor.getLong(2);//得到视频的时间长度
            String data = cursor.getString(3);//得到视频的路径，可以转化为uri进行视频播放
            //使用静态方法获取视频的缩略图
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.MINI_KIND);
            VideoInfo videoInfo = new VideoInfo();
            //创建视频信息对象
            videoInfo.setData(data);
            videoInfo.setVedioName(name);
            videoInfo.setDuration(durantion);
            videoInfo.setSize(size);
            videoInfo.setThumImage(thumbnail);

            temp.add(videoInfo);
        }

        return temp;
    }

}
