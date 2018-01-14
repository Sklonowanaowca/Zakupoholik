package com.example.monia.zakupoholik;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.monia.zakupoholik.data.ListsProductContract;
import com.example.monia.zakupoholik.data.ListsProductsDbHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class ShopingMode extends AppCompatActivity {
    String nazwaSklepu = "";
    String nazwaListy = "";
    String adresSklepu = "";
    SQLiteDatabase mDb;
    RecyclerView mRecyclerView;
    public ArrayList<String> checked;
    ShoppingModeAdapter shoppingModeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoping_mode);

        TextView nazwaL = (TextView) findViewById(R.id.tv_nazwa_listy);
        TextView nazwaS = (TextView) findViewById(R.id.tv_nazwa_sklepu);
        TextView adresS = (TextView) findViewById(R.id.tv_adres_sklepu);

        checked = new ArrayList<String>();

        Intent dataFromProductsActivity = getIntent();
        if (dataFromProductsActivity.hasExtra("NAZWA_SKLEPU") && dataFromProductsActivity.hasExtra("NAZWA_LISTY")) {
            nazwaSklepu = dataFromProductsActivity.getStringExtra("NAZWA_SKLEPU");
            nazwaListy = dataFromProductsActivity.getStringExtra("NAZWA_LISTY");
            adresSklepu = dataFromProductsActivity.getStringExtra("ADRES_SKLEPU");
            nazwaS.setText(nazwaSklepu);
            nazwaL.setText(nazwaListy);
            adresS.setText(adresSklepu);
            Toast.makeText(this, "Kupujesz w " + nazwaSklepu, Toast.LENGTH_SHORT).show();
        }

        ListsProductsDbHelper listsProductsDbHelper = new ListsProductsDbHelper(this);
        mDb = listsProductsDbHelper.getWritableDatabase();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_products_shopping_mode);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();
        shoppingModeAdapter = new ShoppingModeAdapter(ShopingMode.this, getAllProductsFromSQLite());
        mRecyclerView.setAdapter(shoppingModeAdapter);

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

    private void saveShopping(String NazwaSklepu, String checkedArrayString) {
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from usun_produkt.php (json array)
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success)
                            Toast.makeText(ShopingMode.this, "zapisano zakupy", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ShopingMode.this, "Nie udało sie zapisać zakupow", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }

        };
        SaveShoppingRequest saveShoppingRequest = new SaveShoppingRequest(checkedArrayString, NazwaSklepu, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ShopingMode.this);
        queue.add(saveShoppingRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_mode_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back_to_list_mode:
                Gson gson = new Gson();
                String checkedArrayString = gson.toJson(checked);
                saveShopping(nazwaSklepu, checkedArrayString);
                setResult(ShopingMode.RESULT_OK);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ShoppingModeAdapter extends RecyclerView.Adapter<ShoppingModeAdapter.MyViewHolder> {
        Context mContext;
        Cursor mCursor;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mProductNameTextView;
            public TextView mProductNumberTextView;
            public TextView mProductUnitTextView;
            public final CheckBox mCheckBox;

            public MyViewHolder(View view) {
                super(view);
                mProductNameTextView = (TextView) itemView.findViewById(R.id.tv_products_name);
                mProductNumberTextView = (TextView) itemView.findViewById(R.id.tv_products_number);
                mProductUnitTextView = (TextView) itemView.findViewById(R.id.tv_products_unit);
                mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            }
        }


        public ShoppingModeAdapter(Context context, Cursor cursor) {
            mContext = context;
            mCursor = cursor;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_shopping_mode_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            if (!mCursor.moveToPosition(position)) {
            return; // bail if returned null
        }
                String nazwaProduktu = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_NAZWA));
                float ilosc = mCursor.getFloat(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ILOSC));
                String jednostka = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_JEDNOSTKA));
                long id = mCursor.getLong(mCursor.getColumnIndex(ListsProductContract.ListsEntry._ID));

                holder.mProductNameTextView.setText(nazwaProduktu);
                holder.mProductNumberTextView.setText(String.valueOf(ilosc));
                holder.mProductUnitTextView.setText(jednostka);
                holder.itemView.setTag(id);

                holder.mCheckBox.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        mCursor.moveToPosition(position);

                        if (holder.mCheckBox.isChecked()) {
                            //Toast.makeText(ModeShopping.this, cursor.getString(cursor.getColumnIndex(db.NAZWA_PRODUKTU)), Toast.LENGTH_SHORT).show();
                            checked.add(mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT)));
                        } else {
                            checked.remove(mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT)));
                        }
                    }
                });

            }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
}
