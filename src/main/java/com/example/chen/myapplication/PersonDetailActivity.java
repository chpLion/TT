package com.example.chen.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Adapter.PersonDetailAdapter;
import DataContext.AppData;
import DataContext.MethodUtil;
import Model.User;
import MyInterface.onSetHeadImageUpdateListener;
import chen.service.UpdateHeadImageService;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;

public class PersonDetailActivity extends Activity {

    List<User> mDatas;
    public static final int PICK_IMAGE_LIBERARY = 0;
    onSetHeadImageUpdateListener listener;

    public void setListener(onSetHeadImageUpdateListener listener) {
        this.listener = listener;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PersonDetailAdapter.LOG_OUT){
                Intent intent = new Intent();
                //将执行结果回传
                setResult(PersonDetailAdapter.LOG_OUT);
                PersonDetailActivity.this.finish();
            }
        }
    };

    PersonDetailAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        TextView tvTitle = (TextView) findViewById(R.id.tv_top_titile);
        tvTitle.setText("个人信息");
        ImageView imgReturn = (ImageView) findViewById(R.id.img_return);
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonDetailActivity.this.finish();
            }
        });
        final ListView detailList = (ListView) findViewById(R.id.list_person_detail);
        mDatas = new ArrayList<>();
        mDatas.add(AppData.user);
        mDatas.add(AppData.user);
        adapter = new PersonDetailAdapter(PersonDetailActivity.this,mDatas,R.layout.person_detail_list_layout);
        adapter.sethandler(mHandler);

        //设置点击顶部头像更换头像
        detailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                    startActivityForResult(intent,PICK_IMAGE_LIBERARY);
                }
            }
        });
        detailList.setAdapter(adapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_LIBERARY){
            Toast.makeText(this,"正在更新请稍等...",Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK){
                Uri selectedImage = data.getData();
                selectedImage.getPath();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Bitmap bitmap = null;
                //上传头像文件到服务器
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                    final String filePath = saveBitmapInLocal("headImage"+ MethodUtil.geTimeStr(),bitmap);
                    BTPFileResponse response = BmobProFile.getInstance(this).upload(filePath, new UploadListener() {
                        @Override
                        public void onSuccess(String s, String s1, BmobFile bmobFile) {
                            //更新用户信息
                            BmobUser bmobUser = new BmobUser();
                            bmobUser = BmobUser.getCurrentUser(PersonDetailActivity.this);
                            User user = AppData.user;
                            user.setHeadImage(filePath);
                            user.setHeadImageUrl(bmobFile.getUrl());
                            user.update(PersonDetailActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(PersonDetailActivity.this,"头像更换成功",Toast.LENGTH_SHORT).show();
                                    //更新适配器
                                    mDatas.add(new User());
                                    mDatas.remove(mDatas.size()-1);
                                    adapter.notifyDataSetChanged();
                                    //启动服务，通知主界面
                                    Intent service = new Intent(PersonDetailActivity.this, UpdateHeadImageService.class);
                                    service.setAction(UpdateHeadImageService.ACTION_UPDATE_HEAD);
                                    startService(service);

                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(PersonDetailActivity.this,s,Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                        @Override
                        public void onProgress(int i) {

                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(PersonDetailActivity.this,"上传头像失败"+s,Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 将图片保存在本地
     * @param bmpName
     * @param bitmap
     * @return
     */
    private String saveBitmapInLocal(String bmpName,Bitmap bitmap){
        String filePath = "";
        String packageName = getPackageName();
        try {

            //创建读写文件流对象
            FileOutputStream out = openFileOutput(bmpName+".png",MODE_PRIVATE);
            //将缩略图保存在本地
            bitmap.compress(Bitmap.CompressFormat.PNG,90,out);
            filePath = "/data/data/"+packageName+"/files/"+bmpName+".png";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(PersonDetailActivity.this,""+e.toString(),Toast.LENGTH_SHORT).show();
        }

        return filePath;
    }

}
