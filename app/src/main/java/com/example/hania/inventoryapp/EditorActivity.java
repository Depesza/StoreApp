package com.example.hania.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hania.inventoryapp.data.StoreContract.StoreEntry;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;
    private Uri mCurrentItemUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupNameEditText;
    private EditText mSupPhoneEditText;
    private boolean mItemHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));

            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mSupNameEditText = (EditText) findViewById(R.id.edit_sup_name);
        mSupPhoneEditText = (EditText) findViewById(R.id.edit_sup_phone);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupNameEditText.setOnTouchListener(mTouchListener);
        mSupPhoneEditText.setOnTouchListener(mTouchListener);

        Button minusButton = (Button) findViewById(R.id.minus_button);
        Button plusButton = (Button) findViewById(R.id.plus_button);

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantityEditText.getText().toString().trim())) {
                    int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    if (quantity >= 1) {
                        mQuantityEditText.setText(String.valueOf(quantity - 1));
                    }
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantityEditText.getText().toString().trim())) {
                    int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    mQuantityEditText.setText(String.valueOf(quantity + 1));
                }
            }
        });


    }

    private void saveItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supNameString = mSupNameEditText.getText().toString().trim();
        String supPhoneString = mSupPhoneEditText.getText().toString().trim();


        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supNameString) ||
                TextUtils.isEmpty(supPhoneString)) {
            Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
        } else {

            ContentValues values = new ContentValues();
            values.put(StoreEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(StoreEntry.COLUMN_PRICE, priceString);
            values.put(StoreEntry.COLUMN_QUANTITY, quantityString);
            values.put(StoreEntry.COLUMN_SUPPLIER_NAME, supNameString);
            values.put(StoreEntry.COLUMN_SUPPLIER_PHONE, supPhoneString);


            if (mCurrentItemUri == null) {
                Uri newUri = getContentResolver().insert(StoreEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_item_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_item_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_call:
                mSupPhoneEditText = (EditText) findViewById(R.id.edit_sup_phone);
                String phoneNumber = mSupPhoneEditText.getText().toString();
                callButton(phoneNumber);
                return true;

            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                StoreEntry._ID,
                StoreEntry.COLUMN_PRODUCT_NAME,
                StoreEntry.COLUMN_PRICE,
                StoreEntry.COLUMN_QUANTITY,
                StoreEntry.COLUMN_SUPPLIER_NAME,
                StoreEntry.COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_QUANTITY);
            int supNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_NAME);
            int supPhoneColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supName = cursor.getString(supNameColumnIndex);
            int supPhone = cursor.getInt(supPhoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupNameEditText.setText(supName);
            mSupPhoneEditText.setText(Integer.toString(supPhone));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupNameEditText.setText("");
        mSupPhoneEditText.setText("");

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void deleteItem() {

        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();

    }

    private void callButton(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }
}
