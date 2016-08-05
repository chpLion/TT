package com.example.chen.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
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

import Adapter.FragAdapter;
import Adapter.PersonDetailAdapter;
import Adapter.PersonListAdapter;
import Adapter.SingleAdapter;
import Adapter.SingleLayoutViewholder;
import Adapter.VideoAdapter;
import DataContext.AppData;
import DataContext.MethodUtil;
import DataContext.TypeModel;
import Model.FirstPageTitle;
import Model.PersonPageModel;
import Model.User;
import Model.UserAccount;
import Model.VideoModel;
import MyFragment.RequestVideoFragment;
import MyFragment.ShareVideoFragment;
import MyView.RefreshListview;
import chen.service.UpdateHeadImageService;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends FragmentActivity {


    private TabHost tabHost;
    private RefreshListview mVedioList;
    private ViewPager viewPager;
    private ImageView btnUpload;//上传视频
    PersonListAdapter personAdapter;//个人界面适配器
    List<VideoModel> mVedioModels = new ArrayList<>();//视频模型集合
    List<FirstPageTitle> mTtiles = new ArrayList<>();//视频标题集合
    public static final int PICK_IMAGE_LIBERARY = 2;
    private static int LOGIN_CODE = 0;
    EditText etSearch;//顶部的搜索框
    private ImageView imgSearch;//顶部的搜索按钮
    private ImageView imgAdd;//顶部添加按钮
    private ImageView imgUpload;//顶部上传按钮
    private ImageView imgDownload;//顶部下载按钮
    private ImageView imageView;//显示动画的图片
    private ImageView imgLogo;//显示APP logo
    private ImageView imgHint;//搜索框中的搜索图片
    private TextView tvSearchHint;//搜索框的文字
    private int imageWidth ;
    private static int PERSON_INFO = 1;
    private TextView tvTopName;
    private int offset = 0;//偏移量
    private List<PersonPageModel> mPersonModels;
    VideoAdapter adapter;//适配器
    private int []ImageArr = {R.drawable.home,R.drawable.pindao,R.drawable.lexiang,R.drawable.wode};
    private int [] ImagePressArrr = {R.drawable.homep,R.drawable.pindaopress,R.drawable.lexiangp,R.drawable.wodepress};
    String [] tabs = {"首页","频道","乐享","我的"};
    private ImageView cousor;//动画图片
    private int offSet = 0 ;//偏移量
    private int currentPage;//当前页
    private int bmpWidth = 0;//动画图片的宽度
    private ProgressBar progressBar;
    private static final int MSG_TITLE_LOAD_COMPLETE = 0;
    private static final int MSG_TITLE_LOAD_FILE = 1;
    private static final int MSG_DATA_LOAD_COMPLETE = 2;
    private static final int MSG_DATAE_LOAD_FAIL = 3;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == MSG_TITLE_LOAD_COMPLETE){

                //等待的菊花消失
                progressBar.setVisibility(View.GONE);
                //标题加载成功才能开始加载数据
                getDatasFromBmob();
                //才要设置适配器
                mVedioList.setAdapter(adapter);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this,"i have broken your app!",Toast.LENGTH_LONG).show();
        //初始化
        init();

        //初始化tabhost切换事件
        final TabWidget tabWidget = tabHost.getTabWidget();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                String tag = tabHost.getCurrentTabTag();
                int index = Integer.parseInt(tag);
                if (index == 0){
                    //当前是首页
                    //搜索框出来
                    etSearch.setVisibility(View.VISIBLE);
                    //顶部标题消失
                    tvTopName.setVisibility(View.GONE);
                    //搜索按钮消失
                    imgSearch.setVisibility(View.GONE);
                    //下载按钮消失
                    imgDownload.setVisibility(View.GONE);
                    //logo出来
                    imgLogo.setVisibility(View.VISIBLE);
                    //搜索框图片出来
                    imgHint.setVisibility(View.VISIBLE);
                    //文字出来
                    tvSearchHint.setVisibility(View.VISIBLE);

                }
                else {
                    etSearch.setVisibility(View.GONE);
                    tvTopName.setVisibility(View.VISIBLE);
                    imgDownload.setVisibility(View.VISIBLE);
                    imgSearch.setVisibility(View.VISIBLE);
                    String tabName = tabs[index];
                    //logo消失
                    imgLogo.setVisibility(View.GONE);
                    //搜索框图片消失
                    imgHint.setVisibility(View.GONE);
                    //文字消失
                    tvSearchHint.setVisibility(View.GONE);
                    tvTopName.setText(tabName);
                }

                for (int i=0;i<tabWidget.getChildCount();i++){
                    ImageView imageView = (ImageView) tabWidget.getChildAt(i).findViewById(android.R.id.icon);
                    if (i == index){
                        //是当前选中的的tab
                        imageView.setImageResource(ImagePressArrr[i]);
                    }
                    else{
                        //未选中的
                        imageView.setImageResource(ImageArr[i]);
                    }
                }
            }
        });

        //初始化顶部按钮
        initTop();
        //初始化首页

        //获取网络数据
        //getDatasFromBmob();
        getTitilesFromBmob();

        //初始化导航页
        initLeadPage();
        //初始化乐享界面
        initEnjoySharePage();
        //初始化个人界面
        initPersonPage();

    }

    /**
     * 初始化顶部
     */
    private void initTop() {

        //分享上传视频
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToIntent(UpLoadVedioActivty.class);
            }
        });
        //发布视频需求
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToIntent(RequestVideoActivity.class);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CODE){
            //登录完成
            if (resultCode == LoginActivity.LOGIN_SUCCESS){
                //登录成功刷新个人界面
                personAdapter.notifyDataSetChanged();
            }
        }
        else if (requestCode == PERSON_INFO){
            //判断是否退出登录
            if (resultCode == PersonDetailAdapter.LOG_OUT){
                //刷新
                personAdapter.notifyDataSetChanged();
            }
        }
        else if (requestCode == PICK_IMAGE_LIBERARY){

            //是更换头像
            if (resultCode == RESULT_OK){
                //换了头像 获得选中图片
                //获取带会的图片参数
                //选择了确定按钮
                Uri selectedImage = data.getData();
                selectedImage.getPath();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                    //将图片保存在本地
                    String filePath = saveBitmapInLocal("headImage",bitmap);
                    Toast.makeText(this,filePath,Toast.LENGTH_SHORT).show();
                    AppData.user.setHeadImage(filePath);
                    BTPFileResponse response = BmobProFile.getInstance(this).upload(filePath, new UploadListener() {
                        @Override
                        public void onSuccess(String s, String s1, BmobFile bmobFile) {
                            //更新用户信息
                            BmobUser bmobUser = new BmobUser();
                            bmobUser = BmobUser.getCurrentUser(MainActivity.this);
                            User user = AppData.user;
                            user.setHeadImageUrl(bmobFile.getUrl());
                            user.update(MainActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(MainActivity.this,"头像更换成功",Toast.LENGTH_SHORT).show();

                                    mPersonModels.add(new PersonPageModel());
                                    mPersonModels.remove(mPersonModels.size()-1);
                                    personAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                        @Override
                        public void onProgress(int i) {

                        }

                        @Override
                        public void onError(int i, String s) {

                            Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
                        }
                    });
                    personAdapter.notifyDataSetChanged();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    /**
     * 初始化个人界面
     */
    private void initPersonPage() {

        final ListView lvPerson = (ListView) findViewById(R.id.lv_person);

        mPersonModels = new ArrayList<>();
        String strs[] = {"收藏","上传视频","乐享记录","设置"};
        int imgs[] = {R.drawable.collect,R.drawable.upload,R.drawable.jilu,R.drawable.settings};
        for (int i=0;i<strs.length;i++){
            PersonPageModel p = new PersonPageModel();
            p.setContent(strs[i]);
            p.setImageId(imgs[i]);
            mPersonModels.add(p);
        }


        //注册接收换头像的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdateHeadImageService.ACTION_UPDATE_HEAD);
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //通知更新头像
                personAdapter.notifyDataSetChanged();
            }
        };
        //注册
        registerReceiver(mReceiver,filter);
        personAdapter = new PersonListAdapter(MainActivity.this,mPersonModels,2);
        lvPerson.setAdapter(personAdapter);
        lvPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    if (AppData.user == null) {
                        //登录
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_CODE);
                    }
                    else{

                        //个人详情
                        Intent intent = new Intent(MainActivity.this, PersonDetailActivity.class);
                        startActivityForResult(intent,PERSON_INFO);
                        //换头像
//                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
//                        startActivityForResult(intent,PICK_IMAGE_LIBERARY);
                    }
                }
            }
        });
    }

    /**
     * 初始化乐享界面
     */
    private void initEnjoySharePage() {

        viewPager = (ViewPager) findViewById(R.id.vp_enjod);
        //顶部导航标签
        final TextView tvEnjoyZone = (TextView) findViewById(R.id.tv_tab1);
        tvEnjoyZone.setTextSize(15);
        tvEnjoyZone.setTextColor(getResources().getColor(R.color.tab_color));
        final TextView tvVideoTrade = (TextView) findViewById(R.id.tv_tab2);
        tvVideoTrade.setTextSize(15);
        tvVideoTrade.setTextColor(getResources().getColor(R.color.main_color));
        //设置点击事件
        tvEnjoyZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);

            }
        });

        tvVideoTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });

        //构造适配器 需要显示的fragment的集合
        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ShareVideoFragment());
        fragments.add(new RequestVideoFragment());

        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);

        initImageView();
        //设置viewpager滑动时间
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            Animation animation = null;
            int one = offset*2+imageWidth; //切换一页的偏移量
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    tvEnjoyZone.setTextColor(getResources().getColor(R.color.tab_color));
                    tvVideoTrade.setTextColor(getResources().getColor(R.color.main_color));
                    animation = new TranslateAnimation(one,0,0,0);

                }
                else{
                    tvEnjoyZone.setTextColor(getResources().getColor(R.color.main_color));
                    tvVideoTrade.setTextColor(getResources().getColor(R.color.tab_color));
                    animation = new TranslateAnimation(0,one,0,0);

                }
                animation.setFillAfter(true);   //设置图片停止在动画结束的地方
                animation.setDuration(300);
                imageView.startAnimation(animation);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }



    /**
     * 初始化动画
     */
    private void initImageView(){
        imageView = (ImageView) findViewById(R.id.img_cusor_enjoy);
        imageWidth  = imageView.getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;   //获取分辨率宽度
        offset = (screenW/2 - imageWidth)/2-imageWidth/3;    //计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);    //设置移动效果和距离
        imageView.setImageMatrix(matrix);
    }


    /**
     * 初始化导航界面
     */
    private void initLeadPage() {
        GridView gridview = (GridView) findViewById(R.id.grid_type);
        String typeNames [] = {"热点推荐","互联网","风土人情",
                               "金融","爱生活","英语",
                                "健康","自制微电影","法律",
                                "热卖","工程","哲学",
                                "明星大v","电商","iOS开发",
                                "Android开发"};
        int images[] = {R.drawable.recommand,R.drawable.internet,R.drawable.travel,
                        R.drawable.economy,R.drawable.life,R.drawable.english,
                        R.drawable.health,R.drawable.yuanchuang,R.drawable.law,
                        R.drawable.sale,R.drawable.project,R.drawable.zhexue,
                        R.drawable.award,R.drawable.taobao,R.drawable.ios,
                        R.drawable.android};
        List<TypeModel> types = new ArrayList<>();
        for (int i=0;i<typeNames.length;i++){
            TypeModel model = new TypeModel();
            model.setTypeName(typeNames[i]);
            model.setImageId(images[i]);
            types.add(model);
        }
        SingleAdapter<TypeModel> adapter = new SingleAdapter<TypeModel>(MainActivity.this,types,R.layout.type_item) {
            @Override
            public void ConfigView(SingleLayoutViewholder holder, TypeModel typeModel) {
                ((TextView)holder.getView(R.id.tv_type_name)).setText(typeModel.getTypeName());
                ((ImageView)holder.getView(R.id.img_type)).setImageResource(typeModel.getImageId());

            }
        };
        //网格的点击事件
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int type = position;
                Intent intent = new Intent(MainActivity.this,VideoTypeActivity.class);
                intent.putExtra("type",type);
                startActivity(intent);
            }
        });
        gridview.setAdapter(adapter);
    }


    /**
     * 初始化控件
     */
    private void init(){

        //等待提示的菊花
        progressBar = (ProgressBar) findViewById(R.id.pb_loading);
        //首页列表
        mVedioList = (RefreshListview) findViewById(R.id.vedio_list);
        tvTopName = (TextView) findViewById(R.id.tv_main_top_titile);

        imgSearch = (ImageView) findViewById(R.id.img_search);
        imgUpload = (ImageView) findViewById(R.id.img_upload);
        imgAdd = (ImageView) findViewById(R.id.btn_add);
        imgDownload = (ImageView) findViewById(R.id.img_dowload);
        imgLogo = (ImageView) findViewById(R.id.img_logo);
        imgHint = (ImageView) findViewById(R.id.img_hint);
        tvSearchHint = (TextView) findViewById(R.id.tv_search_hint);

        etSearch = (EditText) findViewById(R.id.et_main_search);

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        // BmobIM.init(this);
        //初始化底部tabhost
        tabHost.addTab(tabHost.newTabSpec("0").setIndicator("",getResources().getDrawable(R.drawable.homep)).setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("1").setIndicator("",getResources().getDrawable(R.drawable.pindao)).setContent(R.id.tab2));
        tabHost.addTab(tabHost.newTabSpec("2").setIndicator("",getResources().getDrawable(R.drawable.lexiang)).setContent(R.id.tab3));
        tabHost.addTab(tabHost.newTabSpec("3").setIndicator("",getResources().getDrawable(R.drawable.wode)).setContent(R.id.tab4));

        //显示顶部logo和搜索框
        etSearch.setVisibility(View.VISIBLE);
        tvTopName.setVisibility(View.GONE);
        Bmob.initialize(this, AppData.APPID);

        adapter = new VideoAdapter(this,mVedioModels,mTtiles,2);
        //mVedioList.setAdapter(adapter);

        //刷新
        mVedioList.setOnRefreshListener(new RefreshListview.onRefreshListener() {
            @Override
            public void onPullRefresh() {
                getDatasFromBmob();
                adapter.notifyDataSetChanged();
                mVedioList.onRefreshComplete();
            }

            @Override
            public void onUpResfresh() {
                mVedioList.onUpComplete();
            }
        });
        //获取当前缓存的登录对象
        final User user = BmobUser.getCurrentUser(MainActivity.this,User.class);
        //判断是否为空
        if (user != null){
            //当前已经有登录的对象
            //查询账户表，更新余额数据
            BmobQuery<UserAccount> query = new BmobQuery<>();
            query.addWhereEqualTo("userName",user.getUsername());
            query.findObjects(this, new FindListener<UserAccount>() {
                @Override
                public void onSuccess(List<UserAccount> list) {
                    float userMoney = list.get(0).getUserMoney();
                    BmobUser bmobUser = new BmobUser();
                    bmobUser = BmobUser.getCurrentUser(MainActivity.this);
                    user.setUserMoney(userMoney);
                    AppData.user = user;
                    user.update(MainActivity.this, bmobUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            //为全局赋值
                            AppData.user = user;
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(MainActivity.this,""+s,Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(int i, String s) {

                }
            });

        }

    }

    /**
     * 从数据库中获取首页信息数据并适配到listview中
     */
    private void getDatasFromBmob(){

        BmobQuery<VideoModel> query = new BmobQuery<VideoModel>();
        query.setLimit(50);
        query.findObjects(this, new FindListener<VideoModel>() {
            @Override
            public void onSuccess(List<VideoModel> list) {
                //将查询到的数据适配到listview中
                mVedioModels.clear();
                mVedioModels.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 从数据库中获取首页信息数据并适配到listview中
     */
    private void getTitilesFromBmob(){

        BmobQuery<FirstPageTitle> query = new BmobQuery<FirstPageTitle>();
        query.setLimit(5);
        query.findObjects(this, new FindListener<FirstPageTitle>() {
            @Override
            public void onSuccess(List<FirstPageTitle> list) {
                //将查询到的数据适配到listview中
                mTtiles.addAll(list);
                mHandler.sendEmptyMessage(MSG_TITLE_LOAD_COMPLETE);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
            }
        });

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
            Toast.makeText(MainActivity.this,""+e.toString(),Toast.LENGTH_SHORT).show();
        }

        return filePath;
    }


    /**
     * 需要登录才能享受的功能
     * 跳转到制定界面
     * @param destination
     */
    private void goToIntent(Class destination){
        MethodUtil.goToIntent(this,destination);
    }

}
