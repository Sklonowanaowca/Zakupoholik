package com.example.monia.zakupoholik;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.monia.zakupoholik.data.ListsProductContract;
import com.example.monia.zakupoholik.data.ListsProductsDbHelper;

public class ShopingMode extends AppCompatActivity {
    String nazwaSklepu = "";
    String nazwaListy = "";
    SQLiteDatabase mDb;
    RecyclerView mRecyclerView;
    ProductsAdapterShoppingMode productsAdapterShoppingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoping_mode);

        TextView nazwaL = (TextView) findViewById(R.id.tv_nazwa_listy);
        TextView nazwaS = (TextView) findViewById(R.id.tv_nazwa_sklepu);

        Intent dataFromProductsActivity = getIntent();
        if(dataFromProductsActivity.hasExtra("NAZWA_SKLEPU") && dataFromProductsActivity.hasExtra("NAZWA_LISTY")){
            nazwaSklepu = dataFromProductsActivity.getStringExtra("NAZWA_SKLEPU");
            nazwaListy = dataFromProductsActivity.getStringExtra("NAZWA_LISTY");
            nazwaS.setText(nazwaSklepu);
            nazwaL.setText(nazwaListy);
        }

        ListsProductsDbHelper listsProductsDbHelper = new ListsProductsDbHelper(this);
        mDb = listsProductsDbHelper.getWritableDatabase();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_products_shopping_mode);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();
        productsAdapterShoppingMode = new ProductsAdapterShoppingMode(ShopingMode.this,getAllProductsFromSQLite());
        mRecyclerView.setAdapter(productsAdapterShoppingMode);

    }

    private Cursor getAllProductsFromSQLite() {
        return mDb.query(
                ListsProductContract.ListsEntry.PRODUKT_NAZWA_TABELI,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
