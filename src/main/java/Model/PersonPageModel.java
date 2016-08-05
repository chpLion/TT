package Model;

/**
 * Created by chen on 16/2/22.
 * 个人主页的模型
 */
public class PersonPageModel {

    private int imageId;//表示图片的id
    private String content;//内容

    public PersonPageModel() {
    }

    public int getImageId() {
        return imageId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
