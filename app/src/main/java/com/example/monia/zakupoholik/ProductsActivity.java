package com.example.monia.zakupoholik;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.monia.zakupoholik.data.ListData;
import com.example.monia.zakupoholik.data.ListsProductContract;
import com.example.monia.zakupoholik.data.ListsProductsDbHelper;
import com.example.monia.zakupoholik.data.ProductData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductsActivity extends AppCompatActivity {
    private SQLiteDatabase mDb;
    private RecyclerView mRecyclerView;
    private ProductsAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        int idLista = 0;
        String nazwaListy="";

        TextView mNazwaListyTextView = (TextView) findViewById(R.id.tv_nazwa_listy);

        Intent dataFromListsActivity = getIntent();
        if(dataFromListsActivity.hasExtra("ID") && dataFromListsActivity.hasExtra("NAZWA_LISTY")) {
            idLista = dataFromListsActivity.getIntExtra("ID", 0);
            nazwaListy = dataFromListsActivity.getStringExtra("NAZWA_LISTY");
        }

        mNazwaListyTextView.setText(nazwaListy);

        ListsProductsDbHelper listsProductsDbHelper = new ListsProductsDbHelper(this);
        mDb = listsProductsDbHelper.getWritableDatabase();

        loadProductsFromSerwerToSQLite(idLista);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_product_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductsActivity.this, "fab pressed", Toast.LENGTH_SHORT).show();
            }
        });

        //loadProductsFromSQLite();
    }

    private void loadProductsFromSerwerToSQLite(final int idList){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_produkty.php (json array)
                if(response!=null && response.length()>0){
                    removeAllProductsFromSQLite();
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray produkty = jsonObject.getJSONArray("products");
                        for (int i = 0; i < produkty.length(); i++) {
                            ProductData currentData = new ProductData();
                            JSONObject json_data = produkty.getJSONObject(i);
                            currentData.idProdukt = json_data.getInt("ID_Produkt");
                            currentData.ilosc = json_data.getInt("Ilosc");
                            currentData.cena = json_data.getDouble("Cena");
                            currentData.idLista = json_data.getLong("ID_Lista");
                            currentData.nazwa = json_data.getString("Nazwa");
                            addProductToSQLite(currentData.getIdProdukt(), currentData.getIlosc(), currentData.cena,
                                    currentData.getIdLista(), currentData.getNazwa());
                        }
                        loadProductsFromSQLite();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        FetchProductsRequest fetchProductsRequest = new FetchProductsRequest(idList, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProductsActivity.this);
        queue.add(fetchProductsRequest);
    }

    private void loadProductsFromSQLite(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_products);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();
        productAdapter = new ProductsAdapter(ProductsActivity.this,getAllProductsFromSQLite());
        mRecyclerView.setAdapter(productAdapter);
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

    private boolean removeAllProductsFromSQLite(){
        return mDb.delete(ListsProductContract.ListsEntry.PRODUKT_NAZWA_TABELI,null, null) > 0;
    }

    private long addProductToSQLite(int idProduct, int ilosc, double cena, long idLista, String nazwa) {
        ContentValues cv = new ContentValues();
        cv.put(ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT, idProduct);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_ILOSC, ilosc);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_CENA, cena);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_ID_LISTA, idLista);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_NAZWA, nazwa);
        return mDb.insert(ListsProductContract.ListsEntry.PRODUKT_NAZWA_TABELI, null, cv);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadProductsFromSQLite();
    }
}
