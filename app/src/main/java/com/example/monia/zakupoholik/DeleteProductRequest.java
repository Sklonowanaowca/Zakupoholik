package com.example.monia.zakupoholik;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Monia on 2018-01-08.
 */

public class DeleteProductRequest extends StringRequest {
    private static final String FETCH_LISTS_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/usun_produkt.php";
    private Map<String, String> params;

    public DeleteProductRequest(long id_product, Response.Listener<String> listener){
        super(Method.POST, FETCH_LISTS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("id_product", String.valueOf(id_product));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
