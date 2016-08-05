package Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by chen on 16/2/4.
 */
public abstract class MutiLayoutAdapter<T> extends CommonAdapter<T> {

    private int count;//需要加载的布局的数量
    public MutiLayoutAdapter(Context context, List<T> mDates) {
        super(context, mDates);

    }
    public MutiLayoutAdapter(Context context, List<T> mDates,int count) {
        super(context, mDates);
        this.count = count;
    }

    @Override
    public int getCount() {
        return count+mDates.size()-1;
    }

    /**
     * 实现getView方法
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CommonViewHolder holder = getViewHolderByPosition( position, convertView, parent);
        ConfigView(holder,mDates,position);

        return holder.getConvertView();
    }

    /**
     * 根据不同的位置来选择不同的holder 加载不同的布局
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public abstract CommonViewHolder getViewHolderByPosition(int position, View convertView, ViewGroup parent);

    /**
     * 配置其中一个布局listview的各个控件的值
     * @param holder
     * @param mDatas
     */
    public abstract void ConfigView(CommonViewHolder holder,List<T> mDatas,int position);

}
