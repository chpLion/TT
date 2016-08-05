package Model;

import android.graphics.Bitmap;

/**
 * Created by chen on 16/2/28.
 */
public class PersonDetailModel {
    private String detail;
    private String value;
    private Bitmap bitmap;

    public PersonDetailModel() {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
