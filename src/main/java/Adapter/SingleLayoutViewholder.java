package Adapter;

/**
 * Created by chen on 16/2/22.
 */

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chen on 15/12/26.
 *
 * 通用viewholder
 */
public class SingleLayoutViewholder {

    private SparseArray<View> mViews ;//用于存储控件
    private int mPosition;
    private View mConvertView;

    private SingleLayoutViewholder(Context context, ViewGroup parent, int layoutId , int position){

        this.mPosition = position;
        this.mViews = new SparseArray<View>();

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
    public static SingleLayoutViewholder getInstance(Context context,ViewGroup parent,int layoutId ,int position,View convertView){

        if (convertView == null){
            return new SingleLayoutViewholder(context,parent,layoutId,position);
        }

        else{
            SingleLayoutViewholder holder = (SingleLayoutViewholder) convertView.getTag();
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

    public int getPosition() {
        return mPosition;
    }
}

