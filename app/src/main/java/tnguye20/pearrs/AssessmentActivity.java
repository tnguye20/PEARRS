package tnguye20.pearrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class AssessmentActivity extends AppCompatActivity {
    private static final String ASSET_REQUEST_URL = "http://tnguye20.w3.uvm.edu/pearrs/asset.php";
    private Button mProceedButton;
    private TextView mQuestion;
    private TextView mError;
    private RadioGroup mRadioGroup;
    private TextView mTextAnswer;
    private RadioButton mRadioButton;
    private Spinner mSpinner;
    private RelativeLayout mRelativeLayout;
    private RelativeLayout.LayoutParams mLayoutParams;

    /* GLOBAL VARIABLES FOR THE ENTIRE SURVEY */
    private int userId;
    private JSONArray assetSurvey;
    private int assetSurveyLength;
    private JSONObject question;
    private String questionText;
    private String questionId;
    private String surveyId;
    private String type;
    private String answerType;
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
        setContentView(R.layout.activity_assessment);

        /* Get all the Views */
        mRelativeLayout = (RelativeLayout)findViewById(R.id.activity_assessment);
        mProceedButton = (Button)findViewById(R.id.proceedButton);
        mQuestion = (TextView)findViewById(R.id.questionTextView);
        mError = (TextView)findViewById(R.id.errorTextView);

        /* Get the params for the layout */
        mLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.addRule(RelativeLayout.BELOW, R.id.questionTextView);

        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                assetSurvey = response;
                assetSurveyLength = assetSurvey.length();
                tempAnswerList = new ArrayList<Integer>(assetSurveyLength);
                loadPage(0);
            }
        };

        /* Get the corresponding questions for the Introduction activity */
        JsonArrayRequest introductionRequest = new JsonArrayRequest(ASSET_REQUEST_URL, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(AssessmentActivity.this);
        queue.add(introductionRequest);
    }

    private void loadPage(int index){
        final int nextIndex = index + 1;
        try{
            question = assetSurvey.getJSONObject(index);
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
            if(nextIndex >= assetSurveyLength){
                mProceedButton.setText("Submit Assessment");
            }

            if(type.equals("Text")){
                mTextAnswer = new EditText(AssessmentActivity.this);
                mTextAnswer.setGravity(Gravity.CENTER);
                mRelativeLayout.addView(mTextAnswer, mLayoutParams);
            }else if(type.equals("Radio")) {
                /* Create all the radio button(s) for the question dynamically */
                mRadioGroup = new RadioGroup(AssessmentActivity.this);
                mRadioGroup.setOrientation(RadioGroup.VERTICAL);

                answers = new JSONObject(question.getString("fldAnswers"));

                Iterator<String> iterator = answers.keys();

                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    String temp2 = answers.getString(key);
                    JSONObject answer = new JSONObject(temp2);
                    String answerText = answer.getString("text");
                    int answerValue = answer.getInt("value");

                    mRadioButton = new RadioButton(AssessmentActivity.this);
                    mRadioButton.setId(answerValue);
                    mRadioButton.setText(answerText);
                    mRadioGroup.addView(mRadioButton);
                }
                mRelativeLayout.addView(mRadioGroup, mLayoutParams);
            }else if(type.equals("Spinner")){
                mSpinner = new Spinner(AssessmentActivity.this);

                // Create an Array Adapter using the string array in the String Resource and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.scale, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appear
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                mSpinner.setAdapter(adapter);

                mRelativeLayout.addView(mSpinner, mLayoutParams);
            }

            // Set the Proceed Button to go to the next question
            mProceedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get rid of the intro text
                    //mIntro.setText("");

                    boolean proceed = true;
                    if(type.equals("Radio")){
                        if(mRadioGroup.getCheckedRadioButtonId() == -1) {
                            // This means they haven't clicked anything yet
                            proceed = false;
                        }
                    }else if(type.equals("Text")){
                        if(mTextAnswer.getText().toString().equals("")){
                            proceed = false;
                        }if(mTextAnswer.getText().toString().equals("")){
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
                    }else if(type.equals("Spinner")){
                        if(mSpinner.getSelectedItem() == null){
                            proceed = false;
                        }
                    }
                    if(proceed){
                        if(type.equals("Radio")) {
                            // Get value of the checked radio button and add to total score
                            int checkedValue = mRadioGroup.getCheckedRadioButtonId();
                            total += checkedValue;
                            tempAnswerList.add(checkedValue);

                            // Save the answer to the JSON file
                            JSONObject questionResult = new JSONObject();
                            try {
                                questionResult.put("pmkQuestionId", questionId);
                                questionResult.put("fnkSurveyId", surveyId);
                                questionResult.put("fldQuestion", questionText);
                                // Get the actual text of the answer
                                String radioText = ((RadioButton) mRadioGroup.findViewById(checkedValue)).getText().toString();
                                questionResult.put("fldAnswerText", radioText);
                                questionResult.put("fldAnswerValue", checkedValue);

                                // Put this object into an JSONArray
                                questionResults.put(questionResult);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (nextIndex >= assetSurveyLength) {
                                // Put JSONArray of answers into the result JSONObject and send to server
                                try {
                                    results.put("questions", questionResults);
                                    results.put("totalScore", total);
                                    Intent intent = new Intent(AssessmentActivity.this, ThankActivity.class);
                                    AssessmentActivity.this.startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                /*
                                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Intent positiveIntent = new Intent(SurveyActivity.this, PositiveActivity.class);
                                        positiveIntent.putExtra("total", total);
                                        SurveyActivity.this.startActivity(positiveIntent);
                                    }
                                };

                                SurveyRequest surveyRequest = new SurveyRequest(nextSurvey, surveyId, Integer.toString(userId), results.toString(), responseListener);
                                RequestQueue queue = Volley.newRequestQueue(SurveyActivity.this);
                                queue.add(surveyRequest);
                                */

                            } else {
                                try {
                                    /* Perform check to see if there is branching logic */
                                    branchValues = new JSONObject(question.getString("fldBranchValues"));
                                    int branchValue = branchValues.getInt("branchValue");

                                    /* This checks if the value is to be compared with the total score or the current question's score */
                                    boolean isTotal = branchValues.getBoolean("isTotal");

                                    boolean extraCondition = false; // Assume the second condition is always false

                                    if (!branchValues.isNull("extraCondition")) {
                                        /* This get the extra condition(s) for the survey to branch */
                                        JSONObject condition = new JSONObject(branchValues.getString("extraCondition"));
                                        ArrayList<Integer> values = new ArrayList<Integer>();
                                        ArrayList<Integer> valuesCompare = new ArrayList<Integer>();

                                        Iterator<String> iterator1 = condition.keys();
                                        while (iterator1.hasNext()) {
                                            int key = Integer.parseInt(iterator1.next());
                                            values.add(tempAnswerList.get(key));
                                            valuesCompare.add(condition.getInt(Integer.toString(key)));
                                        }

                                        extraCondition = compare(values, valuesCompare);
                                    }

                                    if (isTotal) {
                                        if (branchValue >= total || extraCondition) {
                                            if (branchValues.isNull("nextIndex")) {
                                                // Put JSONArray of answers into the result JSONObject and send to server
                                                try {
                                                    results.put("questions", questionResults);
                                                    results.put("totalScore", total);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                /*
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
                                                */
                                            } else {
                                                int newNextIndex = branchValues.getInt("nextIndex");
                                                // Delete all the existing button to prepare for the new one
                                                mError.setText("");
                                                mRadioGroup.clearCheck();
                                                mRelativeLayout.removeView(mRadioGroup);
                                                loadPage(nextIndex);
                                            }
                                        } else {
                                            // Delete all the existing button to prepare for the new one
                                            mError.setText("");
                                            mRadioGroup.clearCheck();
                                            mRelativeLayout.removeView(mRadioGroup);
                                            loadPage(nextIndex);
                                        }
                                    } else {
                                        if (branchValue == checkedValue || extraCondition) {
                                            if (branchValues.isNull("nextIndex")) {
                                                // Put JSONArray of answers into the result JSONObject and send to server
                                                try {
                                                    results.put("questions", questionResults);
                                                    results.put("totalScore", total);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                /* TEMPORARY FOR TESTING */
                                                String activity = branchValues.getString("activity");
                                                Intent branchIntent = new Intent(AssessmentActivity.this, Branch.getActivity(activity));
                                                AssessmentActivity.this.startActivity(branchIntent);

                                                /*
                                                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            String activity = branchValues.getString("activity");
                                                            Intent branchIntent = new Intent(AssessmentActivity.this, Branch.getActivity(activity));
                                                            AssessmentActivity.this.startActivity(branchIntent);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };

                                                SurveyRequest surveyRequest = new SurveyRequest(nextSurvey, surveyId, Integer.toString(userId), results.toString(), responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(SurveyActivity.this);
                                                queue.add(surveyRequest);
                                                */

                                            } else {
                                                int newNextIndex = branchValues.getInt("nextIndex");
                                                // Delete all the existing button to prepare for the new one
                                                mError.setText("");
                                                mRadioGroup.clearCheck();
                                                mRelativeLayout.removeView(mRadioGroup);
                                                loadPage(newNextIndex);
                                            }
                                        } else {
                                            // Delete all the existing button to prepare for the new one
                                            mError.setText("");
                                            mRadioGroup.clearCheck();
                                            mRelativeLayout.removeView(mRadioGroup);
                                            loadPage(nextIndex);
                                        }
                                    }
                                } catch (JSONException e) {
                                    // Delete all the existing button to prepare for the new one
                                    mError.setText("");
                                    mRadioGroup.clearCheck();
                                    mRelativeLayout.removeView(mRadioGroup);
                                    loadPage(nextIndex);
                                }
                            }
                        }else if(type.equals("Text")){
                            String textAnwer = mTextAnswer.getText().toString();

                            // Save the answer to the JSON file
                            JSONObject questionResult = new JSONObject();
                            try {
                                questionResult.put("pmkQuestionId", questionId);
                                questionResult.put("fnkSurveyId", surveyId);
                                questionResult.put("fldQuestion", questionText);
                                questionResult.put("fldAnswerText", textAnwer);

                                // Put this object into an JSONArray
                                questionResults.put(questionResult);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (nextIndex >= assetSurveyLength) {
                                // Put JSONArray of answers into the result JSONObject and send to server
                                try {
                                    results.put("questions", questionResults);
                                    results.put("totalScore", total);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                /*
                                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Intent positiveIntent = new Intent(SurveyActivity.this, PositiveActivity.class);
                                        positiveIntent.putExtra("total", total);
                                        SurveyActivity.this.startActivity(positiveIntent);
                                    }
                                };

                                SurveyRequest surveyRequest = new SurveyRequest(nextSurvey, surveyId, Integer.toString(userId), results.toString(), responseListener);
                                RequestQueue queue = Volley.newRequestQueue(SurveyActivity.this);
                                queue.add(surveyRequest);
                                */
                            } else {
                                // Delete all the existing button to prepare for the new one
                                mError.setText("");
                                mRelativeLayout.removeView(mTextAnswer);
                                loadPage(nextIndex);
                            }
                        }else if(type.equals("Spinner")){
                            String spinnerAnswer = (String)mSpinner.getSelectedItem();

                            // Save the answer to the JSON file
                            JSONObject questionResult = new JSONObject();
                            try {
                                questionResult.put("pmkQuestionId", questionId);
                                questionResult.put("fnkSurveyId", surveyId);
                                questionResult.put("fldQuestion", questionText);
                                questionResult.put("fldAnswerText", spinnerAnswer);

                                // Put this object into an JSONArray
                                questionResults.put(questionResult);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (nextIndex >= assetSurveyLength) {
                                // Put JSONArray of answers into the result JSONObject and send to server
                                try {
                                    results.put("questions", questionResults);
                                    results.put("totalScore", total);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                /*
                                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Intent positiveIntent = new Intent(SurveyActivity.this, PositiveActivity.class);
                                        positiveIntent.putExtra("total", total);
                                        SurveyActivity.this.startActivity(positiveIntent);
                                    }
                                };

                                SurveyRequest surveyRequest = new SurveyRequest(nextSurvey, surveyId, Integer.toString(userId), results.toString(), responseListener);
                                RequestQueue queue = Volley.newRequestQueue(SurveyActivity.this);
                                queue.add(surveyRequest);
                                */
                            } else {
                                // Delete all the existing button to prepare for the new one
                                mError.setText("");
                                mRelativeLayout.removeView(mSpinner);
                                loadPage(nextIndex);
                            }
                        }
                    }else{
                        mError.setText("Please choose an answer before proceeding.");
                    }
                }
            });
        }catch(JSONException e){
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
