package Model;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.List;

import DataContext.AppData;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class DealPayMoney {

	public DealPayMoney(Context context) {
		super();
		this.context = context;
	}

	public static final int PAY_SUCCESS = 1;
	public static final int PAY_FAIL = 2;
	private Context context;	//获取父组件
	boolean STATE = false;	//执行状态
	private Handler mHandler;

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	/**
	 * 处理交易金额
	 */
	public boolean dealPrice(final User user, final String payPwd, final VideoModel vModel) {

		//判断密码
		if (!payPwd.equals(user.getPayPassword())) {
			Toast.makeText(context, user.getPayPassword()+"密码错误,交易失败！"+payPwd, Toast.LENGTH_SHORT).show();
			STATE = false;
		}else if(user.getUserMoney()<vModel.getPrice()){
			//判断余额
			Toast.makeText(context, "余额不足,交易失败！", Toast.LENGTH_SHORT).show();
			STATE = false;
		}else {
			//当密码和余额均满足要求时扣除费用
			final float userMoney = user.getUserMoney();
			//更新本地用户
			BmobUser bmobUser = new BmobUser();
			bmobUser = BmobUser.getCurrentUser(context);
			user.setUserMoney(userMoney-vModel.getPrice());
			user.update(context, bmobUser.getObjectId(), new UpdateListener() {
				@Override
				public void onSuccess() {
					//更新账户表
					final BmobQuery<UserAccount> query = new BmobQuery<>();
					query.addWhereEqualTo("userName", AppData.user.getUsername());
					query.findObjects(context, new FindListener<UserAccount>() {
						@Override
						public void onSuccess(List<UserAccount> list) {
							UserAccount userAccount = list.get(0);
							userAccount.setUserMoney(userMoney-vModel.getPrice());
							userAccount.update(context, userAccount.getObjectId(), new UpdateListener() {
								@Override
								public void onSuccess() {
									//更新成功
									Toast.makeText(context,"更新成功",Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onFailure(int i, String s) {

								}
							});
						}

						@Override
						public void onError(int i, String s) {

						}
					});
					//更新对方账户表
					BmobQuery<UserAccount> query1 = new BmobQuery<UserAccount>();
					query1.addWhereEqualTo("userName",vModel.getOwnerName());
					query1.findObjects(context, new FindListener<UserAccount>() {
						@Override
						public void onSuccess(List<UserAccount> list) {
							UserAccount userAccount = list.get(0);
							userAccount.setUserMoney(userAccount.getUserMoney() + vModel.getPrice());
							userAccount.update(context, userAccount.getObjectId(), new UpdateListener() {
								@Override
								public void onSuccess() {
									Toast.makeText(context,"转账成功",Toast.LENGTH_SHORT).show();
									mHandler.sendEmptyMessage(DealPayMoney.PAY_SUCCESS);
								}

								@Override
								public void onFailure(int i, String s) {
									Toast.makeText(context,"转账失败"+s,Toast.LENGTH_SHORT).show();

								}
							});
						}

						@Override
						public void onError(int i, String s) {

						}
					});
				}

				@Override
				public void onFailure(int i, String s) {

				}
			});

		}
		if(!STATE){
			return false;
		}else {
			//当密码和余额均满足要求时转账到视频所有者
		}
		return STATE;
	}

	/**
	 * 保存交易记录
	 */
	public boolean recordTrade(String userName, VideoModel vModel) {

		VideoPay videoPay = new VideoPay();
		videoPay.setUserName(userName);
		videoPay.setVideoName(vModel.getVideoName());
		videoPay.setVideoUrl(vModel.getPlayUrl());
		videoPay.setOwnerName(vModel.getOwnerName());
		videoPay.setThumUrl(vModel.getThumUrl());
		videoPay.setPrice(vModel.getPrice());

		videoPay.save(context, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "保存成功", 300).show();
				STATE = true;
				Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "保存记录失败"+arg1, 300).show();
				STATE = false;
			}
		});

		return STATE;
	}

}
