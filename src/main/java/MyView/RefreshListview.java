package MyView;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chen.myapplication.R;

/**
 * Created by chen on 16/2/23.
 */

public class RefreshListview extends ListView implements OnScrollListener{

    View head;
    View footer;
    int headHeight;//顶部布局文件的高度
    int footerHeight;//底部布局的高度
    int firstVisiable;//第一个可见的item项的位置
    int lastVisibale;//最后一个可见的item的位置
    int totolCount ;//定义一共有多少item项
    boolean isFirst = false;//判断是否是在listview的最顶端
    boolean isLast = false;//判断是否处在listview的底部
    int startY;//记录手指按下时候的位置
    int state;
    int scollState;

    private final int NONE = 0;//正常情况
    private final int PULL = 1;//向下拉的状态
    private final int RELESE = 2;//在向下拉伸到一定程度显示可以刷新时候的状态
    private final int REFRESHING = 3;//在达到刷新条件的时候释放了的状态
    private final int UP = 4;//向上拖拽的时候的状态
    private final int LOADING = 5;//拖拽完成之后释放要显示加载更多时候的状态
    onRefreshListener listener;

    public RefreshListview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }
    public RefreshListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public RefreshListview(Context context) {
        super(context);
        initView(context);
    }

    public void initView(Context context){

        LayoutInflater inflater = LayoutInflater.from(context);
        head = inflater.inflate(R.layout.head_layout, null);
        footer = inflater.inflate(R.layout.foot_layout, null);
        measureView(head);//通知父布局head占用的空间大小，才能获取head布局的高度,android就是这么傻逼
        measureView(footer);
        headHeight = head.getMeasuredHeight();
        footerHeight = footer.getMeasuredHeight();
        System.out.println(footerHeight);
        System.out.println(headHeight);

        setTopPadding(-headHeight);
        setButtomPadding(-footerHeight);
        this.setOnScrollListener(this);
        this.addHeaderView(head);
        this.addFooterView(footer);
    }

    /**
     * 设置head布局的上边距
     * @param top
     */

    private void setTopPadding(int top){

        head.setPadding(head.getPaddingLeft(),
                top,
                head.getPaddingRight(),
                head.getPaddingBottom());
    }

    /**
     * 设置底部footer布局的下边距，以实现隐藏和出现初始设置成高度负数
     * @param buttom
     */
    private void setButtomPadding(int buttom){
        footer.setPadding(footer.getPaddingLeft(),
                footer.getPaddingTop(),
                footer.getPaddingRight(),
                buttom);
    }

    /**
     * 通知父布局head或者是footer的占用的空间大小
     * @param view
     */
    private void measureView(View view){

        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp==null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

        }

        int wide = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int height;
        int tempHeight = lp.height;
        if (tempHeight>0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        }
        else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        view.measure(wide, height);

    }

    /**
     * 通知父级容器底部布局footer占用的位置大小
     * @param view
     */
    public void mesuanFooterView(View view){

        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int wide = lp.width;
        int height;
        int tempheight = lp.height;
        if (tempheight>0) {
            height = MeasureSpec.makeMeasureSpec(tempheight, MeasureSpec.EXACTLY);
        }
        else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        view.measure(wide, height);
    }

    /**
     * 滚动监听的事件
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scollState = scrollState;
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstVisiable = firstVisibleItem;
        this.lastVisibale = visibleItemCount+firstVisibleItem-1;
        this.totolCount = totalItemCount;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisiable==0) {
                    isFirst = true;
                    startY = (int) ev.getY();
                }
                else if (lastVisibale == totolCount-1) {
                    isLast = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:

                onPullMove(ev);
                onUpMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state==RELESE) {
                    state = REFRESHING;
                    setTopPadding(0);
                    listener.onPullRefresh();//设置上拉刷新的监听接口回调
                    changeViewByState();
                    //加载数据

                }
                else if (state==PULL) {
                    state = NONE;
                    changeViewByState();

                }
                break;
        }

        return super.onTouchEvent(ev);


    }

    /**
     * 判断向下刷新的移动过程中需要执行的操作
     * @param ev：当前触摸事件的参数
     */
    public void onPullMove(MotionEvent ev){

        if (!isFirst) {
            return;
        }

        int tempY = (int) ev.getY();//当前手指移动的位置
        int space = (tempY - startY)/3;//移动了的距离
        int topPadding = space-headHeight;//根据当前移动距离和header的高度设置上边距

        switch (state) {
            case NONE:
                if (space>0) {
                    state = PULL;
                    setTopPadding(topPadding);
                    changeViewByState();
                }
                break;

            case PULL:
                setTopPadding(topPadding);
                Log.e("height", "top"+topPadding+"heighr = "+headHeight);
                if (space>headHeight+30&&scollState==SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    changeViewByState();
                }

                break;

            case RELESE:
                setTopPadding(topPadding);
                if (space<headHeight+30) {
                    state = PULL;
                    changeViewByState();
                }
                else if (space<0){
                    //恢复初始状态
                    state = NONE;
                    isFirst = false;
                    changeViewByState();
                }
                break;

            case REFRESHING:
                changeViewByState();
                break;
        }

    }
    /**
     * 在向上拖动时候的移动
     * @param ev
     */
    public void onUpMove(MotionEvent ev){


        int space;
        if (!isLast) {
            return;
        }
        int tempY = (int) ev.getY();
        space = (startY - tempY)/3;
        int butttomPadding = footerHeight - space;

        switch (state) {
            case NONE:
                if (space>0) {//说明当前是向上拖动
                    state = UP;//将当前状态修改为向上拖动
                    changeViewByState();
                    setButtomPadding(space);
                }
                break;

            case UP:
                if (space>30&&scollState == SCROLL_STATE_FLING&&state!=LOADING) {
                    state = LOADING;
                    setButtomPadding(30);
                    changeViewByState();
                }
            case LOADING:
                changeViewByState();
                setButtomPadding(30);
                listener.onUpResfresh();
                break;

        }

    }

    /**
     * 根据相应的当前状态改变head和footer的布局
     */

    private void changeViewByState(){

        TextView tvTip = (TextView) head.findViewById(R.id.tip);
        ProgressBar progressBar = (ProgressBar) head.findViewById(R.id.progress_bar);

        TextView tipforload = (TextView) footer.findViewById(R.id.tv_tips);
        ProgressBar p = (ProgressBar) footer.findViewById(R.id.progressBar1);
        switch (state) {
            case NONE:
                setTopPadding(-headHeight);
                setButtomPadding(-footerHeight);
                break;
            case PULL:
                tvTip.setText("下拉刷新");
                break;
            case RELESE:
                tvTip.setText("松开可以刷新");
                break;
            case REFRESHING:
                setTopPadding(0);
                tvTip.setText("正在刷新...");
                progressBar.setVisibility(VISIBLE);
                break;
            case UP:
                tipforload.setText("拖动加载更多");

                break;
            case LOADING:

                tipforload.setText("正在加载");
                p.setVisibility(VISIBLE);
                break;
        }

    }


    /**
     * 设置刷新事件监听器
     * @param listener
     */
    public void setOnRefreshListener(onRefreshListener listener){
        this.listener = listener;
    }

    /**
     * 设置刷新完成后的状态
     */
    public void onRefreshComplete(){

        TextView tView = (TextView) head.findViewById(R.id.tip);
        tView.setText("刷新完成");
        ProgressBar progressBar = (ProgressBar) head.findViewById(R.id.progress_bar);
        progressBar.setVisibility(GONE);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                return null;
            }

            protected void onPostExecute(Void result) {

                state = NONE;
                isFirst = false;
                animationforComplete();
                invalidate();
            };
        }.execute();
    }
    /**
     * 当上拉操作完成时候执行的操作
     */
    public void onUpComplete(){

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Void result) {

                state = NONE;
                isLast = false;
                setButtomPadding(-footerHeight);
                changeViewByState();
                invalidate();
            };
        }.execute();
    }

    /**
     * 在完成刷新之后回到原先正常布局时候的过度动画
     */

    private void animationforComplete(){
        new AsyncTask<Void, Void, Void>(){

            int i = 0;
            @Override
            protected Void doInBackground(Void... params) {
                while(i>=-headHeight){
                    i-=5;
                    try {
                        Thread.sleep(1);
                        publishProgress();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return null;
            }

            protected void onProgressUpdate(Void[] values) {
                setTopPadding(i);
            };

        }.execute();
    }
    /**
     * 刷新事件接口
     * @author Chenhp
     *
     */
    public interface onRefreshListener{
        void onPullRefresh();
        void onUpResfresh();
    }

}