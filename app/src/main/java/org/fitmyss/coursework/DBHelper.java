package org.fitmyss.coursework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String MY_TABLE = "contacts";
    private static final String COL_EMAIL = "Email";
    private static final String COL_PASSWORD = "Password";
    private static final int DATABASE_VERSION = 2;  // Обновите номер версии

    public DBHelper(@Nullable Context context) {
        super(context, "example.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + MY_TABLE + " (" + COL_EMAIL + " TEXT, " + COL_PASSWORD + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MY_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

    public void deleteAll() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(MY_TABLE, null, null);
        sqLiteDatabase.close();
    }

    public void addOne(Data objData){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues db = new ContentValues();
        db.put(COL_EMAIL, objData.email);
        db.put(COL_PASSWORD, objData.password);
        long result = sqLiteDatabase.insert(MY_TABLE, null, db);
        sqLiteDatabase.close();
    }

    public Cursor getAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + MY_TABLE, null);
    }

    public boolean checkUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MY_TABLE, new String[]{COL_EMAIL}, COL_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUserPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MY_TABLE, new String[]{COL_PASSWORD}, COL_EMAIL + "=? AND " + COL_PASSWORD + "=?", new String[]{email, password}, null, null, null);
        boolean correctPassword = cursor.getCount() > 0;
        cursor.close();
        return correctPassword;
    }
}
