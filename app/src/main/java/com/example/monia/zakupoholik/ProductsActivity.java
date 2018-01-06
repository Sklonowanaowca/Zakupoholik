package com.example.monia.zakupoholik;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.monia.zakupoholik.data.ListsProductContract;
import com.example.monia.zakupoholik.data.ListsProductsDbHelper;
import com.example.monia.zakupoholik.data.ProductData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private SQLiteDatabase mDb;
    private RecyclerView mRecyclerView;
    private ProductsAdapter productAdapter;
    public String[] allProductsFromMysqlDb;
    int idLista = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

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
        loadAllProductsFromMysqlToArray();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_product_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddProductDialog();
            }
        });

        //loadProductsFromSQLite();
    }

    private void showAddProductDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        dialogBuilder.setView(dialogView);

        final AutoCompleteTextView nazwaProduktu = (AutoCompleteTextView) dialogView.findViewById(R.id.atv_nazwa_produktu);
        final EditText iloscProduktu = (EditText) dialogView.findViewById(R.id.et_ilosc_produktu);
        final Spinner spinnerJednostki = (Spinner) dialogView.findViewById(R.id.spinner_jednostki);

        final List<String> list = new ArrayList<String>();
        ArrayAdapter<String> productAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, allProductsFromMysqlDb);
        productAdapter.setDropDownViewResource(R.layout.spinner_item);
        nazwaProduktu.setThreshold(1);//min liczba znakow aby zlapalo podpowiedzi
        nazwaProduktu.setAdapter(productAdapter);

        String[] unitsArray = getResources().getStringArray(R.array.units);
        ArrayAdapter<String> unitsAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,R.id.atv_spinner_item,unitsArray);
        spinnerJednostki.setAdapter(unitsAdapter);

        dialogBuilder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                String nazwa = nazwaProduktu.getText().toString().trim();
                String ilosc = iloscProduktu.getText().toString().trim();
                if (!nazwa.isEmpty()) {
                    String jednostkaZeSpinnera = spinnerJednostki.getSelectedItem().toString();
                    //Toast.makeText(ProductsActivity.this, "ilosc " + Integer.parseInt(ilosc) + ", jednostka " + jednostkaZeSpinnera, Toast.LENGTH_SHORT).show();
                    getIdProduktowFromMysql(nazwa, Integer.parseInt(ilosc), jednostkaZeSpinnera, idLista);
                }
            }
        });
        dialogBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
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
                            currentData.ilosc = json_data.getDouble("Ilosc");
                            currentData.jednostka = json_data.getString("Jednostka");
                            currentData.cena = json_data.getDouble("Cena");
                            currentData.idLista = json_data.getLong("ID_Lista");
                            currentData.nazwa = json_data.getString("Nazwa");
                            addProductToSQLite(currentData.getIdProdukt(), currentData.getIlosc(), currentData.getJednostka(), currentData.cena,
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

    private void loadAllProductsFromMysqlToArray(){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_wszystkie_produkty.php (json array)
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray produkty = jsonObject.getJSONArray("products");
                        allProductsFromMysqlDb = new String[produkty.length()];
                        for (int i = 0; i < produkty.length(); i++) {
                            JSONObject json_data = produkty.getJSONObject(i);
                            allProductsFromMysqlDb[i] = json_data.getString("Nazwa");
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        FetchAllProductsRequest fetchAllProductsRequest = new FetchAllProductsRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProductsActivity.this);
        queue.add(fetchAllProductsRequest);
    }

    private void getIdProduktowFromMysql(String nazwaProduktu, final double ilosc, final String jednostka, final int idLista){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_id_produktow.php (json array)
                int idProduct = 0;
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        idProduct = jsonObject.getInt("idProduktow");
                        //Toast.makeText(ProductsActivity.this, "id " + idProduct, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                addProduct(ilosc,jednostka,idProduct,idLista);
            }
        };
        FetchIdProductsRequest fetchIdProductsRequest = new FetchIdProductsRequest(nazwaProduktu, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProductsActivity.this);
        queue.add(fetchIdProductsRequest);
    }

    private void addProduct(double ilosc, String jednostka, int idProduktow, final int idLista){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    boolean success = jsonObj.getBoolean("success");
                    String message="";
                    if(success) {
                        message = jsonObj.getString("message");
                        Toast.makeText(ProductsActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadProductsFromSerwerToSQLite(idLista);
                    } else {
                        message = jsonObj.getString("message");
                        Toast.makeText(ProductsActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        AddProductRequest addProductRequest = new AddProductRequest(ilosc, jednostka, idProduktow, idLista, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProductsActivity.this);
        queue.add(addProductRequest);
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

    private long addProductToSQLite(int idProduct, double ilosc, String jednostka, double cena, long idLista, String nazwa) {
        ContentValues cv = new ContentValues();
        cv.put(ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT, idProduct);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_ILOSC, ilosc);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_JEDNOSTKA, jednostka);
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
