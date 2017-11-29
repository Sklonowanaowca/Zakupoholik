package com.example.monia.zakupoholik;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monia on 2017-11-28.
 */

public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://bozena-lublin.pl/zakupoholik_new/register.php";
    private Map<String, String> params;

    public RegisterRequest(String imie, String login, String password, String email, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        //create new params to send data to php file
        params = new HashMap<>();
        params.put("imie", imie);
        params.put("login", login);
        params.put("password", password);
        params.put("email", email);
    }

    //po wykonaniu zapytania volley uruchomi tę metodę, ktora zwroci parametry (imie, ligin, passwd, email)
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
