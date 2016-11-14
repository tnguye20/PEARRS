package tnguye20.pearrs;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thang Nguyen on 11/8/2016.
 */

public class SurveyRequest extends StringRequest{

    private static final String SURVEY_REQUEST_URL = "http://tnguye20.w3.uvm.edu/pearrs/survey.php";
    private Map<String,String> params;

    public SurveyRequest(String nextSurvey, String surveyId, String userId, String results ,Response.Listener<String> listener){

        // NOTICE: LAST PARAM IS USED TO HANDLE ERRORS, LEAVE BLANK FOR NOW
        super(Method.POST,SURVEY_REQUEST_URL,listener, null);

        params = new HashMap<>();
        params.put("nextSurvey", nextSurvey);
        params.put("surveyId", surveyId);
        params.put("userId", userId);
        params.put("results", results);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
