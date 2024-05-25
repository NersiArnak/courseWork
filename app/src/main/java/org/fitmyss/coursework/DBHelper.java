package org.fitmyss.coursework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String CONTACTS_TABLE = "contacts";
    private static final String COL_EMAIL = "Email";
    private static final String COL_PASSWORD = "Password";

    private static final String PRODUCTS_TABLE = "products";
    private static final String COL_ID = "id";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_PRICE = "price";
    private static final String COL_NAME = "nameProduct";
    private static final String COL_CHARACTERISTICS = "characteristics";
    private int cashBalance = 0;

    public void updateCashBalance(int amount) {
        cashBalance += amount;
    }

    private static final int DATABASE_VERSION = 3;  // Обновите номер версии

    public DBHelper(@Nullable Context context) {
        super(context, "example.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Создание таблицы для контактов
        sqLiteDatabase.execSQL("CREATE TABLE " + CONTACTS_TABLE + " (" + COL_EMAIL + " TEXT, " + COL_PASSWORD + " TEXT);");

        // Создание таблицы для продуктов
        sqLiteDatabase.execSQL("CREATE TABLE " + PRODUCTS_TABLE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_QUANTITY + " INTEGER, " +
                COL_PRICE + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_CHARACTERISTICS + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

    // Методы для таблицы контактов
    public void deleteAllContacts() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(CONTACTS_TABLE, null, null);
        sqLiteDatabase.close();
    }

    public void addContact(Data objData){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues db = new ContentValues();
        db.put(COL_EMAIL, objData.email);
        db.put(COL_PASSWORD, objData.password);
        sqLiteDatabase.insert(CONTACTS_TABLE, null, db);
        sqLiteDatabase.close();
    }

    public Cursor getAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + CONTACTS_TABLE, null);
    }

    public boolean checkUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CONTACTS_TABLE, new String[]{COL_EMAIL}, COL_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUserPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CONTACTS_TABLE, new String[]{COL_PASSWORD}, COL_EMAIL + "=? AND " + COL_PASSWORD + "=?", new String[]{email, password}, null, null, null);
        boolean correctPassword = cursor.getCount() > 0;
        cursor.close();
        return correctPassword;
    }

    // Методы для таблицы продуктов
    public void addProduct(Data objData){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues db = new ContentValues();
        db.put(COL_QUANTITY, objData.quantity);
        db.put(COL_PRICE, objData.price);
        db.put(COL_NAME, objData.nameProduct);
        db.put(COL_CHARACTERISTICS, objData.characteristics);
        sqLiteDatabase.insert(PRODUCTS_TABLE, null, db);
        sqLiteDatabase.close();
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCTS_TABLE, null);
    }

    public Cursor getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(PRODUCTS_TABLE, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        return cursor;
    }

    public void deleteAll() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(CONTACTS_TABLE, null, null);
        sqLiteDatabase.close();
    }

    public void addOne(Data objData){
        addContact(objData);
    }
}
