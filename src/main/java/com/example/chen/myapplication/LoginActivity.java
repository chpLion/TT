package com.example.chen.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import DataContext.AppData;
import Model.UserAccount;
import Model.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends Activity {

    public static final int LOGIN_SUCCESS = 1;
    public static final int LOGIN_FAIL = 2;
    private EditText etUserName ;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnNewUser;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //点击隐藏输入法
        LinearLayout ll = (LinearLayout) findViewById(R.id.linear);
        ll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //点击隐藏输入法

                InputMethodManager manager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        //初始化各个控件
        etUserName = (EditText) findViewById(R.id.et_name);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        //新用户
        btnNewUser = (Button) findViewById(R.id.btn_newuser);
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到注册界面
                Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
                LoginActivity.this.finish();
                startActivity(intent);
            }
        });
        //点击登陆
        //在数据库中查询记录
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(LoginActivity.this,"请稍等","正在登陆...");
                String userName = etUserName.getText().toString();
                final String password = etPassword.getText().toString();

                //登陆
                User user = new User();
                user.setUsername(userName);
                user.setPassword(password);

                user.loginByAccount(LoginActivity.this, userName, password, new LogInListener<User>() {
                    @Override
                    public void done(final User myUser, BmobException e) {
                        dialog.dismiss();
                        if (myUser != null){
                            //当前用户登陆成功
                            BmobQuery<UserAccount> query = new BmobQuery<UserAccount>();
                            query.addWhereEqualTo("userName",myUser.getUsername());
                            query.findObjects(LoginActivity.this, new FindListener<UserAccount>() {
                                @Override
                                public void onSuccess(List<UserAccount> list) {

                                    myUser.setUserMoney(list.get(0).getUserMoney());
                                    Toast.makeText(LoginActivity.this,list.get(0).getUserMoney()+"",Toast.LENGTH_SHORT).show();
                                    //为全局user赋值
                                    AppData.user = myUser;
                                    BmobUser bmobUser = new BmobUser();
                                    AppData.user.setUserMoney(list.get(0).getUserMoney());
                                    bmobUser = BmobUser.getCurrentUser(LoginActivity.this);
                                    AppData.user.update(LoginActivity.this, bmobUser.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            //跳转主界面
                                            setResult(LOGIN_SUCCESS);
                                            LoginActivity.this.finish();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {

                                        }
                                    });

                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });

                        }
                        else
                        {
                            //登陆失败
                            Toast.makeText(LoginActivity.this,"登陆失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

}
