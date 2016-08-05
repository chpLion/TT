package chen.trade;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chen.myapplication.R;
import com.example.chen.myapplication.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;

import Adapter.MyAdapter;
import DataContext.AppData;
import DataContext.ConstantOfTT;
import Model.DealPayMoney;
import Model.VideoModel;

public class BuyNowAcitivity extends Activity {

	private ProgressDialog progressDialog;
	private static int LOAD_SUCESS = 0;

	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == LOAD_SUCESS) {
				progressDialog.dismiss();
			}
			else if (msg.what == DealPayMoney.PAY_SUCCESS){
				//支付成功 界面消失
				BuyNowAcitivity.this.finish();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		progressDialog = ProgressDialog.show(this, "请稍后", "正在努力狂奔加载中...");

		// 订单界面
		setContentView(R.layout.buy_now_layout);
		Button btnCancel = (Button) findViewById(R.id.buttonBCancel);
		final Button btnAffirm = (Button) findViewById(R.id.buttonBAffirm);
		final ListView listView = (ListView) findViewById(R.id.listViewOfBuy);

		//listView操作
		final List<VideoModel> list = new ArrayList<VideoModel>();
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		final VideoModel vModel = (VideoModel) bundle.getSerializable("VideoModel");
		list.add(vModel);
		final MyAdapter buyAdapter = new MyAdapter(BuyNowAcitivity.this,list);
		buyAdapter.setLayoutType(ConstantOfTT.LIST_BUY_NOW);	//设置布局方式
		listView.setAdapter(buyAdapter);
		mHandler.sendEmptyMessage(LOAD_SUCESS);

		/**
		 * 进入支付界面
		 */
		btnAffirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//防止重新支付
				btnAffirm.setClickable(false);
				btnAffirm.setVisibility(View.INVISIBLE);	//不可见
				// 支付对话框
				AlertDialog.Builder builder = new AlertDialog.Builder(
						BuyNowAcitivity.this);
				builder.setTitle("支付");
				final View view = LayoutInflater.from(BuyNowAcitivity.this).inflate(
						R.layout.pay_dialog, null);
				builder.setView(view);	//加载预设支付界面

				/**
				 * 取消支付
				 */
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						btnAffirm.setVisibility(View.VISIBLE);
						btnAffirm.setClickable(true);	//取消支付可以再次选择支付
					}
				});

				/**
				 * 支付处理
				 */
				builder.setNeutralButton("支付", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						DealPayMoney dealMoney = new DealPayMoney(BuyNowAcitivity.this);
						dealMoney.setHandler(mHandler);

						//交易金额处理
						EditText editPayPwd = (EditText) view.findViewById(R.id.editTextPayPwd);
						String payPwd = null;
						payPwd = editPayPwd.getText().toString().trim();	//获取密码
						boolean tradeState = false;	//支付状态
						if (payPwd.equals("")) {
							//判断密码是否为空
							tradeState = false;
							Toast.makeText(BuyNowAcitivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
						}else {
							//支付金额处理，进行转账
							tradeState=dealMoney.dealPrice(AppData.user,payPwd,vModel);
							if(tradeState){

								Toast.makeText(BuyNowAcitivity.this, "支付成功", Toast.LENGTH_SHORT).show();
								//当上一步完成时记录支付内容

								tradeState = dealMoney.recordTrade(AppData.user.getUsername(), vModel);
							}
						}
						if (tradeState) {
							Toast.makeText(BuyNowAcitivity.this, "支付成功", Toast.LENGTH_SHORT).show();
						}else {
							btnAffirm.setVisibility(View.VISIBLE);	//可见
							btnAffirm.setClickable(true);	//支付失败可再次支付

						}
					}
				});

				builder.show();	//显示
			}
		});

		/**
		 * 取消订单
		 */
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				intent.setClass(BuyNowAcitivity.this, VideoPlayActivity.class);
				startActivity(intent);
			}
		});

	}

}
