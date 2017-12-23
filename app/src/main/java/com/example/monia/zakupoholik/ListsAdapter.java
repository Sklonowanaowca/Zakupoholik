package com.example.monia.zakupoholik;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.monia.zakupoholik.data.ListData;
import com.example.monia.zakupoholik.data.ListsContract;

import java.util.ArrayList;

/**
 * Created by Monia on 2017-12-11.
 */

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListsAdapterViewHolder> {
    private ArrayList<ListData> listDatas = new ArrayList<>();
    private Cursor mCursor;
    private Context mContext;
    interface ListsAdapterOnClickHandler{
        void click(String list);
    }
    final private ListsAdapterOnClickHandler mClickHandler;

    public ListsAdapter(ListsAdapterOnClickHandler listsAdapterOnClickHandler,Context context, Cursor cursor){
        mClickHandler = listsAdapterOnClickHandler;
        this.mContext = context;
        this.mCursor = cursor;
    }

    public class ListsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mListsNameTextView;
        public final TextView mListsDateTextView;

        public ListsAdapterViewHolder(View itemView) {
            super(itemView);
            mListsNameTextView = (TextView) itemView.findViewById(R.id.tv_lists_name);
            mListsDateTextView = (TextView) itemView.findViewById(R.id.tv_lists_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedListPos = getAdapterPosition();
            ListData cilkedList = listDatas.get(clickedListPos);
            String lista = cilkedList.getNazwaListy();
            mClickHandler.click(lista);
        }
    }

    public void setListDatas(ArrayList<ListData> listDatas){
        this.listDatas = listDatas;
        notifyItemRangeChanged(0, listDatas.size());
    }

    @Override
    public ListsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.activity_lists_item, parent, false);
        return new ListsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListsAdapterViewHolder holder, int position) {
//        ListData current = listDatas.get(position);
//        holder.mListsNameTextView.setText(current.getNazwaListy());
//        holder.mListsDateTextView.setText(current.getDataZakupow());

        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        // Update the view holder with the information needed to display
        String nazwaListy = mCursor.getString(mCursor.getColumnIndex(ListsContract.ListsEntry.NAZWA_LISTY));
        String dataZakupow = mCursor.getString(mCursor.getColumnIndex(ListsContract.ListsEntry.DATA_ZAKUPOW));
        // (6) Retrieve the id from the cursor and
        long id = mCursor.getLong(mCursor.getColumnIndex(ListsContract.ListsEntry._ID));
        // Display the guest name
        holder.mListsNameTextView.setText(nazwaListy);
        // Display the party count
        holder.mListsDateTextView.setText(String.valueOf(dataZakupow));
        // (7) Set the tag of the itemview in the holder to the id
        holder.itemView.setTag(id);
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
}
