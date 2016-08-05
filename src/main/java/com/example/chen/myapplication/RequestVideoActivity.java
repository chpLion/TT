package com.example.chen.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import DataContext.AppData;
import DataContext.MethodUtil;
import Model.RequestVideo;
import cn.bmob.v3.listener.SaveListener;

public class RequestVideoActivity extends Activity {

    private RequestVideo requestVideo;
    EditText etTitle,etContent,etPrice;
    public static int SUCCESS = 0;
    public static int FAIL = 1;
    private Handler mHandler;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_video);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == SUCCESS){
                    //发布成功 提示框消失 界面消失
                    progressDialog.dismiss();
                    Toast.makeText(RequestVideoActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                    RequestVideoActivity.this.finish();

                }else if (msg.what == FAIL){
                    //失败
                    progressDialog.dismiss();
                    Toast.makeText(RequestVideoActivity.this,"发布失败",Toast.LENGTH_SHORT).show();
                }
            }
        };
        etContent = (EditText) findViewById(R.id.et_request_price);
        etTitle = (EditText) findViewById(R.id.et_request_title);
        etPrice = (EditText) findViewById(R.id.et_request_price);
        TextView tvOk = (TextView) findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发布需求
                requestVideo = new RequestVideo();
                requestVideo.setUserName(AppData.user.getUsername());
                if (AppData.user.getHeadImageUrl() !=null){
                    requestVideo.setHeadImageUrl(AppData.user.getHeadImageUrl());
                }
                if (etContent.getText().toString().trim().equals("")||
                        etPrice.getText().toString().trim().equals("")||
                        etTitle.getText().toString().trim().equals("")){
                    Toast.makeText(RequestVideoActivity.this,"请填写完整",Toast.LENGTH_SHORT).show();
                }else{
                    //判断价格是否合法
                    float price = isRightPrice(etPrice.getText().toString());
                    if (price<0){
                        //输入不合法
                        Toast.makeText(RequestVideoActivity.this,"价格错误",Toast.LENGTH_SHORT).show();

                    }else{
                        //上传
                        requestVideo.setTimeStr(MethodUtil.geTimeStr());
                        requestVideo.setTitle(etTitle.getText().toString());
                        requestVideo.setDecrabe(etContent.getText().toString());
                        requestVideo.setPrice((int)price);
                        progressDialog = ProgressDialog.show(RequestVideoActivity.this,"提示","正在发布...");

                        requestVideo.save(RequestVideoActivity.this, new SaveListener() {
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

            }
        });

        ImageView imgReturn = (ImageView) findViewById(R.id.img_return);
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestVideoActivity.this.finish();
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

}
