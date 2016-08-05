package chen.trade;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chen.myapplication.R;
import com.example.chen.myapplication.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;

import Adapter.MyAdapter;
import DataContext.AppData;
import DataContext.ConstantOfTT;
import Model.VideoPay;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class ListOrderedActivity extends Activity{

	private ProgressDialog progressDialog;
	private static int LOAD_SUCESS = 0;

	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == LOAD_SUCESS) {
				progressDialog.dismiss();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		//绑定控件
		progressDialog = new ProgressDialog(this);
		progressDialog = ProgressDialog.show(this, "请稍后", "正在努力狂奔加载中...");
		Button btnCancel = (Button) findViewById(R.id.buttonLOCancel);
		ListView listView = (ListView) findViewById(R.id.listViewOfListOrdered);

		//listView操作
		final List<VideoPay> list = new ArrayList<VideoPay>();
		final MyAdapter orderAdapter = new MyAdapter(this);
		orderAdapter.setLayoutType(ConstantOfTT.LIST_ORDER_ED);	//设置布局方式
		orderAdapter.setListOfOrdered(list);	//设置布局的list
		listView.setAdapter(orderAdapter);
		BmobQuery<VideoPay> query = new BmobQuery<VideoPay>();

		query.addWhereEndsWith("userName", AppData.user.getUsername());

		query.findObjects(this, new FindListener<VideoPay>() {

			@Override
			public void onSuccess(List<VideoPay> arg0) {
				// TODO Auto-generated method stub
				list.addAll(arg0);
				orderAdapter.notifyDataSetChanged();
				mHandler.sendEmptyMessage(LOAD_SUCESS);		//停止转圈
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(ListOrderedActivity.this, arg1, Toast.LENGTH_SHORT).show();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ListOrderedActivity.this, VideoPlayActivity.class);
			}
		});
	}

}
