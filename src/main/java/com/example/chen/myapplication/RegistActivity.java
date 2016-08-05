package com.example.chen.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import DataContext.AppData;
import Model.User;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends Activity {

    private EditText etRegistName;
    private EditText etRegistPasswrod;
    private EditText etRegistEmail;
    private Button btnRegist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        //初始化控件
        etRegistName = (EditText) findViewById(R.id.et_regist_name);
        etRegistPasswrod = (EditText) findViewById(R.id.et_regist_password);
        etRegistEmail = (EditText) findViewById(R.id.et_regist_email);
        btnRegist = (Button) findViewById(R.id.btn_regist);

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册点击事件
                final ProgressDialog progressDialog = ProgressDialog.show(RegistActivity.this,"提示","请稍后");
                String registName = etRegistName.getText().toString();
                String registPassword = etRegistPasswrod.getText().toString();
                String registEmail = etRegistEmail.getText().toString();
                final User user = new User();
                user.setUsername(registName);
                user.setPassword(registPassword);
                user.setEmail(registEmail);

                //注册并上传bmob服务器
                user.signUp(RegistActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        //注册成功进入主界面，并将全局user对象赋值
                        AppData.user = user;
                        Intent intent = new Intent(RegistActivity.this,MainActivity.class);
                        startActivity(intent);
                        RegistActivity.this.finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistActivity.this,"注册失败"+s,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

}
