package Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Peng on 2016/3/16.
 */
public class MyCart extends BmobObject{

    private String userName;   //用户名
    private String videoId; //视频唯一标识

    public MyCart() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
