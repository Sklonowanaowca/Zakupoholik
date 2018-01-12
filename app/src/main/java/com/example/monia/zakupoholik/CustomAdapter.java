package com.example.monia.zakupoholik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Monia on 2018-01-11.
 */

public class CustomAdapter extends BaseAdapter {
    private LayoutInflater layoutinflater;
    private List<CategoryObject> listStorage;
    private Context mContext;

    public CustomAdapter(Context context, List<CategoryObject> customizedListView) {
        this.mContext = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.activity_add_new_product_grid_item, parent, false);
            listViewHolder.mTextViewCategoryName = (TextView)convertView.findViewById(R.id.tv_grid_item_category_name);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }

        listViewHolder.mTextViewCategoryName.setText(listStorage.get(position).getContent());

        return convertView;
    }

    static class ViewHolder{
        TextView mTextViewCategoryName;
    }

}
