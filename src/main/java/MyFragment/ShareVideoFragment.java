package MyFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chen.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.ShareAdapter;
import Model.SharedVideo;
import MyView.RefreshListview;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by chen on 16/2/22.
 */
public class ShareVideoFragment extends Fragment {

    private List<SharedVideo> mDatas = new ArrayList<>();
    ShareAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        //绑定分享视频的布局
        View view = inflater.inflate(R.layout.share_vedio_layout,container,false);
        final RefreshListview lvShare = (RefreshListview) view.findViewById(R.id.list_video_share);
        adapter = new ShareAdapter(getActivity(),mDatas,R.layout.share_item);
        BmobQuery<SharedVideo> query = new BmobQuery<>();
        //从数据库中查询数据
        loadData();
        lvShare.setAdapter(adapter);
        //设置下拉刷新
        lvShare.setOnRefreshListener(new RefreshListview.onRefreshListener() {
            @Override
            public void onPullRefresh() {
                loadData();
                lvShare.onRefreshComplete();
            }

            @Override
            public void onUpResfresh() {

                lvShare.onUpComplete();
            }
        });
        //返回绑定布局
        return view;
    }

    private void loadData(){
        BmobQuery<SharedVideo> query = new BmobQuery<>();
        //从数据库中查询数据
        query.findObjects(getActivity(), new FindListener<SharedVideo>() {
            @Override
            public void onSuccess(List<SharedVideo> list) {
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
