package tnguye20.pearrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {
    // Instantiate variables
    private EditText mUserName;
    private EditText mPass;
    private TextView mError;
    private Button mLoginButton;
    private TextView mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserName = (EditText)findViewById(R.id.userNameEditText);
        mPass = (EditText)findViewById(R.id.passTextView);
        mLoginButton = (Button)findViewById(R.id.loginButton);
        mRegister = (TextView)findViewById(R.id.registerTextView);
        mError = (TextView)findViewById(R.id.errorTextView);

        //Handler for the Register
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        //Handler for the Login
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = mUserName.getText().toString();
                final String password = mPass.getText().toString();

                final Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success) {
                                final int userId = jsonResponse.getInt("userId");
                                final String firstName = jsonResponse.getString("firstName");
                                final String lastName = jsonResponse.getString("lastName");
                                final String gender = jsonResponse.getString("gender");
                                final String nextSurvey = jsonResponse.getString("nextSurvey");
                                final String surveyQuestions = jsonResponse.getString("surveyQuestions");

                                if(nextSurvey.equals("1")) {
                                    /* This means they are a new user, take them to the welcome screen */
                                    Intent welcomeIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                    welcomeIntent.putExtra("userId", userId);
                                    welcomeIntent.putExtra("firstName",firstName);
                                    welcomeIntent.putExtra("lastName", lastName);
                                    welcomeIntent.putExtra("gender", gender);
                                    welcomeIntent.putExtra("nextSurvey", nextSurvey);
                                    welcomeIntent.putExtra("surveyQuestions", surveyQuestions);
                                    LoginActivity.this.startActivity(welcomeIntent);
                                }else{
                                    /* Existing user, take the survey right away */
                                    Intent surveyIntent = new Intent(LoginActivity.this, SurveyActivity.class);
                                    surveyIntent.putExtra("userId", userId);
                                    surveyIntent.putExtra("firstName", firstName);
                                    surveyIntent.putExtra("lastName", lastName);
                                    surveyIntent.putExtra("gender", gender);
                                    surveyIntent.putExtra("nextSurvey", nextSurvey);
                                    surveyIntent.putExtra("surveyQuestions", surveyQuestions);
                                    LoginActivity.this.startActivity(surveyIntent);
                                }
                            }else{
                                String errorText = "";
                                mError.setText(jsonResponse.toString());

                                Iterator<String> iterator = jsonResponse.keys();
                                while(iterator.hasNext()){
                                    String key = (String)iterator.next();
                                    if(!key.equals("success")){
                                        errorText += jsonResponse.getString(key) + "\n";
                                    }
                                }
                                mError.setText(errorText);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(userName, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }
}
