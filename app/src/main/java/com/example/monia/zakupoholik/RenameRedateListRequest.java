package com.example.monia.zakupoholik;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monia on 2017-12-30.
 */

public class RenameRedateListRequest extends StringRequest{
    private static final String FETCH_LISTS_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/zmien_nazwe_listy.php";
    private Map<String, String> params;

    public RenameRedateListRequest(long id_list, String nazwaListy, String dataZakupow, double kosztZakupow, long idUzytkownika, Response.Listener<String> listener){
        super(Method.POST, FETCH_LISTS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("id_list", String.valueOf(id_list));
        params.put("nazwa_listy", nazwaListy);
        params.put("data_zakupow", dataZakupow);
        params.put("koszt_zakupow", String.valueOf(kosztZakupow));
        params.put("id_uzytkownika", String.valueOf(idUzytkownika));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
