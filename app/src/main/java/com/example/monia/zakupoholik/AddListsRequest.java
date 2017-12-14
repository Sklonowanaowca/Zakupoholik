package com.example.monia.zakupoholik;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monia on 2017-12-14.
 */

public class AddListsRequest extends StringRequest{
    private static final String FETCH_LISTS_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/dodaj_liste.php";
    private Map<String, String> params;

    public AddListsRequest(String nazwaListy, String dataZakupow, int id_user, Response.Listener<String> listener){
        super(Request.Method.POST, FETCH_LISTS_REQUEST_URL, listener, null);
        //create new params to send data to php file
        params = new HashMap<>();
        params.put("Nazwa_listy", nazwaListy);
        params.put("Data_zakupow", String.valueOf(dataZakupow));
        params.put("id_user", String.valueOf(id_user));
    }

    //po wykonaniu zapytania volley uruchomi tę metodę, ktora zwroci parametry (id_user)
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
