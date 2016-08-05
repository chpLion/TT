package Adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chen on 16/2/1.
 */
public class CommonViewHolder {
    private SparseArray<View> mViews ;//用于存储控件
    private int mPosition;
    private static View mConvertView;
    static int mLaytoutId;//已经加载过的布局

    private CommonViewHolder(Context context, ViewGroup parent, int layoutId , int position){

        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        this.mLaytoutId = layoutId;

        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }

    /**
     * 入口方法
     * @param context
     * @param parent
     * @param layoutId
     * @param position
     * @param convertView
     * @return
     */
    public static CommonViewHolder getInstance(Context context,ViewGroup parent,int layoutId ,int position,View convertView){

        if (convertView == null){
            return new CommonViewHolder(context,parent,layoutId,position);
        }

        else{
            if (mLaytoutId != layoutId){
                //重新设置当前holder需要绑定的布局
                setLaytoutId(layoutId);
                return new CommonViewHolder(context,parent,layoutId,position);
            }
            CommonViewHolder holder = (CommonViewHolder) convertView.getTag();
            //即便是复用的view，但是position是在变化的，所以也需要更新position
            holder.mPosition = position;
            return holder;
        }
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过viewId获取控件
     * @param viewId 控件的id
     * @param <T> 返回泛型集合
     * @return
     */
    public <T extends View>T getView(int viewId){

        View view = mViews.get(viewId);
        if (view == null){
            //当前控件没有保存
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }

        return (T)view;
    }

    /**
     * 获取当前已经加载的布局
     * @return
     */
    public int getLayoutId(){
        return mLaytoutId;
    }

    /**
     * 获取当前的位置
     * @return
     */
    public int getPosition(){
        return mPosition;
    }

    public static void setLaytoutId(int mLaytoutId) {
        CommonViewHolder.mLaytoutId = mLaytoutId;
    }


}
