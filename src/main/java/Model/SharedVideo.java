package Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by chen on 16/2/21.
 */
public class SharedVideo extends BmobObject {

    private String thumUrl;//视频缩略图
    private String describe;//对视频的描述
    private String videoPlayUrl;//视频播放的url
    private String userName;//用户名
    private String headImageUrl;//用户头像地址
    private String timeStr;//发表的时间

    public SharedVideo() {
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getThumUrl() {
        return thumUrl;
    }

    public void setThumUrl(String thumUrl) {
        this.thumUrl = thumUrl;
    }

    public String getVideoPlayUrl() {
        return videoPlayUrl;
    }

    public void setVideoPlayUrl(String videoPlayUrl) {
        this.videoPlayUrl = videoPlayUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }
}
