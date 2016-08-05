package chen.trade;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chen.myapplication.R;

import DataContext.AppData;
import DataContext.MethodUtil;
import Model.ReplyModel;
import Model.RequestVideo;
import Model.User;
import cn.bmob.v3.listener.SaveListener;

public class ReplyActivity extends Activity {

    EditText etReplyConent;
    private String requestId;//当前需求对象的唯一标识
    private RequestVideo requestVideo;
    private static int SUCCESS = 0;//留言成功
    private static int FAIL = 1;//留言失败
    private ProgressDialog progressDialog;

    //消息处理对象
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //不论成功与否 等待框都要消失
            progressDialog.dismiss();
            if (msg.what == SUCCESS){
                //发布留言成功
                Toast.makeText(ReplyActivity.this,"发布留言成功 请等待对方回复",Toast.LENGTH_LONG).show();
                //界面消失
                ReplyActivity.this.finish();
            }
            else
            {
                Toast.makeText(ReplyActivity.this,"发布留言失败，请重试",Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        //获取传递过来的需求对象
        Intent mIntent = this.getIntent();
        final Bundle bundle = mIntent.getExtras();
        requestVideo = (RequestVideo) bundle.getSerializable("requestvideo");
        requestId = requestVideo.getObjectId();

        etReplyConent = (EditText) findViewById(R.id.et_reply_content);
        //取消按钮
        Button btnCanle = (Button) findViewById(R.id.btn_canle);
        btnCanle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyActivity.this.finish();
            }
        });
        //留言按钮
        Button btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etReplyConent.getText().toString();
                if (content.trim().equals("")){
                    Toast.makeText(ReplyActivity.this,"请写点啥呢",Toast.LENGTH_SHORT).show();
                }else{
                    //上传数据库
                    ReplyModel replyModel = new ReplyModel();
                    User user = AppData.user;
                    replyModel.setUserName(user.getUsername());
                    replyModel.setContent(content);
                    replyModel.setHeadUrl(user.getHeadImageUrl());
                    replyModel.setTime(MethodUtil.geTimeStr());
                    replyModel.setRequestId(requestId);
                    //等待框出现
                    progressDialog = ProgressDialog.show(ReplyActivity.this,"稍等","正在留言");
                    replyModel.save(ReplyActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            mHandler.sendEmptyMessage(SUCCESS);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            mHandler.sendEmptyMessage(FAIL);
                        }
                    });
                }
            }
        });
    }

}
