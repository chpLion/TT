package Model;

import android.graphics.Bitmap;

import cn.bmob.v3.BmobObject;

/**
 * Created by chen on 16/2/23.
 */
public class ReplyModel extends BmobObject{

    private String userName;
    private String headUrl;//头像的可访问地址
    private String content;//回复的内容
    private Bitmap headImage;//缓存的头像
    private String time;
    private String requestId;//需求的唯一标识


    public ReplyModel() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public void setHeadImage(Bitmap headImage) {
        this.headImage = headImage;
    }

    public Bitmap getHeadImage() {
        return headImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
