package chen.upload;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.example.chen.myapplication.UpLoadVedioActivty;
import com.youku.uploader.IUploadResponseHandler;
import com.youku.uploader.YoukuUploader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import DataContext.AppData;
import DataContext.MethodUtil;
import Model.VideoModel;
import chen.db.UploadDataHelper;
import chen.db.UploadFinishedDataHelper;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by chen on 16/3/16.
 */
public class UploadService extends Service {

    public static String ACTION_START = "ACTION_START";
    public static String ACTION_ONPROGRESS = "ACTION_ONPROGRESS";
    public static String ACTION_SUCCESS = "ACTION_SUCCESS";
    public static String ACTION_FINISH = "ACTION_FINISH";
    public static String ACTION_FAIL = "ACTION_FAIL";
    public static String KEY_VIDEOMODEL = UpLoadVedioActivty.KEY_VIDEO_MODEL;
    public static String KEY_PROGRESS = "KEY_PROGRESS";
    private static int MSG_UPLOAD_COMPLETE = 0;
    private VideoModel videoModel;
    private UploadFinishedDataHelper finishedDataHelper;
    YoukuUploader uploader;
    private String videoPath;
    private UploadInfo info = new UploadInfo();//需要保存的上传信息
    private UploadDataHelper helper;//数据库处理工具类
    private String filePath;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_UPLOAD_COMPLETE){
                //上传完成
                videoModel.setOwnerName(AppData.user.getUsername());
                videoModel.setVideoOwnerId(AppData.user.getObjectId());
                videoModel.save(getApplicationContext());
                //将当前视频写入本地数据库
                MyUploadVideoInfo myUploadVideoInfo = new MyUploadVideoInfo();
                myUploadVideoInfo.setThumPath(filePath);
                myUploadVideoInfo.setDescription(videoModel.getDescription());
                myUploadVideoInfo.setType(videoModel.getVideoType());
                myUploadVideoInfo.setLength(videoModel.getVideoLength());
                myUploadVideoInfo.setPrice(videoModel.getPrice());
                myUploadVideoInfo.setVideoName(videoModel.getVideoName());
                myUploadVideoInfo.setThumUrl(videoModel.getThumUrl());
                finishedDataHelper = new UploadFinishedDataHelper(getApplicationContext());
                //写入本地数据库
                finishedDataHelper.saveUploadFinished(myUploadVideoInfo);
                Intent intent = new Intent();
                intent.setAction(ACTION_FINISH);
                sendBroadcast(intent);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


//
        //获取传过来的视频模型对象

        if (intent == null){
            Toast.makeText(getApplicationContext(),"传入intent为空",Toast.LENGTH_SHORT).show();
            return -1;
        }
        videoModel = (VideoModel) intent.getSerializableExtra(UpLoadVedioActivty.KEY_VIDEO_MODEL);
        videoPath = intent.getStringExtra(UpLoadVedioActivty.KEY_VIDEO_PATH);
        filePath = intent.getStringExtra(UpLoadVedioActivty.KEY_FILE_PATH);

        //为视频上传信息对象保存数据
        info.setThum(filePath);
        info.setUploadProgress(0);
        info.setVideoDescription(videoModel.getDescription());
        info.setVideoName(videoModel.getVideoName());

        //实例化数据库使用工具类
        helper = new UploadDataHelper(getApplicationContext());
         //使用优酷上传
        uploader = YoukuUploader.getInstance(AppData.clientid,AppData.clientsecret,this);
        HashMap<String, String> params = new HashMap<String, String>();
        //access_token可以在http://cloud.youku.com/tools的手动获取选项卡获取
        params.put("access_token", "2322ddef0ed902615f2d092f860c9262");
        HashMap<String, String> uploadInfo = new HashMap<String, String>();
        uploadInfo.put("title", videoModel.getVideoName());
        uploadInfo.put("tags", MethodUtil.getTYpeNameFromType(videoModel.getVideoType()));
        uploadInfo.put("description",videoModel.getDescription());
        uploadInfo.put("file_name", videoPath);

        uploader.upload(params, uploadInfo, new IUploadResponseHandler() {

            @Override
            public void onStart() {

                //发送表示开始上传的广播
                Intent intent2 = new Intent();
                intent2.setAction(ACTION_START);
                //写入数据库
                helper.saveUploadInfo(info);
                sendBroadcast(intent);
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    videoModel.setVideoId(response.getString("video_id"));
                    //表示已经完成的动作
                    Intent intent2 = new Intent();
                    intent2.setAction(ACTION_SUCCESS);
                    intent2.putExtra(KEY_VIDEOMODEL,videoModel);
                    sendBroadcast(intent2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //异步上传视频缩略图
                uploadVedioThumImage(filePath);
            }

            @Override
            public void onProgressUpdate(int counter) {;

                Intent intent2 = new Intent();
                intent2.setAction(ACTION_ONPROGRESS);
                intent2.putExtra(KEY_PROGRESS,counter);

                //更新数据
                info.setUploadProgress(counter);
                helper.updateUploadProgress(info);
                sendBroadcast(intent2);
            }

            @Override
            public void onFailure(JSONObject errorResponse) {

                Intent intent2 = new Intent();
                intent2.setAction(ACTION_FAIL);
                sendBroadcast(intent2);

            }

            @Override
            public void onFinished() {

                Intent intent2 = new Intent();
                intent2.setAction(ACTION_FINISH);
                sendBroadcast(intent2);
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 上传视频缩略图
     */
    private void uploadVedioThumImage(String filePath){
        BTPFileResponse response = BmobProFile.getInstance(getApplicationContext()).upload(filePath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1, BmobFile bmobFile) {
                //Toast.makeText(UpLoadVedioActivty.this,"上传缩略图成功",Toast.LENGTH_SHORT).show();
                //获取可访问的URL
                videoModel.setThumUrl(bmobFile.getUrl());
                //发送消息给handler完成上传
                mHandler.sendEmptyMessage(MSG_UPLOAD_COMPLETE);
            }

            @Override
            public void onProgress(int i) {

            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplicationContext(),"上传缩略图失败"+s,Toast.LENGTH_LONG).show();

            }
        });
    }
}
