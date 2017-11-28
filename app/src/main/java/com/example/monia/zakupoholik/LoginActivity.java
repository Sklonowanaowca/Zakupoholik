package com.example.monia.zakupoholik;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<String> {

    private ProgressBar mProgressBar;
    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private Button mZalogujSieButton;
    private Button mZarejestrujSieButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLoginEditText = (EditText) findViewById(R.id.login);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);

        mZalogujSieButton = (Button) findViewById(R.id.button_log_in);
        mZarejestrujSieButton = (Button) findViewById(R.id.register_button);
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

