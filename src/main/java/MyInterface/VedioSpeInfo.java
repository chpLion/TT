package MyInterface;

/**
 * Created by chen on 16/1/27.
 *
 * 视频的标签接口
 */
public interface VedioSpeInfo {

    //获取用户自定义的视频标签
    int getVideoTag();
    void setVideoTag(int tag);
    //获取平台的视频标签
    int getVideoType();
    void setVideoType(int type);
}
