package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by chen on 16/2/1.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    //需要让子类访问，所以需要使用protected
    protected Context context;
    protected List<T> mDates;//泛型集合的bean
    protected LayoutInflater inflater;//布局解释器
    protected int layoutId;//需要解释的布局

    public CommonAdapter(Context context,List<T> mDates,int layoutId) {

        this.context = context;
        this.mDates = mDates;
        this.inflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
    }

    public CommonAdapter(Context context,List<T> mDates) {

        this.context = context;
        this.mDates = mDates;
        this.inflater = LayoutInflater.from(context);

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
    public abstract View getView(int position, View convertView, ViewGroup parent);

}
