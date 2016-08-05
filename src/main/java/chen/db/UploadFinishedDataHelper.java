package chen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import chen.upload.MyUploadVideoInfo;

/**
 * Created by chen on 16/3/20.
 * 已经上传的数据
 */
public class UploadFinishedDataHelper {
    //数据库名称
    private static String DB_NAME = "ttuploadfinish.db";
    UploadFinishedSqliteHelper dbHelper;
    SQLiteDatabase db;
    //数据库版本
    private static int DB_VERSION = 1;
    public UploadFinishedDataHelper(Context context){
        //获取数据表
        dbHelper = new UploadFinishedSqliteHelper(context,DB_NAME,null,DB_VERSION);
        //获取可以用于操作数据库的对象
        db = dbHelper.getWritableDatabase();
    }


    public void close(){
        db.close();
        dbHelper.close();
    }
    /**
     * 获取数据库的已上传视频
     * @return
     */
    public List<MyUploadVideoInfo> getUploadVideoInfo(){
        List<MyUploadVideoInfo> infos = new ArrayList<>();

        Cursor cursor = db.query(UploadFinishedSqliteHelper.TB_UPLOAD_FINISHED_NAME,null,null,null,null,null,UploadFinishedSqliteHelper.VIDEO_ID+" DESC");
        cursor.moveToFirst();
        while(cursor.moveToNext()){

            MyUploadVideoInfo info = new MyUploadVideoInfo();
            info.setVideoId(cursor.getString(0));
            info.setVideoName(cursor.getString(1));
            info.setDescription(cursor.getString(2));
            info.setLength(cursor.getString(3));
            info.setPrice(cursor.getFloat(4));
            info.setType(cursor.getInt(5));
            info.setThumUrl(cursor.getString(6));
            info.setThumPath(cursor.getString(7));

            infos.add(info);
        }
        close();
        return infos;
    }

    /**
     * 保存一条用户上传的视频信息
     * @param info
     */
    public void saveUploadFinished(MyUploadVideoInfo info){

        ContentValues values = new ContentValues();

        values.put(UploadFinishedSqliteHelper.VIDEO_ID,info.getVideoId());
        values.put(UploadFinishedSqliteHelper.VIDEO_NAME,info.getVideoName());
        values.put(UploadFinishedSqliteHelper.VIDEO_DESCRIPTION,info.getDescription());
        values.put(UploadFinishedSqliteHelper.VIDEO_LENGTH,info.getLength());
        values.put(UploadFinishedSqliteHelper.VIDEO_PRICE,info.getPrice());
        values.put(UploadFinishedSqliteHelper.VIDEO_TYPE,info.getType());
        values.put(UploadFinishedSqliteHelper.VIDEO_THUME_URL,info.getThumUrl());
        values.put(UploadFinishedSqliteHelper.VIDEO_THUME_PATH,info.getThumPath());

        db.insert(UploadFinishedSqliteHelper.TB_UPLOAD_FINISHED_NAME,UploadFinishedSqliteHelper.VIDEO_ID,values);
    }
}
