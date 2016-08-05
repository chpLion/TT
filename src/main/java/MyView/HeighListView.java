package MyView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by chen on 16/2/22.
 * 可嵌套的listview
 */
public class HeighListView extends ListView{


    public HeighListView(Context context) {
        super(context);
    }

    public HeighListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeighListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }
}
