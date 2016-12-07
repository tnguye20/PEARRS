package tnguye20.pearrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private TextView mWelcome;
    private Button mProceedButton;
    private int userId;
    private String lastName;
    private String gender;
    private String nextSurvey;
    private String surveyQuestions;
    private String displayString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Get Intent variables and values
        Intent intent = getIntent();
        //int userId = intent.getIntExtra("userId", 1000);
        if(intent == null){
            lastName = "Friend";
        } else {
            userId = intent.getIntExtra("userId", 0);
            lastName = intent.getStringExtra("lastName");
            gender = intent.getStringExtra("gender");
            nextSurvey = intent.getStringExtra("nextSurvey");
            surveyQuestions = intent.getStringExtra("surveyQuestions");
        }

        displayString = "Welcome " + lastName + "! Thank you for downloading the PEARRS app. Let's get started!\n";
        displayString += "We have some questions about your health behaviors.\n";
        displayString += "We will not share yout answers with your doctor unless you ask us to. Please press the button below to proceed.";

        mWelcome = (TextView)findViewById(R.id.welcomeTextView);
        mWelcome.setText(displayString);
        mProceedButton = (Button)findViewById(R.id.proceedButton);

        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent;
                if(nextSurvey.equals("1")) {
                    newIntent = new Intent(WelcomeActivity.this, IntroductionActivity.class);
                }else{
                    newIntent = new Intent(WelcomeActivity.this, SurveyActivity.class);
                }
                newIntent.putExtra("userId", userId);
                newIntent.putExtra("firstName", lastName);
                newIntent.putExtra("nextSurvey", nextSurvey);
                newIntent.putExtra("surveyQuestions", surveyQuestions);
                WelcomeActivity.this.startActivity(newIntent);
            }
        });
    }
}
