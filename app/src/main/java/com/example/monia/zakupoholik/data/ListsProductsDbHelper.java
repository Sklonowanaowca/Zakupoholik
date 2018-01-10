package com.example.monia.zakupoholik.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Monia on 2017-12-20.
 */

public class ListsProductsDbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "zakupoholik.db";
    static final int DATABASE_VERSION = 1;

    public ListsProductsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_LISTS_TABLE = "CREATE TABLE " + ListsProductContract.ListsEntry.NAZWA_TABELI + "("
                + ListsProductContract.ListsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ListsProductContract.ListsEntry.ID_LISTA + " LONG NOT NULL, "
                + ListsProductContract.ListsEntry.NAZWA_LISTY + " TEXT NOT NULL, "
                + ListsProductContract.ListsEntry.DATA_ZAKUPOW + " DATE NOT NULL, "
                //+ ListsProductContract.ListsEntry.KOSZT_ZAKUPOW + " DOUBLE);";
                + ListsProductContract.ListsEntry.ID_UZYTKOWNIKA + " LONG NOT NULL);";
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ListsProductContract.ListsEntry.PRODUKT_NAZWA_TABELI + "("
                + ListsProductContract.ListsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT + " LONG NOT NULL, "
                + ListsProductContract.ListsEntry.PRODUKT_ILOSC + " FLOAT NOT NULL, "
                + ListsProductContract.ListsEntry.PRODUKT_JEDNOSTKA + " TEXT NOT NULL, "
                + ListsProductContract.ListsEntry.PRODUKT_CENA + " DOUBLE, "
                + ListsProductContract.ListsEntry.PRODUKT_ID_LISTA + " LONG NOT NULL, "
                + ListsProductContract.ListsEntry.PRODUKT_NAZWA + " TEXT NOT NULL, "
                + ListsProductContract.ListsEntry.PRODUKT_ID_SKLEP + " INTEGER);";
        String SQL_CREATE_SHOPS_TABLE = "CREATE TABLE " + ListsProductContract.ListsEntry.SKLEP_NAZWA_TABELI + "("
                + ListsProductContract.ListsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ListsProductContract.ListsEntry.SKLEP_ID_SKLEP + " INTEGER NOT NULL, "
                + ListsProductContract.ListsEntry.SYGNATURA + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_LISTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SHOPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ListsProductContract.ListsEntry.NAZWA_TABELI);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ListsProductContract.ListsEntry.PRODUKT_NAZWA_TABELI);
        onCreate(sqLiteDatabase);
    }
}
