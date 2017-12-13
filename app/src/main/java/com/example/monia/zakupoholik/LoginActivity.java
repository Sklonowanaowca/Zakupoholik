package com.example.monia.zakupoholik;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<String> {

    public final static String KEY_IMIE = "KEY_IMIE";
    public final static String KEY_ID_UZYTKOWNIKA = "KEY_ID_UZYTKOWNIKA";

    private ProgressBar mProgressBar;
    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private Button mZalogujSieButton;
    private TextView mZarejestrujSieTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLoginEditText = (EditText) findViewById(R.id.et_login);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);

        mZalogujSieButton = (Button) findViewById(R.id.button_log_in);
        mZarejestrujSieTextView = (TextView) findViewById(R.id.tv_register);

        mZarejestrujSieTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent zarejesrujSie = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(zarejesrujSie);
            }
        });

        mZalogujSieButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser(){
        final String login = mLoginEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from login.php (json string)
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        String imie = jsonObject.getString("imie");
                        int idUzytkownika = jsonObject.getInt("id_uzytkownika");
                        Intent listy = new Intent(LoginActivity.this, ListsActivity.class);
                        listy.putExtra("ID", idUzytkownika);
                        listy.putExtra("IMIE", imie);
                        LoginActivity.this.startActivity(listy);
                    } else{
                        String message = jsonObject.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(message).setNegativeButton("Spróbuj jeszcze raz", null).create().show();
                        mLoginEditText.setText("");
                        mPasswordEditText.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest = new LoginRequest(login, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }

    private boolean isLoginValid(String login) {
        //TODO: Replace this with your own logic
        return login.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    /**
     * Shows the progress UI and hides the login form.
     */

}

