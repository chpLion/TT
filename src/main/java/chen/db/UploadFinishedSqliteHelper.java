package chen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by chen on 16/3/20.
 * 上传完成的数据库建表工具
 */
public class UploadFinishedSqliteHelper extends SQLiteOpenHelper {

    public static final String TB_UPLOAD_FINISHED_NAME = "uploadfinished";

    public static String VIDEO_NAME = "VIDEO_NAME";
    public static String VIDEO_ID = "VIDEO_ID";
    public static String VIDEO_DESCRIPTION = "VIDEO_DESCRIPTION";
    public static String VIDEO_LENGTH = "VIDEO_LENGTH";
    public static String VIDEO_PRICE = "VIDEO_PRICE";
    public static String VIDEO_TYPE = "VIDEO_TYPE";
    public static String VIDEO_THUME_URL = "VIDEO_THUME_URL";
    public static String VIDEO_THUME_PATH = "VIDEO_THUME_PATH";

    public UploadFinishedSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+TB_UPLOAD_FINISHED_NAME+"("+
                VIDEO_ID +" varchar primary key,"+
                VIDEO_NAME +" varchar,"+
                VIDEO_DESCRIPTION+" varchar,"+
                VIDEO_LENGTH + " varchar,"+
                VIDEO_PRICE +" float,"+
                VIDEO_TYPE+" integer,"+
                VIDEO_THUME_URL+" varchar,"+
                VIDEO_THUME_PATH+" varchar"+
                ")"

        );

        Log.v("tb","create table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TB_UPLOAD_FINISHED_NAME);
        onCreate(sqLiteDatabase);
    }
}
