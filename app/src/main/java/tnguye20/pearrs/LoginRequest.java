package tnguye20.pearrs;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thang Nguyen on 10/27/2016.
 */

public class LoginRequest extends StringRequest {

    public static final String LOGIN_REQUEST_URL = "http://tnguye20.w3.uvm.edu/shoes/login.php";
    private Map<String, String> params;

    public LoginRequest(String userName, String password, Response.Listener<String> listener){

        // NOTICE: LAST PARAM IS USED TO HANDLE ERRORS, LEAVE BLANK FOR NOW
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
