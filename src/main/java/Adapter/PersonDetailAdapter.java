package Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.PersonDetailActivity;
import com.example.chen.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import DataContext.AppData;
import DataContext.ImagesUtil;
import Model.PersonDetailModel;
import Model.User;
import MyView.HeighListView;
import cn.bmob.v3.BmobUser;

/**
 * Created by chen on 16/2/28.
 */
public class PersonDetailAdapter extends SingleAdapter<User> {

    private Handler mHandler;
    public static final int LOG_OUT = 10;
    private Activity activity;
    public PersonDetailAdapter(Activity activity, List<User> mDates, int layoutId) {
        super(activity.getApplicationContext(), mDates, layoutId);
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return 2;
    }

    public void sethandler(Handler mhandler) {
        this.mHandler = mhandler;
    }

    @Override
    public void ConfigView(SingleLayoutViewholder holder, User user) {
        User currentuser = AppData.user;
        if (holder.getPosition() == 0){
            HeighListView heighListView = (HeighListView) holder.getView(R.id.list_detail_layout);
            List<PersonDetailModel> models = new ArrayList<>();
            //第一部分
            String [] details = {"头像","用户名","TT币"};
            //要显示的数据
            String [] valus = {"",currentuser.getUsername(),currentuser.getUserMoney()+""};
            for (int i=0;i<details.length;i++){
                PersonDetailModel detailModel = new PersonDetailModel();
                detailModel.setDetail(details[i]);
                if (details[i].equals("头像")){
                    //detailModel.setBitmap(BitmapFactory.decodeFile(currentuser.getHeadImage()));
                    detailModel.setValue("");
                }else{
                    detailModel.setValue(valus[i]);
                }
                models.add(detailModel);
            }
            SingleAdapter<PersonDetailModel> adapter = new SingleAdapter<PersonDetailModel>(context,models,R.layout.person_detail_list_item) {
                @Override
                public void ConfigView(SingleLayoutViewholder holder, PersonDetailModel personDetailModel) {
                    ((TextView)holder.getView(R.id.tv_detail)).setText(personDetailModel.getDetail());
                    if (holder.getPosition() == 0){
                        //需要显示头像
                        ImageView head = (ImageView)holder.getView(R.id.img_person_detail_head);
                        head.setVisibility(View.VISIBLE);
                        ImagesUtil imagesUtil = ImagesUtil.getInstance();
                        ((TextView) holder.getView(R.id.tv_detail_value)).setText(personDetailModel.getValue());
                        if (AppData.user.getHeadImageUrl() == null){
                            return;
                        }
                        imagesUtil.getBitmapByAsyncTask(context,head,AppData.user.getHeadImageUrl());
                    }
                    else{
                        ((TextView) holder.getView(R.id.tv_detail_value)).setText(personDetailModel.getValue());
                        ImageView head = (ImageView)holder.getView(R.id.img_person_detail_head);
                        head.setVisibility(View.GONE);
                    }
                }
            } ;

            heighListView.setAdapter(adapter);
            heighListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0){
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                        activity.startActivityForResult(intent, PersonDetailActivity.PICK_IMAGE_LIBERARY);
                    }
                }
            });

        }
        else{
            //第二部分 退出登录
            HeighListView heighListView = (HeighListView) holder.getView(R.id.list_detail_layout);
            List<PersonDetailModel> models = new ArrayList<>();
            //第一部分
            String [] details = {"退出登录"};
            //要显示的数据
            String [] valus = {""};
            for (int i=0;i<details.length;i++){
                PersonDetailModel detailModel = new PersonDetailModel();
                detailModel.setDetail(details[i]);
                if (details[i].equals("头像")&&currentuser.getHeadImage()!=null){

                    detailModel.setBitmap(BitmapFactory.decodeFile(currentuser.getHeadImage()));
                }else{
                    detailModel.setValue(valus[i]);
                }
                models.add(detailModel);
            }
            SingleAdapter<PersonDetailModel> adapter = new SingleAdapter<PersonDetailModel>(context,models,R.layout.person_detail_list_item) {
                @Override
                public void ConfigView(SingleLayoutViewholder holder, PersonDetailModel personDetailModel) {
                    ((TextView)holder.getView(R.id.tv_detail)).setText(personDetailModel.getDetail());
                    if (holder.getPosition() == 0){
                        //需要显示头像
                        ImageView head = (ImageView)holder.getView(R.id.img_person_detail_head);
                        head.setVisibility(View.VISIBLE);
                        head.setImageBitmap(personDetailModel.getBitmap());
                    }
                    else{
                        ((TextView) holder.getView(R.id.tv_detail_value)).setText(personDetailModel.getValue());
                    }
                }
            } ;

            heighListView.setAdapter(adapter);
            heighListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0){
                        //点击退出登录
                        BmobUser.logOut(context);   //清除缓存用户对象
                        BmobUser currentUser = BmobUser.getCurrentUser(context);
                        AppData.user = null;
                        mHandler.sendEmptyMessage(LOG_OUT);
                    }
                }
            });
        }
    }

    //选择头像之后的回调

}
