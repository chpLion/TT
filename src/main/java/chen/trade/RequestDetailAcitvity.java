package chen.trade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.myapplication.LoginActivity;
import com.example.chen.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import DataContext.AppData;
import Model.ReplyModel;
import Model.RequestVideo;
import MyView.RefreshListview;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class RequestDetailAcitvity extends Activity {

    private List<ReplyModel> mDatas = new ArrayList();//评论的集合
    RequestVideo requestVideo;
    private Bundle bundle;
    RequestDetailAdapter adapter;
    private static int LOGIN = 0;
    private TextView tvHint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail_acitvity);

        //获取传递过来的需求对象
        Intent mIntent = this.getIntent();
        bundle = mIntent.getExtras();
        requestVideo = (RequestVideo) bundle.getSerializable("requestvideo");

        //初始化各控件
        tvHint = (TextView) findViewById(R.id.tv_reply_hint);
        ImageView imgReturn = (ImageView) findViewById(R.id.img_return);
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDetailAcitvity.this.finish();
            }
        });

        //留言按钮
        TextView tvReply = (TextView) findViewById(R.id.tv_reply);
        tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断是否已经登录
                if (AppData.user == null){
                    //需要登录
                    Intent intent = new Intent(RequestDetailAcitvity.this, LoginActivity.class);
                    startActivityForResult(intent,LOGIN);
                }
                else{
                    Intent intent = new Intent(RequestDetailAcitvity.this,ReplyActivity.class);
                    //传递对象
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
        final RefreshListview lv = (RefreshListview) findViewById(R.id.lv_request_detail_list);

        adapter = new RequestDetailAdapter(this,mDatas,2,requestVideo);

        //通过传递的需求对象查找对应的留言
        loadData();
        lv.setOnRefreshListener(new RefreshListview.onRefreshListener() {
            @Override
            public void onPullRefresh() {

                loadData();
                adapter.notifyDataSetChanged();
                lv.onRefreshComplete();
            }

            @Override
            public void onUpResfresh() {


                adapter.notifyDataSetChanged();
                lv.onUpComplete();
            }
        });
        lv.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN){
            if (resultCode == LoginActivity.LOGIN_SUCCESS){
                //登录成功 跳转留言界面
                Intent intent = new Intent(RequestDetailAcitvity.this,ReplyActivity.class);
                //传递对象
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

    private void loadData(){
        //通过传递的需求对象查找对应的留言
        BmobQuery<ReplyModel> query = new BmobQuery<>();
        query.addWhereEqualTo("requestId",requestVideo.getObjectId());

        query.findObjects(this, new FindListener<ReplyModel>() {
            @Override
            public void onSuccess(List<ReplyModel> list) {
                mDatas.clear();
                mDatas.addAll(list);
                Collections.reverse(mDatas);
                adapter.notifyDataSetChanged();
                if (list.size()!=0){
                    tvHint.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(RequestDetailAcitvity.this,s,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
