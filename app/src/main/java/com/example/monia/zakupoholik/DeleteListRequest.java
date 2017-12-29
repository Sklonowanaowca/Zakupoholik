package com.example.monia.zakupoholik;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monia on 2017-12-23.
 */

public class DeleteListRequest extends StringRequest {
    private static final String FETCH_LISTS_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/usun_liste.php";
    private Map<String, String> params;

    public DeleteListRequest(long id_list, Response.Listener<String> listener){
        super(Method.POST, FETCH_LISTS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("id_list", String.valueOf(id_list));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
