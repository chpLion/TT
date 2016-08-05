package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.R;

import java.util.List;

import DataContext.ConstantOfTT;
import DataContext.ImagesUtil;
import DataContext.MethodUtil;
import Model.VideoModel;
import Model.VideoPay;


public class MyAdapter extends BaseAdapter{

	public MyAdapter(Context context,List<VideoModel> list) {
		super();
		this.context = context;
		this.listOfBuy = list;
	}

	public MyAdapter(Context context) {
		super();
		this.context = context;
	}

	private Context context;
	private List<VideoModel> listOfBuy;
	private List<VideoPay> listOfOrdered;
	private int layoutType;



	public List<VideoModel> getListOfBuy() {
		return listOfBuy;
	}

	public void setListOfBuy(List<VideoModel> listOfBuy) {
		this.listOfBuy = listOfBuy;
	}

	public List<VideoPay> getListOfOrdered() {
		return listOfOrdered;
	}

	public void setListOfOrdered(List<VideoPay> listOfOrdered) {
		this.listOfOrdered = listOfOrdered;
	}

	public int getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(int layoutType) {
		this.layoutType = layoutType;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		//根据不同界面返回不同的list大小
		if (layoutType == ConstantOfTT.LIST_BUY_NOW) {
			return listOfBuy.size();
		}else{
			return listOfOrdered.size();

		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		GridLayout layout = (GridLayout) LayoutInflater.from(context).inflate
				(R.layout.listview_layout, null);

		TextView textName = (TextView) layout.findViewById(R.id.textViewBName);
		ImageView imageView = (ImageView) layout.findViewById(R.id.imageView1);
		TextView textAuthor = (TextView) layout.findViewById(R.id.textViewAuthor);
		TextView textType = (TextView) layout.findViewById(R.id.textViewType);
		TextView textPrice = (TextView) layout.findViewById(R.id.textViewBPrice);
		TextView textOldPrice = (TextView) layout.findViewById(R.id.textViewBoldPrice);
		TextView textDescr= (TextView) layout.findViewById(R.id.textViewDescribe);

		if (layoutType == ConstantOfTT.LIST_BUY_NOW) {
			ImagesUtil imagesUtil = ImagesUtil.getInstance();
			imagesUtil.getBitmapByAsyncTask(context,imageView,listOfBuy.get(position).getThumUrl());
			textName.setText(listOfBuy.get(position).getVideoName());
			textAuthor.setText("作者:"+listOfBuy.get(position).getOwnerName());
			textType.setText("类型:"+ MethodUtil.getTYpeNameFromType(listOfBuy.get(position).getVideoType()));
			textPrice.setText("现价:¥"+listOfBuy.get(position).getPrice());
			textOldPrice.setText("原价:¥"+listOfBuy.get(position).getPrice());
			textDescr.setText("描述:"+listOfBuy.get(position).getPrice());
		}

		if (layoutType == ConstantOfTT.LIST_ORDER_ED) {
			ImagesUtil imagesUtil = ImagesUtil.getInstance();
			imagesUtil.getBitmapByAsyncTask(context,imageView,listOfOrdered.get(position).getThumUrl());
			textName.setText(listOfOrdered.get(position).getVideoName());
			textAuthor.setText("作者:"+listOfOrdered.get(position).getOwnerName());
			textType.setText("类型:"+listOfOrdered.get(position).getVideoType());
			textPrice.setText("现价:¥"+listOfOrdered.get(position).getPrice());
			textOldPrice.setText("原价:¥"+listOfOrdered.get(position).getPrice());
			textDescr.setText("描述:"+listOfOrdered.get(position).getPrice());
		}

		return layout;
	}


}
