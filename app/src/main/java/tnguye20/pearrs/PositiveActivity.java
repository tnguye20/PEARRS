package tnguye20.pearrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PositiveActivity extends AppCompatActivity {
    private TextView mPositive;
    private Button mProcced;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positive);

        /* Get all the Views */
        mPositive = (TextView)findViewById(R.id.positiveTextView);
        mProcced = (Button)findViewById(R.id.proceedButton);

        /* Get the intent from the survey */
        Intent intent = getIntent();
        int total = intent.getIntExtra("total", 0);

        if(total < 8){
            String text = getResources().getString(R.string.negative);
            mPositive.setText(text);
            // Hide the proceed button
            mProcced.setVisibility(View.INVISIBLE);
            mProcced.setEnabled(false);
        }else{
            String text = getResources().getString(R.string.positive);
            mPositive.setText(text);

            mProcced.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent assessmentActivity = new Intent(PositiveActivity.this, AssessmentActivity.class);
                    PositiveActivity.this.startActivity(assessmentActivity);
                }
            });
        }
    }
}
