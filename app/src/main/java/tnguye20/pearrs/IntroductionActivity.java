package tnguye20.pearrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class IntroductionActivity extends AppCompatActivity {
    private static final String INTRO_REQUEST_URL = "http://tnguye20.w3.uvm.edu/pearrs/intro.php";
    private Button mProceedButton;
    private TextView mIntro;
    private TextView mQuestion;
    private TextView mError;
    private EditText mTextAnswer;
    private RadioGroup mRadioGroup;
    private RadioButton radioButton;
    private RelativeLayout mRelativeLayout;
    RelativeLayout.LayoutParams mLayoutParams;

    /* GLOBAL VARIABLES FOR THE ENTIRE SURVEY */
    private int userId;
    private String nextSurvey;
    private String surveyQuestions;
    private int introSurveyLength;
    private JSONArray introSurvey;
    private JSONObject question;
    private String questionText;
    private String type;
    private String answerType;
    private String questionId;
    private String surveyId;
    private JSONObject answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        /* Get all the intent values */
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        nextSurvey = intent.getStringExtra("nextSurvey");
        surveyQuestions = intent.getStringExtra("surveyQuestions");

        /* Get all the views */
        mRelativeLayout = (RelativeLayout)findViewById(R.id.activity_introduction);
        mQuestion = (TextView)findViewById(R.id.questionTextView);
        mIntro = (TextView)findViewById(R.id.introTextView);
        mError = (TextView)findViewById(R.id.errorTextView);
        mProceedButton = (Button)findViewById(R.id.proceedButton);

        /* Get the params for the layout */
        mLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.addRule(RelativeLayout.BELOW, R.id.questionTextView);

        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                introSurvey = response;
                introSurveyLength = introSurvey.length();
                loadPage(0);
            }
        };

        /* Get the corresponding questions for the Introduction activity */
        JsonArrayRequest introductionRequest = new JsonArrayRequest(INTRO_REQUEST_URL, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(IntroductionActivity.this);
        queue.add(introductionRequest);
    }

    private void loadPage(int index){
        final int nextIndex = index + 1;
        try{
            question = introSurvey.getJSONObject(index);

            /* Get and set the question text */
            questionText = question.getString("fldQuestion");
            mQuestion.setText(Integer.toString(nextIndex) + ". " + questionText);

            /* Get the question ID */
            questionId = question.getString("pmkQuestionId");

            /* Get the survey ID */
            surveyId = question.getString("fnkSurveyId");

            /* Get the question type */
            type = question.getString("fldType");

            /* Get the question answer type */
            answerType = question.getString("fldAnswerType");

            /* Change the button text if it is the last question */
            if(nextIndex >= introSurveyLength){
                mProceedButton.setGravity(Gravity.CENTER);
                mProceedButton.setText("Submit");
            }

            if(type.equals("Text")){
                mTextAnswer = new EditText(IntroductionActivity.this);
                mTextAnswer.setGravity(Gravity.CENTER);
                mRelativeLayout.addView(mTextAnswer, mLayoutParams);
            }else if (type.equals("Radio")){
                mRadioGroup = new RadioGroup(IntroductionActivity.this);
                mRadioGroup.setOrientation(RadioGroup.VERTICAL);

                /* Create all the radio button(s) for the question dynamically */
                answers = new JSONObject(question.getString("fldAnswers"));

                Iterator<String> iterator = answers.keys();

                while(iterator.hasNext()){
                    String key = (String)iterator.next();
                    String temp2 = answers.getString(key);
                    JSONObject answer = new JSONObject(temp2);
                    String answerText = answer.getString("text");
                    int answerValue = answer.getInt("value");

                    radioButton = new RadioButton(IntroductionActivity.this);
                    radioButton.setId(answerValue);
                    radioButton.setText(answerText);
                    mRadioGroup.addView(radioButton);
                }
                mRelativeLayout.addView(mRadioGroup, mLayoutParams);
            }

            mProceedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get rid of the intro text
                    /*
                    if(nextIndex == 1){
                        mIntro.setText("");
                    }
                    */
                    boolean proceed = true;
                    /* Get the values of the answer */
                   if(type.equals("Radio")){
                       if(mRadioGroup.getCheckedRadioButtonId() == -1) {
                           // This means they haven't clicked anything yet
                           proceed = false;
                       }
                   }else if(type.equals("Text")){
                       if(mTextAnswer.getText().toString().equals("")){
                           proceed = false;
                       }else {
                            if(answerType.equals("float")){
                                try{
                                    float x = Float.parseFloat(mTextAnswer.getText().toString());
                                }catch (NumberFormatException e){
                                    proceed = false;
                                }
                            }
                       }
                   }

                    if(proceed) {
                        if(nextIndex >= introSurveyLength){
                            Intent surveyIntent = new Intent(IntroductionActivity.this, SurveyActivity.class);
                            surveyIntent.putExtra("userId", userId);
                            surveyIntent.putExtra("nextSurvey", nextSurvey);
                            surveyIntent.putExtra("surveyQuestions", surveyQuestions);
                            //surveyIntent.putExtra("intro", "Thank you for your information. Please complete the survey below");
                            IntroductionActivity.this.startActivity(surveyIntent);
                        }else {
                            mError.setText("");
                            mRelativeLayout.removeView(mRadioGroup);
                            mRelativeLayout.removeView(mTextAnswer);
                            loadPage(nextIndex);
                        }
                    }else{
                        mError.setText("Please input valid answer before proceeding.");
                    }
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
