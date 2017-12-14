package com.example.monia.zakupoholik;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListsActivity extends AppCompatActivity implements ListsAdapter.ListsAdapterOnClickHandler{
    private RecyclerView mRecyclerView;
    private TextView mErrorMessage;
    private ListsAdapter mListsAdapter;
    public int id_user;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        mErrorMessage = (TextView) findViewById(R.id.error_message);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_lists);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);//zawartosc nie zmeni rozmiaru widoku

        mListsAdapter = new ListsAdapter(this);
        mRecyclerView.setAdapter(mListsAdapter);

        Intent dataFromLoginActivity = getIntent();
        id_user = dataFromLoginActivity.getIntExtra("ID", 0);
        String imie = dataFromLoginActivity.getStringExtra("IMIE");

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LoginActivity.KEY_IMIE, imie);
        editor.putInt(LoginActivity.KEY_ID_UZYTKOWNIKA, id_user);
        editor.apply();

        loadLists(id_user);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new AddListDialog();
                dialogFragment.show(getFragmentManager(),"add_lists");
            }
        });
    }

    private void loadLists(Integer id_user){
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
                String[] stringJsonArray = getDataToJsonArray(response);

                mListsAdapter.setListsData(stringJsonArray);
            }
        };
        FetchListsRequest fetchListsRequest = new FetchListsRequest(id_user, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ListsActivity.this);
        queue.add(fetchListsRequest);
    }

    private String[] getDataToJsonArray(String response){
        String[] stringJsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray listy = jsonObject.getJSONArray("lists");
            stringJsonArray = new String[listy.length()];
                for(int i=0; i<listy.length(); i++){
                    JSONObject json_data = listy.getJSONObject(i);
                    String nazwaListy = json_data.getString("Nazwa_listy");
                    stringJsonArray[i] = nazwaListy;
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringJsonArray;
    }

    public void addList(String nazwaListy, String dataZakupow, Context context){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    boolean success = jsonObj.getBoolean("success");
                    String message="";
                    if(success) {
                        message = jsonObj.getString("message");
                    } else {
                        message = jsonObj.getString("message");
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        AddListsRequest fetchListsRequest = new AddListsRequest(nazwaListy, dataZakupow, id_user, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(fetchListsRequest);
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
}
