package com.example.monia.zakupoholik.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.monia.zakupoholik.data.ListsContract;

/**
 * Created by Monia on 2017-12-20.
 */

public class ListsDbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "zakupoholik.db";
    static final int DATABASE_VERSION = 1;

    public ListsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_LISTS_TABLE = "CREATE TABLE " + ListsContract.ListsEntry.NAZWA_TABELI + "("
                + ListsContract.ListsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ListsContract.ListsEntry.ID_LISTA + " LONG NOT NULL, "
                + ListsContract.ListsEntry.NAZWA_LISTY + " TEXT NOT NULL, "
                + ListsContract.ListsEntry.DATA_ZAKUPOW + " DATE NOT NULL, "
                //+ ListsContract.ListsEntry.KOSZT_ZAKUPOW + " DOUBLE);";
                + ListsContract.ListsEntry.ID_UZYTKOWNIKA + " LONG NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_LISTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ListsContract.ListsEntry.NAZWA_TABELI);
        onCreate(sqLiteDatabase);
    }
}
