package tnguye20.pearrs;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thang Nguyen on 10/27/2016.
 */

public class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://tnguye20.w3.uvm.edu/pearrs/register.php";
    private Map<String, String> params;

    public RegisterRequest(String firstName, String lastName, String gender, String userName, String password, String password2, String doctorCode, Response.Listener<String> listener){

        // NOTICE: LAST PARAM IS USED TO HANDLE ERRORS, LEAVE BLANK FOR NOW
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("gender", gender);
        params.put("userName", userName);
        params.put("password", password);
        params.put("password2", password2);
        params.put("doctorCode", doctorCode);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
