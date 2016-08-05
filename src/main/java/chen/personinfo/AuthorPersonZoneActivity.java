package chen.personinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.myapplication.R;
import com.example.chen.myapplication.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;

import Model.User;
import Model.VideoModel;
import MyView.RefreshListview;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 作者的个人主页界面
 */
public class AuthorPersonZoneActivity extends Activity {

    private ImageView imgReturn;
    private TextView tvTitle;
    RefreshListview lv ;
    private User author;
    //个人上传的视频的数据源集合
    private List<VideoModel> mDatas = new ArrayList<>();
    //个人上传视频的适配器
    PersonInfoAdapter<VideoModel> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_person_zone);
        init();
    }

    private void init(){

        //查询数据库 得到个人上传的视频集合
        BmobQuery<VideoModel> query = new BmobQuery<>();imgReturn = (ImageView) findViewById(R.id.img_return);
        tvTitle = (TextView) findViewById(R.id.tv_top_titile);

        //获取传递过来的作者 用户 对象
        author = (User) this.getIntent().getSerializableExtra(VideoPlayActivity.KEY_AUTHOR_OBJECT);
        query.addWhereEqualTo("ownerUser",author);
        query.findObjects(this, new FindListener<VideoModel>() {
            @Override
            public void onSuccess(List<VideoModel> list) {
                //获取当前作者上传的视频列表
                mDatas.addAll(list);
                //更新视频列表
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        lv = (RefreshListview) findViewById(R.id.lv_author_person_zone);
        tvTitle.setText("个人空间");
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthorPersonZoneActivity.this.finish();
            }
        });
        if (author == null){
            Toast.makeText(this,"网络异常",Toast.LENGTH_SHORT).show();
            //强行new 已防止空指针
            author = new User();
        }

//        adapter = new PersonInfoAdapter<VideoModel>(author,this, mDatas) {
//            @Override
//            public ViewGroup configCommonListView(View convertView, int position, ViewGroup parent) {
//                return null;
//            }
//        };

        adapter = new PersonVideoListAdapter(author,this,mDatas);

        lv.setAdapter(adapter);
        lv.setOnRefreshListener(new RefreshListview.onRefreshListener() {
            @Override
            public void onPullRefresh() {
                lv.onRefreshComplete();
            }

            @Override
            public void onUpResfresh() {

                lv.onUpComplete();
            }
        });
    }
}
