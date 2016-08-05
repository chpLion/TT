package com.example.chen.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.youku.uploader.IUploadResponseHandler;
import com.youku.uploader.YoukuUploader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.SingleAdapter;
import Adapter.SingleLayoutViewholder;
import DataContext.AppData;
import DataContext.MethodUtil;
import DataContext.TypeModel;
import Model.VideoModel;
import MyView.MyGridView;
import chen.upload.UploadService;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

public class UpLoadVedioActivty extends Activity {

    private TextView tvSelectVedio;
    private YoukuUploader uploader;
    private TextView btnUpload;
    private ImageView selectImage;//选择封面
    EditText etPrice,etDesreable,etVideoName;//视频价格,视频描述,名字
    private String videoPath = "";//需要上传的视频的文件路径
    private String filePath = "";//保存在本地的缩略图文件路径
    private static final int SELECT_VEDIO = 0;//请求码
    private static final int UPLOAD_COMPLETE = 2;
    private MyGridView gridType;
    private TextView tvSelectType;
    private TextView tvProgress;
    private ProgressBar progressBar;//进度条
    public static String KEY_VIDEO_PATH = "KEY_VIDEO_PATH";//表示视频路径的键值
    public static String KEY_VIDEO_MODEL = "KEY_VIDEO_MODEL";//表示视频模型对象的键值
    public static String KEY_FILE_PATH = "KEY_FILE_PATH";//表示视频模型对象的键值
    private int type = -1;
    private VideoModel videoModel = new VideoModel();//要上传的视频对象

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPLOAD_COMPLETE){
                //异步上传视频完成
                //保存视频模型
                videoModel.save(UpLoadVedioActivty.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UpLoadVedioActivty.this,"视频已经上传，请等待审核",Toast.LENGTH_SHORT).show();
                        UpLoadVedioActivty.this.finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_load_vedio_activty);

        gridType = (MyGridView) findViewById(R.id.grid_upload_type);
        etVideoName = (EditText) findViewById(R.id.et_video_name);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        tvSelectType = (TextView) findViewById(R.id.tv_select_type);
        //选择上传视频的类型
        String typeNames [] = {"热点推荐","互联网","风土人情",
                "金融","爱生活","英语",
                "健康","自制微电影","法律",
                "热卖","工程","哲学",
                "明星大v","电商","iOS开发",
                "Android开发"};
        List<TypeModel> types = new ArrayList<>();
        for (int i=0;i<typeNames.length;i++){
            TypeModel model = new TypeModel();
            model.setTypeName(typeNames[i]);
            types.add(model);
        }
        SingleAdapter<TypeModel> adapter = new SingleAdapter<TypeModel>(UpLoadVedioActivty.this,types,R.layout.grid_type_item) {
            @Override
            public void ConfigView(SingleLayoutViewholder holder, TypeModel typeModel) {
                ((TextView)holder.getView(R.id.tv_type_name)).setText(typeModel.getTypeName());
            }
        };
        gridType.setAdapter(adapter);
        //设置item选择事件
        gridType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //显示所选的类型
                tvSelectType.setText(MethodUtil.getTYpeNameFromType(position));
                type = position;
            }
        });
        etDesreable = (EditText) findViewById(R.id.et_descreble);
        etPrice = (EditText) findViewById(R.id.et_vedio_price);
        //进度
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        //选取本地视频
        tvSelectVedio = (TextView) findViewById(R.id.tv_select);
        tvSelectVedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到选择界面
                Intent intent = new Intent(UpLoadVedioActivty.this,SelectLocalVedioActivity.class);
                //返回选择的视频对象
                startActivityForResult(intent,SELECT_VEDIO);
            }
        });
       //设置封面
        selectImage = (ImageView) findViewById(R.id.img_vedio_image);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //确认发布
        btnUpload = (TextView) findViewById(R.id.tv_ok);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //上传视频
                uploadToServer();
            }
        });
        ImageView imgReturn = (ImageView) findViewById(R.id.img_return);
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpLoadVedioActivty.this.finish();
            }
        });
    }

    /**
     * 判断输入的价格是否合法
     * @param price
     * @return
     */
    private float isRightPrice(String etPrice){
        boolean flag = true;
        try {
            float price = Float.parseFloat(etPrice);
            return price;
        }
        catch (Exception e){
            return -1;
        }

    }

    /**
     * 将用户选中的视频上传到bmob的后台
     */
    private void uploadToServer(){
        //判断输入的价格是否合法
        final float price = isRightPrice(etPrice.getText().toString());
        if (price < 0){
            //输入数字不合法
            Toast.makeText(UpLoadVedioActivty.this,"您输入的价格不合法",Toast.LENGTH_SHORT).show();
        }
        else{
            //输入数字合法
            //判断简介是否填写
            if (etDesreable.getText().toString().trim() == ""||etVideoName.getText().toString().trim() == ""){
                //为空
                Toast.makeText(UpLoadVedioActivty.this,"请输入视频名称",Toast.LENGTH_SHORT).show();
            }
            else{
                //一切正常
                if (videoPath.equals("")){
                    Toast.makeText(UpLoadVedioActivty.this,"请选择要上传的视频",Toast.LENGTH_SHORT).show();
                }
                if (type == -1){
                    Toast.makeText(UpLoadVedioActivty.this,"请选择类型",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    //启动service
                    Intent intent = new Intent(UpLoadVedioActivty.this,UploadService.class);
                    intent.setAction(UploadService.ACTION_START);
                    //实例化要传送给service 的数据。

                    videoModel.setDescription(etDesreable.getText().toString());
                    videoModel.setVideoName(etVideoName.getText().toString());
                    videoModel.setVideoType(type);
                    videoModel.setPrice(price);
                    //视频文件在手机中的路径
                    intent.putExtra(KEY_VIDEO_MODEL,videoModel);
                    intent.putExtra(KEY_VIDEO_PATH,videoPath);
                    intent.putExtra(KEY_FILE_PATH,filePath);

                    Intent intent1 = new Intent(UpLoadVedioActivty.this,UploadVideoRecordActivity.class);
                    intent1.putExtra(KEY_VIDEO_MODEL,videoModel);
                    intent1.putExtra(KEY_VIDEO_PATH,videoPath);
                    intent1.putExtra(KEY_FILE_PATH,filePath);
                    startActivity(intent1);

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == SelectLocalVedioActivity.COMPLETE){
            //用户选择了视频
            String vedioName = data.getStringExtra(SelectLocalVedioActivity.VEDIO_NAME);
            long vedioSize = data.getLongExtra(SelectLocalVedioActivity.VEDIO_SIZE,0);
            long vedioDuration = data.getLongExtra(SelectLocalVedioActivity.VEDIO_DURATION,0);
            videoPath = data.getStringExtra(SelectLocalVedioActivity.VEDIO_URL);

            //为上传视频赋值
            videoModel.setVideoLength(MethodUtil.getVedioLengthFromDuration(vedioDuration));
            videoModel.setVideoName(vedioName);
            //创建缩略图
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
            //将缩略图保存在本地
            filePath = saveImageInLocal(vedioName,thumbnail);
            //为封面设置缩略图
            selectImage.setImageBitmap(thumbnail);

        }
    }

    /**
     * 上传视频缩略图
     */
    private void uploadVedioThumImage(String filePath){
        BTPFileResponse response = BmobProFile.getInstance(UpLoadVedioActivty.this).upload(filePath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1, BmobFile bmobFile) {
                //Toast.makeText(UpLoadVedioActivty.this,"上传缩略图成功",Toast.LENGTH_SHORT).show();
                //获取可访问的URL
                videoModel.setThumUrl(bmobFile.getUrl());
                //发送消息给handler完成上传
                mHandler.sendEmptyMessage(UPLOAD_COMPLETE);
            }

            @Override
            public void onProgress(int i) {

            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(UpLoadVedioActivty.this,"上传缩略图失败"+s,Toast.LENGTH_LONG).show();

            }
        });
    }

    /**
     * 将生成的缩略图保存到本地
     * @param vedioName
     * @param bitmap
     * @return 保存在内存中的图片路径
     */
    private String saveImageInLocal(String vedioName,Bitmap bitmap){

        String filePath = "";
        String packageName = getPackageName();
        try {

            //创建读写文件流对象
            FileOutputStream out = openFileOutput(vedioName+".png",MODE_PRIVATE);
            //将缩略图保存在本地
            bitmap.compress(Bitmap.CompressFormat.PNG,90,out);
            filePath = "/data/data/"+packageName+"/files/"+vedioName+".png";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(UpLoadVedioActivty.this,""+e.toString(),Toast.LENGTH_SHORT).show();
        }

        return filePath;
    }

    /**
     * 上传完成之后删除本地缩略图节省内存
     * @param filePath
     */
    private void deleteThum(String filePath){

        File file = new File(filePath);
        file.delete();
    }



}
