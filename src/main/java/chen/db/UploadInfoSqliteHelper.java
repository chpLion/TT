package chen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chen on 16/3/20.
 * 存储上传视频过程中从临时信息
 */
public class UploadInfoSqliteHelper extends SQLiteOpenHelper{

    //需要保存的字段有
    //视频名称 视频上传进度 视频的缩略图,视频的描述
    public static final String VIDEO_NAME = "VIDEO_NAME";
    public static final String VIDEO_THUM = "VIDEO_THUM";
    public static final String UPLOAD_PROGRESS = "UPLOAD_PROGRESS";
    public static final String VIDEO_DESCRIPTION = "VIDEO_DESCRIPTION";

    public static final String TB_NAME = "uploadinfo";

    public UploadInfoSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+TB_NAME+"("+
                VIDEO_NAME +" varchar primary key,"+
                VIDEO_THUM+" varchar,"+
                VIDEO_DESCRIPTION+" varchar,"+
                UPLOAD_PROGRESS+" integer"+
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TB_NAME);
        onCreate(sqLiteDatabase);
    }
}
