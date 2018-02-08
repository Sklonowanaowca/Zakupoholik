package com.example.monia.zakupoholik;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.monia.zakupoholik.data.ListData;
import com.example.monia.zakupoholik.data.ListsProductContract;
import com.example.monia.zakupoholik.data.ListsProductsDbHelper;
import com.example.monia.zakupoholik.data.ShopData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ListsActivity extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private TextView mNoListsMessage;
    private ListsAdapter mListsAdapter;
    private static int idUserPublic;
    private SQLiteDatabase mDb;
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        mNoListsMessage = (TextView) findViewById(R.id.no_lists_message);

        String imie="";
        Intent dataFromLoginActivity = getIntent();
        if(dataFromLoginActivity.hasExtra(LoginActivity.KEY_ID_UZYTKOWNIKA) && dataFromLoginActivity.hasExtra(LoginActivity.KEY_IMIE)){
            idUserPublic = dataFromLoginActivity.getIntExtra(LoginActivity.KEY_ID_UZYTKOWNIKA, 0);
            imie = dataFromLoginActivity.getStringExtra(LoginActivity.KEY_IMIE);
        }

        if(idUserPublic!=0 && !imie.equals("")) {
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LoginActivity.KEY_IMIE, imie);
            editor.putInt(LoginActivity.KEY_ID_UZYTKOWNIKA, idUserPublic);
            editor.apply();
        }

        //idUserPublic = sharedPreferences.getInt(LoginActivity.KEY_ID_UZYTKOWNIKA,0);

        ListsProductsDbHelper listsProductsDbHelper = new ListsProductsDbHelper(this);
        mDb = listsProductsDbHelper.getWritableDatabase();

        loadListsFromSerwerToSQLite(idUserPublic);
        loadShopsSignatures();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        loadListsFromSqlite();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                showAlertDialogBeforeDeleteList(id);
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void showAlertDialogBeforeDeleteList(final long id){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_remove_list, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.dialog_remove_list_title);

        dialogBuilder.setPositiveButton(R.string.dialog_remove_list_button, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                long idSerwer = getListIdFromSerwer(id);
                if(idSerwer!=-1) {
                    deleteListFromSerwer(idSerwer);
                    removeListFromSQLite(id);
                    mListsAdapter.swapCursor(getListsFromSQLite());
                }
                else
                    Toast.makeText(ListsActivity.this, "ooops id Twojej listy = -1", Toast.LENGTH_LONG).show();
            }
        });
        dialogBuilder.setNegativeButton(R.string.rename_list_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                loadListsFromSqlite();
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private long getListIdFromSerwer(long idSQLite){
        String selectQuery = "select " + ListsProductContract.ListsEntry.ID_LISTA + " from " + ListsProductContract.ListsEntry.NAZWA_TABELI
                + " where " + ListsProductContract.ListsEntry._ID + " = " + idSQLite;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        long idSerwer;

        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            idSerwer = cursor.getInt(cursor.getColumnIndex(ListsProductContract.ListsEntry.ID_LISTA));
        } else idSerwer=-1;
        cursor.close();
        return idSerwer;
    }

    public void showAlertDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_list, null);
        dialogBuilder.setView(dialogView);

        final EditText nazwa = (EditText) dialogView.findViewById(R.id.add_list_name);
        final TextView data = (TextView) dialogView.findViewById(R.id.add_list_data);
        Button wybierzDate = (Button) dialogView.findViewById(R.id.button_choose_date);

        wybierzDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ListsActivity.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                        monthOfYear+=1;
                        data.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        dialogBuilder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                String nazwaListy = nazwa.getText().toString().trim();
                String dataZakupow = data.getText().toString().trim();
                if (!nazwaListy.isEmpty() && !dataZakupow.isEmpty()) {
                    addList(nazwaListy, dataZakupow, getApplicationContext());
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

    public void loadListsFromSerwerToSQLite(Integer id_user){
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
                if(response!=null && response.length()>0){
                    removeAllListFromSQLite();
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray listy = jsonObject.getJSONArray("lists");
                            for (int i = 0; i < listy.length(); i++) {
                                ListData currentData = new ListData();
                                JSONObject json_data = listy.getJSONObject(i);
                                currentData.idLista = json_data.getInt("ID_Lista");
                                currentData.nazwaListy = json_data.getString("Nazwa_listy");
                                currentData.dataZakupow = json_data.getString("Data_zakupow");
                                currentData.kosztZakupow = json_data.getDouble("Koszt_zakupow");
                                currentData.idUzytkownika = json_data.getInt("ID_Uzytkownika");
                                addListToSQLite(json_data.getInt("ID_Lista"), json_data.getString("Nazwa_listy"),
                                        json_data.getString("Data_zakupow"), json_data.getDouble("Koszt_zakupow"),
                                        json_data.getInt("ID_Uzytkownika"));
                            }
                        loadListsFromSqlite();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        FetchListsRequest fetchListsRequest = new FetchListsRequest(id_user, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ListsActivity.this);
        queue.add(fetchListsRequest);
    }

    public void loadListsFromSqlite(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_lists);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);//zawartosc nie zmeni rozmiaru widoku
        mListsAdapter = new ListsAdapter(this,getListsFromSQLite());
        mRecyclerView.setAdapter(mListsAdapter);
    }

    public void addList(String nazwaListy, String dataZakupow, final Context context){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    boolean success = jsonObj.getBoolean("success");
                    String message="";
                    if(success) {
                        message = jsonObj.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        message = jsonObj.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                loadListsFromSerwerToSQLite(idUserPublic);
            }
        };
        AddListsRequest addListsRequest = new AddListsRequest(nazwaListy, dataZakupow, idUserPublic, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(addListsRequest);
    }

    private long addListToSQLite(int idLista, String nazwaListy, String dataZakupow, double kosztZakupow, int idUzytkownika) {
        ContentValues cv = new ContentValues();
        cv.put(ListsProductContract.ListsEntry.ID_LISTA, idLista);
        cv.put(ListsProductContract.ListsEntry.NAZWA_LISTY, nazwaListy);
        cv.put(ListsProductContract.ListsEntry.DATA_ZAKUPOW, dataZakupow);
        cv.put(ListsProductContract.ListsEntry.KOSZT_ZAKUPOW, kosztZakupow);
        cv.put(ListsProductContract.ListsEntry.ID_UZYTKOWNIKA, idUzytkownika);
        return mDb.insert(ListsProductContract.ListsEntry.NAZWA_TABELI, null, cv);
    }

    private long addShopToSQLite(int idSklep, String sygnatura) {
        ContentValues cv = new ContentValues();
        cv.put(ListsProductContract.ListsEntry.SKLEP_ID_SKLEP, idSklep);
        cv.put(ListsProductContract.ListsEntry.SYGNATURA, sygnatura);
        return mDb.insert(ListsProductContract.ListsEntry.SKLEP_NAZWA_TABELI, null, cv);
    }

    public void deleteListFromSerwer(long id_list){
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {// response from usun_liste.php (json array)
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success)
                            Toast.makeText(ListsActivity.this, "Usunieto liste", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ListsActivity.this, "Nie udalo sie usunac listy", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        DeleteListRequest deleteListRequest = new DeleteListRequest(id_list, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ListsActivity.this);
        queue.add(deleteListRequest);
    }

    private boolean removeAllListFromSQLite(){
        return mDb.delete(ListsProductContract.ListsEntry.NAZWA_TABELI,null, null) > 0;
    }

    private boolean removeAllSignaturesFromSQLite(){
        return mDb.delete(ListsProductContract.ListsEntry.SKLEP_NAZWA_TABELI,null, null) > 0;
    }

    private Cursor getListsFromSQLite() {
        return mDb.query(
                ListsProductContract.ListsEntry.NAZWA_TABELI,
                null,
                null,
                null,
                null,
                null,
                ListsProductContract.ListsEntry.ID_LISTA + " DESC",
                null
        );
    }

    private boolean removeListFromSQLite(long id){
        return mDb.delete(ListsProductContract.ListsEntry.NAZWA_TABELI, ListsProductContract.ListsEntry._ID + "=" + id, null) > 0;
    }

    private void showNoListsMessage(){
        mNoListsMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void loadShopsSignatures(){
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
                if(response!=null && response.length()>0){
                    removeAllSignaturesFromSQLite();
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray sklepy = jsonObject.getJSONArray("shops");
                        for (int i = 0; i < sklepy.length(); i++) {
                            ShopData currentData = new ShopData();
                            JSONObject json_data = sklepy.getJSONObject(i);
                            currentData.idSklep = json_data.getInt("ID_Sklep");
                            currentData.sygnatura = json_data.getString("Sygnatura");
                            addShopToSQLite(currentData.getIdSklep(), currentData.getSygnatura());
                        }
                        loadListsFromSqlite();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        FetchShopsSignaturesRequest fetchShopsSignaturesRequest = new FetchShopsSignaturesRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(ListsActivity.this);
        queue.add(fetchShopsSignaturesRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lists_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_map:
                Intent startMapActivity = new Intent(ListsActivity.this, MapActivity.class);
                startActivity(startMapActivity);
                return true;
            case R.id.action_logout:
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(LoginActivity.KEY_ID_UZYTKOWNIKA, 0);
                editor.apply();
                Intent startLoginActivity = new Intent(ListsActivity.this, LoginActivity.class);
                startActivity(startLoginActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadListsFromSqlite();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
