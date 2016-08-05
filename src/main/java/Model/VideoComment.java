package Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by chen on 16/3/20.
 * 视频的评论模型
 */
public class VideoComment extends BmobObject{

    //评论视频的用户的id
    private String userId;
    //评论的时间
    private String time;
    //评论的内容
    private String comment;
    //评论的视频的id
    private String videoId;

    private String userName;
    private String headImageUrl;

    private User commentUser;



    public VideoComment() {
    }

    public User getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(User commentUser) {
        this.commentUser = commentUser;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
