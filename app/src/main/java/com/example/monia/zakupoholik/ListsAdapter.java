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
 * Created by Monia on 2017-12-11.
 */

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListsAdapterViewHolder> {
    private ArrayList<ListData> listDatas = new ArrayList<>();
    private Cursor mCursor;
    private Context mContext;

    public ListsAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
    }


    public class ListsAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView mListsNameTextView;
        public final TextView mListsDateTextView;

        public ListsAdapterViewHolder(final View itemView) {
            super(itemView);
            mListsNameTextView = (TextView) itemView.findViewById(R.id.tv_lists_name);
            mListsDateTextView = (TextView) itemView.findViewById(R.id.tv_lists_date);
        }
    }


    @Override
    public ListsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.activity_lists_item, parent, false);
        return new ListsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListsAdapterViewHolder holder, final int position) {
//        ListData current = listDatas.get(position);
//        holder.mListsNameTextView.setText(current.getNazwaListy());
//        holder.mListsDateTextView.setText(current.getDataZakupow());

        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        // Update the view holder with the information needed to display
        String nazwaListy = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.NAZWA_LISTY));
        String dataZakupow = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.DATA_ZAKUPOW));
        // (6) Retrieve the id from the cursor and
        long id = mCursor.getLong(mCursor.getColumnIndex(ListsProductContract.ListsEntry._ID));
        // Display the guest name
        holder.mListsNameTextView.setText(nazwaListy);
        // Display the party count
        holder.mListsDateTextView.setText(String.valueOf(dataZakupow));
        // (7) Set the tag of the itemview in the holder to the id
        holder.itemView.setTag(id);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Toast.makeText(mContext, "position " + position, Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_rename_redata_list);
                dialog.setTitle(R.string.rename_list_title);

                final EditText rename = (EditText) dialog.findViewById(R.id.rename_list);
                final EditText redate = (EditText) dialog.findViewById(R.id.redate_list);
                mCursor.moveToPosition(position);
                String nazwalisty = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.NAZWA_LISTY));
                String datazakupow = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.DATA_ZAKUPOW));
                final long idlisty = mCursor.getInt(mCursor.getColumnIndex(ListsProductContract.ListsEntry.ID_LISTA));
                final long idUzytkownika = mCursor.getInt(mCursor.getColumnIndex(ListsProductContract.ListsEntry.ID_UZYTKOWNIKA));
                rename.setText(nazwalisty);
                redate.setText(datazakupow);

                final

                Button dialogOkButton = (Button) dialog.findViewById(R.id.ok_button);
                Button dialogCancelButton = (Button) dialog.findViewById(R.id.cancel_button);

                dialogOkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String renazwaListy = rename.getText().toString();
                        String redataZakupow = redate.getText().toString();
                        renameRedateList(idlisty,renazwaListy,redataZakupow,idUzytkownika);
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
                mCursor.moveToPosition(position);
                String nazwaListy = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.NAZWA_LISTY));
                int idList = (int)mCursor.getLong(mCursor.getColumnIndex(ListsProductContract.ListsEntry.ID_LISTA));
                if(mContext instanceof ListsActivity) {
                    Intent startProductActivity = new Intent(mContext, ProductsActivity.class);
                    startProductActivity.putExtra("NAZWA_LISTY", nazwaListy);
                    startProductActivity.putExtra("ID", idList);
                    mContext.startActivity(startProductActivity);
                }
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

    private void renameRedateList(long id_list, String nazwaListy, String dataZakupow,final long idUzytkownika){
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {// response from zmien_nazwe_listy.php (json array)
                if(response!=null && response.length()>0){
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success)
                            Toast.makeText(mContext, "Zmieniono nazwe listy", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mContext, "Ooops! Coś poszło nie tak...", Toast.LENGTH_SHORT).show();
                        ((ListsActivity)mContext).loadListsFromSerwerToSQLite((int)idUzytkownika);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        RenameRedateListRequest fetchListsRequest = new RenameRedateListRequest(id_list, nazwaListy, dataZakupow, idUzytkownika, responseListener);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(fetchListsRequest);
    }
}
