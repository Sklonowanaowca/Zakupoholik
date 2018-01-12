package com.example.monia.zakupoholik;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monia on 2018-01-12.
 */

public class AddNewProductToMysqlRequest extends StringRequest {
    private static final String FETCH_LISTS_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/dodaj_nowy_produkt.php";
    private Map<String, String> params;

    public AddNewProductToMysqlRequest(String nazwaProduktu, String nazwaKategorii, Response.Listener<String> listener){
        super(Request.Method.POST, FETCH_LISTS_REQUEST_URL, listener, null);
        //create new params to send data to php file
        params = new HashMap<>();
        params.put("nazwa_produktu", nazwaProduktu);
        params.put("nazwa_kategorii", nazwaKategorii);
    }

    //po wykonaniu zapytania volley uruchomi tę metodę, ktora zwroci parametry (id_user)
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
