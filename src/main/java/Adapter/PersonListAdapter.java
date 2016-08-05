package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.MyOrderActivity;
import com.example.chen.myapplication.R;
import com.example.chen.myapplication.UploadVideoRecordActivity;

import java.util.List;

import DataContext.AppData;
import DataContext.ImagesUtil;
import DataContext.MethodUtil;
import Model.PersonPageModel;
import Model.User;
import MyView.HeighListView;
import cn.bmob.v3.BmobUser;

/**
 * Created by chen on 16/2/22.
 */
public class PersonListAdapter extends MutiLayoutAdapter<PersonPageModel> {

    private static final int TYPE_TOP = 0;//顶部
    private static final int TYPE_ITEM = 1;//普通子项
    public PersonListAdapter(Context context, List<PersonPageModel> mDates, int count) {
        super(context, mDates, count);
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_TOP;
        }
        else{
            return TYPE_ITEM;
        }
    }

    @Override
    public CommonViewHolder getViewHolderByPosition(int position, View convertView, ViewGroup parent) {

        //根据不同的位置加载不同的holder
        CommonViewHolder holder = null;

        if (position == 0){
            //是第一项 需要加载头条布局
            holder = CommonViewHolder.getInstance(context,parent, R.layout.person_list_top,position,convertView);
        }
        else{
            //加载列表项
            holder = CommonViewHolder.getInstance(context,parent,R.layout.pserson_item,position,convertView);
        }
        return holder;
    }

    @Override
    public void ConfigView(CommonViewHolder holder, List<PersonPageModel> mDatas, int position) {
        int type = getItemViewType(position);
        TextView tvUserName = (TextView) holder.getView(R.id.user_name);
        switch (type){
            case TYPE_TOP:{
                //判断是否登录
                if (isLogin()){
                    //已经登录 显示信息
                    //用户头像的可访问地址
                    User user = AppData.user;
                    String url = user.getHeadImageUrl();
                    String path = AppData.user.getHeadImage();
                    ImageView ivHead = (ImageView) holder.getView(R.id.user_head_image);
                    if (path == null) {
                        if (url!=null) {
                            //异步加载图片
                            ImagesUtil imagesUtil = ImagesUtil.getInstance();
                            imagesUtil.getBitmapByAsyncTask(context, ivHead, url);
                        }
                    }else{
                        //已经下载好直接配置
                        Bitmap headImage = BitmapFactory.decodeFile(path);
                        ivHead.setImageBitmap(headImage);
                    }

                    tvUserName.setText(user.getUsername());
                }
                else{
                    //未登录
                    tvUserName.setTextSize(15);
                    tvUserName.setText("未登录 请点击登录");

                }
                break;
            }

            case TYPE_ITEM:{

                HeighListView listView = (HeighListView) holder.getView(R.id.lv_person_item_list);
                SingleLayoutAdapter<PersonPageModel> adapter = new SingleLayoutAdapter<PersonPageModel>(context,mDatas,R.layout.person_list_item) {
                    @Override
                    public void ConfigView(CommonViewHolder holder, PersonPageModel personPageModel) {
                        ((ImageView)holder.getView(R.id.img_person_list)).setImageResource(personPageModel.getImageId());
                        ((TextView)holder.getView(R.id.tv_person_list)).setText(personPageModel.getContent());
                    }
                };
               listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 2){
                            //乐享记录
                            MethodUtil.goToIntent(context,MyOrderActivity.class);
                        }else if (position == 1){
                            //上传记录
                            MethodUtil.goToIntent(context,UploadVideoRecordActivity.class);
                        }
                    }
                });
                break;
            }
        }
    }

    /**
     * 判断是否登陆
     * @return
     */
    private boolean isLogin(){

        //获取当前缓存的登录对象
        User user = BmobUser.getCurrentUser(context,User.class);
        //判断是否为空
        if (user != null){
            //当前已经有登录的对象
            //为全局赋值
            AppData.user = user;
            return true;
        }

        return false;
    }
}
