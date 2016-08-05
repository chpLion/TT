package com.example.chen.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import DataContext.AppData;
import DataContext.MethodUtil;
import DataContext.TradeAndRecord;
import Model.MyCart;
import Model.PayMent;
import Model.VideoModel;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


public class BuyActivity extends Activity {

    //页面控件
    ScrollView scrollView;
    ImageView imageGoods;    //商品缩略图
    TextView textName;   //名称
    TextView textAuthor;    //作者
    TextView textType;  //商品类型
    TextView textLength;   //商品级别
    TextView textOldPrice;  //原价
    TextView textDescribe;  //描述

    RatingBar ratingBar;    //评分条
    TextView textScore;   //显示评分
    Button btnEvaluate;    //查看评论

    ImageView imageNot; //图文详情补充

    TextView textPrice;    //现价
    Button btnLink;    //联系教师或客服
    Button btnAddCart;  //加入购物车
    Button btnBuy;  //立即购买

    //数据
    VideoModel videoModel;
    Context context;
    boolean hadPay = false;

    private Handler handler;
    private static int ERROR = 0;   //查询错误
    private static int HAD_PAY = 1; //已经购买
    private static int NOT_PAY_ED = 2;  //没有购买
    private static int HAD_ADD_CART = 3;    //已经加入过购物车
    private static int NO_ADD_CART = 4; //尚未加入购物车
    private static int ADD_SUCCESS = 5; //加入购物车成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        context = BuyActivity.this;
        hadPay = false;

        //获取传送的数据
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videoModel = (VideoModel) bundle.getSerializable(AppData.VIDEO_MODEL);

        //scollView设计工具操作
        scrollView = (ScrollView) findViewById(R.id.scrollViewPro);

        //初始化商品相关控件
        initViewOfProduct();

        //设置购买事件
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建支付对象,立即购买只有一个视频
                //Toast.makeText(context,"topay",Toast.LENGTH_SHORT).show();
                TradeAndRecord tradeAndRecord = new TradeAndRecord(context,1);
                tradeAndRecord.dealTrade(videoModel);   //需要交易的视频
            }
        });

        //添加到购物车事件处理
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ERROR){
                   Toast.makeText(context,"未知错误，请检查网络",Toast.LENGTH_SHORT).show();
                }
                if (msg.what == HAD_PAY){
                    //Toast.makeText(context,"testHP",Toast.LENGTH_SHORT).show();
                    toOrderedOrMain();  //若已经购买过选择是否查看
                }
                if (msg.what == NOT_PAY_ED){
                    //Toast.makeText(context,"testNP",Toast.LENGTH_SHORT).show();
                    isHadAddCart(); //未购买过检查是否已经加入过购物车
                }
                if (msg.what == HAD_ADD_CART){
                    Toast.makeText(context,"testHA",Toast.LENGTH_SHORT).show();
                    toCartOrMain(); //若已经加入过选择是否查看
                }
                if (msg.what == NO_ADD_CART){
                    Toast.makeText(context,"testNA",Toast.LENGTH_SHORT).show();
                    addToCart();    //尚未加入则加入
                }
                if (msg.what == ADD_SUCCESS){
                    toCartOrMain();
                }
            }
        };

        //设置加入购物车事件
        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHadPay(); //引发第一个加入购物车事件：检查是否已经购买
            }
        });
    }

    /**
     * 初始化商品控件
     */
    public void initViewOfProduct(){

        //页面控件
        imageGoods = (ImageView) findViewById(R.id.imageGoods);    //商品缩略图
        textName = (TextView) findViewById(R.id.textName); //名称
        textAuthor = (TextView) findViewById(R.id.textAuthor); //作者
        textType = (TextView) findViewById(R.id.textType); //商品类型
        textLength = (TextView) findViewById(R.id.textLength);   //商品级别
        textOldPrice = (TextView) findViewById(R.id.textOldPrice); //原价
        textDescribe = (TextView) findViewById(R.id.textDescribe); //描述

        ratingBar = (RatingBar) findViewById(R.id.RatingBar1);    //评分条
        textScore = (TextView) findViewById(R.id.textScore);   //显示评分
        btnEvaluate = (Button) findViewById(R.id.buttonEvaluate);    //查看评论

        imageNot = (ImageView) findViewById(R.id.imageNot); //图文详情补充

        textPrice = (TextView) findViewById(R.id.textPrice);    //现价
        btnLink  = (Button) findViewById(R.id.buttonLink);    //联系教师或客服
        btnAddCart = (Button) findViewById(R.id.buttonAddCart);  //加入购物车
        btnBuy = (Button) findViewById(R.id.buttonBuy);  //立即购买

        /**
         * 初始化信息
         */
        imageGoods.setImageResource(R.drawable.notimage);
        textName.setText("视频: "+videoModel.getVideoName());
        textAuthor.setText("发布者: "+videoModel.getOwnerName());
        textType.setText("类型: "+ MethodUtil.getTYpeNameFromType(videoModel.getVideoType()));
        textOldPrice.setText("原价: "+videoModel.getPrice()+"元");
        textLength.setText("时长: "+videoModel.getVideoLength());
        if (videoModel.getDescription()==null){
            textDescribe.setText("描述: 暂无更多描述");
        }else {
            textDescribe.setText("描述: "+videoModel.getDescription());
        }

        /**
         * 评论部分和图文详情部分暂缺
         */
        ratingBar.setClickable(false);

        textPrice.setText("￥"+videoModel.getPrice());


        /**
         * 联系部分暂缺
         */
    }

    /**
     * 是否以前已经购买过
     */
    public void isHadPay(){
        BmobQuery<PayMent> payUser = new BmobQuery<PayMent>();
        payUser.addWhereEqualTo("userName",AppData.user.getUsername());
        BmobQuery<PayMent> payVideo = new BmobQuery<PayMent>();
        payVideo.addWhereEqualTo("videoId",videoModel.getVideoId());
        List<BmobQuery<PayMent>> queryList = new ArrayList<BmobQuery<PayMent>>();
        queryList.add(payUser);
        queryList.add(payVideo);
        BmobQuery<PayMent> queryPay = new BmobQuery<PayMent>();
        queryPay.and(queryList);
        queryPay.findObjects(context, new FindListener<PayMent>() {
            @Override
            public void onSuccess(List<PayMent> list) {
                if (list.size()!=0){   //当查询结果不为空时
                    handler.sendEmptyMessage(HAD_PAY);   //已购买标志
                }else {
                    handler.sendEmptyMessage(NOT_PAY_ED);   //未购买标志
                }
            }

            @Override
            public void onError(int i, String s) {
                handler.sendEmptyMessage(ERROR);   //查询异常
            }
        });
    }

    /**
     * 是否以前已经添加到购物车
     */
    public void isHadAddCart(){
        BmobQuery<MyCart> quCartUser = new BmobQuery<MyCart>();
        quCartUser.addWhereEqualTo("userName",AppData.user.getUsername());
        BmobQuery<MyCart> quCartVideo = new BmobQuery<MyCart>();
        quCartVideo.addWhereEqualTo("videoId",videoModel.getVideoId());
        List<BmobQuery<MyCart>> quList = new ArrayList<BmobQuery<MyCart>>();
        quList.add(quCartUser);
        quList.add(quCartVideo);
        BmobQuery<MyCart> cartQuery = new BmobQuery<MyCart>();
        cartQuery.and(quList);
        cartQuery.findObjects(context, new FindListener<MyCart>() {
            @Override
            public void onSuccess(List<MyCart> list) {
                if (list.size()!=0){
                    //若以前已经添过
                    handler.sendEmptyMessage(HAD_ADD_CART);
                }else{
                    handler.sendEmptyMessage(NO_ADD_CART);
                }
            }

            @Override
            public void onError(int i, String s) {
                handler.sendEmptyMessage(ERROR);
            }
        });
    }

    /**
     * 加入到购物车
     */
    public void addToCart(){
        //没有则添加
        MyCart myCart = new MyCart();
        myCart.setUserName(AppData.user.getUsername());
        myCart.setVideoId(videoModel.getVideoId());
        myCart.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(ADD_SUCCESS);
            }

            @Override
            public void onFailure(int i, String s) {
                handler.sendEmptyMessage(ERROR);
            }
        });
    }

    /**
     * 查看历史订单或返回主页
     */
    private void toOrderedOrMain(){
        AlertDialog.Builder selBuilder = new AlertDialog.Builder(context);
        selBuilder.setTitle("提示");
        selBuilder.setMessage("您已购买过该商品,是否查看所有历史订单");
        //返回主页
        selBuilder.setNegativeButton("返回主页", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(context, MainActivity.class);
                context.startActivity(intent);
            }
        });
        //前往查看历史订单
        selBuilder.setNeutralButton("查看订单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(context,MyOrderActivity.class);
                context.startActivity(intent);
            }
        });
        selBuilder.show();
    }

    /**
     * 查看购物车或返回主页
     */
    public void toCartOrMain(){
        final Intent intent = new Intent();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("添加成功");
        builder.setMessage("返回主页或查看购物车？");
        builder.setNegativeButton("主页", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent.setClass(context,MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("购物车", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent.setClass(context,MyCartActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

}
