//package com.example.monia.zakupoholik;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.database.Cursor;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.toolbox.Volley;
//import com.example.monia.zakupoholik.data.ListsProductContract;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
// * Created by Monia on 2018-01-10.
// */
//
//public class ProductsAdapterShoppingMode extends RecyclerView.Adapter<ProductsAdapterShoppingMode.ProductsAdapterViewHolder>{
//    private Cursor mCursor;
//    private Context mContext;
//
//    public ProductsAdapterShoppingMode(Context context, Cursor cursor){
//        this.mContext = context;
//        this.mCursor = cursor;
//        checked = new ArrayList<String>();
//    }
//
//
//    public class ProductsAdapterViewHolder extends RecyclerView.ViewHolder{
//        public final TextView mProductNameTextView;
//        public final TextView mProductNumberTextView;
//        public final TextView mProductUnitTextView;
//        public final CheckBox mCheckBox;
//
//        public ProductsAdapterViewHolder(final View itemView) {
//            super(itemView);
//            mProductNameTextView = (TextView) itemView.findViewById(R.id.tv_products_name);
//            mProductNumberTextView = (TextView) itemView.findViewById(R.id.tv_products_number);
//            mProductUnitTextView = (TextView) itemView.findViewById(R.id.tv_products_unit);
//            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
//        }
//    }
//
//    @Override
//    public ProductsAdapterShoppingMode.ProductsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//
//        View view = inflater.inflate(R.layout.activity_shopping_mode_item, parent, false);
//        return new ProductsAdapterShoppingMode.ProductsAdapterViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final ProductsAdapterShoppingMode.ProductsAdapterViewHolder holder, final int position) {
//        if (!mCursor.moveToPosition(position)) {
//            return; // bail if returned null
//        }
//        // Update the view holder with the information needed to display
//        String nazwaProduktu = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_NAZWA));
//        float ilosc = mCursor.getFloat(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ILOSC));
//        String jednostka = mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_JEDNOSTKA));
//        long id = mCursor.getLong(mCursor.getColumnIndex(ListsProductContract.ListsEntry._ID));
//        holder.mProductNameTextView.setText(nazwaProduktu);
//        holder.mProductNumberTextView.setText(String.valueOf(ilosc));
//        holder.mProductUnitTextView.setText(jednostka);
//        // (7) Set the tag of the itemview in the holder to the id
//        holder.itemView.setTag(id);
//
//       holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                mCursor.moveToPosition(position);
//
//                if (holder.mCheckBox.isChecked()) {
//                    //Toast.makeText(ModeShopping.this, cursor.getString(cursor.getColumnIndex(db.NAZWA_PRODUKTU)), Toast.LENGTH_SHORT).show();
//                    checked.add(mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT)));
//                } else {
//                    checked.remove(mCursor.getString(mCursor.getColumnIndex(ListsProductContract.ListsEntry.PRODUKT_ID_PRODUKT)));
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        //return listDatas.size();
//        return mCursor.getCount();
//    }
//}
