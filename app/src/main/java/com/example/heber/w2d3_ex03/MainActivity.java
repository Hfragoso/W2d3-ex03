package com.example.heber.w2d3_ex03;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heber.w2d3_ex03.FeedReaderContract.FeedEntry;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName() + "_TAG";
    private DBHelper helper;
    private SQLiteDatabase database;

    private EditText titleET;
    private EditText subtitleET;
    private  EditText newTitleET;
    private TextView showRecordsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper(this);
        database = helper.getWritableDatabase();

        titleET = (EditText) findViewById(R.id.et_title);
        subtitleET = (EditText) findViewById(R.id.et_subtitle);
        newTitleET = (EditText) findViewById(R.id.et_new_title);
        showRecordsTV = (TextView) findViewById(R.id.tv_show_records);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    private void saveRecord(String title, String subtitle){
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        long recordId = database.insert(
                FeedEntry.TABLE_NAME,
                null,
                values
        );
        if(recordId > 0){
            Toast.makeText(this, "SaveRecord: Record saved.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SaveRecord: Record saved.");
        }else{
            Toast.makeText(this, "saveRecord: Record not saved", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"saveRecord: Record not saved");
        }
    }

    private void readRecord(){
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };
        String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArg = {
                "Record title"
        };

        String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE +"DESC";

        Cursor cursor = database.query(
                FeedEntry.TABLE_NAME,   //TABLE
                projection,             //projection
                null,                   //Selection (WHERE)
                null,                   //Values for selection
                null,                   //Group by
                null,                   //Filters
                null                    //Sort order
        );
        while(cursor.moveToNext()){
            long entryId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
            String entryTile = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            String entrySubltitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE));
            Log.d(TAG, "readRecord: id: " + entryId + " title: " + entryTile + " subtitle: " + entrySubltitle);

//            showRecordsTV.append("id: " + entryId + " title: " + entryTile + " subtitle: " + entrySubltitle + "\n");


            //////STRING BUILDER
            //FASTER THAN USING STRINGS
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("id: ");
            sBuilder.append(entryId);
            sBuilder.append(" title: ");
            sBuilder.append(entryTile);
            sBuilder.append(" subtitle: ");
            sBuilder.append(entrySubltitle);
            sBuilder.append("\n");

            showRecordsTV.append(sBuilder);
        }
    }

    private void deleteRecord(String deletingTitle){
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = {
                deletingTitle
        };

        int deleted = database.delete(
                FeedEntry.TABLE_NAME,
                selection,
                selectionArgs
        );

        if(deleted > 0){
            Log.d(TAG, "deleteRecord: record deleted");
            Toast.makeText(this, "Title:" + deletingTitle + " has been deleted", Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "deleteRecord: record not deleted");
            Toast.makeText(this, "deleteRecord: record not deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRecord(String currentTitle, String newTitle){
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, newTitle);
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = {
                currentTitle
        };

        int count = database.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        if(count > 0){
            Log.d(TAG, "updateRecord: Updated records " + count);
            Toast.makeText(this, "updateRecord: Updated records " + count, Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "updateRecord: Records not updated");
            Toast.makeText(this, "updateRecord: Records not updated", Toast.LENGTH_SHORT).show();
        }
    }
    public void saveRecord(View view) {
        String title = titleET.getText().toString();
        String subtitle = subtitleET.getText().toString();
        saveRecord(title, subtitle);
        titleET.setText("");
        subtitleET.setText("");
    }

    public void readRecord(View view) {
        showRecordsTV.setText("Read: \n");
        readRecord();
    }

    public void updateRecord(View view) {
        String currentTitle = titleET.getText().toString();
        String newTitle = newTitleET.getText().toString();
        updateRecord(currentTitle, newTitle);
        titleET.setText("");
        subtitleET.setText("");
        newTitleET.setText("");
    }

    public void deleteRecord(View view) {
        String deletingTitle = titleET.getText().toString();
        deleteRecord(deletingTitle);
    }
}
