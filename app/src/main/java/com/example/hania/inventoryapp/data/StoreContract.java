package com.example.hania.inventoryapp.data;

import android.provider.BaseColumns;

public class StoreContract {
    private StoreContract() {}

    public static final class StoreEntry implements BaseColumns {
        public final static String TABLE_NAME = "things";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "sup_name";
        public final static String COLUMN_SUPPLIER_PHONE = "sup_phone";

    }
}
