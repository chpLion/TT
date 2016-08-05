package chen.upload;

/**
 * Created by chen on 16/3/20.
 * 上传视频过程中需要使用的数据库模型
 */
public class UploadInfo {

    private String videoName;
    private String videoDescription;
    private int uploadProgress;
    private String thum;

    public UploadInfo() {
    }


    public String getThum() {
        return thum;
    }

    public void setThum(String thum) {
        this.thum = thum;
    }

    public int getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(int uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
