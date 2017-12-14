package com.example.monia.zakupoholik;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

/**
 * Created by Monia on 2017-12-14.
 */

public class AddListDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_list,null);
        final EditText mNawaListy = (EditText) dialogView.findViewById(R.id.add_list_name);
        final EditText mDataZakupow = (EditText) dialogView.findViewById(R.id.add_list_data);
        final Context mContext = getActivity();
        builder.setView(dialogView).setPositiveButton(R.string.add_list_positive_button, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nazwaListy = mNawaListy.getText().toString();
                        String dataZakupow = mDataZakupow.getText().toString();
                        ListsActivity addList = new ListsActivity();
                        addList.addList(nazwaListy, dataZakupow, mContext);
                    }
                })
                .setNegativeButton(R.string.add_list_negative_button, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddListDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }
}
