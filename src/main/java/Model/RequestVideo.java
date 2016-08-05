package Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by chen on 16/2/21.
 */
public class RequestVideo extends BmobObject {

    private String title;//要求的标题
    private String decrabe;//要求的具体描述
    private int price;//愿意付出的价格
    private String userName;//用户名
    private String headImageUrl;//用户头像地址
    private String timeStr;


    public RequestVideo(){}

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public String getDecrabe() {
        return decrabe;
    }

    public void setDecrabe(String decrabe) {
        this.decrabe = decrabe;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
