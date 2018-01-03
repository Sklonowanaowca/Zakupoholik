package com.example.monia.zakupoholik;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monia on 2018-01-02.
 */

public class FetchProductsRequest extends StringRequest {
    private static final String FETCH_LISTS_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/pokaz_produkty.php";
    private Map<String, String> params;

    public FetchProductsRequest(int id_list, Response.Listener<String> listener){
        super(Method.POST, FETCH_LISTS_REQUEST_URL, listener, null);
        //create new params to send data to php file
        params = new HashMap<>();
        params.put("id_list", String.valueOf(id_list));
    }

    //po wykonaniu zapytania volley uruchomi tę metodę, ktora zwroci parametry (id_user)
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
