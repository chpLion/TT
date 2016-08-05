package Model;

import cn.bmob.v3.BmobObject;

public class VideoPay extends BmobObject{

	private String userName;
	private String videoUrl;
	private String videoName;
	private String videoType;
	private String thumUrl;	//缩略图连接
	private String ownerName;
	private float price;

	public String getVideoType() {
		return videoType;
	}
	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public String getVideoName() {
		return videoName;
	}
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	public String getThumUrl() {
		return thumUrl;
	}
	public void setThumUrl(String thumUrl) {
		this.thumUrl = thumUrl;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}


}
