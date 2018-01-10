package com.example.monia.zakupoholik;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.monia.zakupoholik.data.ListData;
import com.example.monia.zakupoholik.data.ListsProductContract;
import com.example.monia.zakupoholik.data.ListsProductsDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Monia on 2018-01-03.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsAdapterViewHolder> {
    private Cursor mCursor, sCursor;
    private Context mContext;
    String[] allProductsFromMysqlDb;
    SQLiteDatabase mDb;

    public ProductsAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
        loadAllProductsFromMysqlToArray();
    }


    public class ProductsAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView mProductNameTextView;
        public final TextView mProductNumberTextView;
        public final TextView mProductUnitTextView;
        public final TextView mShopSignature;

        public ProductsAdapterViewHolder(final View itemView) {
            super(itemView);
            mProductNameTextView = (TextView) itemView.findViewById(R.id.tv_products_name);
            mProductNumberTextView = (TextView) itemView.findViewById(R.id.tv_products_number);
            mProductUnitTextView = (TextView) itemView.findViewById(R.id.tv_products_unit);
            mShopSignature = (TextView) itemView.findViewById(R.id.tv_shop_signature);
        }
    }

    @Override
    public ProductsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.activity_product_item, parent, false);
        return new ProductsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductsAdapter.ProductsAdapterViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return; // bail if returned null
        }
        // Update the view holder with the information needed to display
        String nazwaProduktu = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_NAZWA));
        float ilosc = mCursor.getFloat(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ILOSC));
        String jednostka = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_JEDNOSTKA));
        long idSqliteProdukt = mCursor.getLong(mCursor.getColumnIndex(ListsProductContract.ListsEntry._ID));
        int idSklep = mCursor.getInt(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ID_SKLEP));
        if(idSklep!=0){
            ListsProductsDbHelper listsProductsDbHelper = new ListsProductsDbHelper(mContext);
            mDb = listsProductsDbHelper.getWritableDatabase();
            String selection = ListsProductContract.ListsEntry.SKLEP_ID_SKLEP + " = " + idSklep;
            sCursor = mDb.query(ListsProductContract.ListsEntry.SKLEP_NAZWA_TABELI, null, selection, null,null,null,null);
            if(!sCursor.moveToFirst())
                return;
            String sygnatura = sCursor.getString(sCursor.getColumnIndex(ListsProductContract.ListsEntry.SYGNATURA));
            holder.mShopSignature.setText(sygnatura);
        }
        holder.mProductNameTextView.setText(nazwaProduktu);
        holder.mProductNumberTextView.setText(String.valueOf(ilosc));
        holder.mProductUnitTextView.setText(jednostka);
        // (7) Set the tag of the itemview in the holder to the id
        holder.itemView.setTag(idSqliteProdukt);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Toast.makeText(mContext, "position " + position, Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_edit_product);
                //dialog.setTitle(R.string.rename_list_title);

                final AutoCompleteTextView nazwaProduktu = (AutoCompleteTextView) dialog.findViewById(R.id.atv_renazwa_produktu);
                final EditText iloscProduktu = (EditText) dialog.findViewById(R.id.et_reilosc_produktu);
                final Spinner spinnerJednostki = (Spinner) dialog.findViewById(R.id.spinner_rejednostki);

                mCursor.moveToPosition(position);
                final long idProdukt = mCursor.getInt(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT));
                String nazwa = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_NAZWA));
                double ilosc = mCursor.getDouble(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ILOSC));
                String jednostka = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_JEDNOSTKA));
                final int idList = mCursor.getInt(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ID_LISTA));
                nazwaProduktu.setText(nazwa);
                iloscProduktu.setText(String.valueOf(ilosc));

                ArrayAdapter<String> productAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, allProductsFromMysqlDb);
                productAdapter.setDropDownViewResource(R.layout.spinner_item);
                nazwaProduktu.setThreshold(1);//min liczba znakow aby zlapalo podpowiedzi
                nazwaProduktu.setAdapter(productAdapter);

                String[] unitsArray = {"szt","l", "ml", "kg", "dag"};
                ArrayAdapter<String> unitsAdapter = new ArrayAdapter<String>(mContext,R.layout.spinner_item,R.id.atv_spinner_item,unitsArray);
                spinnerJednostki.setAdapter(unitsAdapter);
                if(jednostka.contains("szt"))
                    spinnerJednostki.setSelection(0);
                else if(jednostka.equals("l"))
                    spinnerJednostki.setSelection(1);
                else if(jednostka.equals("ml"))
                    spinnerJednostki.setSelection(2);
                else if(jednostka.equals("kg"))
                    spinnerJednostki.setSelection(3);
                else if(jednostka.equals("dag"))
                    spinnerJednostki.setSelection(4);

                Button dialogOkButton = (Button) dialog.findViewById(R.id.ok_button);
                Button dialogCancelButton = (Button) dialog.findViewById(R.id.cancel_button);

                dialogOkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double ilosc = Double.parseDouble(iloscProduktu.getText().toString());
                        getIdProduktowFromMysql(nazwaProduktu.getText().toString(), idProdukt,ilosc,
                                spinnerJednostki.getSelectedItem().toString(), idList);
                        dialog.dismiss();
                    }
                });

                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        //return listDatas.size();
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    private void getIdProduktowFromMysql(String nazwaProduktu, final long idProdukt, final double ilosc, final String jednostka, final int idList){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_id_produktow.php (json array)
                int idProductow = 0;
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        idProductow = jsonObject.getInt("idProduktow");
                        editProduct(idProdukt,ilosc,jednostka,idProductow, idList);

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        FetchIdProductsRequest fetchIdProductsRequest = new FetchIdProductsRequest(nazwaProduktu, responseListener);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(fetchIdProductsRequest);
    }

    private void editProduct(long idProdukt, double ilosc, String jednostka, long idProduktow, final int idList){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from zmien_nazwe_listy.php (json array)
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success)
                            Toast.makeText(mContext, "Zmieniono produkt", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mContext, "Ooops! Coś poszło nie tak...", Toast.LENGTH_SHORT).show();
                        ((ProductsActivity)mContext).loadProductsFromSerwerToSQLite(idList);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        EditProductRequest editProductRequest = new EditProductRequest(idProdukt, ilosc, jednostka, idProduktow, responseListener);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(editProductRequest);
    }

    private void loadAllProductsFromMysqlToArray(){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {// response from pokaz_wszystkie_produkty.php (json array)
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray produkty = jsonObject.getJSONArray("products");
                        allProductsFromMysqlDb = new String[produkty.length()];
                        for (int i = 0; i < produkty.length(); i++) {
                            JSONObject json_data = produkty.getJSONObject(i);
                            allProductsFromMysqlDb[i] = json_data.getString("Nazwa");
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        FetchAllProductsRequest fetchAllProductsRequest = new FetchAllProductsRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(fetchAllProductsRequest);
    }


//    private void renameRedateList(long id_list, String nazwaListy, String dataZakupow,final long idUzytkownika){
//        Response.Listener<String> responseListener = new Response.Listener<String>(){
//
//            @Override
//            public void onResponse(String response) {// response from zmien_nazwe_listy.php (json array)
//                if(response!=null && response.length()>0){
//                    try{
//                        JSONObject jsonObject = new JSONObject(response);
//                        boolean success = jsonObject.getBoolean("success");
//                        if(success)
//                            Toast.makeText(mContext, "Zmieniono nazwe listy", Toast.LENGTH_SHORT).show();
//                        else
//                            Toast.makeText(mContext, "Ooops! Coś poszło nie tak...", Toast.LENGTH_SHORT).show();
//                        ((ListsActivity)mContext).loadListsFromSerwerToSQLite((int)idUzytkownika);
//                    } catch (JSONException e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        RenameRedateListRequest fetchListsRequest = new RenameRedateListRequest(id_list, nazwaListy, dataZakupow, idUzytkownika, responseListener);
//        RequestQueue queue = Volley.newRequestQueue(mContext);
//        queue.add(fetchListsRequest);
//    }
}
