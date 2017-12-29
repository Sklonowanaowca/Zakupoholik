package com.example.monia.zakupoholik;

import android.app.AlertDialog;
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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.monia.zakupoholik.data.ListData;
import com.example.monia.zakupoholik.data.ListsContract;
import com.example.monia.zakupoholik.data.ListsDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private TextView mErrorMessage;
    private ListsAdapter mListsAdapter;
    private static int idUserPublic;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        mErrorMessage = (TextView) findViewById(R.id.error_message);

        Intent dataFromLoginActivity = getIntent();
        idUserPublic = dataFromLoginActivity.getIntExtra("ID", 0);
        String imie = dataFromLoginActivity.getStringExtra("IMIE");

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LoginActivity.KEY_IMIE, imie);
        editor.putInt(LoginActivity.KEY_ID_UZYTKOWNIKA, idUserPublic);
        editor.apply();

        //idUserPublic = sharedPreferences.getInt(LoginActivity.KEY_ID_UZYTKOWNIKA,0);

        ListsDbHelper listsDbHelper = new ListsDbHelper(this);
        mDb = listsDbHelper.getWritableDatabase();

        loadListsFromSerwerToSQLite(idUserPublic);

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
                long idSerwer = getListIdFromSerwer(id);
                if(idSerwer!=-1) {
                    deleteListFromSerwer(idSerwer);
                    Toast.makeText(ListsActivity.this, "Usunieto liste", Toast.LENGTH_SHORT).show();
                    removeListFromSQLite(id);
                    mListsAdapter.swapCursor(getListsFromSQLite());
                }
                else
                    Toast.makeText(ListsActivity.this, "ooops id Twojej listy = -1", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private long getListIdFromSerwer(long idSQlite){
        String selectQuery = "select " + ListsContract.ListsEntry.ID_LISTA + " from " + ListsContract.ListsEntry.NAZWA_TABELI
                + " where " + ListsContract.ListsEntry._ID + " = " + idSQlite;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        long idSerwer;

        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            idSerwer = cursor.getInt(cursor.getColumnIndex(ListsContract.ListsEntry.ID_LISTA));
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
        final EditText data = (EditText) dialogView.findViewById(R.id.add_list_data);

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
                        for(int i=0; i<listy.length(); i++){
                            ListData currentData = new ListData();
                            JSONObject json_data = listy.getJSONObject(i);
                            currentData.idLista = json_data.getInt("ID_Lista");
                            currentData.nazwaListy = json_data.getString("Nazwa_listy");
                            currentData.dataZakupow = json_data.getString("Data_zakupow");
                            currentData.idUzytkownika = json_data.getInt("ID_Uzytkownika");
                            addListToSQLite(json_data.getInt("ID_Lista"), json_data.getString("Nazwa_listy"),
                                    json_data.getString("Data_zakupow"), json_data.getInt("ID_Uzytkownika"));
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

    private void loadListsFromSqlite(){
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
                        loadListsFromSerwerToSQLite(idUserPublic);
                        loadListsFromSqlite();
                        Toast.makeText(context, "Dodano liste", Toast.LENGTH_SHORT).show();
                    } else {
                        message = jsonObj.getString("message");
                        Toast.makeText(context, "Nie udało sie dodac listy", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        AddListsRequest fetchListsRequest = new AddListsRequest(nazwaListy, dataZakupow, idUserPublic, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(fetchListsRequest);
    }

    private long addListToSQLite(int idLista, String nazwaListy, String dataZakupow, int idUzytkownika) {
        ContentValues cv = new ContentValues();
        cv.put(ListsContract.ListsEntry.ID_LISTA, idLista);
        cv.put(ListsContract.ListsEntry.NAZWA_LISTY, nazwaListy);
        cv.put(ListsContract.ListsEntry.DATA_ZAKUPOW, dataZakupow);
        cv.put(ListsContract.ListsEntry.ID_UZYTKOWNIKA, idUzytkownika);
        return mDb.insert(ListsContract.ListsEntry.NAZWA_TABELI, null, cv);
    }

    public void deleteListFromSerwer(long id_list){
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
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
        return mDb.delete(ListsContract.ListsEntry.NAZWA_TABELI,null, null) > 0;
    }

    private Cursor getListsFromSQLite() {
        return mDb.query(
                ListsContract.ListsEntry.NAZWA_TABELI,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private boolean removeListFromSQLite(long id){
        return mDb.delete(ListsContract.ListsEntry.NAZWA_TABELI, ListsContract.ListsEntry._ID + "=" + id, null) > 0;
    }



    private void showLists(){
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mErrorMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onRestart() {
       loadListsFromSqlite();
    }
}
