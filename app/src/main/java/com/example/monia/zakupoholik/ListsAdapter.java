package com.example.monia.zakupoholik;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monia on 2017-12-11.
 */

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListsAdapterViewHolder> {
    private String[] mListsData;
    private ArrayList<ListData> listDatas = new ArrayList<>();
    interface ListsAdapterOnClickHandler{
        void click(String list);
    }
    final private ListsAdapterOnClickHandler mClickHandler;

    public ListsAdapter(ListsAdapterOnClickHandler listsAdapterOnClickHandler){
        mClickHandler = listsAdapterOnClickHandler;
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
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.activity_lists_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ListsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListsAdapterViewHolder holder, int position) {
        ListData current = listDatas.get(position);
        holder.mListsNameTextView.setText(current.getNazwaListy());
        holder.mListsDateTextView.setText(current.getDataZakupow());
    }

    @Override
    public int getItemCount() {
        return listDatas.size();
    }
}
