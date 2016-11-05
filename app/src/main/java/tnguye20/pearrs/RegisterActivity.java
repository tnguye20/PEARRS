package tnguye20.pearrs;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mName;
    private EditText mUserName;
    private EditText mPass;
    private EditText mPass2;
    private EditText mDoctorCode;
    private Button mRegisterButton;

    private TextView mAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstName = (EditText)findViewById(R.id.firstNameEditText);
        mLastName = (EditText)findViewById(R.id.lastNameEditText);
        mUserName = (EditText)findViewById(R.id.userNameEditText);
        mPass = (EditText)findViewById(R.id.passEditText);
        mPass2 = (EditText)findViewById(R.id.pass2EditText);
        mDoctorCode = (EditText)findViewById(R.id.doctorEditText);
        mRegisterButton = (Button)findViewById(R.id.registerButton);

        mAlert = (TextView)findViewById(R.id.alertTextView);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from View
                final String firstName = mFirstName.getText().toString();
                final String lastName = mLastName.getText().toString();
                final String userName = mUserName.getText().toString();
                final String password = mPass.getText().toString();
                final String password2 = mPass2.getText().toString();
                final String doctorCode = mDoctorCode.getText().toString();

                boolean error = false;

                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(loginIntent);
                            } else {
                                mAlert.setText(jsonResponse.getString("password"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, userName, password, password2, doctorCode, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }
}
