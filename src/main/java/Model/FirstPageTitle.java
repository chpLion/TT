package Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by chen on 16/2/13.
 */
public class FirstPageTitle extends BmobObject{

    private String title;
    private int tag;//视频标志
    public FirstPageTitle() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }
}
