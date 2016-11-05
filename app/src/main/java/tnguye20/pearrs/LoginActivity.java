package tnguye20.pearrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    // Instantiate variables
    private EditText mUserName;
    private EditText mPass;
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

                Intent welcomeIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
                welcomeIntent.putExtra("firstName", "Thang");
                LoginActivity.this.startActivity(welcomeIntent);


                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            //if(success) {

                            //}
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
