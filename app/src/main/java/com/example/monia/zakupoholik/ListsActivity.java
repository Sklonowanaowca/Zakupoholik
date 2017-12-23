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

public class ListsActivity extends AppCompatActivity implements ListsAdapter.ListsAdapterOnClickHandler{
    private RecyclerView mRecyclerView;
    private TextView mErrorMessage;
    private ListsAdapter mListsAdapter;
    private static int idUserPublic;
    private SQLiteDatabase mDb;
    ArrayList<ListData> listDatas = new ArrayList<>();
    Toast mToast;

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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //remove list
            }
        }).attachToRecyclerView(mRecyclerView);
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
        mListsAdapter = new ListsAdapter(this,this,getListsFromSQLite());
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

    private boolean removeAllListFromSQLite(){
        return mDb.delete(ListsContract.ListsEntry.NAZWA_TABELI,null, null) > 0;
    }

    private Cursor getListsFromSQLite() {
        String[] cols = {ListsContract.ListsEntry.NAZWA_LISTY, ListsContract.ListsEntry.DATA_ZAKUPOW};
        String selection = ListsContract.ListsEntry.ID_UZYTKOWNIKA + "=" + idUserPublic;
        return mDb.query(
                ListsContract.ListsEntry.NAZWA_TABELI,
                null,
                null,
                null,
                null,
                null,
                ListsContract.ListsEntry.NAZWA_LISTY
        );
    }

    @Override
    public void click(String list) {
        if(mToast  != null)
            mToast.cancel();
        mToast = Toast.makeText(this, list, Toast.LENGTH_SHORT);
        mToast.show();
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
