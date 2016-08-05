package DataContext;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chen.myapplication.MainActivity;
import com.example.chen.myapplication.MyOrderActivity;
import com.example.chen.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import Model.PayMent;
import Model.UserAccount;
import Model.VideoModel;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Peng on 2016/3/15.
 */
public class TradeAndRecord {

    private VideoModel videoModel = null;    //商品类
    private String passWord = null;    //用户支付密码
    private Context context = null;     //父控件上下文
    private Handler handler;    //异步消息传递
    private boolean hadCheckPwd = false;    //是否检查过密码
    private int curDealNumber = 1;  //当前处理视频
    private int payVideoNumber = 1; //总共处理视频数
    private static int PAY_ERROR = -1;  //支付错误
    private static int NOT_PAY_ED = 0;  //已经购买过
    private static int PAY_ED = 1;  //未购买过
    private static int PWD_ERROR = 2;   //密码错误
    private static int PWD_RIGHT = 3;   //验证密码
    private static int SUB_ERROR = 4;   //扣除费用失败
    private static int SUB_SUCCESS = 5;   //扣除费用成功
    private static int ADD_ERROR = 6;   //收入增加失败
    private static int ADD_SUCCESS = 7;   //收入增加成功
    private static int RECORD_ERROR = 8;    //保存记录失败
    private static int RECORD_SUCCESS = 9;    //保存记录成功
    ProgressDialog proDeal;

    public TradeAndRecord(Context context, int payVideoNumber) {
        this.context = context;
        this.payVideoNumber = payVideoNumber;
        curDealNumber=1; //当前处理视频
        hadCheckPwd = false;   //是否检查过密码
    }

    public void dealTrade(VideoModel temModel)
    {
        videoModel = temModel;  //将要购买的视频

        proDeal = new ProgressDialog(context);
        proDeal.setMessage("正在支付。。。");

        isPayed();  //是否已经购买过

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                //支付错误
                if (msg.what == PAY_ERROR){
                    Toast.makeText(context,"未知错误,请检查网络连接是否正常!",Toast.LENGTH_SHORT).show();
                }

                //检测是否已经购买过
                if (msg.what == PAY_ED){
                    if (payVideoNumber==1){    //当只有一件商品时即立即购买时显示提示
                        seleNextAction();   //已购买过选择是否查看订单
                    }
                    curDealNumber++; //处理下一个视频
                    //Toast.makeText(context,"pay",Toast.LENGTH_SHORT).show();
                }
                //没有购买过则输入密码进行支付
                if (msg.what == NOT_PAY_ED){
                    if (!hadCheckPwd){  //密码只需检查一次，因此只当没有检查过密码时检查密码
                        inputAndCheckPayPwd();  //输入并校验密码
                        hadCheckPwd=true;   //将密码检查标志置为真
                        //proDeal.show(); //显示进度
                    }
                    //Toast.makeText(context,"notpay",Toast.LENGTH_SHORT).show();
                }

                //对支付进行操作
                if (msg.what == PWD_ERROR){
                    Toast.makeText(context,"密码错误!",Toast.LENGTH_SHORT).show();
                }
                if(hadCheckPwd && msg.what==PWD_RIGHT) {  //检查过密码且密码没错时进行交易
                    subPrice(); //当确认密码正确后才进行扣除费用
                }
                if(msg.what == SUB_ERROR){
                    Toast.makeText(context,"下单失败!",Toast.LENGTH_SHORT).show();
                }
                if (msg.what==SUB_SUCCESS) { //扣除费用成功的前提下才尝试转账并记录
                    addAuthorPrice();   //扣除费用成功才为发布者获得收益
                }
                if(msg.what == ADD_ERROR){
                        Toast.makeText(context,"转账失败!",Toast.LENGTH_SHORT).show();
                }
                if (msg.what == ADD_SUCCESS){
                    recordTrade();  //转账完成才记录交易
                }
                if(msg.what == RECORD_ERROR){
                    Toast.makeText(context,"保存记录失败!",Toast.LENGTH_SHORT).show();
                }
                if (msg.what == RECORD_SUCCESS){

                    if (curDealNumber<payVideoNumber){
                        curDealNumber++;   //当未处理玩时处理下一个购买视频
                    }else { //购买完毕是否查看订单
                       // proDeal.dismiss();  //隐藏进度
                        seleNextAction(); //购买完成选择是否查看订单
                    }
                }
            }
        };

    }

    /**
     * 是否已经购买
     */
    private void isPayed(){
        BmobQuery<PayMent> uaUser = new BmobQuery<PayMent>();
        uaUser.addWhereEqualTo("userName",AppData.user.getUsername());
        BmobQuery<PayMent> uaVideo = new BmobQuery<PayMent>();
        uaVideo.addWhereEqualTo("videoId",videoModel.getVideoId());
        List<BmobQuery<PayMent>> queryList = new ArrayList<BmobQuery<PayMent>>();
        queryList.add(uaUser);
        queryList.add(uaVideo);
        BmobQuery<PayMent> queryPay = new BmobQuery<PayMent>();
        queryPay.and(queryList);
        queryPay.findObjects(context, new FindListener<PayMent>() {
            @Override
            public void onSuccess(List<PayMent> list) {
                if (list.size()!=0){   //当查询结果不为空时
                    handler.sendEmptyMessage(PAY_ED);   //已购买标志
                }else {
                    handler.sendEmptyMessage(NOT_PAY_ED);   //未购买标志
                }
            }

            @Override
            public void onError(int i, String s) {
                handler.sendEmptyMessage(PAY_ERROR);   //查询异常
            }
        });
    }


    /**
     * 输入密码并进行校验
     */
    private void inputAndCheckPayPwd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("宝贝即将到手，请先支付！");
        View payDiaLog = LayoutInflater.from(context).inflate(R.layout.pay_dialog, null);
        builder.setView(payDiaLog);    //加载预设支付界面
        final EditText editText = (EditText) payDiaLog.findViewById(R.id.editTextPayPwd);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNeutralButton("支付", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                passWord = editText.getText().toString();   //获取输入的密码
                if ( AppData.user.getPayPassword().equals(passWord)){
                    handler.sendEmptyMessage(PWD_RIGHT);
                }else {
                    handler.sendEmptyMessage(PWD_ERROR);
                }
            }
        });
        builder.show(); //显示输入密码界面
    }

    /**
     * 进行转账之扣除用户费用
     */
    private void subPrice(){
        BmobQuery<UserAccount> accQuery = new BmobQuery<UserAccount>();
        accQuery.addWhereEqualTo("userName",AppData.user.getUsername());
        accQuery.findObjects(context, new FindListener<UserAccount>() {
            @Override
            public void onSuccess(List<UserAccount> list) {
                float userMoney = list.get(0).getUserMoney();
                if (list.size()==0 || userMoney<videoModel.getPrice()){   //当查询结果不为空时
                    handler.sendEmptyMessage(SUB_ERROR);     //报告余额不足
                }else{
                    list.get(0).setUserMoney(userMoney-videoModel.getPrice());
                    final UserAccount ua = list.get(0);
                    ua.update(context,list.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            handler.sendEmptyMessage(SUB_SUCCESS);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            handler.sendEmptyMessage(SUB_ERROR);     //报告扣除费用失败
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                //Toast.makeText(context,"找不到用户账户信息",Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessage(PAY_ERROR);   //查询异常
            }
        });
    }

    /**
     * 转账之发布者收入
     */
    private void addAuthorPrice(){
        BmobQuery<UserAccount> accQuery = new BmobQuery<UserAccount>();
        accQuery.addWhereEqualTo("userName",videoModel.getOwnerName());
        accQuery.findObjects(context, new FindListener<UserAccount>() {
            @Override
            public void onSuccess(final List<UserAccount> list) {
                float userMoney = list.get(0).getUserMoney();
                list.get(0).setUserMoney(userMoney+videoModel.getPrice());
                UserAccount ua = list.get(0);
                ua.update(context,list.get(0).getObjectId(), new UpdateListener()  {
                    @Override
                    public void onSuccess() {
                        if (list.size()==0){
                            handler.sendEmptyMessage(ADD_ERROR);
                        }else {
                            handler.sendEmptyMessage(ADD_SUCCESS);
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        //Toast.makeText(context,"发布者收入增加失败！",Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(ADD_ERROR);     //报告收益失败
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                handler.sendEmptyMessage(PAY_ERROR);   //查询异常
            }
        });
    }

    /**
     * 保存交易记录
     */
    private void recordTrade(){
        PayMent payMent = new PayMent();
        payMent.setUserName(AppData.user.getUsername());    //记录购买者
        payMent.setPrice(videoModel.getPrice());     //记录成交价
        payMent.setVideoId(videoModel.getVideoId());  //存储商品id
        payMent.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(RECORD_SUCCESS);   //报告扣除费用成功
            }

            @Override
            public void onFailure(int i, String s) {
                handler.sendEmptyMessage(RECORD_ERROR);     //报告扣除费用失败
            }
        });
    }

    private void seleNextAction(){
        AlertDialog.Builder selBuilder = new AlertDialog.Builder(context);
        selBuilder.setTitle("提示");
        selBuilder.setMessage("已购买,是否查看所有历史订单");
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

}
