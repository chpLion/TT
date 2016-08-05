package com.example.chen.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import DataContext.AppData;
import Model.User;
import Model.VideoComment;
import cn.bmob.v3.listener.SaveListener;

public class WriteVidioCommentActivity extends Activity {

    //写评论
    private EditText etVideoComment;
    //完成评论
    private TextView tvOK;
    //返回
    private ImageView imgReturn;
    //标题
    private TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_vidio_comment);
        etVideoComment = (EditText) findViewById(R.id.et_video_comment);
        tvTitle = (TextView) findViewById(R.id.tv_top_titile);
        tvOK = (TextView) findViewById(R.id.tv_ok);
        tvOK.setVisibility(View.VISIBLE);
        imgReturn = (ImageView) findViewById(R.id.img_return);

        tvTitle.setText("写评论");
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteVidioCommentActivity.this.finish();
            }
        });

        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoComment videoComment = new VideoComment();
                if (etVideoComment.getText().toString().trim() == ""){
                    //没有写评论
                    Toast.makeText(WriteVidioCommentActivity.this,"写一下评论再说嘛",Toast.LENGTH_SHORT).show();
                    return;
                }
                videoComment.setComment(etVideoComment.getText().toString());
                User user = AppData.user;
                videoComment.setUserId(user.getObjectId());
                videoComment.setCommentUser(user);
                //设置从上个界面传来的视频id号
                videoComment.setVideoId(getIntent().getStringExtra(VideoPlayActivity.KEY_VIDEO_ID));
                //保存
                videoComment.save(WriteVidioCommentActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        //发表成功
                        Toast.makeText(WriteVidioCommentActivity.this,"发表评论成功",Toast.LENGTH_SHORT).show();
                        WriteVidioCommentActivity.this.finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(WriteVidioCommentActivity.this,"失败"+s,Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

}
