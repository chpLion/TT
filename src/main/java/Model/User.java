package Model;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * Created by chen on 16/1/27.
 */
public class User extends BmobUser implements Serializable{

    private String userId;
    private String userSex;
    private float userMoney;
    private String headImageUrl;
    private String payPassword;
    private String  headImage;//下载好的头像在本地的路径
    private String description;//对个人的描述
    private String moto;//个人的座右铭或者给访问空间的人的寄语
    private String fansCount;//关注的人的数量

    public User() {
    }

    public String getFansCount() {
        return fansCount;
    }

    public void setFansCount(String fansCount) {
        this.fansCount = fansCount;
    }

    public String getDescription() {
        return description;
    }

    public String getMoto() {
        return moto;
    }

    public void setMoto(String moto) {
        this.moto = moto;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public void setHeadImageUrl(String hedImageUrl) {
        this.headImageUrl = hedImageUrl;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public void setUserMoney(float userMoney) {
        this.userMoney = userMoney;
    }

    public float getUserMoney() {
        return userMoney;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }
}
