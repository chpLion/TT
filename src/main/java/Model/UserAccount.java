package Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Peng on 2016/3/14.
 */
public class UserAccount extends BmobObject{


    private String userName;
    private float userMoney;

    public UserAccount() {
    }

    public float getUserMoney() {
        return userMoney;
    }

    public void setUserMoney(float userMoney) {
        this.userMoney = userMoney;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}


