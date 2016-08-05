package MyInterface;

/**
 * Created by chen on 16/1/27.
 *
 * 视频的播放信息的接口
 */
public interface VedioPlayInfo {

    //视频的播放格式 1 流畅 2 高清
    void setPlayMode(int mode);
    //获取视频播放格式
    int getPlayMode();
    //视频播放的url地址
    String getPlayUrl();
    void setPlayUrl(String url);
}
