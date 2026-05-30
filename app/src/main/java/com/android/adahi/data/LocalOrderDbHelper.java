package com.android.adahi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LocalOrderDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "adahi_local_orders.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_ORDERS = "orders";

    public LocalOrderDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_ORDERS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id TEXT UNIQUE NOT NULL, " +
                "created_at INTEGER NOT NULL" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No-op for now
    }

    public boolean insertOrder(String orderId, long createdAt) {
        if (orderId == null || orderId.trim().isEmpty()) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("order_id", orderId);
        cv.put("created_at", createdAt);
        long id = db.insertWithOnConflict(TABLE_ORDERS, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        return id != -1;
    }

    public List<OrderRecord> getAllOrders() {
        List<OrderRecord> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ORDERS, new String[]{"order_id", "created_at"}, null, null, null, null, "created_at DESC");
        if (c != null) {
            while (c.moveToNext()) {
                String orderId = c.getString(0);
                long createdAt = c.getLong(1);
                result.add(new OrderRecord(orderId, createdAt));
            }
            c.close();
        }
        return result;
    }

    public static class OrderRecord {
        public final String orderId;
        public final long createdAt;

        public OrderRecord(String orderId, long createdAt) {
            this.orderId = orderId;
            this.createdAt = createdAt;
        }
    }
}
