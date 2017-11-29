package com.example.monia.zakupoholik;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText mImieEditText = (EditText) findViewById(R.id.et_imie);
        final EditText mLoginEditText = (EditText) findViewById(R.id.et_login);
        final EditText mHasloEditText = (EditText) findViewById(R.id.et_haslo);
        final EditText mEnailEditText = (EditText) findViewById(R.id.et_email);
        final Button mZarejestrujSieButton = (Button) findViewById(R.id.button_register);

        mZarejestrujSieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String imie = mImieEditText.getText().toString();
                final String login = mLoginEditText.getText().toString();
                final String haslo = mHasloEditText.getText().toString();
                final String email = mEnailEditText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {// response from register.php (json string)
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success){
                                String message = jsonObject.getString("message");
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent zalogujSie = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(zalogujSie);
                            } else{
                                String message = jsonObject.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(message).setNegativeButton("Spróbuj jeszcze raz", null).create().show();
                                mLoginEditText.setText("");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(imie, login, haslo, email, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }
}
