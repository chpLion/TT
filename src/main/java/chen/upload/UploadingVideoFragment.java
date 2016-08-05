package chen.upload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chen.myapplication.R;
import com.example.chen.myapplication.UploadVideoRecordActivity;

import DataContext.AppData;
import Model.VideoModel;
import chen.db.UploadDataHelper;

/**
 * Created by chen on 16/3/16.
 */
public class UploadingVideoFragment extends Fragment {

    private TextView tvCount ;
    private TextView tvUploadStatus ;
    private ProgressBar progressBar;
    private TextView tvVideoName;
    private TextView tvVideoDescription;
    private ImageView imgThum;
    TextView tvNoUploadVideo;
    LinearLayout llUploadView;//显示上次进度的view
    private UploadDataHelper helper;//数据库操作对象
    private UploadInfo info;//上传信息对象
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_uploading_video,container,false);

        helper = new UploadDataHelper(this.getActivity());
        //初始化各个控件
        tvCount = (TextView) view.findViewById(R.id.tv_upload_counter);
        tvUploadStatus = (TextView) view.findViewById(R.id.tv_upload_status);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_uploading);
        llUploadView = (LinearLayout) view.findViewById(R.id.ll_upload_view);
        tvVideoName = (TextView) view.findViewById(R.id.tv_upload_video_name);
        tvVideoDescription = (TextView) view.findViewById(R.id.tv_upload_video_description);
        tvNoUploadVideo = (TextView) view.findViewById(R.id.tv_no_upload);
        imgThum = (ImageView) view.findViewById(R.id.img_upload_video);
        UploadVideoRecordActivity uploadVideoRecordActivity = (UploadVideoRecordActivity) getActivity();
        VideoModel videoModel = uploadVideoRecordActivity.getVideoUpload();
        String filePath = uploadVideoRecordActivity.getFilePath();
        if (videoModel !=null){
            tvVideoDescription.setText(videoModel.getDescription());
            tvVideoName.setText(videoModel.getVideoName());
            imgThum.setImageBitmap(BitmapFactory.decodeFile(filePath));
        }
        else{
            info = helper.getUPloadInfo(false);
            if (info == null){
                tvNoUploadVideo.setVisibility(View.VISIBLE);
                llUploadView.setVisibility(View.GONE);
            }
            else{
                imgThum.setImageBitmap(BitmapFactory.decodeFile(info.getThum()));
                tvVideoName.setText(info.getVideoName());
                tvVideoDescription.setText(info.getVideoDescription());
                progressBar.setMax(100);
                tvCount.setText(info.getUploadProgress()+"%");
                progressBar.setProgress(info.getUploadProgress());
            }
        }


        //注册广播接收器
        IntentFilter filter = new IntentFilter();

        filter.addAction(UploadService.ACTION_START);
        filter.addAction(UploadService.ACTION_ONPROGRESS);
        filter.addAction(UploadService.ACTION_SUCCESS);
        filter.addAction(UploadService.ACTION_FAIL);
        this.getActivity().registerReceiver(mBroadcastReceiver,filter);
        return view;
    }


    /**
     * 广播接收器，用于获取service传来的数据
     */
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            llUploadView.setVisibility(View.VISIBLE);
            info = helper.getUPloadInfo(false);
            tvVideoName.setText(info.getVideoName());
            tvVideoDescription.setText(info.getVideoDescription());
            imgThum.setImageBitmap(BitmapFactory.decodeFile(info.getThum()));
            if (action.equals(UploadService.ACTION_START)){
                tvUploadStatus.setText("开始上传");
            }else if (action.equals(UploadService.ACTION_ONPROGRESS)){
                int counter = intent.getIntExtra(UploadService.KEY_PROGRESS,0);
                tvUploadStatus.setText("正在上传");
                progressBar.setProgress(counter);
                tvCount.setText(counter+"%");
            }else if (action.equals(UploadService.ACTION_SUCCESS)){
                tvUploadStatus.setText("上传视频成功");
                //关闭service 设置service启动标志位false
                Intent service = new Intent(UploadingVideoFragment.this.getActivity(),UploadService.class);
                getActivity().stopService(service);
                //删除存储的数据库表信息
                helper.deleteUploadInfo(info);
                AppData.isStartService = false;
                //设置上传界面消失
                llUploadView.setVisibility(View.GONE);
                //设置提示当前无上传视频启动
                tvNoUploadVideo.setVisibility(View.VISIBLE);
                getActivity().unregisterReceiver(mBroadcastReceiver);

            }else if (action.equals(UploadService.ACTION_FINISH)){
                //上传成功
                //关闭service 设置service启动标志位false
                Intent service = new Intent(UploadingVideoFragment.this.getActivity(),UploadService.class);
                getActivity().stopService(service);
                helper.deleteUploadInfo(info);
                AppData.isStartService = false;
                //设置上传界面消失
                llUploadView.setVisibility(View.GONE);
                //设置提示当前无上传视频启动
                tvNoUploadVideo.setVisibility(View.VISIBLE);
                getActivity().unregisterReceiver(mBroadcastReceiver);


            }else if (action.equals(UploadService.ACTION_FAIL)){
                helper.deleteUploadInfo(info);
                tvUploadStatus.setText("抱歉上传失败，请重试");
                AppData.isStartService = false;
            }
        }
    };


}


