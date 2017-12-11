package com.example.monia.zakupoholik;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Monia on 2017-12-11.
 */

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListsAdapterViewHolder> {
    private String[] mListsData;
    interface ListsAdapterOnClickHandler{
        void click(String list);
    }
    final private ListsAdapterOnClickHandler mClickHandler;

    public ListsAdapter(ListsAdapterOnClickHandler listsAdapterOnClickHandler){
        mClickHandler = listsAdapterOnClickHandler;
    }

    public class ListsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mListsTextView;

        public ListsAdapterViewHolder(View itemView) {
            super(itemView);
            mListsTextView = (TextView) itemView.findViewById(R.id.tv_lists_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedList = getAdapterPosition();
            String list = mListsData[clickedList];
            mClickHandler.click(list);
        }
    }

    @Override
    public ListsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.activity_lists_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ListsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListsAdapterViewHolder holder, int position) {
        String list = mListsData[position];
        holder.mListsTextView.setText(list);
    }

    @Override
    public int getItemCount() {
        if(mListsData == null)
            return 0;
        return mListsData.length;
    }

    public void setListsData(String[] listsdata){
        mListsData = listsdata;
        notifyDataSetChanged();
    }
}
