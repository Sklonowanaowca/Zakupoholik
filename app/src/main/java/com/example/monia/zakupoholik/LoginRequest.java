package com.example.monia.zakupoholik;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monia on 2017-11-29.
 */

public class LoginRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/login.php";
    private Map<String, String> params;

    public LoginRequest(String login, String password, Response.Listener<String> listener){
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        //create new params to send data to php file
        params = new HashMap<>();
        params.put("login", login);
        params.put("password", password);
    }

    //po wykonaniu zapytania volley uruchomi tę metodę, ktora zwroci parametry (ligin, passwd)
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
