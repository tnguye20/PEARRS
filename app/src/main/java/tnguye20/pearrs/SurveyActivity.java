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
import android.widget.Toast;

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
    private TextView mError;
    private RadioButton radioButton;

    /* GLOBAL VARIABLES FOR THE ENTIRE SURVEY */
    private int userId;
    private String nextSurvey;
    private String surveyQuestions;
    private int surveyQuestionsLength;
    private JSONArray survey;
    private JSONObject question;
    private String questionText;
    private String questionId;
    private String surveyId;
    private String type;
    private JSONObject branchValues;
    private JSONObject answers;
    private ArrayList<Integer> tempAnswerList;
    private int total = 0;

    /* JSONObject to handle all the result */
    private JSONObject results = new JSONObject();
    private JSONArray questionResults = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        /* Get the survey data from LoginActivity */
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        nextSurvey = intent.getStringExtra("nextSurvey");
        surveyQuestions = intent.getStringExtra("surveyQuestions");

        /* Start to construct the result JSON File */
        try {
            results.put("pmkUserId", userId);
        } catch (JSONException e){
            e.printStackTrace();
        }

        /* Get all the views */
        mProceedButton = (Button) findViewById(R.id.proceedButton);
        mRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        mQuestion = (TextView) findViewById(R.id.questionTextView);
        mError = (TextView) findViewById(R.id.errorTextView);

        /* Begin the recursive process of displaying the survey */
        try {
            survey = new JSONArray(surveyQuestions);
            surveyQuestionsLength = survey.length();
            tempAnswerList = new ArrayList<Integer>(surveyQuestionsLength);
            loadPage(0);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    /* Method created to change the View base on question */
    private void loadPage(int index){
        final int nextIndex = index + 1;
        try {
            question = survey.getJSONObject(index);

            /* Get and set the question text */
            questionText = question.getString("fldQuestion");
            mQuestion.setText(questionText);

            /* Get the question ID */
            questionId = question.getString("pmkQuestionId");

            /* Get the survey ID */
            surveyId = question.getString("fnkSurveyId");

            /* Get the question type */
            type = question.getString("fldType");

            /* Change the button text if it is the last question */
            if(nextIndex >= surveyQuestionsLength){
                mProceedButton.setText("Submit Survey");
            }

            /* Create all the radio button(s) for the question dynamically */
            answers = new JSONObject(question.getString("fldAnswers"));

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
                    boolean proceed = true;
                    if(mRadioGroup.getCheckedRadioButtonId() == -1){
                        // This means they haven't clicked anything yet
                        mError.setText("Please choose an answer before proceeding.");
                    }
                    if(proceed){
                        // Get value of the checked radio button and add to total score
                        int checkedValue = mRadioGroup.getCheckedRadioButtonId();
                        total += checkedValue;
                        tempAnswerList.add(checkedValue);

                        // Save the answer to the JSON file
                        JSONObject questionResult = new JSONObject();
                        try{
                            questionResult.put("pmkQuestionId", questionId);
                            questionResult.put("fnkSurveyId", surveyId);
                            questionResult.put("fldQuestion", questionText);
                            // Get the actual text of the answer
                            String radioText =  ((RadioButton)mRadioGroup.findViewById(checkedValue)).getText().toString();
                            questionResult.put("fldAnswerText", radioText);
                            questionResult.put("fldAnswerValue", checkedValue);

                            // Put this object into an JSONArray
                            questionResults.put(questionResult);
                        } catch(JSONException e){
                            e.printStackTrace();
                        }

                        if (nextIndex >= surveyQuestionsLength) {
                            // Put JSONArray of answers into the result JSONObject and send to server
                            try{
                                results.put("questions", questionResults);
                                results.put("totalScore", total);
                            }catch(JSONException e){
                                e.printStackTrace();
                            }

                            final Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Intent thankIntent = new Intent(SurveyActivity.this, ThankActivity.class);
                                    SurveyActivity.this.startActivity(thankIntent);
                                }
                            };

                            SurveyRequest surveyRequest = new SurveyRequest(nextSurvey, surveyId, Integer.toString(userId), results.toString(), responseListener);
                            RequestQueue queue = Volley.newRequestQueue(SurveyActivity.this);
                            queue.add(surveyRequest);

                        } else {
                            try{
                                /* Perform check to see if there is branching logic */
                                branchValues = new JSONObject(question.getString("fldBranchValues"));
                                int branchValue = branchValues.getInt("branchValue");

                                /* This checks if the value is to be compared with the total score or the current question's score */
                                boolean isTotal = branchValues.getBoolean("isTotal");

                                boolean extraCondition = false; // Assume the second condition is always false

                                if(!branchValues.isNull("extraCondition")) {
                                    /* This get the extra condition(s) for the survey to branch */
                                    JSONObject condition = new JSONObject(branchValues.getString("extraCondition"));
                                    ArrayList<Integer> values = new ArrayList<Integer>();
                                    ArrayList<Integer> valuesCompare = new ArrayList<Integer>();

                                    Iterator<String> iterator1 = condition.keys();
                                    while(iterator1.hasNext()){
                                        int key = Integer.parseInt(iterator1.next());
                                        values.add(tempAnswerList.get(key));
                                        valuesCompare.add(condition.getInt(Integer.toString(key)));
                                    }

                                    extraCondition = compare(values, valuesCompare);
                                }

                                if(isTotal){
                                    if(branchValue >= total || extraCondition) {
                                        if (branchValues.isNull("nextIndex")) {
                                            // Put JSONArray of answers into the result JSONObject and send to server
                                            try {
                                                results.put("questions", questionResults);
                                                results.put("totalScore", total);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            final Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        String activity = branchValues.getString("activity");
                                                        Intent branchIntent = new Intent(SurveyActivity.this, Branch.getActivity(activity));
                                                        SurveyActivity.this.startActivity(branchIntent);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            SurveyRequest surveyRequest = new SurveyRequest(nextSurvey, surveyId, Integer.toString(userId), results.toString(), responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(SurveyActivity.this);
                                            queue.add(surveyRequest);
                                        } else {
                                            int newNextIndex = branchValues.getInt("nextIndex");
                                            // Delete all the existing button to prepare for the new one
                                            mError.setText("");
                                            mRadioGroup.removeAllViews();
                                            mRadioGroup.clearCheck();
                                            loadPage(newNextIndex);
                                        }
                                    } else {
                                        // Delete all the existing button to prepare for the new one
                                        mError.setText("");
                                        mRadioGroup.removeAllViews();
                                        mRadioGroup.clearCheck();
                                        loadPage(nextIndex);
                                    }
                                }else{
                                    if(branchValue == checkedValue || extraCondition) {
                                        if (branchValues.isNull("nextIndex")) {
                                            // Put JSONArray of answers into the result JSONObject and send to server
                                            try {
                                                results.put("questions", questionResults);
                                                results.put("totalScore", total);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            final Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        String activity = branchValues.getString("activity");
                                                        Intent branchIntent = new Intent(SurveyActivity.this, Branch.getActivity(activity));
                                                        SurveyActivity.this.startActivity(branchIntent);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            SurveyRequest surveyRequest = new SurveyRequest(nextSurvey, surveyId, Integer.toString(userId), results.toString(), responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(SurveyActivity.this);
                                            queue.add(surveyRequest);
                                        } else {
                                            int newNextIndex = branchValues.getInt("nextIndex");
                                            // Delete all the existing button to prepare for the new one
                                            mError.setText("");
                                            mRadioGroup.removeAllViews();
                                            mRadioGroup.clearCheck();
                                            loadPage(newNextIndex);
                                        }
                                    } else {
                                        // Delete all the existing button to prepare for the new one
                                        mError.setText("");
                                        mRadioGroup.removeAllViews();
                                        mRadioGroup.clearCheck();
                                        loadPage(nextIndex);
                                    }
                                }
                            }catch (JSONException e) {
                                // Delete all the existing button to prepare for the new one
                                mError.setText("");
                                mRadioGroup.removeAllViews();
                                mRadioGroup.clearCheck();
                                loadPage(nextIndex);
                            }
                        }
                    }
                }
            });

        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    private boolean compare(ArrayList<Integer> values, ArrayList<Integer> valuesCompare){

        for(int i = 0; i < values.size(); i++){
            if(values.get(i) != valuesCompare.get(i)){
                return false;
            }
        }

        return true;
    }
}
