package DataContext;

/**
 * Created by chen on 16/2/21.
 * 视频类型的模型
 */
public class TypeModel {
    private String typeName;//类型的名字
    private int imageId;//类型的图片

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
