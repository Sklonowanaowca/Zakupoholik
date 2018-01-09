package com.example.monia.zakupoholik;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monia on 2018-01-08.
 */

public class EditProductRequest extends StringRequest {
    private static final String FETCH_LISTS_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/zmien_nazwe_produktu.php";
    private Map<String, String> params;

    public EditProductRequest(long idProdukt, double ilosc, String jednostka, long idProduktow, Response.Listener<String> listener){
        super(Method.POST, FETCH_LISTS_REQUEST_URL, listener, null);
        //create new params to send data to php file
        params = new HashMap<>();
        params.put("id_produkt", String.valueOf(idProdukt));
        params.put("ilosc", String.valueOf(ilosc));
        params.put("jednostka", jednostka);
        params.put("id_produktow", String.valueOf(idProduktow));
    }

    //po wykonaniu zapytania volley uruchomi tę metodę, ktora zwroci parametry (id_user)
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
