package tnguye20.pearrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private TextView mWelcome;
    private Button mProceedButton;
    private String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Get Intent variables and values
        Intent intent = getIntent();
        if(intent == null){
            firstName = "";
        } else {
            firstName = intent.getStringExtra("firstName");
        }

        String displayString = "Hello, " + firstName + "! Thank you for downloading the PEARRS app. Let's get started!\n";
        displayString += "We have some questions about your health behaviors.\n";
        displayString += "We will not share yout answers with your doctor unless you ask us to. Please press the button below to proceed.";

        mWelcome = (TextView)findViewById(R.id.welcomeTextView);
        mWelcome.setText(displayString);
    }
}
