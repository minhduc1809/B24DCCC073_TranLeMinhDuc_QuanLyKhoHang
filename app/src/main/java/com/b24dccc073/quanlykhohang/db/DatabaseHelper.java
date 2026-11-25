package com.b24dccc073.quanlykhohang.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.b24dccc073.quanlykhohang.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "warehouse.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_PRODUCT_ID = "id";
    public static final String COL_PRODUCT_NAME = "name";
    public static final String COL_PRODUCT_PRICE = "price";
    public static final String COL_PRODUCT_QUANTITY = "quantity";
    public static final String COL_PRODUCT_CATEGORY = "category";
    public static final String COL_PRODUCT_ACTIVE = "active";
    public static final String COL_PRODUCT_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL"
                + ")";
        db.execSQL(createUsersTable);

        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PRODUCT_NAME + " TEXT NOT NULL, "
                + COL_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + COL_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + COL_PRODUCT_CATEGORY + " TEXT NOT NULL, "
                + COL_PRODUCT_ACTIVE + " INTEGER NOT NULL, "
                + COL_PRODUCT_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(createProductsTable);

        insertUserInternal(db, "admin", "123456");

        insertProductInternal(db, new Product(
                "Chuột không dây", 150000, 20, "Điện tử", true,
                "Chuột quang không dây 2.4GHz, pin dùng bền"));
        insertProductInternal(db, new Product(
                "Bàn phím cơ", 450000, 3, "Điện tử", true,
                "Bàn phím cơ switch xanh, có đèn nền"));
        insertProductInternal(db, new Product(
                "Sổ tay A5", 25000, 40, "Văn phòng phẩm", true,
                "Sổ tay bìa cứng khổ A5, giấy dày 200 trang"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS
                + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ? LIMIT 1";
        try (Cursor cursor = db.rawQuery(sql, new String[]{username, password})) {
            return cursor.moveToFirst();
        }
    }

    public long insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        return insertUserInternal(db, username, password);
    }

    private long insertUserInternal(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    public long insertProduct(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        return insertProductInternal(db, p);
    }

    private long insertProductInternal(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, p.getName());
        values.put(COL_PRODUCT_PRICE, p.getPrice());
        values.put(COL_PRODUCT_QUANTITY, p.getQuantity());
        values.put(COL_PRODUCT_CATEGORY, p.getCategory());
        values.put(COL_PRODUCT_ACTIVE, p.isActive() ? 1 : 0);
        values.put(COL_PRODUCT_DESCRIPTION, p.getDescription() != null ? p.getDescription() : "");
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COL_PRODUCT_ID + " ASC";
        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                products.add(mapCursorToProduct(cursor));
            }
        }
        return products;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_PRODUCT_ID + " = ? LIMIT 1";
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                return mapCursorToProduct(cursor);
            }
        }
        return null;
    }

    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_QUANTITY, quantity);
        return db.update(TABLE_PRODUCTS, values, COL_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COL_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COALESCE(SUM(" + COL_PRODUCT_PRICE + " * " + COL_PRODUCT_QUANTITY
                + "), 0) FROM " + TABLE_PRODUCTS;
        try (Cursor cursor = db.rawQuery(sql, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        }
        return 0L;
    }

    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COL_PRODUCT_QUANTITY + " < ?";
        try (Cursor cursor = db.rawQuery(sql, new String[]{"5"})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    private Product mapCursorToProduct(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_QUANTITY));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_CATEGORY));
        boolean active = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ACTIVE)) == 1;
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_DESCRIPTION));
        return new Product(id, name, price, quantity, category, active, description);
    }
}
