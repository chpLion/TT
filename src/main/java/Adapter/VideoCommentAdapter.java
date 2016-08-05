package Adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.myapplication.R;

import java.util.List;

import DataContext.ImagesUtil;
import Model.User;
import Model.VideoComment;

/**
 * Created by chen on 16/3/20.
 * 视频评论适配器
 */
public class VideoCommentAdapter extends SingleAdapter<VideoComment>{
    public VideoCommentAdapter(Context context, List<VideoComment> mDates, int layoutId) {
        super(context, mDates, layoutId);
    }

    @Override
    public void ConfigView(final SingleLayoutViewholder holder, final VideoComment videoComment) {

        //获取评论者信息
        User author = videoComment.getCommentUser();
        //视频评论内容
        ((TextView)holder.getView(R.id.tv_video_play_comment)).setText(videoComment.getComment());

        //获取评论者信息
        //评论用户的用户名
        ((TextView)holder.getView(R.id.tv_video_play_comment_uername)).setText(author.getUsername());
        //评论的时间
        ((TextView)holder.getView(R.id.tv_video_play_comment_time)).setText(videoComment.getCreatedAt());
        //评论者头像
        ImageView headImage = (ImageView) holder.getView(R.id.img_video_play_comment_headimage);
        ImagesUtil imagesUtil = ImagesUtil.getInstance();
        if (author.getHeadImageUrl()!=null){
            imagesUtil.getBitmapByAsyncTask(context,headImage,author.getHeadImageUrl());
        }

    }
}
