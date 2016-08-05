package MyFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.chen.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.RequestTradeAdapter;
import Model.RequestVideo;
import MyView.RefreshListview;
import chen.trade.RequestDetailAcitvity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by chen on 16/2/22.
 */
public class RequestVideoFragment extends Fragment{

    private List<RequestVideo> mDatas = new ArrayList<>();
    RequestTradeAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.request_trade_list,container,false);
        final RefreshListview lvRequest = (RefreshListview) view.findViewById(R.id.list_video_request_trade);
        adapter = new RequestTradeAdapter(getActivity(),mDatas,R.layout.request_trade_item);
        lvRequest.setAdapter(adapter);
        //item点击事件
        lvRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), RequestDetailAcitvity.class);
                //把当前对象传入
                Bundle bundle = new Bundle();
                bundle.putSerializable("requestvideo",mDatas.get(position-1));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        loadData();
        //刷新加载
        lvRequest.setOnRefreshListener(new RefreshListview.onRefreshListener() {
            @Override
            public void onPullRefresh() {
                loadData();
                adapter.notifyDataSetChanged();
                lvRequest.onRefreshComplete();

            }

            @Override
            public void onUpResfresh() {
                lvRequest.onUpComplete();
            }
        });
        return view;
    }

    private void loadData(){
        BmobQuery<RequestVideo> query = new BmobQuery<>();
        //从数据库中查询数据
        query.findObjects(getActivity(), new FindListener<RequestVideo>() {
            @Override
            public void onSuccess(List<RequestVideo> list) {
                mDatas.clear();
                mDatas.addAll(list);
                Collections.reverse(mDatas);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
