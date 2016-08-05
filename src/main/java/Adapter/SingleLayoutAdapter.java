package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by chen on 16/2/22.
 */
public abstract class SingleLayoutAdapter<T> extends BaseAdapter {


    //需要让子类访问，所以需要使用protected
    protected Context context;
    protected List<T> mDates;//泛型集合的bean
    protected LayoutInflater inflater;//布局解释器
    private int layoutId;

    public SingleLayoutAdapter(Context context,List<T> mDates,int layoutId) {

        this.context = context;
        this.mDates = mDates;
        this.inflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mDates.size();
    }

    @Override
    public T getItem(int position) {
        return mDates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 由于不同的listview的getView方法不同，所以需要将getView方法抽象出去
     * 在使用的过程中具体实现
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CommonViewHolder holder = CommonViewHolder.getInstance(context,parent,layoutId,position,convertView);
        ConfigView(holder,getItem(position));

        return holder.getConvertView();
    }

    /**
     * 抽象方法，在使用的时候具体实现，
     * 用于配置listview item项的各控件的值
     * @param holder
     * @param t
     */
    public abstract void ConfigView(CommonViewHolder holder,T t);
}

