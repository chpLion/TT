package chen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import chen.upload.UploadInfo;

/**
 * Created by chen on 16/3/20.
 * 使用数据库数据的工具类
 */
public class UploadDataHelper {

    //数据库名称
    private static String DB_NAME = "tt.db";
    //数据库版本
    private static int DB_VERSION = 1;

    private SQLiteDatabase db;
    private UploadInfoSqliteHelper dbHelper;

    public UploadDataHelper(Context context){

        //获取数据表
        dbHelper = new UploadInfoSqliteHelper(context,DB_NAME,null,DB_VERSION);
        //获取可以用于操作数据库的对象
        db = dbHelper.getWritableDatabase();

    }

    void close(){
        //关闭数据库
        db.close();
        dbHelper.close();
    }

    /**
     * 获取到一条记录
     * @param isSimple
     * @return
     */
    public UploadInfo getUPloadInfo(boolean isSimple){
        UploadInfo info = null;

        //设置查询条件
        Cursor cursor = db.query(UploadInfoSqliteHelper.TB_NAME,null,null,null,null,null,UploadInfoSqliteHelper.VIDEO_NAME+" DESC");
        cursor.moveToFirst();

        while(cursor.moveToNext()){
            info = new UploadInfo();
            //获取视频名称
            info.setVideoName(cursor.getString(0));
            //获取视频缩略图文件的地址
            info.setThum(cursor.getString(1));
            //获取视频描述
            info.setVideoDescription(cursor.getString(2));
            //获取视频上传进度
            info.setUploadProgress(cursor.getInt(3));
        }

        cursor.close();
        return info;
    }

    /**
     * 保存一条上传信息记录
     * @param info
     */
    public void saveUploadInfo(UploadInfo info){

        ContentValues values = new ContentValues();
        //通过values将对应的数据写入表中
        values.put(UploadInfoSqliteHelper.VIDEO_NAME,info.getVideoName());
        values.put(UploadInfoSqliteHelper.VIDEO_THUM,info.getThum());
        values.put(UploadInfoSqliteHelper.VIDEO_DESCRIPTION,info.getVideoDescription());
        values.put(UploadInfoSqliteHelper.UPLOAD_PROGRESS,info.getUploadProgress());

        db.insert(UploadInfoSqliteHelper.TB_NAME,UploadInfoSqliteHelper.VIDEO_NAME,values);
    }

    /**
     * 更新数据库数据
     * @param info
     */
    public void updateUploadProgress(UploadInfo info){
        ContentValues values = new ContentValues();
        values.put(UploadInfoSqliteHelper.VIDEO_NAME,info.getVideoName());
        values.put(UploadInfoSqliteHelper.VIDEO_THUM,info.getThum());
        values.put(UploadInfoSqliteHelper.VIDEO_DESCRIPTION,info.getVideoDescription());
        values.put(UploadInfoSqliteHelper.UPLOAD_PROGRESS,info.getUploadProgress());
        db.update(UploadInfoSqliteHelper.TB_NAME,values,UploadInfoSqliteHelper.VIDEO_NAME+" = '"+info.getVideoName()+"'",null);

    }

    /**
     * 删除数据库的数据
     * @param info
     */
    public void deleteUploadInfo(UploadInfo info){

        db.delete(UploadInfoSqliteHelper.TB_NAME,UploadInfoSqliteHelper.VIDEO_NAME+" = '"+info.getVideoName()+"'",null);

    }


}
