package com.example.monia.zakupoholik;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.monia.zakupoholik.data.ListData;
import com.example.monia.zakupoholik.data.ListsProductContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Monia on 2018-01-03.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;

    public ProductsAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
    }


    public class ProductsAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView mProductNameTextView;
        public final TextView mProductNumberTextView;

        public ProductsAdapterViewHolder(final View itemView) {
            super(itemView);
            mProductNameTextView = (TextView) itemView.findViewById(R.id.tv_products_name);
            mProductNumberTextView = (TextView) itemView.findViewById(R.id.tv_products_number);
        }
    }

    @Override
    public ProductsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.activity_product_item, parent, false);
        return new ProductsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductsAdapter.ProductsAdapterViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            Toast.makeText(mContext, "mCursor null", Toast.LENGTH_SHORT).show();
            return; // bail if returned null
        }
        // Update the view holder with the information needed to display
        String nazwaProduktu = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_NAZWA));
        int ilosc = mCursor.getInt(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ILOSC));
        long id = mCursor.getLong(mCursor.getColumnIndex(ListsProductContract.ListsEntry._ID));
        holder.mProductNameTextView.setText(nazwaProduktu);
        holder.mProductNumberTextView.setText(String.valueOf(ilosc));
        // (7) Set the tag of the itemview in the holder to the id
        holder.itemView.setTag(id);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursor.moveToPosition(position);
                String nazwaProduktu = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_NAZWA));
                Toast.makeText(mContext, nazwaProduktu, Toast.LENGTH_SHORT).show();
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
