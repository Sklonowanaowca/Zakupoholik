package com.example.monia.zakupoholik;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ListsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        final TextView mTextView = (TextView) findViewById(R.id.textView);
        Intent loginActivity = getIntent();
        String imie = loginActivity.getStringExtra(Intent.EXTRA_TEXT);
        mTextView.setText("Witaj " + imie + "! :)");
    }
}
