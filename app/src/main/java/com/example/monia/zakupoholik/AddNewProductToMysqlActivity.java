package com.example.monia.zakupoholik;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddNewProductToMysqlActivity extends AppCompatActivity {
    private String nazwaProduktu = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product_to_mysql);

        TextView mNazwaProduktu = (TextView) findViewById(R.id.tv_add_new_product_name);
        GridView mGridView = (GridView) findViewById(R.id.grid_view);

        Intent dataFromProductActivity = getIntent();
        if(dataFromProductActivity.hasExtra("NAZWA_PRODUKTU")) {
            nazwaProduktu =  dataFromProductActivity.getStringExtra("NAZWA_PRODUKTU");
            mNazwaProduktu.setText(nazwaProduktu);
        }

        final List<CategoryObject> allCategories = getAllItemObject();

        CustomAdapter customAdapter = new CustomAdapter(AddNewProductToMysqlActivity.this, allCategories);
        mGridView.setAdapter(customAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addNewProductToMysql(nazwaProduktu, allCategories.get(position).getContent());
            }
        });
    }

    private void addNewProductToMysql(String nazwaProduktu, final String nazwaKategorii){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from dodaj_nowy_produkt.php (json array)
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    boolean success = jsonObj.getBoolean("success");
                    String message="";
                    if(success) {
                        message = jsonObj.getString("message");
                        Toast.makeText(AddNewProductToMysqlActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        message = jsonObj.getString("message");
                        Toast.makeText(AddNewProductToMysqlActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        AddNewProductToMysqlRequest addNewProductToMysqlRequest = new AddNewProductToMysqlRequest(nazwaProduktu, nazwaKategorii, responseListener);
        RequestQueue queue = Volley.newRequestQueue(AddNewProductToMysqlActivity.this);
        queue.add(addNewProductToMysqlRequest);
    }

    private List<CategoryObject> getAllItemObject(){
        CategoryObject categoryObject = null;
        List<CategoryObject> items = new ArrayList<>();
        items.add(new CategoryObject("Napoje"));
        items.add(new CategoryObject("Owoce"));
        items.add(new CategoryObject("Warzywa"));
        items.add(new CategoryObject("Nabiał"));
        items.add(new CategoryObject("Pieczywo"));
        items.add(new CategoryObject("Słodycze"));
        items.add(new CategoryObject("Mięso"));
        items.add(new CategoryObject("Narzędzia"));
        items.add(new CategoryObject("AGD"));
        items.add(new CategoryObject("Elektronika"));
        items.add(new CategoryObject("Odzież"));
        items.add(new CategoryObject("Dom i ogród"));
        items.add(new CategoryObject("Dziecko"));
        items.add(new CategoryObject("Uroda i zdrowie"));
        items.add(new CategoryObject("Kultura i rozrywka"));
        items.add(new CategoryObject("Sport i wypoczynek"));
        items.add(new CategoryObject("Motoryzacja"));
        items.add(new CategoryObject("Kolekcje i sztuka"));
        items.add(new CategoryObject("Firma"));
        items.add(new CategoryObject("Inne"));
        return items;
    }

//    private void getIdKategoriiFromMysql(final String nazwaKategorii, final String nazwaProduktu){
//        Response.Listener<String> responseListener = new Response.Listener<String>(){
//            @Override
//            public void onResponse(String response) {// response from pokaz_id_produktow.php (json array)
//                int idKategorii = 0;
//                if(response!=null && response.length()>0){
//                    try{
//                        JSONObject jsonObject = new JSONObject(response);
//                        idKategorii = jsonObject.getInt("idKategorii");
//                    } catch (JSONException e){
//                        e.printStackTrace();
//                    }
//                }
//                addNewProductToMysql(nazwaProduktu, idKategorii);
//            }
//        };
//        FetchIdCategoryRequest fetchIdCategoryRequest = new FetchIdCategoryRequest(nazwaKategorii, responseListener);
//        RequestQueue queue = Volley.newRequestQueue(AddNewProductToMysqlActivity.this);
//        queue.add(fetchIdCategoryRequest);
//    }
}
