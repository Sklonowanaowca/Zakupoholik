package com.example.monia.zakupoholik;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monia on 2018-01-13.
 */

public class FindShopsNearMeRequest extends StringRequest{
    private static final String FETCH_LISTS_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/pobierz_najblizsze_sklepy.php";
    private Map<String, String> params;

    public FindShopsNearMeRequest(double szer, double dl, Response.Listener<String> listener){
        super(Method.POST, FETCH_LISTS_REQUEST_URL, listener, null);
        //create new params to send data to php file
        params = new HashMap<>();
        params.put("szer", String.valueOf(szer));
        params.put("dl", String.valueOf(dl));
    }

    //po wykonaniu zapytania volley uruchomi tę metodę, ktora zwroci parametry (id_user)
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
