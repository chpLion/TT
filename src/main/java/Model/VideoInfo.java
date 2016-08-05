package Model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by chen on 16/2/16.
 */
public class VideoInfo implements Serializable{

    private String vedioName;
    private long size;//大小
    private long duration;//视频时长
    private String data;//可播放的uri
    private Bitmap thumImage;//缩略图

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Bitmap getThumImage() {
        return thumImage;
    }

    public void setThumImage(Bitmap thumImage) {
        this.thumImage = thumImage;
    }

    public String getVedioName() {
        return vedioName;
    }

    public void setVedioName(String vedioName) {
        this.vedioName = vedioName;
    }
}
