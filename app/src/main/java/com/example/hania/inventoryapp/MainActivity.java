package com.example.hania.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.database.Cursor;


import com.example.hania.inventoryapp.data.StoreContract.StoreEntry;
import com.example.hania.inventoryapp.data.StoreDbHelper;

public class MainActivity extends AppCompatActivity {

    private StoreDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new StoreDbHelper(this);

        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        insertThing();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                StoreEntry._ID,
                StoreEntry.COLUMN_PRODUCT_NAME,
                StoreEntry.COLUMN_PRICE,
                StoreEntry.COLUMN_QUANTITY,
                StoreEntry.COLUMN_SUPPLIER_NAME,
                StoreEntry.COLUMN_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(
                StoreEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {
            String header = StoreEntry._ID + " - " +
                    StoreEntry.COLUMN_PRODUCT_NAME + " - " +
                    StoreEntry.COLUMN_PRICE + " - " +
                    StoreEntry.COLUMN_QUANTITY + " - " +
                    StoreEntry.COLUMN_SUPPLIER_NAME + " - " +
                    StoreEntry.COLUMN_SUPPLIER_PHONE;

            int idColumnIndex = cursor.getColumnIndex(StoreEntry._ID);
            int prodNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_QUANTITY);
            int supNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_NAME);
            int supPhoneColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_PHONE);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(prodNameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuant = cursor.getInt(quantityColumnIndex);
                String currentSupName = cursor.getString(supNameColumnIndex);
                int currentSupPhone = cursor.getInt(supPhoneColumnIndex);

                String record = currentID + " - " + currentName + " - " +
                        currentPrice + " - " + currentQuant + " - " +
                        currentSupName + " - " + currentSupPhone;

                Log.v("Main", record);
            }
        }
        finally {
            cursor.close();
        }
    }

    private void insertThing() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_PRODUCT_NAME, "Table");
        values.put(StoreEntry.COLUMN_PRICE, 100);
        values.put(StoreEntry.COLUMN_QUANTITY, 10);
        values.put(StoreEntry.COLUMN_SUPPLIER_NAME, "Big Company");
        values.put(StoreEntry.COLUMN_SUPPLIER_PHONE, 321123321);

        long newRowId = db.insert(StoreEntry.TABLE_NAME, null, values);
        Log.v("Main", String.valueOf(newRowId));
    }
}
