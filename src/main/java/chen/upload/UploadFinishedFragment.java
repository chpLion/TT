package chen.upload;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chen.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import DataContext.AppData;
import Model.VideoModel;
import MyView.RefreshListview;
import chen.db.UploadFinishedDataHelper;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by chen on 16/3/16.
 * 已经上传成功的视频
 */
public class UploadFinishedFragment extends Fragment {

    private UploadFinishedDataHelper helper;
    List<MyUploadVideoInfo> infos = new ArrayList<>();
    private List<VideoModel> ups = new ArrayList<>();
    UploadFinishedAdapter adapter;
    RefreshListview lvFinishedVideo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_video_list,container,false);

        //显示视频信息列表
        lvFinishedVideo = (RefreshListview) view.findViewById(R.id.lv_video_record);
        helper = new UploadFinishedDataHelper(getActivity());
        //从本地数据库中读取数据
        infos = helper.getUploadVideoInfo();
        adapter = new UploadFinishedAdapter(getActivity(),ups,R.layout.my_upload_video_list_item);
        loadData();
        lvFinishedVideo.setAdapter(adapter);
        lvFinishedVideo.setOnRefreshListener(new RefreshListview.onRefreshListener() {
            @Override
            public void onPullRefresh() {
                //清空再读取
                ups.clear();
                loadData();
                adapter.notifyDataSetChanged();
                lvFinishedVideo.onRefreshComplete();
            }

            @Override
            public void onUpResfresh() {
                lvFinishedVideo.onUpComplete();
            }
        });

        return view;
    }

    public void loadData(){
        BmobQuery<VideoModel> query = new BmobQuery<>();
        query.setLimit(20);
        query.addWhereEqualTo("ownerName", AppData.user.getUsername());
        query.findObjects(getActivity(), new FindListener<VideoModel>() {
            @Override
            public void onSuccess(List<VideoModel> list) {
                ups.clear();
                ups.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
