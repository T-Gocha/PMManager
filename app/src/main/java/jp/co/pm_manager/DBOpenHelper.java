package jp.co.pm_manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {
    // データベース自体の名前(テーブル名ではない)
    private static final String DB_NAME = "Main_DB";
    private static final String TABLE_NAME = "First";
    private static final String _ID = "_id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_NUMBER = "number";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COLOR = "color";
    private static final String COLUMN_COLORTYPE = "colortype";
    private static final String COLUMN_SIZE1 = "size1";
    private static final String COLUMN_SIZE2 = "size2";
    private static final String COLUMN_SIZE3 = "size3";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_COMMENT = "Comment";
    // データベースのバージョン(2,3と挙げていくとonUpgradeメソッドが実行される)
    private static final int VERSION = 1;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_TYPE + " TEXT," +
                    COLUMN_NUMBER + " INTEGER," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_COLOR + " TEXT," +
                    COLUMN_COLORTYPE + " TEXT," +
                    COLUMN_SIZE1 + " INTEGER," +
                    COLUMN_SIZE2 + " INTEGER," +
                    COLUMN_SIZE3 + " INTEGER," +
                    COLUMN_PRICE + " INTEGER," +
                    COLUMN_COMMENT + " TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    // コンストラクタ　以下のように呼ぶこと
    public DBOpenHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    // データベースが作成された時に実行される処理
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

        Log.d("debug", "onCreate(SQLiteDatabase db)");
    }

    // データベースをバージョンアップした時に実行される処理
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // データベースが開かれた時に実行される処理
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
