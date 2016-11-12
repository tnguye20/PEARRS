package tnguye20.pearrs;

import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class SurveyActivity extends AppCompatActivity {
    private static final String SURVEY_REQUEST_URL = "http://tnguye20.w3.uvm.edu/pearrs/survey.php";
    private Button mProceedButton;
    private RadioGroup mRadioGroup;
    private TextView mQuestion;
    private RadioButton radioButton;

    /* GLOBAL VARIABLES FOR THE ENTIRE SURVEY */
    private int userId;
    private String surveyQuestions;
    private int surveyQuestionsLength;
    private JSONArray survey;
    private JSONObject question;
    private String questionText;
    private JSONObject answers;
    private int[] answerValues = new int[1000];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        /* Get the survey data from LoginActivity */
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        surveyQuestions = intent.getStringExtra("surveyQuestions");

        mProceedButton = (Button) findViewById(R.id.proceedButton);
        mRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        mQuestion = (TextView) findViewById(R.id.questionTextView);

        /* Begin the recursive process of displaying the survey */
        try {
            survey = new JSONArray(surveyQuestions);
            surveyQuestionsLength = survey.length();
            loadPage(0);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    /* Method created to change the View base on question */
    private void loadPage(final int index){
        final int nextIndex = index + 1;
        try {
            question = survey.getJSONObject(index);

            /* Get and set the question text */
            questionText = question.getString("fldQuestion");
            mQuestion.setText(questionText);


            /* Change the button text if it is the last question */
            if(nextIndex >= surveyQuestionsLength){
                mProceedButton.setText("Submit Survey");
            }

            /* Create all the radio button(s) for the question dynamically */
            String temp = question.getString("fldAnswers");
            answers = new JSONObject(temp);

            Iterator<String> iterator = answers.keys();

            while(iterator.hasNext()){
                String key = (String)iterator.next();
                String temp2 = answers.getString(key);
                JSONObject answer = new JSONObject(temp2);
                String answerText = answer.getString("text");
                int answerValue = answer.getInt("value");

                radioButton = new RadioButton(SurveyActivity.this);
                radioButton.setId(answerValue);
                radioButton.setText(answerText);
                mRadioGroup.addView(radioButton);
            }

            // Set the Proceed Button to go to the next question
            mProceedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get value of the checked radio button
                    int checkedValue = mRadioGroup.getCheckedRadioButtonId();
                    answerValues[index] = checkedValue;

                    if(nextIndex >= surveyQuestionsLength) {
                        Intent thankIntent = new Intent(SurveyActivity.this, ThankActivity.class);
                        SurveyActivity.this.startActivity(thankIntent);
                    }else{
                        // Delete all the existing button to prepare for the new one
                        mRadioGroup.removeAllViews();
                        loadPage(nextIndex);
                    }
                }
            });

        } catch(JSONException e){
            e.printStackTrace();
        }
    }
}
