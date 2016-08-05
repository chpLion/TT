package Model;

import android.graphics.Bitmap;

import java.io.Serializable;

import MyInterface.VedioPlayInfo;
import MyInterface.VedioSpeInfo;
import cn.bmob.v3.BmobObject;

/**
 * Created by chen on 16/1/27.
 *
 * 实现视频播放信息接口和视频标签接口
 */
public class VideoModel extends BmobObject implements VedioPlayInfo, VedioSpeInfo,Serializable {

    private String videoName;//视频名称
    private String videoLength;//视频时长
    private String videoId;//视频唯一标识
    private String videoOwnerId;//视频所有者的唯一标识
    private int videoType;//视频的类别
    private int videoTag;//用户自定义视频标签
    private int mode;//视频播放的格式 标清和高清
    private String url;//视频播放的url
    private float price;//视频的价格
    private String thumUrl;//视频缩略图的url
    private Bitmap thum;//已经缓存的视频缩略图
    private String ownerName;//视频所有者的用户名
    private String description;//对视频的描述
    private User author;//视频所有者对象

    public VideoModel() {
        //构造方法
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setThum(Bitmap thum) {
        this.thum = thum;
    }

    public Bitmap getThum() {
        return thum;
    }

    public void setThumUrl(String thumUrl) {
        this.thumUrl = thumUrl;
    }

    public String getThumUrl() {
        return thumUrl;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPrice() {
        return price;
    }

    public String getVideoId() {
        return videoId;
    }



    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(String videoLength) {
        this.videoLength = videoLength;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoOwnerId() {
        return videoOwnerId;
    }

    public void setVideoOwnerId(String videoOwnerId) {
        this.videoOwnerId = videoOwnerId;
    }

    @Override
    public void setPlayMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int getPlayMode() {
        return 0;
    }

    @Override
    public String getPlayUrl() {
        return url;
    }

    @Override
    public void setPlayUrl(String url) {
        this.url = url;
    }

    public int getVideoTag() {
        return this.videoTag;
    }

    public void setVideoTag(int tag) {
        this.videoTag = tag;
    }

    public int getVideoType() {
        return this.videoType;
    }

    public void setVideoType(int type) {
        this.videoType = type;
    }
}
