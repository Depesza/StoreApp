package com.example.hania.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hania.inventoryapp.data.StoreContract.StoreEntry;

public class StoreDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;
    public StoreDbHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_THINGS_TABLE = "CREATE TABLE " + StoreEntry.TABLE_NAME + " ("
                + StoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StoreEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + StoreEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + StoreEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + StoreEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + StoreEntry.COLUMN_SUPPLIER_PHONE + " INTEGER); ";

        db.execSQL(SQL_CREATE_THINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
