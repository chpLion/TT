package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.myapplication.R;

import java.util.List;

import DataContext.AppData;
import DataContext.ImagesUtil;
import DataContext.MethodUtil;
import Model.VideoModel;


/**
 * Created by Peng on 2016/3/15.
 */
public class VideoModelAdapter extends BaseAdapter{

    private Context context;
    private List<VideoModel> videoList;
    private String layout_type;
    private TextView textName;
    private ImageView imageView;
    private TextView textAuthor;
    private TextView textType;
    private TextView textPrice;
    private TextView textTime;
    private Button btnEvl;
    private TextView tvDescription;

    public VideoModelAdapter(Context context, List<VideoModel> videoModelList,
                             String layout_type) {
        this.context = context;
        this.videoList = videoModelList;
        this.layout_type = layout_type;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        //绑定控件
        LinearLayout gridLayout = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.product_simple_listview,null);
        if (gridLayout==null){
            Toast.makeText(context,"null",Toast.LENGTH_SHORT).show();
        }
        textName = (TextView) gridLayout.findViewById(R.id.textViewName);
        imageView = (ImageView) gridLayout.findViewById(R.id.imageGoods);
        textAuthor = (TextView) gridLayout.findViewById(R.id.textAuthor);
        textType= (TextView) gridLayout.findViewById(R.id.textType);
        textPrice= (TextView) gridLayout.findViewById(R.id.textPrice);
        textTime = (TextView) gridLayout.findViewById(R.id.textTime);
        btnEvl = (Button) gridLayout.findViewById(R.id.buttonEvaluate);
        tvDescription = (TextView) gridLayout.findViewById(R.id.tv_my_order_decription);

        //订单布局
        if (layout_type.equals(AppData.ORDER_LAYOUT)){
            getComContent(position);    //设置相同的控件
            btnEvl.setVisibility(View.VISIBLE); //评论按钮可见

            btnEvl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"设置跳转我去评论啊",Toast.LENGTH_SHORT).show();
                }
            });

        }

        //购物车布局
        if (layout_type.equals(AppData.CART_LAYOUT)){
            getComContent(position);
            btnEvl.setVisibility(View.GONE);    //隐藏评论按钮

        }

        return gridLayout;
    }

    /**
     * 设置统一的内容
     */
    public void getComContent(int position){
        textName.setText(""+ videoList.get(position).getVideoName());
        textAuthor.setText("作者: "+ videoList.get(position).getOwnerName());
        textType.setText("类型: "+ MethodUtil.getTYpeNameFromType(videoList.get(position).getVideoType()));
        textPrice.setText("价格: "+ videoList.get(position).getPrice()+"元");
        textTime.setText(""+ videoList.get(position).getCreatedAt());
        tvDescription.setText(videoList.get(position).getDescription());
        ImagesUtil imagesUtil = ImagesUtil.getInstance();
        imagesUtil.getBitmapByAsyncTask(context,imageView,videoList.get(position).getThumUrl());
    }

}
