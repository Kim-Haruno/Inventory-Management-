package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Inventory.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";

    public static final String TABLE_ITEMS = "items";
    public static final String COL_ITEM_ID = "id";
    public static final String COL_ITEM_NAME = "name";
    public static final String COL_ITEM_QUANTITY = "quantity";
    public static final String COL_ITEM_CATEGORY = "category";
    public static final String COL_ITEM_EXPECTED = "expected_quantity";
    public static final String COL_ITEM_SKU = "sku";
    public static final String COL_ITEM_BARCODE = "barcode";
    public static final String COL_ITEM_COST_PRICE = "cost_price";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static String generateSku() {
        long randomDigits = (long) (Math.random() * 899999L) + 100000L;
        return "SKU-" + randomDigits;
    }

    public static String generateBarcode() {
        long randomDigits = (long) (Math.random() * 899999999L) + 100000000L;
        return "600" + randomDigits;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PASSWORD + " TEXT)";
        db.execSQL(createUsers);

        String createItems = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ITEM_NAME + " TEXT, " +
                COL_ITEM_QUANTITY + " INTEGER, " +
                COL_ITEM_CATEGORY + " TEXT, " +
                COL_ITEM_EXPECTED + " INTEGER DEFAULT 40, " +
                COL_ITEM_SKU + " TEXT, " +
                COL_ITEM_BARCODE + " TEXT, " +
                COL_ITEM_COST_PRICE + " REAL DEFAULT 0.0)";
        db.execSQL(createItems);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COL_ITEM_CATEGORY + " TEXT");
            } catch (Exception ignored) {}
        }
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COL_ITEM_EXPECTED + " INTEGER DEFAULT 40");
            } catch (Exception ignored) {}
        }
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COL_ITEM_SKU + " TEXT");
            } catch (Exception ignored) {}
            try {
                db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COL_ITEM_BARCODE + " TEXT");
            } catch (Exception ignored) {}
            try {
                db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COL_ITEM_COST_PRICE + " REAL DEFAULT 0.0");
            } catch (Exception ignored) {}
        }
    }

    public boolean registerUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=?",
                new String[]{email}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean addItem(String name, int quantity, String category, int expectedQuantity, String sku, String barcode, double costPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_NAME, name);
        values.put(COL_ITEM_QUANTITY, quantity);
        values.put(COL_ITEM_CATEGORY, category);
        values.put(COL_ITEM_EXPECTED, expectedQuantity > 0 ? expectedQuantity : 40);

        if (sku == null || sku.trim().isEmpty() || sku.trim().equalsIgnoreCase("N/A")) {
            sku = generateSku();
        } else {
            sku = sku.trim();
        }

        if (barcode == null || barcode.trim().isEmpty() || barcode.trim().equalsIgnoreCase("N/A")) {
            barcode = generateBarcode();
        } else {
            barcode = barcode.trim();
        }

        values.put(COL_ITEM_SKU, sku);
        values.put(COL_ITEM_BARCODE, barcode);
        values.put(COL_ITEM_COST_PRICE, costPrice >= 0 ? costPrice : 0.0);
        long result = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return result != -1;
    }

    public boolean addItem(String name, int quantity, String category, int expectedQuantity) {
        return addItem(name, quantity, category, expectedQuantity, "N/A", "N/A", 0.0);
    }

    public boolean addItem(String name, int quantity, String category) {
        return addItem(name, quantity, category, 40, "N/A", "N/A", 0.0);
    }

    public boolean addItem(String name, int quantity) {
        return addItem(name, quantity, "General", 40, "N/A", "N/A", 0.0);
    }

    public ArrayList<Item> getAllItems() {
        ArrayList<Item> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " ORDER BY " + COL_ITEM_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_QUANTITY));
                int catIdx = cursor.getColumnIndex(COL_ITEM_CATEGORY);
                String category = (catIdx >= 0 && !cursor.isNull(catIdx)) ? cursor.getString(catIdx) : "General";
                int expIdx = cursor.getColumnIndex(COL_ITEM_EXPECTED);
                int expectedQuantity = (expIdx >= 0 && !cursor.isNull(expIdx)) ? cursor.getInt(expIdx) : 40;

                int skuIdx = cursor.getColumnIndex(COL_ITEM_SKU);
                String sku = (skuIdx >= 0 && !cursor.isNull(skuIdx)) ? cursor.getString(skuIdx) : "";
                if (sku.isEmpty() || sku.equalsIgnoreCase("N/A")) {
                    sku = generateSku();
                }

                int bcIdx = cursor.getColumnIndex(COL_ITEM_BARCODE);
                String barcode = (bcIdx >= 0 && !cursor.isNull(bcIdx)) ? cursor.getString(bcIdx) : "";
                if (barcode.isEmpty() || barcode.equalsIgnoreCase("N/A")) {
                    barcode = generateBarcode();
                }

                int cpIdx = cursor.getColumnIndex(COL_ITEM_COST_PRICE);
                double costPrice = (cpIdx >= 0 && !cursor.isNull(cpIdx)) ? cursor.getDouble(cpIdx) : 0.0;

                list.add(new Item(id, name, quantity, category, expectedQuantity, sku, barcode, costPrice));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<Item> searchItems(String query) {
        ArrayList<Item> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_ITEMS + " WHERE " + COL_ITEM_NAME + " LIKE ? OR " + COL_ITEM_SKU + " LIKE ? OR " + COL_ITEM_BARCODE + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"}
        );
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_QUANTITY));
                int catIdx = cursor.getColumnIndex(COL_ITEM_CATEGORY);
                String category = (catIdx >= 0 && !cursor.isNull(catIdx)) ? cursor.getString(catIdx) : "General";
                int expIdx = cursor.getColumnIndex(COL_ITEM_EXPECTED);
                int expectedQuantity = (expIdx >= 0 && !cursor.isNull(expIdx)) ? cursor.getInt(expIdx) : 40;

                int skuIdx = cursor.getColumnIndex(COL_ITEM_SKU);
                String sku = (skuIdx >= 0 && !cursor.isNull(skuIdx)) ? cursor.getString(skuIdx) : "";
                if (sku.isEmpty() || sku.equalsIgnoreCase("N/A")) {
                    sku = generateSku();
                }

                int bcIdx = cursor.getColumnIndex(COL_ITEM_BARCODE);
                String barcode = (bcIdx >= 0 && !cursor.isNull(bcIdx)) ? cursor.getString(bcIdx) : "";
                if (barcode.isEmpty() || barcode.equalsIgnoreCase("N/A")) {
                    barcode = generateBarcode();
                }

                int cpIdx = cursor.getColumnIndex(COL_ITEM_COST_PRICE);
                double costPrice = (cpIdx >= 0 && !cursor.isNull(cpIdx)) ? cursor.getDouble(cpIdx) : 0.0;

                list.add(new Item(id, name, quantity, category, expectedQuantity, sku, barcode, costPrice));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public Item getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_ITEMS + " WHERE " + COL_ITEM_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        Item item = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_QUANTITY));
            int catIdx = cursor.getColumnIndex(COL_ITEM_CATEGORY);
            String category = (catIdx >= 0 && !cursor.isNull(catIdx)) ? cursor.getString(catIdx) : "General";
            int expIdx = cursor.getColumnIndex(COL_ITEM_EXPECTED);
            int expectedQuantity = (expIdx >= 0 && !cursor.isNull(expIdx)) ? cursor.getInt(expIdx) : 40;

            int skuIdx = cursor.getColumnIndex(COL_ITEM_SKU);
            String sku = (skuIdx >= 0 && !cursor.isNull(skuIdx)) ? cursor.getString(skuIdx) : "";
            if (sku.isEmpty() || sku.equalsIgnoreCase("N/A")) {
                sku = generateSku();
            }

            int bcIdx = cursor.getColumnIndex(COL_ITEM_BARCODE);
            String barcode = (bcIdx >= 0 && !cursor.isNull(bcIdx)) ? cursor.getString(bcIdx) : "";
            if (barcode.isEmpty() || barcode.equalsIgnoreCase("N/A")) {
                barcode = generateBarcode();
            }

            int cpIdx = cursor.getColumnIndex(COL_ITEM_COST_PRICE);
            double costPrice = (cpIdx >= 0 && !cursor.isNull(cpIdx)) ? cursor.getDouble(cpIdx) : 0.0;

            item = new Item(id, name, quantity, category, expectedQuantity, sku, barcode, costPrice);
        }

        cursor.close();
        db.close();
        return item;
    }

    public boolean updateItem(int id, String name, int quantity, String category, int expectedQuantity, String sku, String barcode, double costPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_NAME, name);
        values.put(COL_ITEM_QUANTITY, quantity);
        values.put(COL_ITEM_CATEGORY, category);
        values.put(COL_ITEM_EXPECTED, expectedQuantity > 0 ? expectedQuantity : 40);

        if (sku == null || sku.trim().isEmpty() || sku.trim().equalsIgnoreCase("N/A")) {
            sku = generateSku();
        } else {
            sku = sku.trim();
        }

        if (barcode == null || barcode.trim().isEmpty() || barcode.trim().equalsIgnoreCase("N/A")) {
            barcode = generateBarcode();
        } else {
            barcode = barcode.trim();
        }

        values.put(COL_ITEM_SKU, sku);
        values.put(COL_ITEM_BARCODE, barcode);
        values.put(COL_ITEM_COST_PRICE, costPrice >= 0 ? costPrice : 0.0);
        int rows = db.update(TABLE_ITEMS, values, COL_ITEM_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean updateItem(int id, String name, int quantity, String category, int expectedQuantity) {
        return updateItem(id, name, quantity, category, expectedQuantity, "N/A", "N/A", 0.0);
    }

    public boolean updateItem(int id, String name, int quantity, String category) {
        return updateItem(id, name, quantity, category, 40, "N/A", "N/A", 0.0);
    }

    public boolean updateItem(int id, String name, int quantity) {
        return updateItem(id, name, quantity, "General", 40, "N/A", "N/A", 0.0);
    }

    public boolean recordSale(int id, int quantitySold) {
        Item item = getItemById(id);
        if (item != null && item.getQuantity() >= quantitySold) {
            int newQuantity = item.getQuantity() - quantitySold;
            return updateQuantity(id, newQuantity);
        }
        return false;
    }

    public boolean updateQuantity(int id, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_QUANTITY, newQuantity);
        int rows = db.update(TABLE_ITEMS, values, COL_ITEM_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ITEMS, COL_ITEM_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, null, null);
        db.close();
    }

    public int getItemCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
