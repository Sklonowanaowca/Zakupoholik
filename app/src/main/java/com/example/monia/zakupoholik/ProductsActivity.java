package com.example.monia.zakupoholik;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    public String[] allShopsFromMysqlDb;
    int idLista = 0;
    String nazwaListy="";
    String stringListToShare = "";
    private static final int REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

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
        loadAllShopsFromMysqlToArray();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_product_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddProductDialog();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_products);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                showAlertDialogBeforeDeleteProduct(id);
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void showAlertDialogBeforeDeleteProduct(final long id){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_remove_product, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.dialog_remove_product_title);

        dialogBuilder.setPositiveButton(R.string.dialog_remove_list_button, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                long idSerwer = getProductIdFromSerwer(id);
                if(idSerwer!=-1) {
                    deleteProductFromSerwer(idSerwer);
                    removeProductFromSQLite(id);
                    productAdapter.swapCursor(getAllProductsFromSQLite());
                }
                else
                    Toast.makeText(ProductsActivity.this, "ooops id Twojej listy = -1", Toast.LENGTH_LONG).show();
            }
        });
        dialogBuilder.setNegativeButton(R.string.rename_list_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                loadProductsFromSQLite();
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
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
                    getIdProduktowFromMysql(nazwa, Double.parseDouble(ilosc), jednostkaZeSpinnera, idLista);
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

    public void loadProductsFromSerwerToSQLite(final int idList){
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
                            currentData.idSklep = json_data.getInt("ID_Sklep");
                            stringListToShare+=currentData.getNazwa() + " - " + currentData.getIlosc() + " " + currentData.getJednostka() + "\n";
                            addProductToSQLite(currentData.getIdProdukt(), currentData.getIlosc(), currentData.getJednostka(), currentData.getCena(),
                                    currentData.getIdLista(), currentData.getNazwa(), currentData.getIdSklep());
                        }
                        stringListToShare = stringListToShare.substring(0, stringListToShare.length() -1);
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

    public void loadProductsFromSQLite(){
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

    private void loadAllShopsFromMysqlToArray(){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_wszystkie_produkty.php (json array)
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray sklepy = jsonObject.getJSONArray("shops");
                        allShopsFromMysqlDb = new String[sklepy.length()];
                        for (int i = 0; i < sklepy.length(); i++) {
                            JSONObject json_data = sklepy.getJSONObject(i);
                            allShopsFromMysqlDb[i] = json_data.getString("Nazwa");
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        FetchAllShopsRequest fetchAllShopsRequest = new FetchAllShopsRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProductsActivity.this);
        queue.add(fetchAllShopsRequest);
    }

    private void getIdProduktowFromMysql(final String nazwaProduktu, final double ilosc, final String jednostka, final int idLista){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_id_produktow.php (json array)
                int idProductow=0;
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        idProductow = jsonObject.getInt("idProduktow");
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                if(idProductow==0) {
                    Intent startAddNewProductActivity = new Intent(ProductsActivity.this, AddNewProductToMysqlActivity.class);
                    startAddNewProductActivity.putExtra("NAZWA_PRODUKTU", nazwaProduktu);
                    startActivity(startAddNewProductActivity);
                } else {
                    addProduct(ilosc, jednostka, idProductow, idLista);
                    loadProductsFromSerwerToSQLite(idLista);
                }
            }
        };
        FetchIdProductsRequest fetchIdProductsRequest = new FetchIdProductsRequest(nazwaProduktu, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProductsActivity.this);
        queue.add(fetchIdProductsRequest);
    }

    private long getProductIdFromSerwer(long idSQLite){
        String selectQuery = "select " + ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT + " from " + ListsProductContract.ListsEntry.PRODUKT_NAZWA_TABELI
                + " where " + ListsProductContract.ListsEntry._ID + " = " + idSQLite;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        long idSerwer;

        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            idSerwer = cursor.getInt(cursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT));
        } else idSerwer=-1;
        cursor.close();
        return idSerwer;
    }

    private void addProduct(double ilosc, String jednostka, final int idProduktow, final int idLista){
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

    private void deleteProductFromSerwer(long idSerwer){
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {// response from usun_produkt.php (json array)
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success)
                            Toast.makeText(ProductsActivity.this, "Usunieto produkt", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ProductsActivity.this, "Nie udalo sie usunac produktu", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        DeleteProductRequest deleteProductRequest = new DeleteProductRequest(idSerwer, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProductsActivity.this);
        queue.add(deleteProductRequest);
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

    private boolean removeProductFromSQLite(long id){
        return mDb.delete(ListsProductContract.ListsEntry.PRODUKT_NAZWA_TABELI, ListsProductContract.ListsEntry._ID + "=" + id, null) > 0;
    }

    private long addProductToSQLite(int idProduct, double ilosc, String jednostka, double cena, long idLista, String nazwa, int idSklep) {
        ContentValues cv = new ContentValues();
        cv.put(ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT, idProduct);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_ILOSC, ilosc);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_JEDNOSTKA, jednostka);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_CENA, cena);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_ID_LISTA, idLista);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_NAZWA, nazwa);
        cv.put(ListsProductContract.ListsEntry.PRODUKT_ID_SKLEP, idSklep);
        return mDb.insert(ListsProductContract.ListsEntry.PRODUKT_NAZWA_TABELI, null, cv);
    }

    private void showChooseShopDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_choose_shop, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.choose_shop_title);

        final AutoCompleteTextView nazwaSklepu = (AutoCompleteTextView) dialogView.findViewById(R.id.atv_shop_name);

        ArrayAdapter<String> productAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, allShopsFromMysqlDb);
        productAdapter.setDropDownViewResource(R.layout.spinner_item);
        nazwaSklepu.setThreshold(1);//min liczba znakow aby zlapalo podpowiedzi
        nazwaSklepu.setAdapter(productAdapter);

        dialogBuilder.setPositiveButton(R.string.choose_shop_ok_button, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                String nazwa = nazwaSklepu.getText().toString().trim();
                if (!nazwa.isEmpty()) {
                    Intent startShoppingMode = new Intent(ProductsActivity.this, ShopingMode.class);
                    startShoppingMode.putExtra("NAZWA_SKLEPU", nazwa);
                    startShoppingMode.putExtra("NAZWA_LISTY", nazwaListy);
                    startActivityForResult(startShoppingMode,REQUEST);
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

    private void shareList(){
        Intent sendWeather = new Intent();
        ShareCompat.IntentBuilder.from(this).setType("text/plain").setChooserTitle("Udostępnij listę").setText(stringListToShare).startChooser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST:
                loadProductsFromSerwerToSQLite(idLista);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_shopping_mode:
                showChooseShopDialog();
                return true;
            case R.id.menu_share:
                shareList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadProductsFromSQLite();
        loadAllProductsFromMysqlToArray();
    }
}
