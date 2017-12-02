package com.example.monia.zakupoholik;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        final TextView mTextView = (TextView) findViewById(R.id.textView);
        final TextView mJsonResponse = (TextView) findViewById(R.id.json_response);
        final String imie;
        final int idUzytkownika;

        Intent dataFromLoginActivity = getIntent();
        if(dataFromLoginActivity.hasExtra(LoginActivity.KEY_IMIE) && dataFromLoginActivity.hasExtra(LoginActivity.KEY_ID_UZYTKOWNIKA)) {
            imie = dataFromLoginActivity.getStringExtra(LoginActivity.KEY_IMIE);
            idUzytkownika = dataFromLoginActivity.getIntExtra(LoginActivity.KEY_ID_UZYTKOWNIKA, 0);

            mTextView.setText("Witaj " + imie + "! :)");
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LoginActivity.KEY_IMIE, imie);
            editor.putInt(LoginActivity.KEY_ID_UZYTKOWNIKA, idUzytkownika);
            editor.commit();
        }

        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
                //try {
                    mJsonResponse.setText(response);
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONArray listy = jsonObject.getJSONArray("lists");
//                    for(int i=0; i<listy.length(); i++){
//                        JSONObject json_data = listy.getJSONObject(i);
//                        int idLista = json_data.getInt("ID_Lista");
//                        mTextView.append(" id Twojej listy, to: " + idLista);
//                    }
//                        boolean success = jsonObject.getBoolean("success");
//                        if(success){
//                            int idLista = listy.getString("ID_Lista");
//                            String dataZakupow = listy.getString("Data_zakupow");
//                            String nazwaListy = jsonObject.getString("Nazwa_listy");
//                            Double kosztZakupow = jsonObject.getDouble("Koszt_zakupow");
//                        } else{
//                            String message = jsonObject.getString("message");
//                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                            builder.setMessage(message).setNegativeButton("Spróbuj jeszcze raz", null).create().show();
//                            mLoginEditText.setText("");
//                            mPasswordEditText.setText("");
//                        }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        };
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        int id_user = sharedPreferences.getInt(LoginActivity.KEY_ID_UZYTKOWNIKA, 0);
        FetchListsRequest fetchListsRequest = new FetchListsRequest(id_user, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ListsActivity.this);
        queue.add(fetchListsRequest);

    }
}
