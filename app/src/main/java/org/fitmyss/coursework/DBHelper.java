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

    private static final String PRODUCTS_TABLE_STOCK = "products_stock";
    private static final String COL_ID_STOCK = "id";
    private static final String COL_QUANTITY_STOCK = "quantity";
    private static final String COL_PRICE_STOCK = "price";
    private static final String COL_NAME_STOCK = "nameProduct";
    private static final String COL_CHARACTERISTICS_STOCK = "characteristics";

    private int cashBalance = 0;

    public void updateCashBalance(int amount) {
        cashBalance += amount;
    }

    private static final int DATABASE_VERSION = 4;

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

        // Создание таблицы для продуктов в стоке
        sqLiteDatabase.execSQL("CREATE TABLE " + PRODUCTS_TABLE_STOCK + " (" +
                COL_ID_STOCK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_QUANTITY_STOCK + " INTEGER, " +
                COL_PRICE_STOCK + " INTEGER, " +
                COL_NAME_STOCK + " TEXT, " +
                COL_CHARACTERISTICS_STOCK + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_TABLE);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_TABLE_STOCK);
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

    public Data getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(PRODUCTS_TABLE, null, COL_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int quantityIndex = cursor.getColumnIndex(COL_QUANTITY);
            int priceIndex = cursor.getColumnIndex(COL_PRICE);
            int nameIndex = cursor.getColumnIndex(COL_NAME);
            int characteristicsIndex = cursor.getColumnIndex(COL_CHARACTERISTICS);

            if (quantityIndex == -1 || priceIndex == -1 || nameIndex == -1 || characteristicsIndex == -1) {
                cursor.close();
                throw new IllegalStateException("One of the columns is not found");
            }

            int quantity = cursor.getInt(quantityIndex);
            int price = cursor.getInt(priceIndex);
            String name = cursor.getString(nameIndex);
            String characteristics = cursor.getString(characteristicsIndex);
            cursor.close();
            return new Data(id, quantity, price, name, characteristics);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRODUCTS_TABLE, COL_ID + "=?", new String[]{String.valueOf(productId)});
        db.close();
    }

    public int getProductPrice(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int price = 0;

        Cursor cursor = db.rawQuery("SELECT " + COL_PRICE + " FROM " + PRODUCTS_TABLE + " WHERE " + COL_ID + " = ?", new String[]{String.valueOf(productId)});
        if (cursor != null && cursor.moveToFirst()) {
            int priceIndex = cursor.getColumnIndex(COL_PRICE);
            if (priceIndex != -1) {
                price = cursor.getInt(priceIndex);
            }
            cursor.close();
        }
        return price;
    }

    public void updateProduct(Data newData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, newData.getQuantity());
        values.put(COL_PRICE, newData.getPrice());
        values.put(COL_NAME, newData.getName());
        values.put(COL_CHARACTERISTICS, newData.getCharacteristics());

        // Умножаем новую цену на новое количество для обновления кассового баланса
        int newPrice = newData.getPrice() * newData.getQuantity();
        updateCashBalance(newPrice - getProductPrice(newData.getId())); // Обновляем кассовый баланс на разницу старой и новой цен
        db.update(PRODUCTS_TABLE, values, COL_ID + " = ?", new String[]{String.valueOf(newData.getId())});
        db.close();
    }


    public void addProductStock(Data objData){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues db = new ContentValues();
        db.put(COL_QUANTITY_STOCK, objData.quantity);
        db.put(COL_PRICE_STOCK, objData.price);
        db.put(COL_NAME_STOCK, objData.nameProduct);
        db.put(COL_CHARACTERISTICS_STOCK, objData.characteristics);
        sqLiteDatabase.insert(PRODUCTS_TABLE_STOCK, null, db);
        sqLiteDatabase.close();
    }

    public Cursor getAllProductsStock() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCTS_TABLE_STOCK, null);
    }

    public Data getProductByIdStock(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(PRODUCTS_TABLE_STOCK, null, COL_ID_STOCK + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int quantityIndex = cursor.getColumnIndex(COL_QUANTITY_STOCK);
            int priceIndex = cursor.getColumnIndex(COL_PRICE_STOCK);
            int nameIndex = cursor.getColumnIndex(COL_NAME_STOCK);
            int characteristicsIndex = cursor.getColumnIndex(COL_CHARACTERISTICS_STOCK);

            if (quantityIndex == -1 || priceIndex == -1 || nameIndex == -1 || characteristicsIndex == -1) {
                cursor.close();
                throw new IllegalStateException("One of the columns is not found");
            }

            int quantity = cursor.getInt(quantityIndex);
            int price = cursor.getInt(priceIndex);
            String name = cursor.getString(nameIndex);
            String characteristics = cursor.getString(characteristicsIndex);
            cursor.close();
            return new Data(id, quantity, price, name, characteristics);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public void deleteProductStock(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRODUCTS_TABLE_STOCK, COL_ID_STOCK + "=?", new String[]{String.valueOf(productId)});
        db.close();
    }

    public int getProductPriceStock(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int price = 0;

        Cursor cursor = db.rawQuery("SELECT " + COL_PRICE_STOCK + " FROM " + PRODUCTS_TABLE_STOCK + " WHERE " + COL_ID_STOCK + " = ?", new String[]{String.valueOf(productId)});
        if (cursor != null && cursor.moveToFirst()) {
            int priceIndex = cursor.getColumnIndex(COL_PRICE_STOCK);
            if (priceIndex != -1) {
                price = cursor.getInt(priceIndex);
            }
            cursor.close();
        }
        return price;
    }

    public void updateProductStock(Data newData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY_STOCK, newData.getQuantity());
        values.put(COL_PRICE_STOCK, newData.getPrice());
        values.put(COL_NAME_STOCK, newData.getName());
        values.put(COL_CHARACTERISTICS_STOCK, newData.getCharacteristics());

        int newPrice = newData.getPrice() * newData.getQuantity();
        updateCashBalance(newPrice - getProductPriceStock(newData.getId()));
        db.update(PRODUCTS_TABLE_STOCK, values, COL_ID_STOCK + " = ?", new String[]{String.valueOf(newData.getId())});
        db.close();
    }

    public void addOne(Data objData){
        addContact(objData);
    }

}
